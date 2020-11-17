package fi.haagahelia.android.fitnessapp.foodtracking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import fi.haagahelia.android.fitnessapp.Constants;
import fi.haagahelia.android.fitnessapp.R;

public class EditFoodActivity extends AppCompatActivity {

    private long foodDayId;
    private long foodItemId;

    private EditText nameEditText;
    private EditText gramsEditText;
    private  EditText caloriesEditText;
    private EditText carbsEditText;
    private EditText proteinEditText;
    private EditText fatEditText;
    private EditText satFatEditText;
    private EditText transFatEditText;
    private EditText sugarEditText;
    private EditText saltEditText;
    private CheckBox fruitVeggieCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

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

        //Get data from intent
        Intent intent = getIntent();

        foodItemId = intent.getLongExtra(Constants.FOOD_ITEM_ID, 0);
        foodDayId = intent.getLongExtra(Constants.FOOD_DAY_ID, 0);

        //Get the values from intent and show them in the fields if not equal to 0
        String foodName = intent.getStringExtra(Constants.FOOD_NAME);
        nameEditText.setText(foodName);

        getExtraAndShowIt(intent, Constants.FOOD_GRAMS, gramsEditText, true);
        getExtraAndShowIt(intent, Constants.FOOD_CALORIES, caloriesEditText, false);
        getExtraAndShowIt(intent, Constants.FOOD_CARBS, carbsEditText, true);
        getExtraAndShowIt(intent, Constants.FOOD_PROTEIN, proteinEditText, true);
        getExtraAndShowIt(intent, Constants.FOOD_FAT, fatEditText, true);
        getExtraAndShowIt(intent, Constants.FOOD_SAT_FAT, satFatEditText, true);
        getExtraAndShowIt(intent, Constants.FOOD_TRANS_FAT, transFatEditText, true);
        getExtraAndShowIt(intent, Constants.FOOD_SUGAR, sugarEditText, true);
        getExtraAndShowIt(intent, Constants.FOOD_SALT, saltEditText, true);

        boolean isFruitVeggie = intent.getBooleanExtra(Constants.FOOD_FRUIT_VEGGIE, false);
        fruitVeggieCheckBox.setChecked(isFruitVeggie);

        Button saveBtn = findViewById(R.id.button_save);

        saveBtn.setOnClickListener(view -> {

            boolean areRequiredValuesPresent = checkIfRequiredValuesArePresent();

            if(!areRequiredValuesPresent) {
                Toast.makeText(getApplicationContext(), getString(R.string.food_insert_error_msg), Toast.LENGTH_LONG).show();
            } else {
                editFood();
            }
        });

        Button deleteBtn = findViewById(R.id.button_delete);
        deleteBtn.setVisibility(View.VISIBLE);

        deleteBtn.setOnClickListener(view -> showConfirmationDialog());
    }

    //Check if all required values are provided by the user
    private boolean checkIfRequiredValuesArePresent() {
        boolean areValuesPresent = true;

        if (TextUtils.isEmpty(nameEditText.getText().toString().trim()) || TextUtils.isEmpty(gramsEditText.getText().toString().trim())) {
            areValuesPresent = false;
        }
        return areValuesPresent;
    }

    //Send the data with intent and finish the activity
    private void editFood() {
        Intent replyIntent = new Intent();

        replyIntent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_UPDATE);
        replyIntent.putExtra(Constants.FOOD_ITEM_ID, foodItemId);
        replyIntent.putExtra(Constants.FOOD_DAY_ID, foodDayId);
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

    //Getting value from intent and showing it in the input field
    private void getExtraAndShowIt(Intent intent, String property, EditText editText, boolean isDoubleExtra) {
        if (isDoubleExtra) {
            double extra = intent.getDoubleExtra(property, 0);

            if(extra != 0) {
                editText.setText(String.valueOf(extra));
            }
        } else {
            int extra = intent.getIntExtra(property, 0);

            if(extra != 0) {
                editText.setText(String.valueOf(extra));
            }
        }
    }

    //Method to show confirmation dialog when DELETE is clicked
    private void showConfirmationDialog() {
        //Create an AlertDialog, set message and respond to answers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_dialog_msg);
        builder.setMessage(R.string.delete_dialog_submsg);
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            if(foodDayId != 0) {
                Intent replyIntent = new Intent();
                replyIntent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_DELETE);
                replyIntent.putExtra(Constants.FOOD_DAY_ID, foodDayId);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
            //CANCEL clicked, so dismiss the dialog
            if(dialogInterface != null) {
                dialogInterface.dismiss();
            }
        });

        //Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

}