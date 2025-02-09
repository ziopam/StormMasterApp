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
     * Deletes all messages from the database.
     */
    @Query("DELETE FROM messages_table")
    void deleteAll();
}
