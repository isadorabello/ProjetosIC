package com.example.appgerenciador.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgerenciador.R;
import com.example.appgerenciador.model.Paciente;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterPaciente extends RecyclerView.Adapter<AdapterPaciente.MyViewHolder>{

    private final List<Paciente> pacientes;

    public AdapterPaciente(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_paciente, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterPaciente.MyViewHolder holder, int position) {
        Paciente paciente = pacientes.get(position);
        holder.nome.setText(paciente.getNomePaciente());

        //Carregar imagem
        String urlImagem = paciente.getCaminhoFoto();
        if(urlImagem.isEmpty()){
            Picasso.get().load( R.drawable.perfil ).into( holder.imagem );
        }else{
            Picasso.get().load( urlImagem ).into( holder.imagem );
        }
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagem;
        TextView nome;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomePaciente);
            imagem = itemView.findViewById(R.id.imagemPerfilPaciente);
        }
    }
}
