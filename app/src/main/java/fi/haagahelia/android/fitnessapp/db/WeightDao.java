package fi.haagahelia.android.fitnessapp.db;

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
}
