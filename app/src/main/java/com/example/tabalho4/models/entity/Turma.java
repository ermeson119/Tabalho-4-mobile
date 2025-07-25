package com.example.tabalho4.models.entity;

import java.util.ArrayList;
import java.util.List;

public class Turma {

    private List<Estudante> estudantes;

    public Turma() {
    }

    public Turma(List<Estudante> estudantes) {
        this.estudantes = estudantes != null ? estudantes : new ArrayList<>();
    }

    public float calcularMedia(Estudante estudante) {
        List<Double> notas = estudante.getNotas();
        if (notas == null || notas.isEmpty()) return 0;
        float soma = 0;
        for (double nota : notas) {
            soma += nota;
        }
        return soma / notas.size();
    }

    public float calcularFrequencia(Estudante estudante) {
        List<Boolean> presenca = estudante.getPresenca();
        if (presenca == null || presenca.isEmpty()) return 0;
        int presentes = 0;
        for (Boolean p : presenca) {
            if (p != null && p) presentes++; // Verifica se p não é null
        }
        return (presentes * 100.0f) / presenca.size();
    }

    public float calcularMediaTurma() {
        if (estudantes == null || estudantes.isEmpty()) return 0;
        float soma = 0;
        for (Estudante e : estudantes) {
            soma += calcularMedia(e);
        }
        return soma / estudantes.size();
    }

    public float calcularMediaIdade() {
        if (estudantes == null || estudantes.isEmpty()) return 0;
        float soma = 0;
        for (Estudante e : estudantes) {
            soma += e.getIdade() != null ? e.getIdade() : 0;
        }
        return soma / estudantes.size();
    }

    public Estudante alunoMaiorMedia() {
        if (estudantes == null || estudantes.isEmpty()) return null;
        Estudante maior = estudantes.get(0);
        for (Estudante estudante : estudantes) {
            if (calcularMedia(estudante) > calcularMedia(maior)) {
                maior = estudante;
            }
        }
        return maior;
    }

    public Estudante alunoMenorMedia() {
        if (estudantes == null || estudantes.isEmpty()) return null;
        Estudante menor = estudantes.get(0);
        for (Estudante estudante : estudantes) {
            if (calcularMedia(estudante) < calcularMedia(menor)) {
                menor = estudante;
            }
        }
        return menor;
    }

    public List<Estudante> getAprovados() {
        List<Estudante> aprovados = new ArrayList<>();
        if (estudantes == null) return aprovados;
        for (Estudante e : estudantes) {
            if (calcularMedia(e) >= 7.0 && calcularFrequencia(e) >= 75.0) {
                aprovados.add(e);
            }
        }
        return aprovados;
    }

    public List<Estudante> getReprovados() {
        List<Estudante> reprovados = new ArrayList<>();
        if (estudantes == null) return reprovados;
        for (Estudante e : estudantes) {
            if (calcularMedia(e) < 7.0 || calcularFrequencia(e) < 75.0) {
                reprovados.add(e);
            }
        }
        return reprovados;
    }
}