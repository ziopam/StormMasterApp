package com.example.stormmasterclient.helpers.others;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.stormmasterclient.LoginActivity;

public class LoggerOut {

    private final Context context;

    public LoggerOut(Context context) {
        this.context = context;
    }


    public void logOut(){
        SharedPreferences preferences = context.getSharedPreferences("USER_DATA", 0);
        preferences.edit().clear().apply();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
