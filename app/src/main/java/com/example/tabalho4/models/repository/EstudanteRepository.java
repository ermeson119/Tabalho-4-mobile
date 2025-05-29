package com.example.tabalho4.models.repository;

import com.example.tabalho4.models.entity.Estudante;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EstudanteRepository {
    @GET("estudantes")
    Call<List<Estudante>> buscarEstudantes();

    @GET("estudantes/{id}")
    Call<Estudante> buscarEstudantePorId(@Path("id") int id);

    @POST("estudantes")
    Call<Estudante> criarEstudante(@Body Estudante estudante);

    @PUT("estudantes/{id}")
    Call<Estudante> atualizarEstudante(@Path("id") int id, @Body Estudante estudante);

    @DELETE("estudantes/{id}")
    Call<Void> deletarEstudante(@Path("id") int id);

}
