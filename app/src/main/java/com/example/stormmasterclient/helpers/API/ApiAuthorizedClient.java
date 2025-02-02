package com.example.stormmasterclient.helpers.API;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
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
 * @see ApiProblemsHandler
 */
public class ApiAuthorizedClient {
    private static final String BASE_URL = APIConfig.BASE_URL;
    private static ApiService apiService;
    private final Context context;
    private final String token;
    private final ApiProblemsHandler problemsHandler;

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
        this.problemsHandler = new ApiProblemsHandler(context);
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
                    problemsHandler.processUserUnauthorized();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                problemsHandler.processConnectionFailed();
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
                problemsHandler.processConnectionFailed();
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
            problemsHandler.processUserUnauthorized();
        } else if (response.code() == 400) {
            JsonObject problem = problemsHandler.processErrorBody(response);

            // Inform the user about the error
            if(problem != null && problem.has("current_password")) {
                Toast.makeText(context, "Неверный текущий пароль", Toast.LENGTH_SHORT)
                        .show();
            } else if (problem != null && problem.has("new_password")) {
                Toast.makeText(context, problem.getAsJsonArray("new_password")
                        .get(0).getAsString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            problemsHandler.processConnectionFailed();
        }
    }

}
