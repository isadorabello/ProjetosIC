package com.example.appgerenciador.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.appgerenciador.R;
import com.example.appgerenciador.activity.FichaMedicaActivity;
import com.example.appgerenciador.adapter.AdapterPaciente;
import com.example.appgerenciador.helper.ConfiguracaoFirebase;
import com.example.appgerenciador.helper.UsuarioFirebase;
import com.example.appgerenciador.listener.RecyclerItemClickListener;
import com.example.appgerenciador.model.Paciente;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment {

    private RecyclerView recyclerPacientes;
    private SearchView searchPaciente;
    private final List<Paciente> pacientes = new ArrayList<>();
    private AdapterPaciente adapterPaciente;

    private ValueEventListener valueEventListenerPacientes;
    private DatabaseReference pacientesRef;
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

    public InicioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        inicializarComponentes(view);

        String idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        pacientesRef = firebaseRef.child("paciente_hospital").child(idUsuarioLogado);

        //configurar o searchView
        searchPaciente.setQueryHint("Buscar paciente");
        searchPaciente.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toLowerCase();
                pesquisarPaciente(textoDigitado);
                return true;
            }
        });

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerPacientes.setLayoutManager(layoutManager);
        recyclerPacientes.setHasFixedSize(true);

        adapterPaciente = new AdapterPaciente(pacientes);
        recyclerPacientes.setAdapter(adapterPaciente);

        recuperarPacientes();

        //adicionar evento de clique no recyclerView
        recyclerPacientes.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerPacientes, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Paciente pacienteSelecionado = pacientes.get(position);
                Intent i = new Intent(getActivity(), FichaMedicaActivity.class);
                i.putExtra("pacienteSelecionado", pacienteSelecionado);

                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarPacientes();
    }

    @Override
    public void onStop() {
        super.onStop();
        pacientesRef.removeEventListener(valueEventListenerPacientes);
    }

    private void recuperarPacientes() {
        valueEventListenerPacientes = pacientesRef.addValueEventListener(new ValueEventListener() {
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

    private void pesquisarPaciente(String textoDigitado) {

        //limpar lista
        pacientes.clear();

        //pesquisar caso tenha texto na pesquisa
        if(textoDigitado.length()>=2){
            Query query = pacientesRef.orderByChild("nomeMinusculo").startAt(textoDigitado).endAt(textoDigitado+"\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    //limpar lista
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

    private void inicializarComponentes(View v){
        recyclerPacientes = v.findViewById(R.id.recyclerPacientes);
        searchPaciente = v.findViewById(R.id.searchPacientes);
    }
}