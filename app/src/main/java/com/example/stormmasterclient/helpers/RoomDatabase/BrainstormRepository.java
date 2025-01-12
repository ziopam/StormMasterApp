package com.example.stormmasterclient.helpers.RoomDatabase;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.stormmasterclient.helpers.API.APIConfig;
import com.example.stormmasterclient.helpers.API.ApiService;
import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BrainstormRepository {
    private final BrainstormDao brainstormDao;
    private final ApiService apiService;

    private static final String BASE_URL = APIConfig.BASE_URL;

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
                    List<Integer> brainstormsIds = response.body().stream().map(BrainstormEntity::getId).
                            collect(Collectors.toList());
                    brainstormDao.deleteMissingBrainstorms(brainstormsIds);
                    brainstormDao.insertAll(response.body());
                    displayToast("Информация о мозговых штурмах успешно обновлена");
                } else if(response.code() == 401){
                    logOut();
                }
            } catch (Exception e) {
                e.printStackTrace();
                displayToast("Не удалось обновить данные, проверьте подключение к интернету.");
            }
            isProcessing = false;
        });
    }

    public void deleteById(int id, Activity activity){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try{
                Response<JsonElement> response = apiService.deleteUserBrainstorm("Token " + token, id).execute();
                if(response.isSuccessful()){
                    brainstormDao.deleteById(id);
                    displayToast("Мозговой штурм успешно удален");
                    activity.finish();
                } else if(response.code() == 401){
                    logOut();
                } else if (response.code() == 400 && response.errorBody() != null){
                    JsonObject jsonObject = new Gson().fromJson(response.errorBody().string(), JsonObject.class);
                    displayToast(jsonObject.get("detail").getAsString());
                } else {
                    displayToast("Не удалось удалить мозговой штурм");
                }
            } catch (Exception e){
                e.printStackTrace();
                displayToast("Проверьте подключение к интернету");
            }
        });
    }

    public void deleteAll(){
        AppDatabase.databaseWriteExecutor.execute(brainstormDao::deleteAll);
    }

    private void logOut(){
        displayToast("Ваша авторизация устарела. Авторизуйтесь повторно");
        LoggerOut loggerOut = new LoggerOut(context);
        loggerOut.logOut();
    }

    private void displayToast(String message){
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message,
                    Toast.LENGTH_SHORT).show();
        });
    }
}

