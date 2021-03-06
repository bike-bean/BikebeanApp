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
    void insert(final State state);

    @Query("DELETE FROM state_table")
    void deleteAll();

    /*
    Async
     */
    @Query("SELECT * FROM state_table WHERE _key LIKE :key ORDER BY timestamp DESC")
    LiveData<List<State>> getAllByKey(final String key);

    @Query("SELECT * FROM state_table WHERE _key = :key AND state = :state ORDER BY timestamp DESC")
    LiveData<List<State>> getByKeyAndState(final String key, int state);

    /*
    Sync
     */
    @Query("SELECT * FROM state_table")
    List<State> getAllSync();

    @Query("SELECT * FROM state_table WHERE sms_id = :smsId ORDER BY timestamp DESC")
    List<State> getAllByIdSync(int smsId);

    @Query("SELECT * FROM state_table WHERE _key = :key AND state = :state ORDER BY timestamp DESC")
    List<State> getByKeyAndStateSync(final String key, int state);

    @Query("SELECT * FROM state_table WHERE _key = :key AND sms_id = :smsId ORDER BY timestamp DESC")
    List<State> getByKeyAndIdSync(final String key, int smsId);

    @Query("UPDATE state_table SET state = :state, value = :value WHERE _key = :key AND sms_id = :smsId")
    void updateStateByKeyAndSmsId(int state, double value, final String key, int smsId);
}
