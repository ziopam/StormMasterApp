package com.example.stormmasterclient.helpers.API;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
 * Client for interacting with the API requests connected to .
 *
 * @see APIConfig
 * @see ApiService
 * @see ApiProblemsHandler
 */
public class ApiRoomClient {
    private static final String BASE_URL = APIConfig.BASE_URL;
    private static ApiService apiService;
    private final Context context;
    private final String token;
    private final ApiProblemsHandler problemsHandler;

    /**
     * Constructor for ApiRoomClient.
     *
     * @param context The context in which the client is used.
     * @param token The authorization token of the user.
     */
    public ApiRoomClient(Context context, String token){
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiService.class);
        this.context = context.getApplicationContext();
        this.token = token;
        this.problemsHandler = new ApiProblemsHandler(context);
    }

    public void createRoom(String title){
        // Create json object for the request body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);

        // Create request body
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());

        Call<JsonObject> call = apiService.createRoom("Token " + token, body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    Toast.makeText(context, "Комната успешно создана", Toast.LENGTH_SHORT).show();
                } else {
                    processCreatingRoomFailure(response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                problemsHandler.processConnectionFailed();
            }
        });

    }

    /**
     * Processes a failed creating room request.
     *
     * @param response The response of the failed request.
     */
    private void processCreatingRoomFailure(Response<JsonObject> response) {
        if (response.code() == 400){
            JsonObject problem = problemsHandler.processErrorBody(response);

            // If parsing the error body was successful, show the error message
            if (problem != null && problem.has("detail")) {
                Toast.makeText(context, problem.get("detail").getAsString(), Toast.LENGTH_SHORT).show();
            }
        } else if (response.code() == 401){
            problemsHandler.processUserUnauthorized();
        } else {
            problemsHandler.processConnectionFailed();
        }
    }


    /**
     * Joins a room with the given room code.
     *
     * @param roomCode The code of the room to join.
     */
    public void joinRoom(String roomCode){
        // Create json object for the request body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("room_code", roomCode);

        // Create request body
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());

        Call<JsonObject> call = apiService.joinRoom("Token " + token, body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    Toast.makeText(context, "Вы успешно присоединились к комнате", Toast.LENGTH_SHORT).show();
                } else {
                    processJoiningRoomFailure(response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                problemsHandler.processConnectionFailed();
            }
        });
    }

    /**
     * Processes a failed joining room request.
     *
     * @param response The response of the failed request.
     */
    private void processJoiningRoomFailure(Response<JsonObject> response) {
        if(response.code() == 404){
            Toast.makeText(context, "Комната с таким кодом не найдена", Toast.LENGTH_SHORT).show();
        } else if(response.code() == 401){
            problemsHandler.processUserUnauthorized();
        } else {
            problemsHandler.processConnectionFailed();
        }
    }
}
