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

        MaterialTextView appName = findViewById(R.id.appNameTextView);
        MaterialTextView participants = findViewById(R.id.participantsView);
        MaterialTextView participantsAmount = findViewById(R.id.participantsNumberTextView);

        // Get room code from the intent
        String roomCode = getIntent().getStringExtra("roomCode");

        // Show room code in the header
        String header = appName.getText().toString() + " · " + roomCode;
        appName.setText(header);

        if(getIntent().getBooleanExtra("justCreated", true)) {
            String username = preferences.getString("username", "");

            // Set the creator as the first participant by default
            participants.setText(username);

            // Set the number of participants to 1 by default
            String participantsAmountText = getResources().getString(R.string.amount_of_participants_text) + " 1";
            participantsAmount.setText(participantsAmountText);
        } else{
            String participantsText = getIntent().getStringExtra("participants");
            int participantsAmountValue = getIntent().getIntExtra("participantsAmount", 1);

            // Set the participants text
            participants.setText(participantsText);

            // Set the number of participants
            String participantsAmountText = getResources().getString(R.string.amount_of_participants_text) +
                    " " + participantsAmountValue;
            participantsAmount.setText(participantsAmountText);

        }

        WebSocketClient webSocketClient = new WebSocketClient(roomCode, token);
    }

    private void updateParticipants(String participants){
        MaterialTextView participantsView = findViewById(R.id.participantsView);
        participantsView.setText(participants);
    }
}