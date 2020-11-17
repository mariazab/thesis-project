package fi.haagahelia.android.fitnessapp.db;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_day_table", foreignKeys = @ForeignKey(entity = FoodItem.class, parentColumns = "food_item_id", childColumns = "foodItemId", onDelete = ForeignKey.CASCADE))
public class FoodDay {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "food_day_id")
    private long id;

    @NonNull
    private Date date;

    public long foodItemId;

    private double grams;

    public FoodDay(@NonNull Date date, long foodItemId, double grams) {
        this.date = date;
        this.foodItemId = foodItemId;
        this.grams = grams;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(long foodItemId) {
        this.foodItemId = foodItemId;
    }

    public double getGrams() {
        return grams;
    }

    public void setGrams(double grams) {
        this.grams = grams;
    }
}
