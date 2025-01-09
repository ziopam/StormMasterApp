package com.example.stormmasterclient.helpers.RoomDatabase;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.stormmasterclient.helpers.API.ApiService;
import com.example.stormmasterclient.helpers.others.LoggerOut;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BrainstormRepository {
    private final BrainstormDao brainstormDao;
    private final ApiService apiService;

    private static final String BASE_URL = "http://192.168.0.126:8000/";

    private final String token;
    private final LiveData<List<BrainstormEntity>> localData;

    private final Context context;

    private static volatile boolean isProcessing = false;

    public BrainstormRepository(Application application, String token) {
        AppDatabase database = AppDatabase.getInstance(application);
        this.context = application.getApplicationContext();
        brainstormDao = database.brainstormDao();
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiService.class);
        this.token = token;
        localData = brainstormDao.getAllBrainstorms();
    }

    public LiveData<List<BrainstormEntity>> getAllBrainstorms() {
        return localData;
    }

    public void fetchDataFromApi() {
        if(isProcessing){
            return;
        }
        AppDatabase.databaseWriteExecutor.execute(() -> {
            isProcessing = true;
            try {
                Response<List<BrainstormEntity>> response = apiService.getUserBrainstorms("Token " + token).execute();
                if (response.isSuccessful() && response.body() != null) {
                    brainstormDao.insertAll(response.body());
                    displayToast("Информация о мозговых штурмах успешно обновлена");
                } else if(response.code() == 401){
                    displayToast("Ваша авторизация устарела. Авторизуйтесь повторно");
                    LoggerOut loggerOut = new LoggerOut(context);
                    loggerOut.logOut();
                }
            } catch (Exception e) {
                e.printStackTrace();
                displayToast("Не удалось обновить данные, проверьте подключение к интернету.");
            }
            isProcessing = false;
        });
    }

    public void deleteAll(){
        AppDatabase.databaseWriteExecutor.execute(brainstormDao::deleteAll);
    }

    private void displayToast(String message){
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message,
                    Toast.LENGTH_SHORT).show();
        });
    }
}

