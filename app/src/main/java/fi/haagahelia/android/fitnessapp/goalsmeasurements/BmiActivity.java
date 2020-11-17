package fi.haagahelia.android.fitnessapp.goalsmeasurements;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import fi.haagahelia.android.fitnessapp.ChallengesActivity;
import fi.haagahelia.android.fitnessapp.GoalsMeasurementsActivity;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.foodtracking.FoodJournalActivity;

public class BmiActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navigationView;

    private TextView resultTextView;
    private EditText heightEditText;
    private EditText weightEditText;
    private TextView bmiTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        resultTextView = findViewById(R.id.result_textview);
        heightEditText = findViewById(R.id.input_height);
        weightEditText = findViewById(R.id.input_weight);
        bmiTitleTextView = findViewById(R.id.bmi_title_textview);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        Button calculateBtn = findViewById(R.id.calculate_btn);
        calculateBtn.setOnClickListener(view -> {
            calculateBmi();
        });
    }

    private void calculateBmi(){
        //Toast in case of invalid values
        Toast errorToast = Toast.makeText(this, R.string.insert_error_msg, Toast.LENGTH_LONG);
        String heightStr = heightEditText.getText().toString();
        String weightStr = weightEditText.getText().toString();

        if(heightStr.isEmpty() || weightStr.isEmpty()) {
            errorToast.show();
        } else {
            Double height = (Double.parseDouble(heightStr)) / 100;
            Double weight = Double.parseDouble(weightStr);

            if(height <= 0 || weight <= 0 ) {
                errorToast.show();
                return;
            }
            //BMI Formula weight / height in m squared
            double bmi = weight / (height * height);
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");

            bmiTitleTextView.setVisibility(View.VISIBLE);
            resultTextView.setText(decimalFormat.format(bmi));

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
                startActivity(new Intent(this, FoodJournalActivity.class));
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