package de.bikebean.app.db.state;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StateDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(State state);

    @Query("DELETE FROM state_table")
    void deleteAll();

    @Query("DELETE FROM state_table WHERE _key LIKE :key AND state = :state")
    void deleteByKeyAndState(String key, int state);

    /*
    Async
     */
    @Query("SELECT * FROM state_table WHERE _key LIKE :key ORDER BY timestamp DESC")
    LiveData<List<State>> getAllByKey(String key);

    @Query("SELECT * FROM state_table WHERE _key = :key AND state = :state ORDER BY timestamp DESC")
    LiveData<List<State>> getByKeyAndState(String key, int state);

    /*
    Sync
     */
    @Query("SELECT * FROM state_table WHERE _key LIKE :key ORDER BY timestamp DESC LIMIT 1")
    List<State> getByKey(String key);

    @Query("SELECT * FROM state_table WHERE _key = :key AND state = :state ORDER BY timestamp DESC")
    List<State> getByKeyAndStateSync(String key, int state);

    @Query("UPDATE state_table SET state = :state WHERE sms_id = :smsId")
    void updateStateBySmsId(int state, int smsId);

    @Query("UPDATE state_table SET state = :state WHERE _key = :key")
    void updateStateByKey(int state, String key);
}
