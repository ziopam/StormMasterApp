package com.example.stormmasterclient.helpers.RoomDatabase;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class BrainstormViewModel extends AndroidViewModel {
    private final BrainstormRepository repository;
    private final LiveData<List<BrainstormEntity>> allBrainstormsFromDb;

    public BrainstormViewModel(Application application, String token) {
        super(application);
        repository = new BrainstormRepository(application, token);
        allBrainstormsFromDb = repository.getAllBrainstorms();
    }

    public LiveData<List<BrainstormEntity>> getAllBrainstorms() {
        return allBrainstormsFromDb;
    }

    public void fetchDataFromApi() {
        repository.fetchDataFromApi();
    }
}

