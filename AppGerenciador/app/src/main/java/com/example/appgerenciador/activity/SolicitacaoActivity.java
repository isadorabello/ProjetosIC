package com.example.appgerenciador.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.appgerenciador.R;
import com.example.appgerenciador.adapter.AdapterPaciente;
import com.example.appgerenciador.helper.ConfiguracaoFirebase;
import com.example.appgerenciador.helper.UsuarioFirebase;
import com.example.appgerenciador.listener.RecyclerItemClickListener;
import com.example.appgerenciador.model.Paciente;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SolicitacaoActivity extends AppCompatActivity {

    private RecyclerView recyclerSolicitacoes;
    private final List<Paciente> pacientes = new ArrayList<>();
    private AdapterPaciente adapterPaciente;
    private android.app.AlertDialog dialog;
    private String idUsuarioLogado;

    private ValueEventListener valueEventListenerVinculo;
    private DatabaseReference usuariosRef;
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitacao);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Solicitações");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = firebaseRef.child("pedido_vinculo").child(idUsuarioLogado);

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerSolicitacoes.setLayoutManager(layoutManager);
        recyclerSolicitacoes.setHasFixedSize(true);

        adapterPaciente = new AdapterPaciente(pacientes);
        recyclerSolicitacoes.setAdapter(adapterPaciente);

        recuperarDados();

        recyclerSolicitacoes.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(), recyclerSolicitacoes, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Paciente pacienteSelecionado = pacientes.get(position);

                AlertDialog.Builder dialog = new AlertDialog.Builder(SolicitacaoActivity.this);

                //Configurar o titulo e mensagem
                dialog.setTitle("Vínculo Hospitalar");
                dialog.setMessage("Você aceita "+pacienteSelecionado.getNomePaciente()+" na sua lista de pacientes?");

                dialog.setPositiveButton("Sim", (dialog1, which) -> {
                    Paciente paciente = new Paciente();
                    paciente.setIdHospital(idUsuarioLogado);
                    paciente.setIdPaciente(pacienteSelecionado.getIdPaciente());
                    paciente.setCaminhoFoto(pacienteSelecionado.getCaminhoFoto());
                    paciente.setComorbidades(pacienteSelecionado.getComorbidades());
                    paciente.setImportantes(pacienteSelecionado.getImportantes());
                    paciente.setDataNascimento(pacienteSelecionado.getDataNascimento());
                    paciente.setNomeMinusculo(pacienteSelecionado.getNomeMinusculo());
                    paciente.setNomePaciente(pacienteSelecionado.getNomePaciente());
                    paciente.setAnotacoes("Não informado");
                    paciente.setFicha("Não informado");
                    paciente.setDataEntrada("Não informado");
                    paciente.salvarPaciente();
                    paciente.removerSolicitacao();
                    Toast.makeText(SolicitacaoActivity.this, "Solicitação aceita!", Toast.LENGTH_LONG).show();
                });

                dialog.setNegativeButton("Não", null);

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Paciente pacienteSelecionado = pacientes.get(position);

                AlertDialog.Builder dialog = new AlertDialog.Builder(SolicitacaoActivity.this);

                //Configurar o titulo e mensagem
                dialog.setTitle("Vínculo Hospitalar");
                dialog.setMessage("Você deseja excluir a solicitação de vínculo de "+pacienteSelecionado.getNomePaciente()+"?");

                dialog.setPositiveButton("Sim", (dialog1, which) -> {
                    Paciente paciente = new Paciente();
                    paciente.setIdHospital(idUsuarioLogado);
                    paciente.setIdPaciente(pacienteSelecionado.getIdPaciente());
                    paciente.removerSolicitacao();
                    Toast.makeText(SolicitacaoActivity.this, "Solicitação excluída!", Toast.LENGTH_LONG).show();
                });

                dialog.setNegativeButton("Não", null);

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    private void inicializarComponentes(){
        recyclerSolicitacoes = findViewById(R.id.recyclerSolicitacoes);
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarDados();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerVinculo);
    }

    private void recuperarDados(){
        valueEventListenerVinculo = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                pacientes.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    Paciente paciente = ds.getValue(Paciente.class);
                    pacientes.add(paciente);
                }

                adapterPaciente.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}