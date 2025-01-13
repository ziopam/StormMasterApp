package com.example.stormmasterclient.helpers.others;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.stormmasterclient.LoginActivity;
import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormRepository;

/**
 * Helper class for logging out of the account.
 */
public class LoggerOut {
    private final Context context;

    /**
     * Constructor for LoggerOut.
     *
     * @param context The context in which the logger is used.
     */
    public LoggerOut(Context context) {
        this.context = context;
    }

    /**
     * Logs out of the account.
     *
     * @see BrainstormRepository
     */
    public void logOut(){
        // Clear the user data to avoid data leaks
        SharedPreferences preferences = context.getSharedPreferences("USER_DATA", 0);
        preferences.edit().clear().apply();

        // Clear the local database to avoid data leaks
        BrainstormRepository repository = new BrainstormRepository((Application) context.getApplicationContext(), "null");
        repository.deleteAll();

        // Redirect the user to the login activity using flags below since since the user must not be able to go back
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
