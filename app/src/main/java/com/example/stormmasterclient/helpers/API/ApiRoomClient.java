package com.example.stormmasterclient.helpers.API;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.stormmasterclient.AbstractWaitingRoom;
import com.example.stormmasterclient.ChatActivity;
import com.example.stormmasterclient.WaitingRoomCreatorActivity;
import com.example.stormmasterclient.WaitingRoomParticipantActivity;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.example.stormmasterclient.helpers.dialogs.IRepeatable;
import com.example.stormmasterclient.helpers.dialogs.RepeatActionDialog;
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

    /**
     * Creates a room with the given title.
     * @param title
     */
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
                if (response.isSuccessful() && response.body() != null){
                    processSuccessfulRoomCreation(response);
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
     * Processes a successful creating room request.
     *
     * @param response The response of the successful request.
     */
    private void processSuccessfulRoomCreation(Response<JsonObject> response) {
        Toast.makeText(context, "Комната успешно создана", Toast.LENGTH_SHORT).show();

        // It was checked that the response body is not null
        assert response.body() != null;

        // Start the WaitingRoomCreatorActivity with the room code
        Intent intent = new Intent(context, WaitingRoomCreatorActivity.class);
        intent.putExtra("roomCode", response.body().get("room_code").getAsString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
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
                if (response.isSuccessful() & response.body() != null){
                    processSuccessfulJoiningRoom(response, roomCode);
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
     * Processes a successful joining room request.
     *
     * @param response The response of the successful request.
     * @param roomCode The code of the room that was joined.
     */
    private void processSuccessfulJoiningRoom(Response<JsonObject> response, String roomCode) {
        Toast.makeText(context, "Вы успешно присоединились к комнате", Toast.LENGTH_SHORT).show();

        // It was checked that the response body is not null
        assert response.body() != null;

        // Start the right activity according to the user's role in the room
        Intent intent;
        if(response.body().get("isCreator").getAsBoolean()){
            intent = new Intent(context, WaitingRoomCreatorActivity.class);
            intent.putExtra("justCreated", false);
        } else {
            intent = new Intent(context, WaitingRoomParticipantActivity.class);
        }

        // Put the necessary data in the intent
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("participants", response.body().get("participants").getAsString());
        intent.putExtra("participantsAmount", response.body().get("participants_amount").getAsInt());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
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

    /**
     * Leaves the room with the given room code. Close the WebSocket client of the room.
     *
     * @param roomCode The code of the room to leave.
     * @param webSocketClient The WebSocket client of the room.
     */
    public void leaveRoom(String roomCode, WebSocketClient webSocketClient){
        // Create json object for the request body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("room_code", roomCode);

        // Create request body
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());

        Call<JsonObject> call = apiService.leaveRoom("Token " + token, body);

        // Make the request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() & response.body() != null){
                    processSuccessfulLeavingRoom(response);
                } else {
                    processLeavingRoomFailure(response);
                }
                webSocketClient.closeWebSocket();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                problemsHandler.processConnectionFailed();
                webSocketClient.closeWebSocket();
                problemsHandler.returnToMain();
            }
        });
    }

    /**
     * Processes a successful leaving room request.
     * @param response response of the successful request.
     */
    private void processSuccessfulLeavingRoom(Response<JsonObject> response) {
        Toast.makeText(context, "Вы успешно покинули комнату", Toast.LENGTH_SHORT).show();
        problemsHandler.returnToMain();
    }

    /**
     * Processes a failed leaving room request.
     * @param response response of the failed request.
     */
    private void processLeavingRoomFailure(Response<JsonObject> response) {
        if(response.code() == 401){
            problemsHandler.processUserUnauthorized();
        } else {
            Toast.makeText(context, "Ошибка при покидании комнаты", Toast.LENGTH_SHORT).show();
            problemsHandler.returnToMain();
        }
    }

    /**
     * Deletes the room with the given room code. Closes the WebSocket client of the room.
     *
     * @param roomCode The code of the room to delete.
     * @param webSocketClient The WebSocket client of the room.
     * @param activity The activity in which the repeat action dialog should be displayed. (If
     *                 connection goes wrong).
     */
    public void deleteRoom(String roomCode, WebSocketClient webSocketClient, Activity activity){
        Call<JsonObject> call = apiService.deleteRoom("Token " + token, roomCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() & response.body() != null){
                    webSocketClient.closeWebSocket();
                    problemsHandler.returnToMain();
                } else {
                    processDeletingRoomFailure(response, webSocketClient);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new RepeatActionDialog(() -> deleteRoom(roomCode, webSocketClient, activity),
                        activity, webSocketClient).show();
            }
        });
    }

    /**
     * Processes a failed deleting room request.
     *
     * @param response The response of the failed request.
     * @param webSocketClient The WebSocket client of the room.
     */
    private void processDeletingRoomFailure(Response<JsonObject> response, WebSocketClient webSocketClient) {
        if(response.code() == 401){
            problemsHandler.processUserUnauthorized();
            webSocketClient.closeWebSocket();
            return;
        } else if(response.code() == 404) {
            Toast.makeText(context, "Данная комната уже удалена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ошибка при удалении комнаты", Toast.LENGTH_SHORT).show();
        }
        problemsHandler.returnToMain();
        webSocketClient.closeWebSocket();
    }

    public void startBrainStorm(String roomCode, String details, Activity activity,
                                WebSocketClient webSocketClient){
        // Create json object for the request body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("room_code", roomCode);
        jsonObject.addProperty("details", details);

        // Create request body
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString());

        Call<JsonObject> call = apiService.startBrainstorm("Token " + token, body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() & response.body() != null){
                    AbstractWaitingRoom.startChatActivity(activity, roomCode, true);
                } else {
                    processStartBrainstormFailure(response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                problemsHandler.processConnectionFailed();
                new RepeatActionDialog(() -> startBrainStorm(roomCode, details, activity, webSocketClient),
                        activity, webSocketClient).show();
            }
        });
    }

    private void processStartBrainstormFailure(Response<JsonObject> response) {
        if(response.code() == 401){
            problemsHandler.processUserUnauthorized();
            return;
        } else if (response.code() == 404) {
            Toast.makeText(context, "Данной комнаты не существует", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ошибка при попытке начать мозгового штурма", Toast.LENGTH_SHORT).show();
        }
        problemsHandler.returnToMain();
    }
}
