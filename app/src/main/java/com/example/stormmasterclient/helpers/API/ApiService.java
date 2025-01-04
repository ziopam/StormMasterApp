package com.example.stormmasterclient.helpers.API;

import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/users/")
    Call<JsonElement> userRegistration(@Body RequestBody body);
}
