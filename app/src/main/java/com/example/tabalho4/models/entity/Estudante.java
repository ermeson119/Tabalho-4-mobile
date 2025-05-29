package com.example.tabalho4.models.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class Estudante implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("nome")
    @Expose
    private String nome;

    @SerializedName("idade")
    @Expose
    private Integer idade;

    @SerializedName("notas")
    @Expose
    private List<Double> notas;

    @SerializedName("presenca")
    @Expose
    private List<Boolean> presenca;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public List<Double> getNotas() {
        return notas;
    }

    public void setNotas(List<Double> notas) {
        this.notas = notas;
    }

    public List<Boolean> getPresenca() {
        return presenca;
    }

    public void setPresenca(List<Boolean> presenca) {
        this.presenca = presenca;
    }

    @Override
    public String toString() {
        return "id=" + id + '\n' +
                "nome=" + nome + '\n' +
                ", idade=" + idade + '\n' +
                ", notas=" + notas + '\n' +
                ", presenca=" + presenca + '\n' +
                '}';
    }
}
