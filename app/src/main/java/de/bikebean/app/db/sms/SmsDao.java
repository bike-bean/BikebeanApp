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

    @Query("SELECT COUNT(*) FROM sms_table WHERE type LIKE :type")
    int getCountByType(String type);

    @Query("SELECT * FROM sms_table WHERE type LIKE :type ORDER BY timestamp DESC LIMIT :limit")
    List<Sms> getLatestByType(int limit, String type);
}
