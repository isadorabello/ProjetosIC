package com.example.appparticular.helper;

import android.net.Uri;
import android.util.Log;

import com.example.appparticular.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class UsuarioFirebase {
    public static String getIdentificadorUsuario(){
        FirebaseAuth autenticacao =  ConfiguracaoFirebase.getFirebaseAutenticacao();
        return Objects.requireNonNull(autenticacao.getCurrentUser()).getUid();
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static void atulizarNomeUsuario(String nome){
        try{
            //usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName( nome )
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(task -> {
                if(!task.isSuccessful()){
                    Log.d("Perfil", "Erro ao atualizar nome do perfil");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser firebaseUser = getUsuarioAtual();
        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setId(firebaseUser.getUid());

        if(firebaseUser.getPhotoUrl()==null){
            usuario.setCaminhoFoto("");
        }else{
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());
        }

        return usuario;

    }

    public static void atulizarFotoUsuario(Uri url){
        try{
            //usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(url)
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(task -> {
                if(!task.isSuccessful()){
                    Log.d("Perfil", "Erro ao atualizar a foto do perfil");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
