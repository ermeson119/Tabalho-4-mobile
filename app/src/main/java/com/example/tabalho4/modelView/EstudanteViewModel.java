package com.example.tabalho4.modelView;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.entity.Turma;
import com.example.tabalho4.models.repository.EstudanteRepositoryUltils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstudanteViewModel extends ViewModel {

    private final MutableLiveData<List<Estudante>> estudantesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Estudante> selectedEstudanteLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> mediaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> frequenciaLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> situacaoLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cadastroSucessoLiveData = new MutableLiveData<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final EstudanteRepositoryUltils estudanteRepositoryUltils;

    public EstudanteViewModel() {
        estudanteRepositoryUltils = new EstudanteRepositoryUltils();
        startBusca();
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<List<Estudante>> getEstudantes() {
        return estudantesLiveData;
    }

    public LiveData<Estudante> getSelectedEstudante() {
        return selectedEstudanteLiveData;
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

    public LiveData<Boolean> getCadastroSucesso() {
        return cadastroSucessoLiveData;
    }

    private void startBusca() {
        errorLiveData.setValue("Iniciando download…");
        Call<List<Estudante>> call = estudanteRepositoryUltils.buscarEstudantes();
        call.enqueue(new Callback<List<Estudante>>() {
            @Override
            public void onResponse(Call<List<Estudante>> call, Response<List<Estudante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    estudantesLiveData.setValue(response.body());
                    errorLiveData.setValue(null);
                } else {
                    errorLiveData.setValue("Erro ao buscar alunos: " + response.code());
                    estudantesLiveData.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Estudante>> call, Throwable t) {
                errorLiveData.setValue("Falha na conexão: " + t.getMessage());
                estudantesLiveData.setValue(new ArrayList<>());
            }
        });
    }

    public void atualizarLista() {
        startBusca();
    }

    public void estudanteSelecionadoId(int id) {
        executorService.execute(() -> {
            Call<Estudante> call = estudanteRepositoryUltils.buscarEstudantePorId(id);
            call.enqueue(new Callback<Estudante>() {
                @Override
                public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Estudante estudante = response.body();
                        selectedEstudanteLiveData.postValue(estudante);
                        Turma turma = new Turma(new ArrayList<>());
                        double media = turma.calcularMedia(estudante);
                        double frequencia = turma.calcularFrequencia(estudante);
                        String situacao = (media >= 7.0 && frequencia >= 75.0) ? "Aprovado" : "Reprovado";

                        mediaLiveData.postValue(media);
                        frequenciaLiveData.postValue(frequencia);
                        situacaoLiveData.postValue(situacao);
                    } else {
                        errorLiveData.postValue("Estudante não encontrado: " + response.code());
                        clearSelectedData();
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao buscar estudante: " + t.getMessage());
                    clearSelectedData();
                }
            });
        });
    }

    public void atualizarEstudante(Estudante estudante) {
        executorService.execute(() -> {
            Call<Estudante> call = estudanteRepositoryUltils.atualizarEstudante(estudante.getId(), estudante);
            call.enqueue(new Callback<Estudante>() {
                @Override
                public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Estudante atualizado = response.body();
                        // Inicializar listas se null
                        if (atualizado.getNotas() == null) atualizado.setNotas(new ArrayList<>());
                        if (atualizado.getPresenca() == null) atualizado.setPresenca(new ArrayList<>());
                        atualizarLista(); // Atualiza a lista geral
                        selectedEstudanteLiveData.postValue(atualizado); // Atualiza o estudante selecionado
                        Turma turma = new Turma(new ArrayList<>());
                        double media = turma.calcularMedia(atualizado);
                        double frequencia = turma.calcularFrequencia(atualizado);
                        String situacao = (media >= 7.0 && frequencia >= 75.0) ? "Aprovado" : "Reprovado";

                        mediaLiveData.postValue(media);
                        frequenciaLiveData.postValue(frequencia);
                        situacaoLiveData.postValue(situacao);
                        cadastroSucessoLiveData.postValue(true);
                    } else {
                        errorLiveData.postValue("Erro ao atualizar estudante: " + response.code() + ", Mensagem: " + response.message());
                        cadastroSucessoLiveData.postValue(false);
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao atualizar estudante: " + t.getMessage());
                    cadastroSucessoLiveData.postValue(false);
                }
            });
        });
    }

    public void criarEstudante(Estudante estudante) {
        executorService.execute(() -> {
            Call<Estudante> call = estudanteRepositoryUltils.criarEstudante(estudante);
            call.enqueue(new Callback<Estudante>() {
                @Override
                public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                    if (response.isSuccessful()) {
                        cadastroSucessoLiveData.postValue(true);
                        atualizarLista();
                    } else {
                        errorLiveData.postValue("Erro ao cadastrar: " + response.code());
                        cadastroSucessoLiveData.postValue(false);
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao cadastrar: " + t.getMessage());
                    cadastroSucessoLiveData.postValue(false);
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
                        atualizarLista();
                    } else {
                        errorLiveData.postValue("Erro ao deletar estudante: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    errorLiveData.postValue("Erro ao deletar: " + t.getMessage());
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
                        // Inicializar listas se null
                        List<Boolean> presencas = estudante.getPresenca() != null ? new ArrayList<>(estudante.getPresenca()) : new ArrayList<>();
                        presencas.add(presente);
                        estudante.setPresenca(presencas);
                        atualizarEstudante(estudante);
                    } else {
                        errorLiveData.postValue("Estudante não encontrado: " + response.code());
                        cadastroSucessoLiveData.postValue(false);
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao buscar estudante: " + t.getMessage());
                    cadastroSucessoLiveData.postValue(false);
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
                        // Inicializar listas se null
                        List<Double> notas = estudante.getNotas() != null ? new ArrayList<>(estudante.getNotas()) : new ArrayList<>();
                        notas.add(nota);
                        estudante.setNotas(notas);
                        atualizarEstudante(estudante);
                    } else {
                        errorLiveData.postValue("Estudante não encontrado: " + response.code());
                        cadastroSucessoLiveData.postValue(false);
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao buscar estudante: " + t.getMessage());
                    cadastroSucessoLiveData.postValue(false);
                }
            });
        });
    }
    private void clearSelectedData() {
        selectedEstudanteLiveData.postValue(null);
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