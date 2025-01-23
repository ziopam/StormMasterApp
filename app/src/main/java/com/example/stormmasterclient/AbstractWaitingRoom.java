package com.example.stormmasterclient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract class that represents a waiting room.
 */
public abstract class AbstractWaitingRoom extends AppCompatActivity {
    protected WebSocketClient webSocketClient;
    protected int participantsAmountValue = 1;
    protected MaterialTextView participants;
    protected MaterialTextView participantsAmount;

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
