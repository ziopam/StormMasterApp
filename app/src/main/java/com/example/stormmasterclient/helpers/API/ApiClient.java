package com.example.stormmasterclient.helpers.API;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.stormmasterclient.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
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
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());


        Call<JsonElement> call = apiService.userRegistration(body);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Регистрация успешна", Toast.LENGTH_SHORT).show();
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
}