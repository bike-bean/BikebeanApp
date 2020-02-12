package de.bikebean.app.db.sms;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SmsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Sms sms);

    @Query("DELETE FROM sms_table")
    void deleteAll();

    @Query("SELECT * FROM sms_table ORDER BY timestamp DESC")
    LiveData<List<Sms>> getAll();

    @Query("SELECT * FROM sms_table WHERE state = :state AND type = :type ORDER BY timestamp DESC")
    LiveData<List<Sms>> getByStateAndType(int state, int type);

    @Query("SELECT _id FROM sms_table WHERE type = :type ORDER BY timestamp DESC")
    LiveData<List<Integer>> getAllIdsByType(int type);

    @Query("SELECT * FROM sms_table")
    List<Sms> getAllSync();

    @Query("SELECT COUNT(*) FROM sms_table WHERE type LIKE :type")
    int getCountByType(int type);

    @Query("SELECT * FROM sms_table WHERE _id = :id")
    List<Sms> getSmsById(int id);

    @Query("SELECT * FROM sms_table WHERE type = :type LIMIT 1")
    List<Sms> getLatestId(int type);

    @Query("UPDATE sms_table SET state = :state WHERE _id = :id")
    void updateStateById(int state, int id);
}
