package fi.haagahelia.android.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import fi.haagahelia.android.fitnessapp.goalsmeasurements.BmiActivity;
import fi.haagahelia.android.fitnessapp.goalsmeasurements.BmrTeeActivity;
import fi.haagahelia.android.fitnessapp.goalsmeasurements.GoalActivity;
import fi.haagahelia.android.fitnessapp.goalsmeasurements.WeightActivity;

public class GoalsMeasurementsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_measurements);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        ListView listView = findViewById(R.id.list_view);

        String[] listItems = getResources().getStringArray(R.array.goals_measurements_list_labels);

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems));

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i) {
                case 0:
                    startActivity(new Intent(GoalsMeasurementsActivity.this, GoalActivity.class));
                    return;
                case 1:
                    startActivity(new Intent(GoalsMeasurementsActivity.this, WeightActivity.class));
                    return;
                case 2:
                    startActivity(new Intent(GoalsMeasurementsActivity.this, BmiActivity.class));
                    return;
                case 3:
                    startActivity(new Intent(GoalsMeasurementsActivity.this, BmrTeeActivity.class));
                    return;
            }
        });

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