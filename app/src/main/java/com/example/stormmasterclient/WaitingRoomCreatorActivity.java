package com.example.stormmasterclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.textview.MaterialTextView;

public class WaitingRoomCreatorActivity extends AppCompatActivity {

    private WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room_creator);

        // Get data from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = preferences.getString("token", "");
        String username = preferences.getString("username", "");

        // Get room code from the intent
        String roomCode = getIntent().getStringExtra("roomCode");

        // Show room code in the header
        MaterialTextView appName = findViewById(R.id.appNameTextView);
        String header = appName.getText().toString() + " · " + roomCode;
        appName.setText(header);

        // Set the creator as the first participant by default
        MaterialTextView participants = findViewById(R.id.participantsView);
        participants.setText(username);

        // Set the number of participants to 1 by default
        MaterialTextView participantsAmount = findViewById(R.id.participantsNumberTextView);
        String participantsAmountText = getResources().getString(R.string.amount_of_participants_text) + "1";
        participantsAmount.setText(participantsAmountText);

        WebSocketClient webSocketClient = new WebSocketClient(roomCode, token);
    }
}