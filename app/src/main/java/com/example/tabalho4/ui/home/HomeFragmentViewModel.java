package com.example.tabalho4.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.repository.EstudanteRepositoryUltils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class HomeFragmentViewModel extends ViewModel {
    private final MutableLiveData<List<Estudante>> estudantesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final EstudanteRepositoryUltils repository = new EstudanteRepositoryUltils();

    public HomeFragmentViewModel() {
        buscarEstudantes();
    }

    public LiveData<List<Estudante>> getEstudantes() {
        return estudantesLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void atualizarLista() {
        buscarEstudantes();
    }

    private void buscarEstudantes() {
        errorLiveData.setValue("Carregando estudantes...");
        Call<List<Estudante>> call = repository.buscarEstudantes();
        call.enqueue(new Callback<List<Estudante>>() {
            @Override
            public void onResponse(Call<List<Estudante>> call, Response<List<Estudante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    estudantesLiveData.setValue(response.body());
                    errorLiveData.setValue(null);
                } else {
                    errorLiveData.setValue("Erro ao buscar estudantes: " + response.code());
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
}