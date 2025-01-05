package com.example.stormmasterclient.helpers.API;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.stormmasterclient.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.0.126:8000/";
    private static ApiService apiService;

    private final Context context;

    public ApiClient(Context context){
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiService.class);
        this.context = context;
    }

    public void userRegistration(String username, String password){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), 
                jsonObject.toString());

        Call<JsonElement> call = apiService.userRegistration(body);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                JsonElement jsonElement = response.body();
                if(response.isSuccessful() && jsonElement != null){
                    processSuccessfulUserCreation(jsonElement);
                } else {
                    ResponseBody errorBody = response.errorBody();
                    processFailedUserCreation(errorBody);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(context, "Возникла ошибка при попытке связаться с сервером. Проверьте подключение к интернету",
                        Toast.LENGTH_SHORT).show();
                Log.d("ApiClient", "onFailure: " + t.getMessage());
            }

        });
    }

    private void processSuccessfulUserCreation(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SharedPreferences sharedPreferences = context.getSharedPreferences("TOKEN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", jsonObject.get("token").getAsString());
        editor.apply();
        Toast.makeText(context, "Вы успешно зарегистрировались", Toast.LENGTH_SHORT)
                .show();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void processFailedUserCreation(ResponseBody errorBody){
        if (errorBody != null) {
            JsonObject problem;
            try{
                problem = new Gson().fromJson(errorBody.string(), JsonObject.class);
            }catch (IOException e){
                e.printStackTrace();
                Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
                return;
            }

            if(problem.has("username")) {
                Toast.makeText(context, problem.get("username").getAsJsonArray().get(0).getAsString(),
                        Toast.LENGTH_SHORT).show();
            } else if (problem.has("password")) {
                Toast.makeText(context, problem.get("password").getAsJsonArray().get(0).getAsString(),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Возникла ошибка при попытке зарегистрироваться",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void userLogin(String username, String password){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());

        Call<JsonElement> call = apiService.userLogin(body);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                JsonElement jsonElement = response.body();
                if(response.isSuccessful() && jsonElement != null){
                    processSuccessfulUserLogin(jsonElement);
                } else {
                    ResponseBody errorBody = response.errorBody();
                    processFailedUserLogin(errorBody);
                }
            }
            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(context, "Возникла ошибка при попытке связаться с сервером. Проверьте подключение к интернету",
                        Toast.LENGTH_SHORT).show();
                Log.d("ApiClient", "onFailure: " + t.getMessage());
            }

        });
    }

    private void processSuccessfulUserLogin(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SharedPreferences sharedPreferences = context.getSharedPreferences("TOKEN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", jsonObject.get("auth_token").getAsString());
        editor.apply();
        Toast.makeText(context, "Вы успешно вошли в свою учётную запись", Toast.LENGTH_SHORT)
                .show();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void processFailedUserLogin(ResponseBody errorBody) {
        if (errorBody != null) {
            JsonObject problem;
            try{
                problem = new Gson().fromJson(errorBody.string(), JsonObject.class);
            }catch (IOException e){
                e.printStackTrace();
                Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
                return;
            }

            if(problem.has("non_field_errors")) {
                Toast.makeText(context, "Неверное имя пользователя или пароль",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Возникла ошибка при попытке войти в учётную запись",
                    Toast.LENGTH_SHORT).show();
        }
    }




}