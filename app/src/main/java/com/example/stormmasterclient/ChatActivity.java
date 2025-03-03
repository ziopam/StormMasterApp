package com.example.stormmasterclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.API.ApiProblemsHandler;
import com.example.stormmasterclient.helpers.API.ApiRoomClient;
import com.example.stormmasterclient.helpers.RecyclerViewAdapters.MessagesAdapter;
import com.example.stormmasterclient.helpers.RoomDatabase.MessageEntity;
import com.example.stormmasterclient.helpers.RoomDatabase.MessagesRepository;
import com.example.stormmasterclient.helpers.TextWatchers.MessageTextWatcher;
import com.example.stormmasterclient.helpers.WebSocket.IWebSocketMessageListener;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketClient;
import com.example.stormmasterclient.helpers.WebSocket.WebSocketSyncHandler;
import com.example.stormmasterclient.helpers.dialogs.DeleteRoomDialog;
import com.example.stormmasterclient.helpers.dialogs.FinishBrainstormDialog;
import com.example.stormmasterclient.helpers.dialogs.ShowIdeasAndThemeDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;


/**
 * An activity that represents a chat.
 */
public class ChatActivity extends AppCompatActivity implements IWebSocketMessageListener {
    private String roomCode;
    private String username;
    private String roomDetails;
    final private MessagesAdapter messagesAdapter = new MessagesAdapter();
    private final MessagesRepository messagesRepository = new MessagesRepository(getApplication());
    public static WebSocketClient webSocketClient;
    private final WebSocketSyncHandler webSocketSyncHandler = new WebSocketSyncHandler();
    private ApiProblemsHandler apiProblemsHandler;
    private ApiRoomClient apiRoomClient;


    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        roomCode = getIntent().getStringExtra("roomCode");
        roomDetails = getIntent().getStringExtra("roomDetails");
        Log.d("ROOM_DETAILS_FROM_INTENT", roomDetails);


        // Set up menu, so it will lock different on different screens
        NavigationView navigationView = findViewById(R.id.navigationView);
        if(navigationView != null) {
            navigationView.inflateMenu(getCorrectMenu());
            navigationView.setNavigationItemSelectedListener(item -> {
                handleMenu(item);
                return true;
            });
        } else{
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }

        // Get username and token
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        String token = sharedPreferences.getString("token", "");

        apiProblemsHandler = new ApiProblemsHandler(this);
        apiRoomClient = new ApiRoomClient(this, token);

        // Set new listener to enable new processing of messages
        webSocketClient.listener = this;

        // If the username is empty, that means the user is not logged in
        if(username.equals("")){
            new ApiProblemsHandler(this).processUserUnauthorized();
            webSocketClient.closeWebSocket();
        }

        // Set up the messages RecyclerView
        RecyclerView messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setAdapter(messagesAdapter);
        messagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        // Connect database and RecyclerView adapter to see actual messages
        messagesRepository.getAllMessages().observe(this, messagesAdapter::submitList);

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
                // To make line breakers readable on server from json
                message = message.replace("\n", "\\n");

                webSocketClient.sendMessage("{\"type\": \"new_message\", \"message\": \"" + message + "\"}");
                messageInput.setText("");
            }
        });
    }

    /**
     * Processes the received message from the WebSocket.
     *
     * @param message The received message.
     */
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
            SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
            username = sharedPreferences.getString("username", "");

            switch (type) {
                case "error": webSocketClient.handleErrors(messageData, this, apiProblemsHandler); break;
                case "new_message": handleNewMessage(messageData); break;
                case "set_idea": handleSettingIdea(messageData); break;
                case "update_votes": handleUpdateVotes(messageData); break;
                case "sync_data": roomDetails =
                        webSocketSyncHandler.handleMessages(messageData, username, messagesRepository); break;
            }
        }
    }

    /**
     * Handles the new message received from the WebSocket.
     *
     * @param messageData The data of the message.
     */
    private void handleNewMessage(JsonObject messageData){
        String message = messageData.get("message").getAsString();
        String username = messageData.get("username").getAsString();
        int id = messageData.get("id").getAsInt();
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        this.username = sharedPreferences.getString("username", "");
        boolean isThisUser = username.equals(this.username);

        // Add the message to the RecyclerView by adding it to the database
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessage(message);
        messageEntity.setId(id);
        messageEntity.setUsername(username);
        messageEntity.setIsThisUser(isThisUser);
        messagesRepository.insert(messageEntity);
    }

    /**
     * Handles the setting idea received from the WebSocket.
     *
     * @param messageData The data of the message.
     */
    private void handleSettingIdea(JsonObject messageData) {
        int messageId = messageData.get("message_id").getAsInt();
        int ideaNumber = messageData.get("idea_number").getAsInt();
        int ideaVotes = messageData.get("idea_votes").getAsInt();

        messagesRepository.updateIdeaFields(messageId, ideaNumber, ideaVotes);
    }

    /**
     * Handles the update votes received from the WebSocket.
     *
     * @param messageData The data of the message.
     */
    private void handleUpdateVotes(JsonObject messageData) {
        int ideaNumber = messageData.get("idea_number").getAsInt();
        int ideaVotes = messageData.get("idea_votes").getAsInt();

        Log.d("UPDATE_VOTES", "Idea number: " + ideaNumber + " Idea votes: " + ideaVotes);

        messagesRepository.updateIdeaVotes(ideaNumber, ideaVotes);
    }

    /**
     * Gets the correct menu for the chat.
     *
     * @return The correct menu resource id.
     */
    private int getCorrectMenu(){
        boolean isCreator = getIntent().getBooleanExtra("isCreator", false);
        return isCreator ? R.menu.chat_creator_menu : R.menu.chat_participant_menu;
    }

    /**
     * Called on the creation of the options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getCorrectMenu(), menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when an options item is selected.
     *
     * @param item The selected menu item.
     * @return true if the item is selected successfully.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        handleMenu(item);
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the menu item selection.
     *
     * @param item The selected menu item.
     */
    private void handleMenu(MenuItem item){
        if(item.getItemId() == R.id.showIdeasMenuItem){
            new ShowIdeasAndThemeDialog(this, roomDetails, messagesRepository).show();
        } else if(item.getItemId() == R.id.leaveMenuItem){
            apiRoomClient.leaveRoom(roomCode, webSocketClient);
            webSocketClient.closeWebSocket();
        } else if (item.getItemId() == R.id.deleteRoomMenuItem){
            new DeleteRoomDialog(roomCode, webSocketClient, apiRoomClient, this).show();
        } else if (item.getItemId() == R.id.finishBrainstormMenuItem){
            new FinishBrainstormDialog(roomCode, webSocketClient, apiRoomClient, this).show();
        }
    }

    /**
     * Called when a context menu item is selected. Uses MessagesAdapter to handle the selection.
     *
     * @param item The selected menu item.
     * @return true if the item is selected successfully.
     * @see MessagesAdapter#onContextItemSelected(MenuItem, Context, WebSocketClient)
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return messagesAdapter.onContextItemSelected(item,this, webSocketClient);
    }
}