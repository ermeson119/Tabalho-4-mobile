package com.example.tabalho4.ui.cadastrarEstudante;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.repository.EstudanteRepositoryUltils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CadastrarEstudanteViewModel extends ViewModel {
    private final MutableLiveData<Boolean> cadastroSucessoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final EstudanteRepositoryUltils repository = new EstudanteRepositoryUltils();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LiveData<Boolean> getCadastroSucesso() {
        return cadastroSucessoLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void criarEstudante(String nome, int idade) {
        if (nome == null || nome.trim().isEmpty()) {
            errorLiveData.postValue("Nome é obrigatório");
            return;
        }
        if (idade < 0) {
            errorLiveData.postValue("Idade deve ser um número válido");
            return;
        }

        Estudante estudante = new Estudante();
        estudante.setNome(nome);
        estudante.setIdade(idade);
        estudante.setNotas(new ArrayList<>());
        estudante.setPresenca(new ArrayList<>());

        executorService.execute(() -> {
            Call<Estudante> call = repository.criarEstudante(estudante);
            call.enqueue(new Callback<Estudante>() {
                @Override
                public void onResponse(Call<Estudante> call, Response<Estudante> response) {
                    if (response.isSuccessful()) {
                        cadastroSucessoLiveData.postValue(true);
                    }
                }

                @Override
                public void onFailure(Call<Estudante> call, Throwable t) {
                    errorLiveData.postValue("Erro ao cadastrar estudante");
                    cadastroSucessoLiveData.postValue(false);
                }
            });
        });
    }

    @Override
    protected void onCleared() {
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        super.onCleared();
    }
}