package fi.haagahelia.android.fitnessapp.foodtracking;

import android.app.Application;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import fi.haagahelia.android.fitnessapp.db.DataRepository;
import fi.haagahelia.android.fitnessapp.db.FoodDay;
import fi.haagahelia.android.fitnessapp.db.FoodItem;
import fi.haagahelia.android.fitnessapp.db.FoodJournalItem;

public class FoodDayViewModel extends AndroidViewModel {

    private DataRepository repository;

    private LiveData<List<FoodDay>> foodDayList;
    private LiveData<List<FoodJournalItem>> foodJournalList;

    public FoodDayViewModel (Application application) {
        super(application);

        repository = new DataRepository(application);

        foodDayList = repository.getFoodDayList();
        foodJournalList = repository.getFoodJournalList();
    }

    public LiveData<List<FoodDay>> getFoodDayList() {
        return foodDayList;
    }

    public LiveData<List<FoodJournalItem>> getFoodJournalList() {
        return foodJournalList;
    }

    public void insert(FoodDay foodDay) {
        repository.insertFoodDay(foodDay);
    }

    public void deleteById(long id) {
        repository.deleteFoodDayItem(id);
    }

    public void insertFoodItemAndFoodDay(FoodItem foodItem, Date date, double grams) {
        repository.insertFoodItemAndFoodDay(foodItem, date, grams);
    }

    public void updateFoodItemAndFoodDay(long foodItemId, String name, int calories, double carbs, double protein, double fat, double saturatedFat, double transFat, double sugar, double salt, boolean fruitVeggie, long foodDayId, double grams) {
        repository.updateFoodItemAndFoodDay(foodItemId, name, calories, carbs, protein, fat, saturatedFat, transFat, sugar, salt, fruitVeggie, foodDayId, grams);
    }

}