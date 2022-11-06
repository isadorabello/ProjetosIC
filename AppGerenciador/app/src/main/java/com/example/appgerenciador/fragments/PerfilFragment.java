package com.example.appgerenciador.fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appgerenciador.R;
import com.example.appgerenciador.helper.ConfiguracaoFirebase;
import com.example.appgerenciador.helper.UsuarioFirebase;
import com.example.appgerenciador.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.satyajit.thespotsdialog.SpotsDialog;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    private TextView textNome, textEmail, textEndereco, textTelefone;
    private CircleImageView imagePerfil;
    private AlertDialog dialog;

    private final FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_perfil, container, false);

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        inicializarComponentes(view);
        recuperarDados();

        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if(caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity()).load(url).into(imagePerfil);
        }else{
            imagePerfil.setImageResource(R.drawable.perfil);
        }
        
        return view;
    }

    private void recuperarDados() {
        dialog = new SpotsDialog.Builder()
                .setContext(getActivity()).setMessage("Carregando dados")
                .setCancelable(false).build();

        dialog.show();

        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child("hospital").child(Objects.requireNonNull(autenticacao.getCurrentUser()).getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                assert usuario != null;
                textNome.setText(usuario.getNome());
                textEndereco.setText(usuario.getEndereco());
                textEmail.setText(usuario.getEmail());
                textTelefone.setText(usuario.getTelefone());
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes(View v) {
        textNome = v.findViewById(R.id.textNome);
        textEmail = v.findViewById(R.id.textEmail);
        textEndereco = v.findViewById(R.id.textEndereco);
        textTelefone = v.findViewById(R.id.textTelefone);
        imagePerfil = v.findViewById(R.id.imagePerfil);
    }
}