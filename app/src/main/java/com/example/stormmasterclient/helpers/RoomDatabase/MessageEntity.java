package com.example.stormmasterclient.helpers.RoomDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * An entity that represents a message.
 */
@Entity(tableName = "messages_table")
public class MessageEntity {

    @PrimaryKey(autoGenerate = false)
    private int id;

    private String message;
    private String username;
    private boolean isThisUser;
    private int ideaNumber = -1;
    private int ideaVotes = 0;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getIsThisUser() {
        return isThisUser;
    }

    public void setIsThisUser(boolean isThisUser) {
        this.isThisUser = isThisUser;
    }

    public int getIdeaNumber() {
        return ideaNumber;
    }

    public void setIdeaNumber(int ideaNumber) {
        this.ideaNumber = ideaNumber;
    }

    public int getIdeaVotes() {
        return ideaVotes;
    }

    public void setIdeaVotes(int ideaVotes) {
        this.ideaVotes = ideaVotes;
    }

}
