package com.example.tabalho4.ui.estatisticaEstudantes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

public class EstatisticaEstudantesViewModel extends ViewModel {
    private final MutableLiveData<Float> mediaTurmaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Float> mediaIdadeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Estudante> maiorMediaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Estudante> menorMediaLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> aprovadosLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> reprovadosLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final EstudanteRepositoryUltils repository = new EstudanteRepositoryUltils();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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

    public void atualizarEstatisticas(List<Estudante> estudantes) {
        executorService.execute(() -> {
            try {
                if (estudantes == null || estudantes.isEmpty()) {
                    mensagemErro("Lista de estudantes vazia");
                    return;
                }

                List<Estudante> estudantesCompletos = new ArrayList<>();
                CountDownLatch latch = new CountDownLatch(estudantes.size());

                for (Estudante estudante : estudantes) {
                    Call<Estudante> call = repository.buscarEstudantePorId(estudante.getId());
                    call.enqueue(new Callback<Estudante>() {
                        @Override
                        public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    synchronized (estudantesCompletos) {
                                        estudantesCompletos.add(response.body());
                                    }
                                }
                            } finally {
                                latch.countDown();
                            }
                        }

                        @Override
                        public void onFailure(Call<Estudante> call, Throwable t) {
                            latch.countDown();
                        }
                    });
                }

                latch.await();

                if (estudantesCompletos.isEmpty()) {
                    mensagemErro("Nenhum estudante carregado");
                    return;
                }

                calcularEstatisticasTurma(estudantesCompletos);

            } catch (Exception e) {
                mensagemErro("Erro ao processar estatísticas");
            }
        });
    }

    private void calcularEstatisticasTurma(List<Estudante> estudantes) {
        Turma turma = new Turma(estudantes);
        try {
            mediaTurmaLiveData.postValue(turma.calcularMediaTurma());
            mediaIdadeLiveData.postValue(turma.calcularMediaIdade());
            maiorMediaLiveData.postValue(turma.alunoMaiorMedia());
            menorMediaLiveData.postValue(turma.alunoMenorMedia());
            aprovadosLiveData.postValue(turma.getAprovados());
            reprovadosLiveData.postValue(turma.getReprovados());
        } catch (Exception e) {
            mensagemErro("Erro ao calcular estatísticas");
        }
    }

    private void mensagemErro(String mensagem) {
        errorLiveData.postValue(mensagem);
        mediaTurmaLiveData.postValue(0f);
        mediaIdadeLiveData.postValue(0f);
        maiorMediaLiveData.postValue(null);
        menorMediaLiveData.postValue(null);
        aprovadosLiveData.postValue(new ArrayList<>());
        reprovadosLiveData.postValue(new ArrayList<>());
    }

    @Override
    protected void onCleared() {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        super.onCleared();
    }
}