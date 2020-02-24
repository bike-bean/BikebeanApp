package de.bikebean.app.db.log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Log log);

    @Query("DELETE FROM log_table")
    void deleteAll();

    @Query("SELECT * FROM log_table ORDER BY timestamp DESC")
    LiveData<List<Log>> getAll();

    @Query("SELECT * FROM log_table")
    List<Log> getAllSync();
}
