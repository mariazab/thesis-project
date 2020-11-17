package fi.haagahelia.android.fitnessapp.db;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PhysicalActivityDao {

    @Insert
    void insert(PhysicalActivity physicalActivity);

    @Query("SELECT * FROM physical_activity_table ORDER BY date")
    LiveData<List<PhysicalActivity>> getAllPhysicalActivities();

    @Query("DELETE FROM physical_activity_table WHERE physical_activity_id=:id")
    void deleteById(long id);

    @Query("UPDATE physical_activity_table SET name=:name, date=:date, hours=:hours, minutes=:minutes, seconds=:seconds, intensity=:intensity WHERE physical_activity_id=:id ")
    void update(long id, String name, Date date, int hours, int minutes, int seconds, int intensity);
}
