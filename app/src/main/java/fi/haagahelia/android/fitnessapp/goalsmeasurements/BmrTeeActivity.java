package fi.haagahelia.android.fitnessapp.goalsmeasurements;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import fi.haagahelia.android.fitnessapp.ChallengesActivity;
import fi.haagahelia.android.fitnessapp.FoodActivity;
import fi.haagahelia.android.fitnessapp.GoalsMeasurementsActivity;
import fi.haagahelia.android.fitnessapp.PhysicalActivity;
import fi.haagahelia.android.fitnessapp.R;

public class BmrTeeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navigationView;

    private TextView bmrResultTextView;
    private TextView teeResultTextView;
    private EditText heightEditText;
    private EditText weightEditText;
    private EditText ageEditText;
    private Spinner activitySpinner;
    private String activitySelected;
    private String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmr_tee);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        bmrResultTextView = findViewById(R.id.bmr_result_textview);
        teeResultTextView = findViewById(R.id.tee_result_textview);
        heightEditText = findViewById(R.id.input_height);
        weightEditText = findViewById(R.id.input_weight);
        ageEditText = findViewById(R.id.input_age);
        activitySpinner = findViewById(R.id.activity_spinner);

        //Populate the spinner list
        String[] activityItems = getResources().getStringArray(R.array.activity_levels);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, activityItems);
        activitySpinner.setAdapter(adapter);

        //When level selected, assign to the activitySelected
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                activitySelected = adapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //On button click, calculate
        Button calculateBtn = findViewById(R.id.calculate_btn);
        calculateBtn.setOnClickListener(view -> {
            calculate();
        });
    }

    //Handle RadioButton click and assign the value to the variable
    public void onRadioBtnClicked(View view) {
        boolean isBtnChecked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.gender_male:
                if (isBtnChecked) {
                    gender = "male";
                }
                break;
            case R.id.gender_female:
                if (isBtnChecked) {
                    gender = "female";
                }
                break;
        }
    }


    //Calculate the BMR and TEE and show the results
    //BMR
    //For women: 655 + (9,6 × weight in kg) + (1,8 × height in cm) – (4,7 × age in years)
    //For men: 66 + (13,7 × weight in kg) + (5 × height in cm) – (6,8 × age in years)
    //TEE
    //BMR * activity
    //Light = 1.55, mod = 1.75 vig= 2.2
    //TODO: Change Toast to popup???
    private void calculate() {
        //Toast in case of invalid values
        Toast errorToast = Toast.makeText(this, R.string.error_msg, Toast.LENGTH_LONG);

        String heightStr = heightEditText.getText().toString();
        String weightStr = weightEditText.getText().toString();
        String ageStr = ageEditText.getText().toString();

        //If values are empty, show the error toast and do nothing
        //Otherwise proceed
        if (heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty() || gender.isEmpty()) {
            errorToast.show();
        } else {
            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);
            int age = Integer.parseInt(ageStr);

            //If values are equal or less than 0, show error toast and return
            if(height <= 0 || weight <= 0 || age <= 0) {
                errorToast.show();
                return;
            }

            int bmr = 0;
            int tee;
            double activityMultiplier = 1;
            switch (activitySelected) {
                case "Light":
                    activityMultiplier = 1.55;
                    break;
                case "Moderate":
                    activityMultiplier = 1.75;
                    break;
                case "Vigorous":
                    activityMultiplier = 2.2;
                    break;
            }

            if(gender == "female") {
                bmr = (int) (655 + (9.6 * weight) + (1.8 * height) - (4.7 * age));
            } else if(gender == "male") {
                bmr = (int) (66 + (13.7 * weight) + (5 * height) - (6.8 * age));
            }

            tee = (int) (bmr * activityMultiplier);

            String kcal = getResources().getString(R.string.kcal);

            bmrResultTextView.setText(bmr + " " + kcal);
            teeResultTextView.setText(tee + " " + kcal);

            //Save to preferences
            saveToPreferences(tee);
        }
    }

    //Method for saving the TEE value to the SharedPreferences
    private void saveToPreferences(int tee) {
        SharedPreferences goalPreferences = getSharedPreferences(GoalActivity.GOAL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = goalPreferences.edit();
        editor.putInt(GoalActivity.TEE_KEY, tee);
        editor.apply();
    }

    //Create the menu with the action button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Handle the button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.info_btn) {
            InfoDialogFragment fragment = new InfoDialogFragment();
            fragment.show(getSupportFragmentManager(), "info dialog");

        }
        return super.onOptionsItemSelected(item);
    }

    //Information Dialog Fragment creation
    public static class InfoDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.bmr_tee_info).setNeutralButton(R.string.info_dialog_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            return builder.create();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //update the state of the bottom navigation tab, to show the right item
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_goals_measurements);
        item.setChecked(true);
    }

    // Remove the default activity transition, for a smoother bottom navigation tab experience
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    //Start the selected activity after choosing a menu item in bottom navigation view
    //Delay for a nicer transition of the bottom nav
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationView.postDelayed(() -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_food) {
                startActivity(new Intent(this, FoodActivity.class));
            } else if (itemId == R.id.nav_physical) {
                startActivity(new Intent(this, PhysicalActivity.class));
            } else if (itemId == R.id.nav_challenges) {
                startActivity(new Intent(this, ChallengesActivity.class));
            } else if (itemId == R.id.nav_goals_measurements) {
                startActivity(new Intent(this, GoalsMeasurementsActivity.class));
            }
            finish();
        }, 100);
        return true;
    }
}