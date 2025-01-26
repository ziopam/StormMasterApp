package com.example.stormmasterclient.helpers.API;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.stormmasterclient.MainActivity;
import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ApiProblemsHandler {

    private final Context context;
    private final LoggerOut loggerOut;

    public ApiProblemsHandler(Context context) {
        this.context = context;
        this.loggerOut = new LoggerOut(context);
    }

    /**
     * Processes a failed connection to the server.
     */
    public void processConnectionFailed(){
        Toast.makeText(context, "Ошибка подключения. Проверьте интернет соединение",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Processes an unauthorized user.
     */
    public void processUserUnauthorized(){
        Toast.makeText(context, "Ваша авторизация устарела. Авторизуйтесь повторно",
                Toast.LENGTH_SHORT).show();
        new LoggerOut(context).logOut();
    }

    /**
     * Processes an error response from the server.
     *
     * @param response The response from the server.
     * @return The error body of the response. Null if the error body could not be read.
     */
    public JsonObject processErrorBody(Response<?> response){
        JsonObject problem = null;
        if(response != null) {
            // Get the error body and try to parse it
            try (ResponseBody errorBody = response.errorBody()) {
                if (errorBody != null) {
                    problem = new Gson().fromJson(errorBody.string(), JsonObject.class);
                } else {
                    // Inform the user about the error
                    Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();

                // Inform the user about the error
                Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Inform the user about the error
            Toast.makeText(context, "Ошибка при чтении тела ответа сервера", Toast.LENGTH_SHORT).show();
        }
        return problem;
    }

    /**
     * Returns to the main activity.
     */
    public void returnToMain(){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
