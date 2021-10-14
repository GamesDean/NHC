package com.menowattge.nhcity;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Preleva Inserisce o aggiorna i dati nel portale tramite chiamate alle API opportune
 */
public interface JsonApi {


    @GET("/api/ConfigurazioniProfili")
    Call<JsonArray> getConfigProfili(@Header("Authorization") String authkey);

    @GET("/api/Profili")
    Call<JsonArray> getProfili(@Header("Authorization") String authkey);


}