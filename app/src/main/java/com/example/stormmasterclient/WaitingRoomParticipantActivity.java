package com.example.stormmasterclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.textview.MaterialTextView;

public class WaitingRoomParticipantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room_participant);

        // Get data from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        // Get data from the intent
        String roomCode = getIntent().getStringExtra("roomCode");
        String participantsText = getIntent().getStringExtra("participants");
        int participantsAmountValue = getIntent().getIntExtra("participantsAmount", 1);

        // Show room code in the header
        MaterialTextView appName = findViewById(R.id.appNameTextView);
        String header = appName.getText().toString() + " · " + roomCode;
        appName.setText(header);

        // Set the participants text
        MaterialTextView participants = findViewById(R.id.participantsView);
        participants.setText(participantsText);

        // Set the number of participants
        MaterialTextView participantsAmount = findViewById(R.id.participantsNumberTextView);
        String participantsAmountText = getResources().getString(R.string.amount_of_participants_text) +
                " " + participantsAmountValue;
        participantsAmount.setText(participantsAmountText);

        WebSocketClient webSocketClient = new WebSocketClient(roomCode, token);
    }
}