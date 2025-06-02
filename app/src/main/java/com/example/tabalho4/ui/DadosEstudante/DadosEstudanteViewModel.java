package com.example.tabalho4.ui.DadosEstudante;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.entity.Turma;
import com.example.tabalho4.models.repository.EstudanteRepositoryUltils;
import com.example.tabalho4.ui.home.HomeFragmentViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DadosEstudanteViewModel extends ViewModel {
    private final MutableLiveData<Estudante> estudanteLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> mediaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> frequenciaLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> situacaoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cadastroSucessoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> operacaoTipoLiveData = new MutableLiveData<>();
    private final EstudanteRepositoryUltils estudanteRepositoryUltils = new EstudanteRepositoryUltils();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private HomeFragmentViewModel homeViewModel;

    // Método para configurar o HomeFragmentViewModel
    public void setViewModelStoreOwner(ViewModelStoreOwner owner) {
        homeViewModel = new ViewModelProvider(owner).get(HomeFragmentViewModel.class);
    }

    public LiveData<Estudante> getEstudante() {
        return estudanteLiveData;
    }

    public LiveData<Double> getMedia() {
        return mediaLiveData;
    }

    public LiveData<Double> getFrequencia() {
        return frequenciaLiveData;
    }

    public LiveData<String> getSituacao() {
        return situacaoLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getCadastroSucesso() {
        return cadastroSucessoLiveData;
    }

    public LiveData<String> getOperacaoTipo() {
        return operacaoTipoLiveData;
    }

    public void carregarEstudante(int id) {
        executorService.execute(() -> {
            Call<Estudante> call = estudanteRepositoryUltils.buscarEstudantePorId(id);
            call.enqueue(new Callback<Estudante>() {
                @Override
                public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Estudante estudante = response.body();
                        estudanteLiveData.postValue(estudante);
                        Turma turma = new Turma(new ArrayList<>());
                        double media = turma.calcularMedia(estudante);
                        double frequencia = turma.calcularFrequencia(estudante);
                        String situacao = (media >= 7.0 && frequencia >= 75.0) ? "Aprovado" : "Reprovado";
                        mediaLiveData.postValue(media);
                        frequenciaLiveData.postValue(frequencia);
                        situacaoLiveData.postValue(situacao);
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao buscar estudante");
                    limparDados();
                }
            });
        });
    }

    public void adicionarNota(int id, double nota) {
        executorService.execute(() -> {
            Call<Estudante> call = estudanteRepositoryUltils.buscarEstudantePorId(id);
            call.enqueue(new Callback<Estudante>() {
                @Override
                public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Estudante estudante = response.body();
                        List<Double> notas = estudante.getNotas() != null ? new ArrayList<>(estudante.getNotas()) : new ArrayList<>();
                        notas.add(nota);
                        estudante.setNotas(notas);
                        atualizarEstudante(estudante, "adicionar_nota");
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao buscar estudante: " + t.getMessage());
                    cadastroSucessoLiveData.postValue(false);
                    operacaoTipoLiveData.postValue("adicionar_nota");
                }
            });
        });
    }

    public void adicionarFrequencia(int id, boolean presente) {
        executorService.execute(() -> {
            Call<Estudante> call = estudanteRepositoryUltils.buscarEstudantePorId(id);
            call.enqueue(new Callback<Estudante>() {
                @Override
                public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Estudante estudante = response.body();
                        List<Boolean> presencas = estudante.getPresenca() != null ? new ArrayList<>(estudante.getPresenca()) : new ArrayList<>();
                        presencas.add(presente);
                        estudante.setPresenca(presencas);
                        atualizarEstudante(estudante, "adicionar_frequencia");
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao buscar estudante: " + t.getMessage());
                    cadastroSucessoLiveData.postValue(false);
                    operacaoTipoLiveData.postValue("adicionar_frequencia");
                }
            });
        });
    }

    public void deletarEstudante(int id) {
        executorService.execute(() -> {
            Call<Void> call = estudanteRepositoryUltils.deletarEstudante(id);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (homeViewModel != null) {
                            homeViewModel.atualizarLista();
                        }
                        cadastroSucessoLiveData.postValue(true);
                        operacaoTipoLiveData.postValue("deletar");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    errorLiveData.postValue("Erro ao deletar: " + t.getMessage());
                    cadastroSucessoLiveData.postValue(false);
                    operacaoTipoLiveData.postValue("deletar");
                }
            });
        });
    }

    private void atualizarEstudante(Estudante estudante, String tipoOperacao) {
        executorService.execute(() -> {
            Call<Estudante> call = estudanteRepositoryUltils.atualizarEstudante(estudante.getId(), estudante);
            call.enqueue(new Callback<Estudante>() {
                @Override
                public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Estudante atualizado = response.body();
                        if (atualizado.getNotas() == null) atualizado.setNotas(new ArrayList<>());
                        if (atualizado.getPresenca() == null) atualizado.setPresenca(new ArrayList<>());
                        if (homeViewModel != null) {
                            homeViewModel.atualizarLista();
                        }
                        estudanteLiveData.postValue(atualizado);
                        Turma turma = new Turma(new ArrayList<>());
                        double media = turma.calcularMedia(atualizado);
                        double frequencia = turma.calcularFrequencia(atualizado);
                        String situacao = (media >= 7.0 && frequencia >= 75.0) ? "Aprovado" : "Reprovado";

                        mediaLiveData.postValue(media);
                        frequenciaLiveData.postValue(frequencia);
                        situacaoLiveData.postValue(situacao);
                        cadastroSucessoLiveData.postValue(true);
                        operacaoTipoLiveData.postValue(tipoOperacao);
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao atualizar estudante: " + t.getMessage());
                    cadastroSucessoLiveData.postValue(false);
                    operacaoTipoLiveData.postValue(tipoOperacao);
                }
            });
        });
    }

    private void limparDados() {
        estudanteLiveData.postValue(null);
        mediaLiveData.postValue(0.0);
        frequenciaLiveData.postValue(0.0);
        situacaoLiveData.postValue("N/A");
    }

    @Override
    protected void onCleared() {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        super.onCleared();
    }
}