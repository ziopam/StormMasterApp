package com.example.stormmasterclient.helpers.RecyclerViewAdapters;

/**
 * An entity that represents a message.
 */
public class MessageEntity {
    public String message;
    public String username;
    public boolean isThisUser;
    public boolean isIdea;

    public MessageEntity(String message, String username, boolean isThisUser, boolean isIdea) {
        this.message = message;
        this.username = username;
        this.isThisUser = isThisUser;
        this.isIdea = isIdea;
    }
}
