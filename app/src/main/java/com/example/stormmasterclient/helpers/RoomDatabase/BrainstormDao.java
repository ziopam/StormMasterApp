package com.example.stormmasterclient.helpers.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BrainstormDao {
    @Query("SELECT * FROM brainstorm_table ORDER BY completionDate DESC")
    LiveData<List<BrainstormEntity>> getAllBrainstorms();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BrainstormEntity> brainstorms);

    @Query("DELETE FROM brainstorm_table WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM brainstorm_table")
    void deleteAll();
}
