package com.example.appgerenciador.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgerenciador.R;
import com.example.appgerenciador.model.Alerta;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterAlertas extends RecyclerView.Adapter<AdapterAlertas.MyViewHolder>{

    private final List<Alerta> alertas;

    public AdapterAlertas(List<Alerta> pacientes, FragmentActivity activity) {
        this.alertas = pacientes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alertas, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterAlertas.MyViewHolder holder, int position) {
        Alerta alerta = alertas.get(position);
        holder.nome.setText(alerta.getNomeAlerta());
        holder.estado.setText(alerta.getEstado());

        //Carregar imagem
        String urlImagem = alerta.getUrlImagem();
        Picasso.get().load( urlImagem ).into( holder.imagem );
    }

    @Override
    public int getItemCount() {
        return alertas.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagem;
        TextView nome;
        TextView estado;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeAlerta);
            estado = itemView.findViewById(R.id.textEstadoAlerta);
            imagem = itemView.findViewById(R.id.imagemPerfilAlerta);
        }
    }
}
