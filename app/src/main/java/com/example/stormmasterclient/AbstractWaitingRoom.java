package com.example.stormmasterclient;

import static com.example.stormmasterclient.RoundRobinActivity.htmlHeader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stormmasterclient.helpers.API.ApiProblemsHandler;
import com.example.stormmasterclient.helpers.RoomDatabase.MessagesRepository;
import com.example.stormmasterclient.helpers.WebSocket.IWebSocketMessageListener;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketSyncHandler;
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
                    case "error": webSocketClient.handleErrors(messageData, this, apiProblemsHandler); break;
                    case "user_joined": addParticipant(messageData.get("username").getAsString()); break;
                    case "user_left": removeParticipant(messageData.get("username").getAsString()); break;
                    case "chat_started":
                        int room_type = messageData.get("room_type").getAsInt();
                        if (room_type == 1) {
                            startChatActivity(this, roomCode, isCreator,
                                messageData.get("details").getAsString(), webSocketClient);
                        } else {
                            RoundRobinActivity.startRoundRobinActivity(this, roomCode, isCreator,
                                messageData.get("details").getAsString(), webSocketClient);
                        }
                        break;
                }
            }
        });
    }

    /**
     * Starts the chat activity.
     *
     * @param context The context from which the chat activity is started.
     * @param roomCode The code of the room.
     * @param isCreator Whether the user is the creator of the room.
     * @param details The details of the room.
     * @param webSocketClient The WebSocket client.
     */
    public static void startChatActivity(Context context, String roomCode, boolean isCreator, String details, WebSocketClient webSocketClient) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("roomDetails", details);
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
                // Get the username from SharedPreferences
                String username = getSharedPreferences("USER_DATA", 0).getString("username", "");
                if(username.equals("")){
                    apiProblemsHandler.processUserUnauthorized();
                    webSocketClient.closeWebSocket();
                }

                String move_to = data.get("move_to").getAsString();
                // Check in which activity to move
                if (move_to.equals("round_robin")) {
                    boolean wasIdeaSent = data.get("was_idea_sent").getAsBoolean();
                    String details = data.get("details").getAsString();

                    if (wasIdeaSent) {
                        RoundRobinActivity.startRoundRobinActivity(this, roomCode, isCreator,
                                details, webSocketClient, wasIdeaSent, null);
                    } else {
                        String new_idea = data.get("users_ideas").getAsJsonObject().get(username).getAsString();

                        // If there is no text in the idea, just move to the RoundRobinActivity
                        if(new_idea == null || new_idea.trim().isEmpty()){
                            RoundRobinActivity.startRoundRobinActivity(this, roomCode, isCreator,
                                    details, webSocketClient, wasIdeaSent, null);
                            return;
                        }

                        // Else, move to the RoundRobinActivity with the new formatted idea
                        String new_text = (htmlHeader + new_idea).replace("\n", "<br>");
                        RoundRobinActivity.startRoundRobinActivity(this, roomCode, isCreator,
                                details, webSocketClient, wasIdeaSent, new_text);
                    }
                } else {
                    String details = new WebSocketSyncHandler().handleMessages(data, username,
                            new MessagesRepository(getApplication()));
                    startChatActivity(this, roomCode, isCreator, details, webSocketClient);
                }
            }
        });
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
