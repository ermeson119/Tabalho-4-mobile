package com.example.tabalho4.models.entity;

import java.util.ArrayList;
import java.util.List;

public class Turma {

    private List<Estudante> estudantes;

    public Turma(List<Estudante> estudantes) {
        this.estudantes = estudantes;
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
        for (boolean p : presenca) {
            if (p) presentes++;
        }
        return (presentes * 100.0f) / presenca.size();
    }

    public float calcularMediaTurma() {
        float soma = 0;
        for (Estudante e : estudantes) {
            soma += calcularMedia(e);
        }
        return soma / estudantes.size();
    }

    public float calcularMediaIdade() {
        float soma = 0;
        for (Estudante e : estudantes) {
            soma += e.getIdade();
        }
        return soma / estudantes.size();
    }

    public Estudante alunoMaiorMedia() {
        Estudante maior = estudantes.get(0);
        for (Estudante estudante : estudantes) {
            if (calcularMedia(estudante) > calcularMedia(maior)) {
                maior = estudante;
            }
        }
        return maior;
    }

    public Estudante alunoMenorMedia() {
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
        for (Estudante e : estudantes) {
            if (calcularMedia(e) >= 7.0 && calcularFrequencia(e) >= 75.0) {
                aprovados.add(e);
            }
        }
        return aprovados;
    }

    public List<Estudante> getReprovados() {
        List<Estudante> reprovados = new ArrayList<>();
        for (Estudante e : estudantes) {
            if (calcularMedia(e) < 7.0 || calcularFrequencia(e) < 75.0) {
                reprovados.add(e);
            }
        }
        return reprovados;
    }
}
