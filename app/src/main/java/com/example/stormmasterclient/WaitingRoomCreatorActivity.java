package com.example.stormmasterclient;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.stormmasterclient.helpers.WebSocket.IWebSocketMessageListener;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.textview.MaterialTextView;


/**
 * An activity that represents a waiting room for the creator of the room.
 *
 * @see AbstractWaitingRoom
 */
public class WaitingRoomCreatorActivity extends AbstractWaitingRoom implements IWebSocketMessageListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room_creator);

        // Get data from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        MaterialTextView appName = findViewById(R.id.appNameTextView);
        participants = findViewById(R.id.participantsView);
        participantsAmount = findViewById(R.id.participantsNumberTextView);

        // Get room code from the intent
        String roomCode = getIntent().getStringExtra("roomCode");

        // Show room code in the header
        String header = appName.getText().toString() + " · " + roomCode;
        appName.setText(header);

        // Check if activity was started after creation of the room or after joining it
        if(getIntent().getBooleanExtra("justCreated", true)) {
            String username = preferences.getString("username", "");

            // Set the creator as the first participant by default
            participants.setText(username);
        } else{
            // Set the participants text
            String participantsText = getIntent().getStringExtra("participants");
            participants.setText(participantsText);

            // Get the number of participants
            participantsAmountValue = getIntent().getIntExtra("participantsAmount", 1);
        }

        // Set the number of participants (default=1)
        String participantsAmountText = getResources().getString(R.string.amount_of_participants_text)
                + " " + participantsAmountValue;
        participantsAmount.setText(participantsAmountText);

        webSocketClient = new WebSocketClient(roomCode, token);
        webSocketClient.listener = this;
    }
}