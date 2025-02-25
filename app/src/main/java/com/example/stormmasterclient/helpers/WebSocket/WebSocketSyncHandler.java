package com.example.stormmasterclient.helpers.WebSocket;

import android.app.Application;

import com.example.stormmasterclient.helpers.RoomDatabase.MessageEntity;
import com.example.stormmasterclient.helpers.RoomDatabase.MessagesRepository;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * A class that handles the sync data received from the server.
 */
public class WebSocketSyncHandler {

    /**
     * Handles the sync data received from the server.
     *
     * @param syncData The sync data received from the server.
     * @param username The username of the user.
     * @param messagesRepository The repository for the messages database.
     * @return The details of the room that the user has joined (its theme).
     */
    public String handleMessages(JsonObject syncData, String username, MessagesRepository messagesRepository){
        ArrayList<MessageEntity> messages = new ArrayList<>();
        syncData.get("messages").getAsJsonArray().forEach(message -> {
            JsonObject json = message.getAsJsonObject();
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setId(json.get("id").getAsInt());
            messageEntity.setMessage(json.get("text").getAsString());
            messageEntity.setUsername(json.get("sender__username").getAsString());
            messageEntity.setIsThisUser(json.get("sender__username").getAsString().equals(username));

            if(!json.get("idea__idea_number").isJsonNull()){
                messageEntity.setIdeaNumber(json.get("idea__idea_number").getAsInt());
                messageEntity.setIdeaVotes(json.get("idea__votes").getAsInt());
            }

            messages.add(messageEntity);
        });

        // Add the messages to the RecyclerView by adding them to the database
        messagesRepository.insertAll(messages);

        return syncData.get("details").getAsString();
    }
}
