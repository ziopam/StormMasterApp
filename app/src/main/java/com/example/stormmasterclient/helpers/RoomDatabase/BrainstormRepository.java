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

/**
 * Repository for managing brainstorm data.
 *
 * @see BrainstormDao
 * @see BrainstormEntity
 * @see AppDatabase
 * @see ApiService
 */
public class BrainstormRepository {
    private final BrainstormDao brainstormDao;
    private final ApiService apiService;
    private static final String BASE_URL = APIConfig.BASE_URL;
    private final String token;
    private final LiveData<List<BrainstormEntity>> localData;
    private final Context context;
    private static volatile boolean isProcessing = false;

    /**
     * Constructor for BrainstormRepository.
     *
     * @param application The application context.
     * @param token The authorization token of the user.
     */
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

    /**
     * Returns a LiveData list of all brainstorms.
     *
     * @return A LiveData list of all brainstorms.
     */
    public LiveData<List<BrainstormEntity>> getAllBrainstorms() {
        return localData;
    }

    /**
     * Fetches data from the API and updates the local database.
     *
     * @see #displayToast(String)
     * @see #logOut()
     */
    public void fetchDataFromApi() {
        // Prevent multiple requests
        if(isProcessing){
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            isProcessing = true;
            try {
                // Create a response and update the local database
                Response<List<BrainstormEntity>> response = apiService.getUserBrainstorms("Token " + token).execute();

                // Check if the response is successful and update the local database
                if (response.isSuccessful() && response.body() != null) {
                    List<Integer> brainstormsIds = response.body().stream().map(BrainstormEntity::getId).
                            collect(Collectors.toList());

                    // Delete brainstorms that are not in the response (to delete deleted brainstorms)
                    brainstormDao.deleteMissingBrainstorms(brainstormsIds);

                    brainstormDao.insertAll(response.body());
                    displayToast("Информация о мозговых штурмах успешно обновлена");
                } else if(response.code() == 401){
                    // Log out the user if the response is unauthorized
                    logOut();
                }
            } catch (Exception e) {
                e.printStackTrace();
                displayToast("Не удалось обновить данные, проверьте подключение к интернету.");
            }
            isProcessing = false;
        });
    }

    /**
     * Deletes a brainstorm by its ID from both the local and server database. If the request is successful,
     * the given activity will be finished.
     *
     * @param id The ID of the brainstorm to be deleted.
     * @param activity The activity from which the delete is initiated.
     * @see #displayToast(String)
     * @see #logOut()
     */
    public void deleteById(int id, Activity activity){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try{
                // Create a response and delete the brainstorm from the server
                Response<JsonElement> response = apiService.deleteUserBrainstorm("Token " + token, id).execute();
                if(response.isSuccessful()){
                    // Delete the brainstorm from the local database as response is successful
                    brainstormDao.deleteById(id);
                    displayToast("Мозговой штурм успешно удален");

                    // Finish the given activity
                    activity.finish();
                } else if(response.code() == 401){
                    // Log out the user if the response is unauthorized
                    logOut();
                } else if (response.code() == 400 && response.errorBody() != null){
                    // Get the error body and display the error message
                    JsonObject jsonObject = new Gson().fromJson(response.errorBody().string(), JsonObject.class);
                    displayToast(jsonObject.get("detail").getAsString());
                } else {
                    // Inform the user about the error
                    displayToast("Не удалось удалить мозговой штурм");
                }
            } catch (Exception e){
                e.printStackTrace();

                // Inform the user about the error
                displayToast("Проверьте подключение к интернету");
            }
        });
    }

    /**
     * Deletes all brainstorms from the local database.
     */
    public void deleteAll(){
        AppDatabase.databaseWriteExecutor.execute(brainstormDao::deleteAll);
    }

    /**
     * Logs out the user and clears local data.
     *
     * @see LoggerOut
     */
    private void logOut(){
        displayToast("Ваша авторизация устарела. Авторизуйтесь повторно");
        LoggerOut loggerOut = new LoggerOut(context);
        loggerOut.logOut();
    }

    /**
     * Displays a toast message.
     *
     * @param message The message to be displayed.
     */
    private void displayToast(String message){
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, message,
                    Toast.LENGTH_SHORT).show();
        });
    }
}

