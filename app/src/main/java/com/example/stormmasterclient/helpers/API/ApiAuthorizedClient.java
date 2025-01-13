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

/**
 * Client for interacting with the API with authorization.
 *
 * @see APIConfig
 * @see ApiService
 */
public class ApiAuthorizedClient {
    private static final String BASE_URL = APIConfig.BASE_URL;
    private static ApiService apiService;
    private final Context context;
    private final String token;

    /**
     * Constructor for ApiAuthorizedClient.
     *
     * @param context The context in which the client is used.
     * @param token The authorization token of the user.
     */
    public ApiAuthorizedClient(Context context, String token){
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiService.class);
        this.context = context;
        this.token = token;
    }

    /**
     * Processes a failed connection to the server.
     */
    private void processConnectionFailed(){
        Toast.makeText(context, "Ошибка подключения к серверу. Проверьте ваше подключение к интернету",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Processes an unauthorized user.
     */
    private void processUserUnauthorized(){
        Toast.makeText(context, "Ваша авторизация устарела. Авторизуйтесь повторно",
                Toast.LENGTH_SHORT).show();
        new LoggerOut(context).logOut();
    }

    /**
     * Creates response and tries to log out the user from devices. If it's successful, logs out the
     * user locally as well.
     *
     * @see LoggerOut
     */
    public void userLogout(){
        apiService.userLogout("Token " + token).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    // Log out the user locally as well
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

    /**
     * Creates response and tries to changes the password of the current user. If successful, closes
     * the given activity.
     *
     * @param oldPassword The old password of the user.
     * @param newPassword The new password of the user.
     * @param ChangePasswordActivity The activity in which the password change is performed.
     */
    public void userChangePassword(String oldPassword, String newPassword, Activity ChangePasswordActivity){
        // Create a JSON object with the old and new passwords
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("current_password", oldPassword);
        jsonObject.addProperty("new_password", newPassword);

        // Create a request body with the JSON object
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());

        // Send the request to the server and process the response
        apiService.userChangePassword("Token " + token, body).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    // Inform the user about the successful password change and close the activity
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

    /**
     * Processes a failed password change.
     *
     * @param response The response from the server.
     */
    private void changePasswordFailed(@NonNull Response<JsonElement> response) {
        if(response.code() == 401) {
            processUserUnauthorized();
        } else if (response.code() == 400) {
            JsonObject problem;

            // Get the error body and try to parse it
            try (ResponseBody errorBody = response.errorBody()){
                if(errorBody != null){
                    problem = new Gson().fromJson(errorBody.string(), JsonObject.class);
                } else {
                    // Inform the user about the error
                    Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch (IOException e){
                e.printStackTrace();

                // Inform the user about the error
                Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
                return;
            }

            // Inform the user about the error
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
