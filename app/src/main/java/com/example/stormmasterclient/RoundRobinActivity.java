package com.example.stormmasterclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.stormmasterclient.helpers.API.ApiProblemsHandler;
import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.example.stormmasterclient.helpers.RecyclerViewAdapters.MessagesAdapter;
import com.example.stormmasterclient.helpers.RoomDatabase.MessagesRepository;
import com.example.stormmasterclient.helpers.WebSocket.IWebSocketMessageListener;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketSyncHandler;
import com.example.stormmasterclient.helpers.dialogs.DeleteRoomDialog;
import com.example.stormmasterclient.helpers.dialogs.FinishBrainstormDialog;
import com.example.stormmasterclient.helpers.dialogs.ShowIdeasAndThemeDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/** An activity that represents a round robin room. **/
public class RoundRobinActivity extends AppCompatActivity implements IWebSocketMessageListener {

    private String roomCode;
    private boolean isCreator;
    private String roomDetails;
    private String htmlHeader;

    public static WebSocketClient webSocketClient;
    private final WebSocketSyncHandler webSocketSyncHandler = new WebSocketSyncHandler();
    private ApiProblemsHandler apiProblemsHandler;

    private MaterialCardView detailsCardView;
    private MaterialButton sendButton;
    private MaterialTextView waitingTextView;
    private EditText themeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_robin);

        roomCode = getIntent().getStringExtra("roomCode");
        roomDetails = getIntent().getStringExtra("roomDetails");
        isCreator = getIntent().getBooleanExtra("isCreator", false);

        MaterialTextView detailsTextView = findViewById(R.id.detailsTextView);
        detailsCardView = findViewById(R.id.detailsCard);
        themeEditText = findViewById(R.id.themeEditText);
        sendButton = findViewById(R.id.sendButton);
        waitingTextView = findViewById(R.id.waitingTextView);

        // Set the room details
        htmlHeader = "<div style='text-align: center;'><h3>Тема мозгового штурма<h3/></div>" +
                roomDetails.replace("\n", "<br>");
        detailsTextView.setText(Html.fromHtml(htmlHeader, Html.FROM_HTML_MODE_COMPACT));

        // Make the activity listen for WebSocket messages
        webSocketClient.listener = this;

        apiProblemsHandler = new ApiProblemsHandler(this);

        // Set up the send button
        sendButton.setOnClickListener(v -> {
            String theme = themeEditText.getText().toString();
            if (!theme.isEmpty()) {
                String new_text = detailsTextView.getText().toString().replace(Html.fromHtml(htmlHeader,
                                Html.FROM_HTML_MODE_COMPACT), "") + "\n" + themeEditText.getText().toString();
                new_text = new_text.replace("\n", "\\n");
                webSocketClient.sendMessage("{\"type\": \"round_robin_update\", \"new_text\": \"" + new_text + "\"}");
            }
        });
    }


    /**
     * Starts the RoundRobinActivity.
     *
     * @param context The context of the activity.
     * @param roomCode The room code.
     * @param isCreator A boolean indicating if the user is the creator of the room.
     * @param details The details of the room.
     * @param webSocketClient The WebSocket client.
     */
    public static void startRoundRobinActivity(Context context, String roomCode, boolean isCreator, String details, WebSocketClient webSocketClient) {
        Intent intent = new Intent(context, RoundRobinActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("roomDetails", details);
        intent.putExtra("isCreator", isCreator);
        RoundRobinActivity.webSocketClient = webSocketClient;
        context.startActivity(intent);
    }

    @Override
    public void onMessageReceived(String message) {
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
                case "error": webSocketClient.handleErrors(messageData, this, apiProblemsHandler); break;
                case "round_robin_updated": handleRoundRobinUpdated(); break;

            }
        }
    }

    /**
     * Handles the updated round robin. Changes the UI for waiting for the next turn.
     */
    private void handleRoundRobinUpdated() {
        runOnUiThread(() -> {
            themeEditText.setText("");
            sendButton.setEnabled(false);
            waitingTextView.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.GONE);
            detailsCardView.setVisibility(View.GONE);
        });
    }
}