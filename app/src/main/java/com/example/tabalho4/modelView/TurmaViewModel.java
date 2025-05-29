package com.example.tabalho4.modelView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.entity.Turma;

import java.util.ArrayList;
import java.util.List;

public class TurmaViewModel extends ViewModel {
    private final MutableLiveData<Double> mediaTurmaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> mediaIdadeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> maiorMediaLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> menorMediaLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> aprovadosLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> reprovadosLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final EstudanteViewModel estudanteViewModel;
    private final Turma turma = new Turma(new ArrayList<>());

    public TurmaViewModel() {
        this.estudanteViewModel = new EstudanteViewModel();
        calculateTurmaStatistics();
    }

    public LiveData<Double> getMediaTurma() {
        return mediaTurmaLiveData;
    }

    public LiveData<Double> getMediaIdade() {
        return mediaIdadeLiveData;
    }

    public LiveData<String> getMaiorMedia() {
        return maiorMediaLiveData;
    }

    public LiveData<String> getMenorMedia() {
        return menorMediaLiveData;
    }

    public LiveData<List<Estudante>> getAprovados() {
        return aprovadosLiveData;
    }

    public LiveData<List<Estudante>> getReprovados() {
        return reprovadosLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    private void calculateTurmaStatistics() {
        estudanteViewModel.getEstudantes().observeForever(estudantes -> {
            if (estudantes != null && !estudantes.isEmpty()) {
                try {
                    double totalMedia = 0;
                    double totalIdade = 0;
                    double maiorMedia = Double.MIN_VALUE;
                    double menorMedia = Double.MAX_VALUE;
                    Estudante estudanteMaiorMedia = null;
                    Estudante estudanteMenorMedia = null;
                    List<Estudante> aprovados = new ArrayList<>();
                    List<Estudante> reprovados = new ArrayList<>();

                    for (Estudante e : estudantes) {
                        double media = turma.calcularMedia(e);
                        double frequencia = turma.calcularFrequencia(e);
                        totalMedia += media;
                        totalIdade += e.getIdade();

                        if (media > maiorMedia) {
                            maiorMedia = media;
                            estudanteMaiorMedia = e;
                        }
                        if (media < menorMedia) {
                            menorMedia = media;
                            estudanteMenorMedia = e;
                        }

                        if (media >= 7.0 && frequencia >= 75.0) {
                            aprovados.add(e);
                        } else {
                            reprovados.add(e);
                        }
                    }

                    double avgMedia = totalMedia / estudantes.size();
                    double avgIdade = totalIdade / estudantes.size();

                    mediaTurmaLiveData.postValue(avgMedia);
                    mediaIdadeLiveData.postValue(avgIdade);
                    maiorMediaLiveData.postValue(String.format("%s (%.2f)", estudanteMaiorMedia != null ? estudanteMaiorMedia.getNome() : "N/A", maiorMedia));
                    menorMediaLiveData.postValue(String.format("%s (%.2f)", estudanteMenorMedia != null ? estudanteMenorMedia.getNome() : "N/A", menorMedia));
                    aprovadosLiveData.postValue(aprovados);
                    reprovadosLiveData.postValue(reprovados);
                } catch (Exception e) {
                    errorLiveData.postValue("Erro ao calcular estatísticas: " + e.getMessage());
                }
            } else {
                mediaTurmaLiveData.postValue(0.0);
                mediaIdadeLiveData.postValue(0.0);
                maiorMediaLiveData.postValue("N/A");
                menorMediaLiveData.postValue("N/A");
                aprovadosLiveData.postValue(new ArrayList<>());
                reprovadosLiveData.postValue(new ArrayList<>());
            }
        });
    }

}