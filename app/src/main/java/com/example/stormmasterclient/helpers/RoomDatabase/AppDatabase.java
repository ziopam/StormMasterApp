package com.example.stormmasterclient.helpers.RoomDatabase;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Database class for the Room database.
 *
 * @see BrainstormEntity
 * @see BrainstormDao
 * @see RoomDatabase
 */
@Database(entities = {BrainstormEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BrainstormDao brainstormDao();
    private static volatile AppDatabase instance;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    /**
     * Returns the singleton instance of the database.
     *
     * @param context The context in which the database is used.
     * @return The singleton instance of the AppDatabase.
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "brainstorm_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

