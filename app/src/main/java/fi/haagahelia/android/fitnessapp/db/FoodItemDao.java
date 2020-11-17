package fi.haagahelia.android.fitnessapp.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface FoodItemDao {

    @Insert
    void insert(FoodItem foodItem);

    @Insert
    long insertWithReturn(FoodItem foodItem);

    @Query("SELECT * FROM food_item_table")
    LiveData<List<FoodItem>> getFoodItems();

    @Query("DELETE FROM food_item_table WHERE food_item_id=:id")
    void deleteById(long id);

    @Query("UPDATE food_item_table SET name=:name, calories=:calories, carbs=:carbs, protein=:protein, fat=:fat, saturatedFat=:saturatedFat, transFat=:transFat, sugar=:sugar, salt=:salt, fruitVeggie=:fruitVeggie WHERE food_item_id=:id")
    void updateFoodItem(long id, String name, int calories, double carbs, double protein, double fat, double saturatedFat, double transFat, double sugar, double salt, boolean fruitVeggie);

}
