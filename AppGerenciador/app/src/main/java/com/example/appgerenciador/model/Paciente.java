package com.example.appgerenciador.model;

import com.example.appgerenciador.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Paciente implements Serializable {
    private String idPaciente;
    private String idHospital;
    private String nomePaciente;
    private String nomeMinusculo;
    private String caminhoFoto;
    private String dataNascimento;
    private String comorbidades;
    private String importantes;
    private String anotacoes;
    private String ficha;
    private String dataEntrada;

    public Paciente() {
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("paciente_hospital").child(getIdHospital()).child(getIdPaciente());

        Map<String, Object> valoresPaciente = converterParaMap();
        usuarioRef.updateChildren(valoresPaciente);
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> pacienteMap = new HashMap<>();
        pacienteMap.put("idPaciente", getIdPaciente());
        pacienteMap.put("idHospital", getIdHospital());
        pacienteMap.put("nomePaciente", getNomePaciente());
        pacienteMap.put("nomeMinusculo", getNomeMinusculo());
        pacienteMap.put("dataNascimento", getDataNascimento());
        pacienteMap.put("caminhoFoto", getCaminhoFoto());
        pacienteMap.put("comorbidades", getComorbidades());
        pacienteMap.put("dataEntrada", getDataEntrada());
        pacienteMap.put("ficha", getFicha());
        pacienteMap.put("anotacoes", getAnotacoes());
        pacienteMap.put("importantes", getImportantes());

        return pacienteMap;
    }

    public void atualizarFoto(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("paciente_hospital").child(getIdHospital()).child(getIdPaciente());

        Map<String, Object> valoresPaciente = converterParaMapFoto();
        usuarioRef.updateChildren(valoresPaciente);
    }

    public Map<String, Object> converterParaMapFoto(){
        HashMap<String, Object> pacienteMap = new HashMap<>();
        pacienteMap.put("idPaciente", getIdPaciente());
        pacienteMap.put("idHospital", getIdHospital());
        pacienteMap.put("caminhoFoto", getCaminhoFoto());

        return pacienteMap;
    }

    public  void salvarPaciente(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("paciente_hospital").child(getIdHospital()).child(getIdPaciente());
        pedidoRef.setValue(this);
    }

    public  void removerSolicitacao(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedido_vinculo").child(getIdHospital()).child(getIdPaciente());
        pedidoRef.removeValue();
    }

    public  void removerPaciente(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("paciente_hospital").child(getIdHospital()).child(getIdPaciente());
        pedidoRef.removeValue();
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getIdHospital() {
        return idHospital;
    }

    public void setIdHospital(String idHospital) {
        this.idHospital = idHospital;
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

    public String getImportantes() {
        return importantes;
    }

    public void setImportantes(String importantes) {
        this.importantes = importantes;
    }

    public String getFicha() {
        return ficha;
    }

    public void setFicha(String ficha) {
        this.ficha = ficha;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }
}
