package fi.haagahelia.android.fitnessapp.goalsmeasurements;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fi.haagahelia.android.fitnessapp.ChallengesActivity;
import fi.haagahelia.android.fitnessapp.FoodActivity;
import fi.haagahelia.android.fitnessapp.GoalsMeasurementsActivity;
import fi.haagahelia.android.fitnessapp.PhysicalActivity;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.db.Weight;
import fi.haagahelia.android.fitnessapp.db.WeightViewModel;

public class WeightActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navigationView;

    public static final int NEW_WEIGHT_ACTIVITY_REQUEST_CODE = 1;

    private WeightViewModel weightViewModel;

    //Graph
    GraphView graph;
    LineGraphSeries<DataPoint> lineSeries;
    PointsGraphSeries<DataPoint> pointSeries;

    //Min and max values for graph boundaries
    Date maxX;
    Date minX;
    double maxY;
    double minY;

    //TODO: lower than goal weight entry? WHAT THEN?
    //TODO: Empty view, for when there is no weight
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final WeightListAdapter adapter = new WeightListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Assign placeholder data to the graph, before the actual data is received by observer
        graph = findViewById(R.id.graph);
        lineSeries = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),
        });
        pointSeries = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),
        });
        graph.addSeries(lineSeries);
        graph.addSeries(pointSeries);

        //TODO: Check if that can be done differently, in layout for example
        //Set the style of graph
        pointSeries.setSize(12f);
        //Primary color
        lineSeries.setColor(Color.rgb(0, 188, 212));
        //Accent color
        pointSeries.setColor(Color.rgb(205, 220, 57));

        //After tapping the point in the graph, show a toast with details
        pointSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Calendar c = Calendar.getInstance();
                long l = (long) dataPoint.getX();
                c.setTimeInMillis(l);
                Date d = c.getTime();
                String date = c.get(Calendar.DAY_OF_MONTH) + ".";
                int month = c.get(Calendar.MONTH) + 1;

                if (month < 10) {
                    date += "0";
                }
                date += month + "." + c.get(Calendar.YEAR);

                Toast.makeText(getApplicationContext(), dataPoint.getY() + " kg on " + date, Toast.LENGTH_LONG).show();
            }
        });

        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);

        weightViewModel.getAllWeights().observe(this, weights -> {
            adapter.setWeights(weights);
            setGraphData();
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeightActivity.this, AddWeightActivity.class);
                startActivityForResult(intent, NEW_WEIGHT_ACTIVITY_REQUEST_CODE);
            }
        });

        //Get the goal weight from SharedPreferences and assign it to the minY, to set the boundary
        SharedPreferences preferences = getSharedPreferences(GoalActivity.GOAL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        minY = preferences.getInt(GoalActivity.GOAL_WEIGHT_KEY, 0);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String msg;

        if (requestCode == NEW_WEIGHT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            //Get the data from the intent
            double weightValue = Double.parseDouble(data.getStringExtra(AddWeightActivity.EXTRA_REPLY_WEIGHT));
            int year = data.getIntExtra(AddWeightActivity.EXTRA_REPLY_DATE_YEAR, 0);
            int month = data.getIntExtra(AddWeightActivity.EXTRA_REPLY_DATE_MONTH, 0);
            int day = data.getIntExtra(AddWeightActivity.EXTRA_REPLY_DATE_DAY, 0);

            //Insert new weight to the db
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            Date date = c.getTime();
            Weight weight = new Weight(date, weightValue);
            weightViewModel.insert(weight);

            msg = getResources().getString(R.string.weight_saved_msg);

        } else {
            msg = getResources().getString(R.string.weight_save_error_msg);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    //Reset the data in the graph series with the data from db
    private void setGraphData() {
        lineSeries.resetData(getDataPointsFromDb());
        pointSeries.resetData(getDataPointsFromDb());
        graph.onDataChanged(true,true);

        //Format the labels

        //Set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        //Set the number of horizontal labels to 2 to avoid overlapping
        graph.getGridLabelRenderer().setNumHorizontalLabels(2);

        //set Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(minY);
        graph.getViewport().setMaxY(maxY);

        //set X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(minX.getTime());
        graph.getViewport().setMaxX(maxX.getTime());

        //enable horizontal scrolling
        graph.getViewport().setScrollable(true);

        //disable rounding of labels, because of date labels
        graph.getGridLabelRenderer().setHumanRounding(false);
        //set vertical label width, so that whole labels fit in view
        graph.getGridLabelRenderer().setLabelVerticalWidth(90);
    }

    //TODO: highest and lowest values for the graph boundaries: not doing it programatically, but maybe to create a query in the database for getting directly those values.
    //Method for creating graph data from the database
    private DataPoint[] getDataPointsFromDb() {
        List<Weight> weightsInDb = weightViewModel.getAllWeights().getValue();

        DataPoint[] data = new DataPoint[weightsInDb.size()];

        double maxWeight = 0;

        //Date needs to have initial value for comparison purposes
        Calendar calendar = Calendar.getInstance();

        //Max date set to current date - 100 years, in order to be able to find the newest date in db
        calendar.add(Calendar.YEAR, -100);
        Date maxDate = calendar.getTime();

        //Min date set to current date + 100 years, in order to be able to find the oldest date in db
        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 100);
        Date minDate = calendar.getTime();

        if(!weightsInDb.isEmpty()) {
            for(int i = 0; i < weightsInDb.size(); i++) {
                Weight weight = weightsInDb.get(i);
                double weightValue = weight.getWeight();
                Date weightDate = weight.getDate();

                DataPoint d = new DataPoint(weightDate, weightValue);
                data[i] = d;

                //Find the max weight
                if(weightValue > maxWeight) {
                    maxWeight = weightValue;
                }

                //Find the max date
                if(weightDate.after(maxDate)) {
                    maxDate = weightDate;
                }

                //Find the min date
                if (weightDate.before(minDate)) {
                    minDate = weightDate;
                }
            }
        }
        maxY = maxWeight;
        maxX = maxDate;
        minX = minDate;

        return data;
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