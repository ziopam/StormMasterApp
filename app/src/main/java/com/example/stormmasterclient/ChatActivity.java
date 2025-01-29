package com.example.stormmasterclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.API.ApiProblemsHandler;
import com.example.stormmasterclient.helpers.RecyclerViewAdapters.MessageEntity;
import com.example.stormmasterclient.helpers.RecyclerViewAdapters.MessagesAdapter;
import com.example.stormmasterclient.helpers.TextWatchers.MessageTextWatcher;
import com.example.stormmasterclient.helpers.WebSocket.IWebSocketMessageListener;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * An activity that represents a chat.
 */
public class ChatActivity extends AppCompatActivity implements IWebSocketMessageListener {
    private String roomCode;
    private boolean isCreator;
    private String username;
    private MessagesAdapter messagesAdapter = new MessagesAdapter();
    public static WebSocketClient webSocketClient;
    private ApiProblemsHandler apiProblemsHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        apiProblemsHandler = new ApiProblemsHandler(this);

        // Set new listener
        webSocketClient.listener = this;

        // Get room code from the intent
        roomCode = getIntent().getStringExtra("roomCode");
        isCreator = getIntent().getBooleanExtra("isCreator", false);

        // Get username
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        // If the username is empty, that means the user is not logged in
        if(username.equals("")){
            new ApiProblemsHandler(this).processUserUnauthorized();
            webSocketClient.closeWebSocket();
        }

        // Set up the messages RecyclerView
        RecyclerView messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setAdapter(messagesAdapter);
        messagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        // Show room code in the header
        MaterialTextView appName = findViewById(R.id.appNameTextView);
        String header = appName.getText().toString() + " · " + roomCode;
        appName.setText(header);

        // Get the message input views
        MaterialCardView messageInputCardView = findViewById(R.id.messageInputCardView);
        EditText messageInput = findViewById(R.id.messageInputEditText);
        FloatingActionButton sendButton = findViewById(R.id.sendMessageButton);

        // To avoid sending empty messages
        sendButton.setEnabled(false);

        // Set up the message input text watcher
        messageInput.addTextChangedListener(new MessageTextWatcher(sendButton, messageInputCardView));

        // Set up the send button
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString();
            if(!message.trim().isEmpty()){
                webSocketClient.sendMessage("{\"type\": \"new_message\", \"message\": \"" + message + "\"}");
                messageInput.setText("");
            }
        });
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
                case "error": handleErrors(messageData); break;
                case "new_message": handleNewMessage(messageData); break;
            }
        }
    }

    /**
     * Handles the errors received from the WebSocket.
     *
     * @param messageData The data of the message.
     */
    protected void handleErrors(JsonObject messageData){
        int errorCode = messageData.get("error_code").getAsInt();
        switch (errorCode){
            case 1006:
            case 4000:
                Toast.makeText(this, "Проверьте подключение к интернету",
                        Toast.LENGTH_SHORT).show();
                break;
            case 4001:
                apiProblemsHandler.processUserUnauthorized();
                webSocketClient.closeWebSocket();
                break;
            case 4003:
                Toast.makeText(this, "Вы больше не являетесь участником этого мозгового штурма",
                        Toast.LENGTH_SHORT).show();
                webSocketClient.closeWebSocket();
                apiProblemsHandler.returnToMain();
                break;
            case 4004:
                Toast.makeText(this, "Этой комнаты больше не существует",
                        Toast.LENGTH_SHORT).show();
                webSocketClient.closeWebSocket();
                apiProblemsHandler.returnToMain();
                break;
        }
    }

    private void handleNewMessage(JsonObject messageData){
        String message = messageData.get("message").getAsString();
        String username = messageData.get("username").getAsString();
        boolean isThisUser = username.equals(this.username);

        // Add the message to the RecyclerView
        runOnUiThread(() -> {
            messagesAdapter.messagesList.add(new MessageEntity(message, username, isThisUser, false));
            messagesAdapter.notifyItemInserted(messagesAdapter.messagesList.size() - 1);
        });
    }



}