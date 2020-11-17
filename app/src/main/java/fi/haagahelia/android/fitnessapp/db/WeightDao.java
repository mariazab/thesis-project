package fi.haagahelia.android.fitnessapp.db;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface WeightDao {

    @Insert
    void insert(Weight weight);

    @Query("SELECT * FROM weight_table ORDER BY date")
    LiveData<List<Weight>> getWeights();

    @Query("DELETE FROM weight_table")
    void deleteAll();

    @Query("DELETE FROM weight_table WHERE weight_id=:id")
    void deleteById(long id);

    @Query("UPDATE weight_table SET weight=:weight, date=:date WHERE weight_id=:id")
    void update(long id, Date date, double weight);

}
