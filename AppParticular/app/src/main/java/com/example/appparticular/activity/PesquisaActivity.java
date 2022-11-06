package com.example.appparticular.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.example.appparticular.R;
import com.example.appparticular.adapter.AdapterUsuario;
import com.example.appparticular.helper.ConfiguracaoFirebase;
import com.example.appparticular.helper.UsuarioFirebase;
import com.example.appparticular.listener.RecyclerItemClickListener;
import com.example.appparticular.model.Hospital;
import com.example.appparticular.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.satyajit.thespotsdialog.SpotsDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PesquisaActivity extends AppCompatActivity {

    private RecyclerView recyclerVinculo;
    private SearchView searchVinculo;
    private final List<Usuario> usuarios = new ArrayList<>();
    private AdapterUsuario adapterUsuario;
    private android.app.AlertDialog dialog;
    private String idUsuarioLogado;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ValueEventListener valueEventListeneVinculo;
    private DatabaseReference usuariosRef;
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    private Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pesquisar Vínculo Hospitalar");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = firebaseRef.child("usuarios").child("hospital");

        //configurar o searchView
        searchVinculo.setQueryHint("Buscar Hospital/Asilo");
        searchVinculo.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toLowerCase();
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerVinculo.setLayoutManager(layoutManager);
        recyclerVinculo.setHasFixedSize(true);

        adapterUsuario = new AdapterUsuario(usuarios);
        recyclerVinculo.setAdapter(adapterUsuario);

        recuperarDados();
        recuperarDadosUsuario();

        //adicionar evento de clique no recyclerView
        recyclerVinculo.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(), recyclerVinculo, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSeleconado = usuarios.get(position);

                AlertDialog.Builder dialog = new AlertDialog.Builder(PesquisaActivity.this);

                //Configurar o titulo e mensagem
                dialog.setTitle("Vínculo Hospitalar");
                dialog.setMessage("Você permite que "+usuarioSeleconado.getNome()+" tenha acesso aos seus dados?");

                dialog.setPositiveButton("Sim", (dialog1, which) -> {
                    //Fazer o negócio do banco de dados
                    Hospital hospital = new Hospital();
                    if(usuario!=null){
                        hospital.setIdHospital(usuarioSeleconado.getId());
                        hospital.setNomeHospital(usuarioSeleconado.getNome());
                        hospital.setNomePaciente(usuario.getNome());
                        hospital.setNomeMinusculo(usuario.getNomeMinusculo());
                        hospital.setIdPaciente(usuario.getId());
                        hospital.setCaminhoFoto(usuario.getCaminhoFoto());
                        hospital.setComorbidades(usuario.getComorbidades());
                        hospital.setImportantes(usuario.getImportantes());
                        hospital.setDataNascimento(usuario.getDataNascimento());
                        hospital.pedidoPaciente();
                        Toast.makeText(PesquisaActivity.this, "Seus dados foram repassados com sucesso!", Toast.LENGTH_LONG).show();
                    }
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
        }));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            recuperarDados();
            //criar outro metodo com addListenerForSingleValueEvent
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void pesquisarUsuarios(String texto){

        //limpar lista
        usuarios.clear();

        //pesquisar caso tenha texto na pesquisa
        if(texto.length()>=2){
            Query query = usuariosRef.orderByChild("nomeMinusculo").startAt(texto).endAt(texto+"\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    //limpar lista
                    usuarios.clear();

                    for(DataSnapshot ds: snapshot.getChildren()){
                        Usuario usuario = ds.getValue(Usuario.class);
                        usuarios.add(usuario);
                    }

                    adapterUsuario.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

    private void inicializarComponentes(){
        recyclerVinculo = findViewById(R.id.recyclerVinculos);
        searchVinculo = findViewById(R.id.searchVinculos);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarDados();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListeneVinculo);
    }

    private void recuperarDados(){
        valueEventListeneVinculo = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usuarios.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    Usuario usuario = ds.getValue(Usuario.class);
                    usuarios.add(usuario);
                }

                adapterUsuario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void recuperarDadosUsuario() {
        dialog = new SpotsDialog.Builder()
                .setContext(this).setMessage("Carregando dados")
                .setCancelable(false).build();

        dialog.show();

        DatabaseReference pacienteRef = firebaseRef.child("usuarios").child("pacientes").child(idUsuarioLogado);
        pacienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    usuario = snapshot.getValue(Usuario.class);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}