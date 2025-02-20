package com.example.stormmasterclient.helpers.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object (DAO) for accessing messages data in the database.
 */
@Dao
public interface MessagesDao {

    /**
     * Returns a LiveData list of all messages.
     *
     * @return A LiveData list of all messages.
     */
    @Query("SELECT * FROM messages_table")
    LiveData<List<MessageEntity>> getAllMessages();

    /**
     * Inserts a single message into the database.
     *
     * @param message The message to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageEntity message);

    /**
     * Inserts a list of messages into the database. If a message already exists, it will be replaced.
     *
     * @param messages The list of messages to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MessageEntity> messages);

    /**
     * Updates the ideaNumber and ideaVotes fields of a message in the database.
     *
     * @param id The id of the message to be updated.
     * @param ideaNumber The new idea number.
     * @param ideaVotes The new number of votes.
     */
    @Query("UPDATE messages_table SET ideaNumber = :ideaNumber, ideaVotes = :ideaVotes WHERE id = :id")
    void updateIdeaFields(int id, int ideaNumber, int ideaVotes);

    /**
     * Updates the ideaVotes field of all messages with this idea in the database.
     *
     * @param ideaNumber The idea number be updated.
     * @param ideaVotes The new number of votes.
     */
    @Query("UPDATE messages_table SET ideaVotes = :ideaVotes WHERE ideaNumber = :ideaNumber")
    void updateIdeaVotes(int ideaNumber, int ideaVotes);

    /**
     * Deletes all messages from the database.
     */
    @Query("DELETE FROM messages_table")
    void deleteAll();
}
