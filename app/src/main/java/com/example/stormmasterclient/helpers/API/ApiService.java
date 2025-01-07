package com.example.stormmasterclient.helpers.API;

import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/users/")
    Call<JsonElement> userRegistration(@Body RequestBody body);

    @POST("api/auth/token/login/")
    Call<JsonElement> userLogin(@Body RequestBody body);

    @POST("api/auth/token/logout/")
    Call<JsonElement> userLogout(@Header("Authorization") String token);

    @POST("api/auth/users/set_password/")
    Call<JsonElement> userChangePassword(@Header("Authorization") String token, @Body RequestBody body);
}
