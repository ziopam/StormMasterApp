package com.example.stormmasterclient;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.example.stormmasterclient.helpers.WebSocket.IWebSocketListener;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * An abstract class that represents a waiting room.
 */
public class WaitingRoomParticipantActivity extends AbstractWaitingRoom implements IWebSocketListener{

    private ApiRoomClient apiRoomClient;
    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room_participant);

        // Get data from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = preferences.getString("token", "");
        apiRoomClient = new ApiRoomClient(this, token);

        // Get data from the intent
        String roomCode = getIntent().getStringExtra("roomCode");
        String participantsText = getIntent().getStringExtra("participants");
        participantsAmountValue = getIntent().getIntExtra("participantsAmount", 1);

        // Show room code in the header
        MaterialTextView appName = findViewById(R.id.appNameTextView);
        String header = appName.getText().toString() + " · " + roomCode;
        appName.setText(header);

        // Set the participants text
        participants = findViewById(R.id.participantsView);
        participants.setText(participantsText);

        // Set the number of participants
        participantsAmount = findViewById(R.id.participantsNumberTextView);
        String participantsAmountText = getResources().getString(R.string.amount_of_participants_text) +
                " " + participantsAmountValue;
        participantsAmount.setText(participantsAmountText);

        // Create a WebSocket client
        webSocketClient = new WebSocketClient(roomCode, token);
        webSocketClient.listener = this;

        MaterialButton leaveButton = findViewById(R.id.leaveButton);
        leaveButton.setOnClickListener(v -> {
            apiRoomClient.leaveRoom(roomCode, webSocketClient);
        });
    }

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

                if(type.equals("user_joined")){
                    addParticipant(messageData.get("username").getAsString());
                } else if(type.equals("user_left")) {
                    removeParticipant(messageData.get("username").getAsString());
                }
            }
        });
    }
}