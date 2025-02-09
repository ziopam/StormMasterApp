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

    public LiveData<List<MessageEntity>> getAllMessages() {
        return allMessages;
    }

    public void insert(MessageEntity message) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            messagesDao.insert(message);
        });
    }

    public void insertAll(List<MessageEntity> messages) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            messagesDao.insertAll(messages);
        });
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            messagesDao.deleteAll();
        });
    }
}
