package com.example.tabalho4.models.repository;

import com.example.tabalho4.models.entity.Estudante;

import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EstudanteRepositoryUltils {
    private final EstudanteRepository estudanteService;

    public EstudanteRepositoryUltils() {
        // Configuração do Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(obterOkHttpClientInseguro())
                .build();

        estudanteService = retrofit.create(EstudanteRepository.class);
    }

    public Call<List<Estudante>> buscarEstudantes() {
        return estudanteService.buscarEstudantes();
    }

    public Call<Estudante> buscarEstudantePorId(int id) {
        return estudanteService.buscarEstudantePorId(id);
    }

    public Call<Estudante> criarEstudante(Estudante estudante) {
        return estudanteService.criarEstudante(estudante);
    }

    public Call<Estudante> atualizarEstudante(int id, Estudante estudante) {
        return estudanteService.atualizarEstudante(id, estudante);
    }

    public Call<Void> deletarEstudante(int id) {
        return estudanteService.deletarEstudante(id);
    }

    private static OkHttpClient obterOkHttpClientInseguro() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
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
}
