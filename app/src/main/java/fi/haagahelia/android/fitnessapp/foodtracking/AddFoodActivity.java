package fi.haagahelia.android.fitnessapp.foodtracking;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import fi.haagahelia.android.fitnessapp.Constants;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.db.FoodItem;

public class AddFoodActivity extends AppCompatActivity {

    private FoodItemViewModel foodItemViewModel;
    private List<FoodItem> foodItemList;
    private List<String> foodItemStringList = new ArrayList<>();

    private EditText nameEditText;
    private EditText gramsEditText;
    private EditText caloriesEditText;
    private EditText carbsEditText;
    private EditText proteinEditText;
    private EditText fatEditText;
    private EditText satFatEditText;
    private EditText transFatEditText;
    private EditText sugarEditText;
    private EditText saltEditText;
    private CheckBox fruitVeggieCheckBox;

    //Follow if the item is chosen from list of already saved items, because the item will not be saved to db only its size (the journal entry)
    private boolean isItemFromList;
    private long foodItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        foodItemViewModel = new ViewModelProvider(this).get(FoodItemViewModel.class);
        foodItemViewModel.getAllFoodItems().observe(this, foodItems -> {
            foodItemList = foodItems;

            for (int i = 0; i < foodItems.size(); i++) {
                FoodItem item = foodItems.get(i);

                String itemString = item.getName() + getString(R.string.food_item_details_start)
                        + getString(R.string.food_item_details_kcal) + item.getCalories()
                        + getString(R.string.food_item_details_comma)
                        + getString(R.string.food_item_details_carbs) + item.getCarbs()
                        + getString(R.string.food_item_details_comma)
                        + getString(R.string.food_item_details_protein) + item.getProtein()
                        + getString(R.string.food_item_details_comma)
                        + getString(R.string.food_item_details_fat) + item.getFat()
                        + getString(R.string.food_item_details_end);

                foodItemStringList.add(itemString);
            }
        });

        nameEditText = findViewById(R.id.add_food_name);
        gramsEditText = findViewById(R.id.add_grams);
        caloriesEditText = findViewById(R.id.add_calories);
        carbsEditText = findViewById(R.id.add_carbs);
        proteinEditText = findViewById(R.id.add_protein);
        fatEditText = findViewById(R.id.add_fat);
        satFatEditText = findViewById(R.id.add_saturated_fat);
        transFatEditText = findViewById(R.id.add_trans_fat);
        sugarEditText = findViewById(R.id.add_sugar);
        saltEditText = findViewById(R.id.add_salt);

        fruitVeggieCheckBox = findViewById(R.id.fruit_veggies_checkbox);

        Button saveBtn = findViewById(R.id.button_save);

        saveBtn.setOnClickListener(view -> {
            boolean areRequiredValuesPresent = checkIfRequiredValuesArePresent();
            if(!areRequiredValuesPresent) {
                Toast.makeText(getApplicationContext(), getString(R.string.food_insert_error_msg), Toast.LENGTH_LONG).show();
            } else {
                addFood();
            }
        });

        Button itemFromListBtn = findViewById(R.id.button_choose_from_list);

        itemFromListBtn.setVisibility(View.VISIBLE);

        itemFromListBtn.setOnClickListener(view -> showListDialog());

    }

    private void addFood() {
        Intent replyIntent = new Intent();

        String foodName = nameEditText.getText().toString();
        foodName = foodName.trim();
        double grams = Double.parseDouble(gramsEditText.getText().toString());

        //Send details as String and parse it in the receiver activity as they may be empty
        int calories = (int) getInputtedValue(caloriesEditText);
        double carbs = getInputtedValue(carbsEditText);
        double protein = getInputtedValue(proteinEditText);
        double fat = getInputtedValue(fatEditText);
        double satFat = getInputtedValue(satFatEditText);
        double transFat = getInputtedValue(transFatEditText);
        double sugar = getInputtedValue(sugarEditText);
        double salt = getInputtedValue(saltEditText);
        //Get the value from the checkbox
        boolean isFruitVeggie = fruitVeggieCheckBox.isChecked();

        //Add data to intent
        replyIntent.putExtra(Constants.FOOD_NAME, foodName);
        replyIntent.putExtra(Constants.FOOD_GRAMS, grams);
        replyIntent.putExtra(Constants.FOOD_CALORIES, calories);
        replyIntent.putExtra(Constants.FOOD_CARBS, carbs);
        replyIntent.putExtra(Constants.FOOD_PROTEIN, protein);
        replyIntent.putExtra(Constants.FOOD_FAT, fat);
        replyIntent.putExtra(Constants.FOOD_SAT_FAT, satFat);
        replyIntent.putExtra(Constants.FOOD_TRANS_FAT, transFat);
        replyIntent.putExtra(Constants.FOOD_SUGAR, sugar);
        replyIntent.putExtra(Constants.FOOD_SALT, salt);
        replyIntent.putExtra(Constants.FOOD_FRUIT_VEGGIE, isFruitVeggie);
        replyIntent.putExtra(Constants.IS_FOOD_FROM_LIST, isItemFromList);

        //Add food item id if chosen from list
        if(isItemFromList) {
            replyIntent.putExtra(Constants.FOOD_ITEM_ID, foodItemId);
        }

        //Add the result to intent and finish activity
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    //Get the values inputted by the user
    private double getInputtedValue(EditText editText) {
        double value = 0;

        //If the input is not empty, get the value
        if(!(TextUtils.isEmpty(editText.getText().toString()))) {
            value = Double.parseDouble(editText.getText().toString());
        }
        return value;

    }

    //Check if all required values are provided by the user
    private boolean checkIfRequiredValuesArePresent() {
        boolean areValuesPresent = true;

        if (TextUtils.isEmpty(nameEditText.getText().toString().trim()) || TextUtils.isEmpty(gramsEditText.getText().toString().trim())) {
            areValuesPresent = false;
        }
        return areValuesPresent;
    }

    //Method to show confirmation dialog when DELETE is clicked
    private void showListDialog() {

        //Create an AlertDialog, set title
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_from_list);

        //Check if item list is empty
        if(foodItemStringList.isEmpty()) {
            //Tell the user there is no items yet in the db
            builder.setMessage(R.string.food_journal_empty_view_title);
        } else {
            // Convert ArrayList to object array
            Object[] objArr = foodItemStringList.toArray();

            // convert Object array to String array
            String[] items = Arrays.copyOf(objArr, objArr.length, String[].class);

            //Dialog set message and respond to answers
            builder.setItems(items, (dialog, which) -> {
                // The 'which' argument contains the index position
                // of the selected item
                FoodItem item = foodItemList.get(which);
                foodItemId = item.getId();
                nameEditText.setText(item.getName());
                caloriesEditText.setText(String.valueOf(item.getCalories()));
                carbsEditText.setText(String.valueOf(item.getCarbs()));
                proteinEditText.setText(String.valueOf(item.getProtein()));
                fatEditText.setText(String.valueOf(item.getFat()));
                satFatEditText.setText(String.valueOf(item.getSaturatedFat()));
                transFatEditText.setText(String.valueOf(item.getTransFat()));
                sugarEditText.setText(String.valueOf(item.getSugar()));
                saltEditText.setText(String.valueOf(item.getSalt()));
                fruitVeggieCheckBox.setChecked(item.isFruitVeggie());

                isItemFromList = true;

            });
        }

        //Create and show the dialog
        AlertDialog alertDialog = builder.create();
        if(!foodItemStringList.isEmpty()) {
            ListView listView = alertDialog.getListView();
            listView.setDivider(new ColorDrawable(Color.GRAY)); // set color
            listView.setDividerHeight(1); // set height
        }

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }
}