package com.example.appgerenciador.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appgerenciador.R;
import com.example.appgerenciador.adapter.AdapterAlertas;
import com.example.appgerenciador.helper.ConfiguracaoFirebase;
import com.example.appgerenciador.helper.UsuarioFirebase;
import com.example.appgerenciador.listener.RecyclerItemClickListener;
import com.example.appgerenciador.model.Alerta;
import com.example.appgerenciador.model.Paciente;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FichaMedicaActivity extends AppCompatActivity {

    private TextView textNome, textData, textComorbidade, textFicha, textAnotacao, textDataE;
    private CircleImageView imagemPaciente;
    private RecyclerView recyclerAlertas;
    private String idPaciente;
    private Paciente paciente;
    private String idUsuarioAtual;
    private AdapterAlertas adapterAlertas;
    private final List<Alerta> alertas = new ArrayList<>();
    private ValueEventListener valueEventListenerAlertas;
    private DatabaseReference alertaRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_medica);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ficha Médica");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();
        idUsuarioAtual = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            paciente = (Paciente)bundle.getSerializable("pacienteSelecionado");

            idPaciente = paciente.getIdPaciente();

            textNome.setText(paciente.getNomePaciente());
            textData.setText(paciente.getDataNascimento());
            textComorbidade.setText(paciente.getComorbidades());
            textFicha.setText(paciente.getFicha());
            textAnotacao.setText(paciente.getAnotacoes());
            textDataE.setText(paciente.getDataEntrada());

            String url = paciente.getCaminhoFoto();

            if(!url.isEmpty()){
                Picasso.get().load( url ).into( imagemPaciente );
            }else {
                Picasso.get().load( R.drawable.perfil ).into( imagemPaciente );
            }
        }

        alertaRef = firebaseRef.child("alertas").child("usuarios").child(idPaciente);

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerAlertas.setLayoutManager(layoutManager);
        recyclerAlertas.setHasFixedSize(true);

        adapterAlertas = new AdapterAlertas(alertas,this);
        recyclerAlertas.setAdapter(adapterAlertas);

        recuperarAlertas();

        //adicionar evento de clique no recyclerView
        recyclerAlertas.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(), recyclerAlertas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Alerta alertaSelecionado = alertas.get(position);

                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaMedicaActivity.this);

                //Configurar o titulo e mensagem
                dialog.setTitle("Confirmar checagem");
                dialog.setMessage("Você confirma que conferiu o estado do(a) paciente "+
                        alertaSelecionado.getNomeAlerta()+"?");

                dialog.setPositiveButton("Sim", (dialog1, which) -> {
                    alertaSelecionado.deletar();
                    adapterAlertas.notifyDataSetChanged();
                });

                dialog.setNegativeButton("Não", null);

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarAlertas();
    }

    @Override
    public void onStop() {
        super.onStop();
        alertaRef.removeEventListener(valueEventListenerAlertas);
    }

    private void recuperarAlertas() {
        valueEventListenerAlertas = alertaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                alertas.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    Alerta alerta = ds.getValue(Alerta.class);
                    alertas.add(alerta);
                }

                adapterAlertas.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes(){
        textAnotacao = findViewById(R.id.textAnotacoesPac);
        textNome = findViewById(R.id.textNomePac);
        textData = findViewById(R.id.textDataPac);
        textDataE = findViewById(R.id.textDataEntradaPac);
        textComorbidade = findViewById(R.id.textComorbidadesPac);
        textFicha = findViewById(R.id.textFichaPac);
        imagemPaciente = findViewById(R.id.imagemPerfilPac);
        recyclerAlertas = findViewById(R.id.recyclerAlertasPaciente);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ficha_medica, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_deletar:
                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaMedicaActivity.this);

                //Configurar o titulo e mensagem
                dialog.setTitle("Exclusão de usuário");
                dialog.setMessage("Você deseja exluir "+paciente.getNomePaciente()+" da sua lista de pacientes?");

                dialog.setPositiveButton("Sim", (dialog1, which) -> {
                    Paciente p = new Paciente();
                    p.setIdHospital(idUsuarioAtual);
                    p.setIdPaciente(idPaciente);
                    p.removerPaciente();
                    finish();
                    Toast.makeText(FichaMedicaActivity.this, "Paciente excluído", Toast.LENGTH_SHORT).show();
                });

                dialog.setNegativeButton("Não", null);

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                break;
            case R.id.menu_editar_paciente:
                Intent i = new Intent(getApplicationContext(), EditarPacienteActivity.class);
                i.putExtra("id", idPaciente);

                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}