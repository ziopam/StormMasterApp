package com.example.stormmasterclient.helpers.RoomDatabase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MessagesRepository {
    private MessagesDao messagesDao;
    private LiveData<List<MessageEntity>> allMessages;

    public MessagesRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        messagesDao = database.messagesDao();
        allMessages = messagesDao.getAllMessages();
    }

    /**
     * Returns all messages from the database.
     *
     * @return LiveData object containing a list of all messages.
     */
    public LiveData<List<MessageEntity>> getAllMessages() {
        return allMessages;
    }

    /**
     * Inserts a message into the database.
     *
     * @param message The message to be inserted.
     */
    public void insert(MessageEntity message) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            messagesDao.insert(message);
        });
    }

    /**
     * Inserts a list of messages into the database.
     *
     * @param messages The list of messages to be inserted.
     */
    public void insertAll(List<MessageEntity> messages) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            messagesDao.insertAll(messages);
        });
    }

    /**
     * Updates the ideaNumber and ideaVotes fields of a message in the database.
     *
     * @param id        The id of the message to be updated.
     * @param ideaNumber The new idea number.
     * @param ideaVotes  The new number of votes.
     */
    public void updateIdeaFields(int id, int ideaNumber, int ideaVotes) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            messagesDao.updateIdeaFields(id, ideaNumber, ideaVotes);
        });
    }

    /**
     * Updates the ideaVotes field of all messages with this idea in the database.
     *
     * @param ideaNumber The idea number be updated.
     * @param ideaVotes  The new number of votes.
     */
    public void updateIdeaVotes(int ideaNumber, int ideaVotes) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            messagesDao.updateIdeaVotes(ideaNumber, ideaVotes);
        });
    }

    /**
     * Deletes all messages from the database.
     */
    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            messagesDao.deleteAll();
        });
    }
}
