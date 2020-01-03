package de.bikebean.app.db.status;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StatusDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Status status);

    @Query("DELETE FROM status_table")
    void deleteAll();

    @Query("SELECT * FROM status_table WHERE _key LIKE :key ORDER BY timestamp DESC")
    LiveData<List<Status>> getAllByKey(String key);

    @Query("SELECT * FROM status_table WHERE _key LIKE :key ORDER BY timestamp DESC LIMIT 1")
    List<Status> getByKey(String key);
}
