package com.example.stormmasterclient.helpers.RoomDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class for a brainstorm.
 */
@Entity(tableName = "brainstorm_table")
public class BrainstormEntity {

    @PrimaryKey(autoGenerate = false)
    private int id;
    private String title;
    private boolean isCreator;
    private String completionDate;
    private String participants;
    private String details;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isCreator() { return isCreator; }
    public void setCreator(boolean creator) { isCreator = creator; }

    public String getCompletionDate() { return completionDate; }
    public void setCompletionDate(String completionDate) { this.completionDate = completionDate; }

    public String getParticipants() { return participants; }
    public void setParticipants(String participants) { this.participants = participants; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
