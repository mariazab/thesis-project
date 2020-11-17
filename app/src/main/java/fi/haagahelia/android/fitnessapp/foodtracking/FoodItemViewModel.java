package fi.haagahelia.android.fitnessapp.foodtracking;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import fi.haagahelia.android.fitnessapp.db.DataRepository;
import fi.haagahelia.android.fitnessapp.db.FoodItem;

public class FoodItemViewModel extends AndroidViewModel {

    private DataRepository repository;

    private LiveData<List<FoodItem>> allFoodItems;

    public FoodItemViewModel (Application application) {
        super(application);

        repository = new DataRepository(application);
        allFoodItems = repository.getAllFoodItems();
    }

    public LiveData<List<FoodItem>> getAllFoodItems() {
        return allFoodItems;
    }

    public void insert(FoodItem foodItem) {
        repository.insertFoodItem(foodItem);
    }

    public void deleteById(long id) {
        repository.deleteFoodItem(id);
    }
}