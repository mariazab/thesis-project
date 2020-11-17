package fi.haagahelia.android.fitnessapp.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_item_table")
public class FoodItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "food_item_id")
    public long id;

    private String name;

    //Calories per 100g
    private int calories;

    //Carbs per 100g
    private double carbs;

    //Protein per 100g
    private double protein;

    //Fat per 100g
    private double fat;

    //Saturated fat per 100g
    private double saturatedFat;

    //Trans-fat per 100g
    private double transFat;

    //Sugar per 100g
    private double sugar;

    //Salt per 100g
    private double salt;

    //Is the item fruit or veggies
    private boolean fruitVeggie;

    //Constructor with all fields
    @Ignore
    public FoodItem(String name, int calories, double carbs, double protein, double fat, double saturatedFat, double transFat, double sugar, double salt, boolean fruitVeggie) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.saturatedFat = saturatedFat;
        this.transFat = transFat;
        this.sugar = sugar;
        this.salt = salt;
        this.fruitVeggie = fruitVeggie;
    }

    //Constructor with only name
    public FoodItem(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(double saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public double getTransFat() {
        return transFat;
    }

    public void setTransFat(double transFat) {
        this.transFat = transFat;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getSalt() {
        return salt;
    }

    public void setSalt(double salt) {
        this.salt = salt;
    }

    public boolean isFruitVeggie() {
        return fruitVeggie;
    }

    public void setFruitVeggie(boolean fruitVeggie) {
        this.fruitVeggie = fruitVeggie;
    }
}
