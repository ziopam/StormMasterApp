package com.example.stormmasterclient.helpers.RoomDatabase;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;


/**
 * ViewModel for managing brainstorm data.
 *
 * @see BrainstormRepository
 * @see BrainstormEntity
 * @see AndroidViewModel
 */
public class BrainstormViewModel extends AndroidViewModel {
    private final BrainstormRepository repository;
    private final LiveData<List<BrainstormEntity>> allBrainstormsFromDb;

    /**
     * Constructor for BrainstormViewModel.
     *
     * @param application The application context.
     * @param token The authorization token of the user.
     */
    public BrainstormViewModel(Application application, String token) {
        super(application);
        repository = new BrainstormRepository(application, token);
        allBrainstormsFromDb = repository.getAllBrainstorms();
    }

    /**
     * Returns a LiveData list of all brainstorms.
     *
     * @return A LiveData list of all brainstorms.
     */
    public LiveData<List<BrainstormEntity>> getAllBrainstorms() {
        return allBrainstormsFromDb;
    }

    /**
     * Fetches data from the API and updates the local database.
     *
     * @see BrainstormRepository#fetchDataFromApi()
     */
    public void fetchDataFromApi() {
        repository.fetchDataFromApi();
    }
}

