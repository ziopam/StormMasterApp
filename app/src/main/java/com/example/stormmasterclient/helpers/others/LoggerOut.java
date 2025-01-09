package com.example.stormmasterclient.helpers.others;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.stormmasterclient.LoginActivity;
import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormRepository;

public class LoggerOut {

    private final Context context;

    public LoggerOut(Context context) {
        this.context = context;
    }


    public void logOut(){
        Log.d("LoggerOut",  context.toString());
        SharedPreferences preferences = context.getSharedPreferences("USER_DATA", 0);
        preferences.edit().clear().apply();

        BrainstormRepository repository = new BrainstormRepository((Application) context.getApplicationContext(), "null");
        repository.deleteAll();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
