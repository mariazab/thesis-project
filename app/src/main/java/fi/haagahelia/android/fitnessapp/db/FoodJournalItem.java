package fi.haagahelia.android.fitnessapp.db;

import androidx.room.Embedded;
import androidx.room.Relation;

public class FoodJournalItem {

    @Embedded
    FoodDay foodDay;

    @Relation(parentColumn = "foodItemId",
            entityColumn = "food_item_id")
    FoodItem foodItem;

    public FoodDay getFoodDay() {
        return foodDay;
    }

    public void setFoodDay(FoodDay foodDay) {
        this.foodDay = foodDay;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

}
