package com.example.stormmasterclient;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.example.stormmasterclient.helpers.WebSocket.IWebSocketMessageListener;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;


/**
 * An abstract class that represents a waiting room.
 */
public class WaitingRoomParticipantActivity extends AbstractWaitingRoom implements IWebSocketMessageListener {

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

        // Get the participants related views
        participants = findViewById(R.id.participantsView);
        participantsAmount = findViewById(R.id.participantsNumberTextView);

        setParticipantsAndAmount(participantsText, participantsAmountValue);

        // Create a WebSocket client
        webSocketClient = new WebSocketClient(roomCode, token);
        webSocketClient.listener = this;

        MaterialButton leaveButton = findViewById(R.id.leaveButton);
        leaveButton.setOnClickListener(v -> {
            apiRoomClient.leaveRoom(roomCode, webSocketClient);
        });
    }


}