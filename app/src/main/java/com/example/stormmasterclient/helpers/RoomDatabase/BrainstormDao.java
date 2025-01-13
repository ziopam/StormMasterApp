package com.example.stormmasterclient.helpers.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object (DAO) for accessing brainstorm data in the database.
 */
@Dao
public interface BrainstormDao {

    /**
     * Returns a LiveData list of all brainstorms, ordered by completion date in descending order.
     *
     * @return A LiveData list of all brainstorms.
     */
    @Query("SELECT * FROM brainstorm_table ORDER BY completionDate DESC")
    LiveData<List<BrainstormEntity>> getAllBrainstorms();

    /**
     * Inserts a list of brainstorms into the database. If a brainstorm already exists, it will be replaced.
     *
     * @param brainstorms The list of brainstorms to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BrainstormEntity> brainstorms);

    /**
     * Deletes brainstorms from the database that are not in the provided list of IDs.
     *
     * @param brainstormsIds The list of brainstorm IDs to keep.
     */
    @Query("DELETE FROM brainstorm_table WHERE id NOT IN (:brainstormsIds)")
    void deleteMissingBrainstorms(List<Integer> brainstormsIds);

    /**
     * Deletes a brainstorm from the database by its ID.
     *
     * @param id The ID of the brainstorm to be deleted.
     */
    @Query("DELETE FROM brainstorm_table WHERE id = :id")
    void deleteById(int id);

    /**
     * Deletes all brainstorms from the database.
     */
    @Query("DELETE FROM brainstorm_table")
    void deleteAll();
}
