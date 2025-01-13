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

/**
 * Client for interacting with the API.
 *
 * @see APIConfig
 * @see ApiService
 */
public class ApiClient {
    private static final String BASE_URL = APIConfig.BASE_URL;
    private static ApiService apiService;
    private final Context context;

    /**
     * Constructor for ApiClient.
     *
     * @param context The context in which the client is used.
     */
    public ApiClient(Context context){
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiService.class);
        this.context = context;
    }

    /**
     * Creates response with given data and tries to register a new user.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @see #processSuccessfulUserCreation(JsonElement, String)
     * @see #processFailedUserCreation(ResponseBody)
     */
    public void userRegistration(String username, String password){
        // Create a JSON object with the user registration data
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        // Create a request body with the JSON object to send to the server
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), 
                jsonObject.toString());

        // Send the request to the server
        Call<JsonElement> call = apiService.userRegistration(body);

        // Process the response
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                JsonElement jsonElement = response.body();

                // Check if the response was successful
                if(response.isSuccessful() && jsonElement != null){
                    processSuccessfulUserCreation(jsonElement, username);
                } else {
                    ResponseBody errorBody = response.errorBody();
                    processFailedUserCreation(errorBody);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(context, "Возникла ошибка при попытке связаться с сервером. Проверьте подключение к интернету",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * Processes a successful user creation response.
     *
     * @param jsonElement The JSON element containing the response data.
     * @param username The username of the new user.
     */
    private void processSuccessfulUserCreation(JsonElement jsonElement, String username) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // Save the token and username in the shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", jsonObject.get("token").getAsString());
        editor.putString("username", username);
        // Here commit is used instead of apply because we need the data to be saved immediately
        editor.commit();

        // Show a toast message to inform the user about the successful registration
        Toast.makeText(context, "Вы успешно зарегистрировались", Toast.LENGTH_SHORT)
                .show();

        // Start the main activity with flags below since the user must not be able to go back
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Processes a failed user creation response.
     *
     * @param errorBody The response body containing the error data.
     */
    private void processFailedUserCreation(ResponseBody errorBody){
        if (errorBody != null) {
            JsonObject problem;

            // Try to parse the error body to get the error data
            try{
                problem = new Gson().fromJson(errorBody.string(), JsonObject.class);
            }catch (IOException e){
                e.printStackTrace();

                // Show a toast message to inform the user about the error
                Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
                return;
            }

            // If parsed successfully, show the error message to the user
            if(problem.has("username")) {
                Toast.makeText(context, problem.get("username").getAsJsonArray().get(0).getAsString(),
                        Toast.LENGTH_SHORT).show();
            } else if (problem.has("password")) {
                Toast.makeText(context, problem.get("password").getAsJsonArray().get(0).getAsString(),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show a toast message to inform the user about the error
            Toast.makeText(context, "Возникла ошибка при попытке зарегистрироваться",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates response with given data and tries to log in a user.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @see #processSuccessfulUserLogin(JsonElement, String)
     * @see #processFailedUserLogin(ResponseBody)
     */
    public void userLogin(String username, String password){
        // Create a JSON object with the user login data
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        // Create a request body with the JSON object to send to the server
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());

        // Send the request to the server
        Call<JsonElement> call = apiService.userLogin(body);

        // Process the response
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                JsonElement jsonElement = response.body();

                // Check if the response was successful
                if(response.isSuccessful() && jsonElement != null){
                    processSuccessfulUserLogin(jsonElement, username);
                } else {
                    ResponseBody errorBody = response.errorBody();
                    processFailedUserLogin(errorBody);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // Show a toast message to inform the user about the error
                Toast.makeText(context, "Возникла ошибка при попытке связаться с сервером. Проверьте подключение к интернету",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * Processes a successful user login response.
     *
     * @param jsonElement The JSON element containing the response data.
     * @param username The username of the user.
     */
    private void processSuccessfulUserLogin(JsonElement jsonElement, String username) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // Save the token and username in the shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", jsonObject.get("auth_token").getAsString());
        editor.putString("username", username);

        // Here commit is used instead of apply because we need the data to be saved immediately
        editor.commit();

        // Show a toast message to inform the user about the successful login
        Toast.makeText(context, "Вы успешно вошли в свою учётную запись", Toast.LENGTH_SHORT)
                .show();

        // Start the main activity with flags below since the user must not be able to go back
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Processes a failed user login response.
     *
     * @param errorBody The response body containing the error data.
     */
    private void processFailedUserLogin(ResponseBody errorBody) {
        if (errorBody != null) {
            JsonObject problem;

            // Try to parse the error body to get the error data
            try{
                problem = new Gson().fromJson(errorBody.string(), JsonObject.class);
            }catch (IOException e){
                e.printStackTrace();

                // Show a toast message to inform the user about the error
                Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
                return;
            }

            // If parsed successfully, show the error message to the user
            if(problem.has("non_field_errors")) {
                Toast.makeText(context, "Неверное имя пользователя или пароль",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show a toast message to inform the user about the error
            Toast.makeText(context, "Возникла ошибка при попытке войти в учётную запись",
                    Toast.LENGTH_SHORT).show();
        }
    }

}