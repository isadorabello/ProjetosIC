package com.example.appparticular.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appparticular.R;
import com.example.appparticular.model.Usuario;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jamilton
 */

public class AdapterUsuario extends RecyclerView.Adapter<AdapterUsuario.MyViewHolder> {

    private final List<Usuario> usuarios;

    public AdapterUsuario(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_usuario, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Usuario usuario = usuarios.get(i);
        holder.nome.setText(usuario.getNome());

        //Carregar imagem
        String urlImagem = usuario.getCaminhoFoto();
        Picasso.get().load( urlImagem ).into( holder.imagem );

    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagem;
        TextView nome;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeUsuario);
            imagem = itemView.findViewById(R.id.imagemPerfilUsuario);
        }
    }
}
