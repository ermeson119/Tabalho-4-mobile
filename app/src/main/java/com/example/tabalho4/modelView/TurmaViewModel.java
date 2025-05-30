package com.example.tabalho4.modelView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.entity.Turma;
import com.example.tabalho4.models.repository.EstudanteRepositoryUltils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TurmaViewModel extends ViewModel {
    private final MutableLiveData<List<Estudante>> estudantesCompletosLiveData = new MutableLiveData<>();
    private final MutableLiveData<Float> mediaTurmaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Float> mediaIdadeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Estudante> maiorMediaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Estudante> menorMediaLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> aprovadosLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> reprovadosLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final EstudanteViewModel estudanteViewModel;
    private final EstudanteRepositoryUltils repositoryEstudante = new EstudanteRepositoryUltils();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Observer<List<Estudante>> estudantesObserver = this::buscaDadosCompletosEstudantes;

    public TurmaViewModel() {
        this.estudanteViewModel = new EstudanteViewModel();
        estudanteViewModel.getEstudantes().observeForever(estudantesObserver);
    }

    public LiveData<List<Estudante>> getEstudantesCompletos() {
        return estudantesCompletosLiveData;
    }

    public LiveData<Float> getMediaTurma() {
        return mediaTurmaLiveData;
    }

    public LiveData<Float> getMediaIdade() {
        return mediaIdadeLiveData;
    }

    public LiveData<Estudante> getMaiorMedia() {
        return maiorMediaLiveData;
    }

    public LiveData<Estudante> getMenorMedia() {
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

    private void buscaDadosCompletosEstudantes(List<Estudante> estudantes) {
        executorService.execute(() -> {
            try {
                if (estudantes != null && !estudantes.isEmpty()) {
                    List<Estudante> estudantesCompletos = new ArrayList<>();
                    CountDownLatch contageRegressiva = new CountDownLatch(estudantes.size());

                    for (Estudante estudante : estudantes) {
                        Call<Estudante> call = repositoryEstudante.buscarEstudantePorId(estudante.getId());
                        call.enqueue(new Callback<Estudante>() {
                            @Override
                            public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                                try {
                                    if (response.isSuccessful() && response.body() != null) {
                                        synchronized (estudantesCompletos) {
                                            estudantesCompletos.add(response.body());
                                        }
                                    } else {
                                        errorLiveData.postValue("Erro ao buscar estudante " + estudante.getId() + ": " + response.code());
                                    }
                                } finally {
                                    contageRegressiva.countDown();
                                }
                            }

                            @Override
                            public void onFailure(Call<Estudante> call, Throwable t) {
                                errorLiveData.postValue("Erro ao buscar estudante " + estudante.getId() + ": " + t.getMessage());
                                contageRegressiva.countDown();
                            }
                        });
                    }

                    try {
                        contageRegressiva.await(); // Aguarda todas as chamadas serem concluídas
                    } catch (InterruptedException e) {
                        errorLiveData.postValue("Erro ao esperar respostas: " + e.getMessage());
                        return;
                    }

                    if (estudantesCompletos.isEmpty()) {
                        errorLiveData.postValue("Erro: Nenhum estudante carregado");
                        mediaTurmaLiveData.postValue(0f);
                        mediaIdadeLiveData.postValue(0f);
                        maiorMediaLiveData.postValue(null);
                        menorMediaLiveData.postValue(null);
                        aprovadosLiveData.postValue(new ArrayList<>());
                        reprovadosLiveData.postValue(new ArrayList<>());
                    } else {
                        estudantesCompletosLiveData.postValue(estudantesCompletos);
                        calcularEstatisticasTurma(estudantesCompletos);
                    }
                } else {
                    errorLiveData.postValue("Erro: Lista de estudantes vazia ou nula");
                    mediaTurmaLiveData.postValue(0f);
                    mediaIdadeLiveData.postValue(0f);
                    maiorMediaLiveData.postValue(null);
                    menorMediaLiveData.postValue(null);
                    aprovadosLiveData.postValue(new ArrayList<>());
                    reprovadosLiveData.postValue(new ArrayList<>());
                }
            } catch (Exception e) {
                errorLiveData.postValue("Erro ao buscar dados completos: " + e.getMessage());
            }
        });
    }

    private void calcularEstatisticasTurma(List<Estudante> estudantesCompletos) {
        Turma turma = new Turma(estudantesCompletos);
        try {
            float mediaTurma = turma.calcularMediaTurma();
            mediaTurmaLiveData.postValue(mediaTurma);

            float mediaIdade = turma.calcularMediaIdade();
            mediaIdadeLiveData.postValue(mediaIdade);

            Estudante maiorMedia = turma.alunoMaiorMedia();
            maiorMediaLiveData.postValue(maiorMedia);

            Estudante menorMedia = turma.alunoMenorMedia();
            menorMediaLiveData.postValue(menorMedia);

            List<Estudante> aprovados = turma.getAprovados();
            aprovadosLiveData.postValue(aprovados);

            List<Estudante> reprovados = turma.getReprovados();
            reprovadosLiveData.postValue(reprovados);
        } catch (Exception e) {
            errorLiveData.postValue("Erro ao calcular estatísticas: " + e.getMessage());
        }
    }

    @Override
    protected void onCleared() {
        estudanteViewModel.getEstudantes().removeObserver(estudantesObserver);
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        super.onCleared();
    }
}