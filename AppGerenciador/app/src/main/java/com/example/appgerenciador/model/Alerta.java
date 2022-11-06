package com.example.appgerenciador.model;

import com.example.appgerenciador.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Alerta implements Serializable {
    private String idUsuarioAlerta;
    private String idHospitalAlerta;
    private String idAlerta;
    private String nomeAlerta;
    private String estado;
    private String urlImagem;
    private String data;

    public Alerta() {
    }

    public void deletar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ususariosRef = firebaseRef.child("alertas").child("usuarios").child(getIdUsuarioAlerta()).child(getData());
        ususariosRef.removeValue();
    }

    public void deletarParaHospital(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ususariosRef = firebaseRef.child("alertas").child(getIdHospitalAlerta()).child(getIdUsuarioAlerta());
        ususariosRef.removeValue();
    }

    public String getIdUsuarioAlerta() {
        return idUsuarioAlerta;
    }

    public void setIdUsuarioAlerta(String idUsuarioAlerta) {
        this.idUsuarioAlerta = idUsuarioAlerta;
    }

    public String getIdHospitalAlerta() {
        return idHospitalAlerta;
    }

    public void setIdHospitalAlerta(String idHospitalAlerta) {
        this.idHospitalAlerta = idHospitalAlerta;
    }

    public String getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(String idAlerta) {
        this.idAlerta = idAlerta;
    }

    public String getNomeAlerta() {
        return nomeAlerta;
    }

    public void setNomeAlerta(String nomeAlerta) {
        this.nomeAlerta = nomeAlerta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
