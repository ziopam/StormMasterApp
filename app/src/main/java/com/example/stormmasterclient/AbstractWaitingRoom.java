package com.example.stormmasterclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stormmasterclient.helpers.API.ApiProblemsHandler;
import com.example.stormmasterclient.helpers.WebSocket.IWebSocketMessageListener;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract class that represents a waiting room.
 */
public abstract class AbstractWaitingRoom extends AppCompatActivity implements IWebSocketMessageListener {
    protected WebSocketClient webSocketClient;
    protected int participantsAmountValue = 1;
    protected MaterialTextView participants;
    protected MaterialTextView participantsAmount;
    protected ApiProblemsHandler apiProblemsHandler;
    protected String roomCode;
    protected boolean isCreator;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                          down then this Bundle contains the data it most recently supplied in
     *                          onSaveInstanceState.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiProblemsHandler = new ApiProblemsHandler(this);
    }

    /**
     * Processes the message received from the WebSocket.
     *
     * @param message The message received from the WebSocket.
     */
    @Override
    public void onMessageReceived(String message) {
        runOnUiThread(() -> {
            JsonObject messageData;
            try {
                messageData = new Gson().fromJson(message, JsonObject.class);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if(messageData.get("type") != null){
                String type = messageData.get("type").getAsString();

                switch (type) {
                    case "sync_data": syncData(messageData); break;
                    case "error": handleErrors(messageData); break;
                    case "user_joined": addParticipant(messageData.get("username").getAsString()); break;
                    case "user_left": removeParticipant(messageData.get("username").getAsString()); break;
                    case "chat_started": startChatActivity(this, roomCode, isCreator, webSocketClient); break;
                }
            }
        });
    }

    /**
     * Starts the chat activity.
     */
    public static void startChatActivity(Context context, String roomCode, boolean isCreator, WebSocketClient webSocketClient) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("isCreator", isCreator);
        ChatActivity.webSocketClient = webSocketClient;
        context.startActivity(intent);
    }

    /**
     * Processes the data received from the WebSocket.
     *
     * @param data The data received from the WebSocket.
     */
    protected void syncData(JsonObject data){
        runOnUiThread(() -> {
            if(!data.get("isChatStarted").getAsBoolean()){
                setParticipantsAndAmount(data.get("participants").getAsString(),
                        data.get("participants_amount").getAsInt());
            } else {
                startChatActivity(this, roomCode, isCreator, webSocketClient);
            }
        });
    }

    /**
     * Handles the errors received from the WebSocket.
     *
     * @param messageData The data of the message.
     */
    protected void handleErrors(JsonObject messageData){
        int errorCode = messageData.get("error_code").getAsInt();
        switch (errorCode){
            case 1006:
            case 4000:
                Toast.makeText(this, "Проверьте подключение к интернету",
                        Toast.LENGTH_SHORT).show();
                break;
            case 4001:
                apiProblemsHandler.processUserUnauthorized();
                webSocketClient.closeWebSocket();
                break;
            case 4003:
                Toast.makeText(this, "Вы больше не являетесь участником этого мозгового штурма",
                        Toast.LENGTH_SHORT).show();
                webSocketClient.closeWebSocket();
                apiProblemsHandler.returnToMain();
                break;
            case 4004:
                Toast.makeText(this, "Этой комнаты больше не существует",
                        Toast.LENGTH_SHORT).show();
                webSocketClient.closeWebSocket();
                apiProblemsHandler.returnToMain();
                break;
            default: webSocketClient.reconnect();
        }
    }


    /**
     * Sets the participants and the amount of participants in the waiting room.
     *
     * @param newParticipantsText The text of the participants.
     * @param newAmount The amount of participants.
     */
    protected void setParticipantsAndAmount(String newParticipantsText, int newAmount){
        participants.setText(newParticipantsText);
        participantsAmountValue = newAmount;
        String newParticipantsAmount = getResources().getString(R.string.amount_of_participants_text)
                + " " + participantsAmountValue;
        participantsAmount.setText(newParticipantsAmount);
    }

    /**
     * Adds a participant to the list of participants.
     *
     * @param username The username of the participant to add.
     */
    protected void addParticipant(String username){
        String currentParticipants = participants.getText().toString();
        String newParticipants = currentParticipants + ", " + username;
        participants.setText(newParticipants);
        participantsAmountValue++;
        String newParticipantsAmount = getResources().getString(R.string.amount_of_participants_text)
                + " " + participantsAmountValue;
        participantsAmount.setText(newParticipantsAmount);
    }

    /**
     * Removes a participant from the list of participants.
     *
     * @param username The username of the participant to remove.
     */
    protected void removeParticipant(String username){
        String currentParticipants = participants.getText().toString();

        // Remove the participant from the list
        List<String> splitted = new ArrayList<>(Arrays.asList(currentParticipants.split(", ")));
        splitted.remove(username);
        String newParticipants = String.join(", ", splitted);

        // Update the participants list
        participants.setText(newParticipants);
        participantsAmountValue = splitted.size();
        String newParticipantsAmount = getResources().getString(R.string.amount_of_participants_text)
                + " " + participantsAmountValue;
        participantsAmount.setText(newParticipantsAmount);
    }
}
