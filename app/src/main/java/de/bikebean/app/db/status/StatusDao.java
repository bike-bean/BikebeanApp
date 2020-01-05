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

    @Query("SELECT * FROM status_table WHERE _key = :key AND state = :state")
    LiveData<List<Status>> getByKeyAndState(String key, int state);

    @Query("SELECT * FROM status_table WHERE _key LIKE :key ORDER BY timestamp DESC LIMIT 1")
    List<Status> getByKey(String key);

    @Query("UPDATE status_table SET state = :state WHERE sms_id = :smsId")
    void updateStateBySmsId(int state, int smsId);

    @Query("UPDATE status_table SET state = :state WHERE _key = :key")
    void updateStateByKey(int state, String key);
}
