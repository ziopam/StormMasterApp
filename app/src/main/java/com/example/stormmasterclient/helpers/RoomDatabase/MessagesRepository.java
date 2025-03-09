package com.example.stormmasterclient.helpers.RoomDatabase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

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
     * Get all idea numbers from the database (where ideaNumber is not -1).
     */
    public List<Integer> getIdeaNumbers() {
        Future<List<Integer>> future = AppDatabase.databaseWriteExecutor.submit(new Callable<List<Integer>>() {
            @Override
            public List<Integer> call() {
                return messagesDao.getIdeaNumbers();
            }
        });

        try {
            // Wait for the result
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Get all text messages with a specific idea number.
     *
     * @param ideaNumber The idea number to search for.
     * @return A list of texts of messages with the specified idea number.
     */
    public List<String> getTextMessagesByIdeaNumber(int ideaNumber) {
        Future<List<String>> future = AppDatabase.databaseWriteExecutor.submit(new Callable<List<String>>() {
            @Override
            public List<String> call() {
                return messagesDao.getTextMessagesByIdeaNumber(ideaNumber);
            }
        });

        try {
            // Wait for the result
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
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
