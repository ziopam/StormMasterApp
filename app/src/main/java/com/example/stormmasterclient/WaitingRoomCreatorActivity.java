package com.example.stormmasterclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.example.stormmasterclient.helpers.WebSocket.IWebSocketMessageListener;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.example.stormmasterclient.helpers.dialogs.DeleteRoomDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;


/**
 * An activity that represents a waiting room for the creator of the room.
 *
 * @see AbstractWaitingRoom
 */
public class WaitingRoomCreatorActivity extends AbstractWaitingRoom implements IWebSocketMessageListener {
    private ApiRoomClient apiRoomClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room_creator);

        // Set flag for the creator of the room (used for moving to chat activity)
        isCreator = true;

        // Get data from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        MaterialTextView appName = findViewById(R.id.appNameTextView);
        participants = findViewById(R.id.participantsView);
        participantsAmount = findViewById(R.id.participantsNumberTextView);

        // Get room code from the intent
        roomCode = getIntent().getStringExtra("roomCode");

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

        // Set up the WebSocket and API clients
        webSocketClient = new WebSocketClient(roomCode, token);
        webSocketClient.listener = this;
        apiRoomClient = new ApiRoomClient(this, token);

        // Set up the delete button
        MaterialButton deleteButton = findViewById(R.id.deleteRoomButton);
        deleteButton.setOnClickListener(v -> {
            new DeleteRoomDialog(roomCode, webSocketClient, apiRoomClient, this).show();
        });

        EditText themeEditText = findViewById(R.id.themeEditText);

        // Set up the start button
        MaterialButton startButton = findViewById(R.id.startBrainstormButton);
        startButton.setOnClickListener(v -> {
            apiRoomClient.startBrainStorm(roomCode, themeEditText.getText().toString(),this,
                    webSocketClient);
        });

    }
}