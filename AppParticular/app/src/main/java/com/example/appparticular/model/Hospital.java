package com.example.appparticular.model;

import com.example.appparticular.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Hospital {
    private String idHospital;
    private String nomeHospital;
    private String caminhoFoto;
    private String dataNascimento;
    private String comorbidades;
    private String idPaciente;
    private String nomePaciente;
    private String nomeMinusculo;
    private String importantes;

    public Hospital() {
    }

    public  void pedidoPaciente(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedido_vinculo").child(getIdHospital()).child(getIdPaciente());
        pedidoRef.setValue(this);
    }

    public String getIdHospital() {
        return idHospital;
    }

    public void setIdHospital(String idHospital) {
        this.idHospital = idHospital;
    }

    public String getNomeHospital() {
        return nomeHospital;
    }

    public void setNomeHospital(String nomeHospital) {
        this.nomeHospital = nomeHospital;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getComorbidades() {
        return comorbidades;
    }

    public void setComorbidades(String comorbidades) {
        this.comorbidades = comorbidades;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public String getNomeMinusculo() {
        return nomeMinusculo;
    }

    public void setNomeMinusculo(String nomeMinusculo) {
        this.nomeMinusculo = nomeMinusculo.toLowerCase();
    }

    public String getImportantes() {
        return importantes;
    }

    public void setImportantes(String importantes) {
        this.importantes = importantes;
    }
}
