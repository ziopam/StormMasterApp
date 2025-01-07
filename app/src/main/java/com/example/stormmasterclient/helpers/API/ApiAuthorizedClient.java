package com.example.stormmasterclient.helpers.API;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.stormmasterclient.MainActivity;
import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiAuthorizedClient {
    private static final String BASE_URL = "http://192.168.0.126:8000/";
    private static ApiService apiService;
    private final Context context;
    private final String token;

    public ApiAuthorizedClient(Context context, String token){
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiService.class);
        this.context = context;
        this.token = token;
    }

    private void processConnectionFailed(){
        Toast.makeText(context, "Ошибка подключения к серверу. Проверьте ваше подключение к интернету",
                Toast.LENGTH_SHORT).show();
    }

    private void processUserUnauthorized(){
        Toast.makeText(context, "Ваша авторизация устарела. Авторизуйтесь повторно",
                Toast.LENGTH_SHORT).show();
        new LoggerOut(context).logOut();
    }

    public void userLogout(){
        apiService.userLogout("Token " + token).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Вы успешно вышли из аккаунта со всех устройств", Toast.LENGTH_SHORT).show();
                    new LoggerOut(context).logOut();
                } else {
                    processUserUnauthorized();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                processConnectionFailed();
            }
        });
    }

    public void userChangePassword(String oldPassword, String newPassword, Activity ChangePasswordActivity){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("current_password", oldPassword);
        jsonObject.addProperty("new_password", newPassword);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());

        apiService.userChangePassword("Token " + token, body).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Пароль успешно изменен", Toast.LENGTH_SHORT).show();
                    ChangePasswordActivity.finish();
                } else {
                    changePasswordFailed(response);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                processConnectionFailed();
            }
        });
    }

    private void changePasswordFailed(@NonNull Response<JsonElement> response) {
        if(response.code() == 401) {
            processUserUnauthorized();
        } else if (response.code() == 400) {
            JsonObject problem;
            try (ResponseBody errorBody = response.errorBody()){
                if(errorBody != null){
                    problem = new Gson().fromJson(errorBody.string(), JsonObject.class);
                } else {
                    Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch (IOException e){
                e.printStackTrace();
                Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
                return;
            }

            if(problem.has("current_password")) {
                Toast.makeText(context, "Неверный текущий пароль", Toast.LENGTH_SHORT)
                        .show();
            } else if (problem.has("new_password")) {
                Toast.makeText(context, problem.getAsJsonArray("new_password")
                        .get(0).getAsString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            processConnectionFailed();
        }
    }

}
