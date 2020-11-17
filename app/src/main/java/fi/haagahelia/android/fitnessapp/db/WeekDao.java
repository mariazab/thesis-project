package fi.haagahelia.android.fitnessapp.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface WeekDao {

    @Insert
    void insert(Week week);

    @Query("SELECT * FROM week_table ORDER BY weekYear, weekNumber")
    LiveData<List<Week>> getAllWeeks();

    @Query("SELECT * FROM week_table WHERE week_id=:id")
    Week getWeekById(long id);

    @Query("DELETE FROM week_table WHERE week_id=:id")
    void deleteById(long id);

    @Query("UPDATE week_table SET totalHours = totalHours + :totalHours, totalMinutes = totalMinutes + :totalMinutes, totalSeconds = totalSeconds + :totalSeconds WHERE week_id=:id")
    void addToWeekTotal(long id, int totalHours, int totalMinutes, int totalSeconds);

    @Query("UPDATE week_table SET totalHours = totalHours - :totalHours, totalMinutes = totalMinutes - :totalMinutes, totalSeconds = totalSeconds - :totalSeconds WHERE week_id=:id")
    void subtractFromWeekTotal(long id, int totalHours, int totalMinutes, int totalSeconds);

}
