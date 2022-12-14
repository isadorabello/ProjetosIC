package com.example.appgerenciador.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.appgerenciador.R;
import com.example.appgerenciador.adapter.AdapterAlertas;
import com.example.appgerenciador.helper.ConfiguracaoFirebase;
import com.example.appgerenciador.helper.UsuarioFirebase;
import com.example.appgerenciador.listener.RecyclerItemClickListener;
import com.example.appgerenciador.model.Alerta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class AlertasFragment extends Fragment {

    private RecyclerView recyclerAlertas;
    private AdapterAlertas adapterAlertas;
    private final List<Alerta> alertas = new ArrayList<>();

    private ValueEventListener valueEventListenerAlertas;
    private DatabaseReference alertaRef;

    public AlertasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alertas, container, false);

        inicializarComponentes(view);
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        String idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        alertaRef = firebaseRef.child("alertas").child(idUsuarioLogado);

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerAlertas.setLayoutManager(layoutManager);
        recyclerAlertas.setHasFixedSize(true);

        adapterAlertas = new AdapterAlertas(alertas, getActivity());
        recyclerAlertas.setAdapter(adapterAlertas);

        recuperarAlertas();

        //adicionar evento de clique no recyclerView
        recyclerAlertas.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerAlertas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Alerta alertaSelecionado = alertas.get(position);

                AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());

                //Configurar o titulo e mensagem
                dialog.setTitle("Confirmar checagem");
                dialog.setMessage("Voc?? confirma que conferiu o estado do(a) paciente "+
                        alertaSelecionado.getNomeAlerta()+"?");

                dialog.setPositiveButton("Sim", (dialog1, which) -> {
                    alertaSelecionado.deletarParaHospital();
                    adapterAlertas.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Alerta deletado com sucesso!", Toast.LENGTH_SHORT).show();
                });

                dialog.setNegativeButton("N??o", null);

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
        
        return view;
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

    private void inicializarComponentes(View view) {
        recyclerAlertas = view.findViewById(R.id.recyclerAlertas);
    }
}