package fi.haagahelia.android.fitnessapp.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface FoodDayDao {

    @Insert
    void insert(FoodDay foodDay);

    @Query("SELECT * FROM food_day_table")
    LiveData<List<FoodDay>> getAllFoodDays();

    @Query("DELETE FROM food_day_table WHERE food_day_id=:id")
    void deleteById(long id);

    @Transaction
    @Query("SELECT * FROM food_day_table")
    LiveData<List<FoodJournalItem>> getFoodJournal();

    @Query("UPDATE food_day_table SET grams=:grams WHERE food_day_id=:id")
    void updateFoodDayGrams(long id, double grams);

}
