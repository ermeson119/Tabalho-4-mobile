package com.example.tabalho4.modelView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.entity.Turma;
import com.example.tabalho4.models.repository.EstudanteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EstudanteViewModel extends ViewModel {
    private final MutableLiveData<List<Estudante>> estudantesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Estudante> selectedEstudanteLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> mediaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> frequenciaLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> situacaoLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cadastroSucessoLiveData = new MutableLiveData<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private EstudanteRepository repository;

    public EstudanteViewModel() {
        // Inicializa o Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:8080/estudantes/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(obterOkHttpClientInseguro())
                .build();
        repository = retrofit.create(EstudanteRepository.class);
        startBusca();
    }

    public LiveData<List<Estudante>> getEstudantes() {
        return estudantesLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
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
        errorLiveData.setValue("Download começando...");
        Call<List<Estudante>> call = repository.buscarEstudantes();
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
            Call<Estudante> call = repository.buscarEstudantePorId(id);
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

    public void criarEstudante(Estudante estudante) {
        executorService.execute(() -> {
            Call<Estudante> call = repository.criarEstudante(estudante);
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
            Call<Void> call = repository.deletarEstudante(id);
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

    private void clearSelectedData() {
        selectedEstudanteLiveData.postValue(null);
        mediaLiveData.postValue(0.0);
        frequenciaLiveData.postValue(0.0);
        situacaoLiveData.postValue("N/A");
    }

    private static OkHttpClient obterOkHttpClientInseguro() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        @Override public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        @Override public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                    }
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCleared() {
        executorService.shutdownNow();
        super.onCleared();
    }
}