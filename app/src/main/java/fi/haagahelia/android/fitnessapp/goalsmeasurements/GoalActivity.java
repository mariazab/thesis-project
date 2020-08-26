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

public class GoalActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navigationView;

    private TextView dailyKcalInfoTextView;
    private EditText goalWeightEditText;
    private EditText dailyKcalEditText;
    private EditText weeklyMinsEditText;
    private Spinner goalTypeSpinner;

    private String goalTypeSelected;
    private int goalWeight;
    private int dailyKcal;
    private int weeklyMins;

    public static final String GOAL_TYPE_KEY = "Goal Type";
    public static final String GOAL_WEIGHT_KEY = "Goal Weight";
    public static final String DAILY_KCAL_KEY = "Daily Kcal";
    public static final String WEEKLY_MINS_KEY = "Weekly Mins";
    public static final String TEE_KEY = "TEE";
    private int teeResult;

    SharedPreferences preferences;
    public static final String GOAL_PREFERENCE_NAME = "Goal Preferences";


    String toastMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        dailyKcalInfoTextView = findViewById(R.id.daily_kcal_info);
        dailyKcalInfoTextView.setText(R.string.daily_kcal_no_tee_info);

        goalWeightEditText = findViewById(R.id.input_weight);
        dailyKcalEditText = findViewById(R.id.input_kcal);
        weeklyMinsEditText = findViewById(R.id.input_activity_mins);
        goalTypeSpinner = findViewById(R.id.goal_type_spinner);

        //Populate spinner list and set the goal type as the selected item
        String[] goalTypeItems = getResources().getStringArray(R.array.goal_types);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, goalTypeItems);
        goalTypeSpinner.setAdapter(adapter);
        goalTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                goalTypeSelected = adapter.getItem(i);
                }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Get preferences
        preferences = getSharedPreferences(GOAL_PREFERENCE_NAME, Context.MODE_PRIVATE);

        //check if the TEE preference exists, if so show it in the daily kcal field and show a message
        if(preferences.contains(TEE_KEY)) {
            teeResult = preferences.getInt(TEE_KEY, 0);
            dailyKcalEditText.setText(String.valueOf(teeResult));
            dailyKcalInfoTextView.setText(R.string.daily_kcal_based_on_tee_info);
        }

        //check if Goal Type preference exist and if so show the preferences in the fields
        if(preferences.contains(GOAL_TYPE_KEY)) {
            goalTypeSpinner.setSelection(adapter.getPosition(preferences.getString(GOAL_TYPE_KEY, "Lose weight")));
            goalWeightEditText.setText(String.valueOf(preferences.getInt(GOAL_WEIGHT_KEY, 0)));
            dailyKcalEditText.setText(String.valueOf(preferences.getInt(DAILY_KCAL_KEY, 0)));
            weeklyMinsEditText.setText(String.valueOf(preferences.getInt(WEEKLY_MINS_KEY, 0)));

            //If there is a TEE result, show it in the TextView
            //Otherwise, hide the TextView
            if(teeResult != 0) {
                String txt = getResources().getString(R.string.daily_kcal_tee_preferences_present);
                txt += teeResult + " ";
                txt += getResources().getString(R.string.kcal);
                dailyKcalInfoTextView.setText(txt);
            } else {
                dailyKcalInfoTextView.setVisibility(View.INVISIBLE);
            }
        }

        //On button click save the preferences and show appropriate message
        Button saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSaveSuccessful = savePreferences();
                if (isSaveSuccessful) {
                    toastMsg = getResources().getString(R.string.goal_preferences_saved_msg);
                } else {
                    toastMsg = getResources().getString(R.string.error_msg);
                }
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean savePreferences() {
        String goalWeightText = goalWeightEditText.getText().toString();
        String dailyKcalText = dailyKcalEditText.getText().toString();
        String weeklyMinsText = weeklyMinsEditText.getText().toString();

        //If the fields are empty, set an error message and return
        if(goalWeightText.isEmpty() || dailyKcalText.isEmpty() || weeklyMinsText.isEmpty()) {
            return false;
        }

        goalWeight = Integer.parseInt(goalWeightText);
        dailyKcal = Integer.parseInt(dailyKcalText);
        weeklyMins = Integer.parseInt(weeklyMinsText);

        //If the values are equal or less than 0, set an error message and return
        if(goalWeight <= 0 || dailyKcal <= 0 || weeklyMins <= 0) {
            return false;
        }

        //Otherwise, save the values
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(GOAL_TYPE_KEY, goalTypeSelected);
        editor.putInt(GOAL_WEIGHT_KEY, goalWeight);
        editor.putInt(DAILY_KCAL_KEY, dailyKcal);
        editor.putInt(WEEKLY_MINS_KEY, weeklyMins);
        editor.apply();

        return true;
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
            builder.setMessage(R.string.goal_info).setNeutralButton(R.string.info_dialog_btn, new DialogInterface.OnClickListener() {
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