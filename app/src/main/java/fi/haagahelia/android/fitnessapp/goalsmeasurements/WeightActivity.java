package fi.haagahelia.android.fitnessapp.goalsmeasurements;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fi.haagahelia.android.fitnessapp.ChallengesActivity;
import fi.haagahelia.android.fitnessapp.Constants;
import fi.haagahelia.android.fitnessapp.GoalsMeasurementsActivity;
import fi.haagahelia.android.fitnessapp.HelperMethods;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.db.Weight;
import fi.haagahelia.android.fitnessapp.foodtracking.FoodJournalActivity;

public class WeightActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, WeightListAdapter.OnItemListener {

    private BottomNavigationView navigationView;

    private WeightViewModel weightViewModel;

    //Graph
    private GraphView graph;
    private LineGraphSeries<DataPoint> lineSeries;
    private PointsGraphSeries<DataPoint> pointSeries;

    //Goal type
    private String goalType;
    private boolean isGoalSet;
    private double goalWeight;

    //Min and max values for graph boundaries
    private Date maxX;
    private Date minX;
    private double maxY;
    private double minY;

    private List<Weight> weightList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        //Variable for the empty view
        View emptyView = findViewById(R.id.empty_view);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final WeightListAdapter adapter = new WeightListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add the divider to the list
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

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

        //Set the style of graph
        pointSeries.setSize(12f);
        //Primary color
        lineSeries.setColor(Color.rgb(0, 188, 212));
        //Accent color
        pointSeries.setColor(Color.rgb(205, 220, 57));

        Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        //After tapping the point in the graph, show a toast with details
        pointSeries.setOnDataPointTapListener((series, dataPoint) -> {
            Calendar c = Calendar.getInstance();
            long l = (long) dataPoint.getX();
            c.setTimeInMillis(l);
            String date = HelperMethods.getDateInStringFormat(c);

            toast.setText(dataPoint.getY() + " kg on " + date);
            toast.show();

        });

        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);

        weightViewModel.getAllWeights().observe(this, weights -> {
            weightList = weights;
            adapter.setWeights(weights);

            //Reset the data in the graph series with the data from db
            lineSeries.resetData(getDataPointsFromDb());
            pointSeries.resetData(getDataPointsFromDb());
            graph.onDataChanged(true,true);

            //Format the graph
            formatGraph();

            //If there is no items in the adapter, show the empty view and hide the graph
            if(adapter.getItemCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                graph.setVisibility(View.INVISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
                graph.setVisibility(View.VISIBLE);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(WeightActivity.this, AddWeightActivity.class);
            startActivityForResult(intent, Constants.NEW_WEIGHT_ACTIVITY_REQUEST_CODE);
        });

        //Get the goal type from SharedPreferences and based on it assign the goal weight to minY or maxY, to set the boundary
        SharedPreferences preferences = getSharedPreferences(Constants.GOAL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if(preferences.contains(Constants.GOAL_TYPE_KEY)) {
            isGoalSet = true;
            goalType = preferences.getString(Constants.GOAL_TYPE_KEY, Constants.LOSE_WEIGHT_GOAL);
            if(TextUtils.equals(goalType, Constants.LOSE_WEIGHT_GOAL)) {
                goalWeight = preferences.getFloat(Constants.GOAL_WEIGHT_KEY, 0);
                minY = goalWeight;
            } else if (TextUtils.equals(goalType, Constants.GAIN_WEIGHT_GOAL)) {
                goalWeight = preferences.getFloat(Constants.GOAL_WEIGHT_KEY, 0);
                maxY = goalWeight;
            }
        } else {
            isGoalSet = false;

        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String msg;

        if (requestCode == Constants.NEW_WEIGHT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            String operationType = data.getStringExtra(Constants.OPERATION_TYPE);

            //If operation type is delete, delete the row and set an appropriate message
            if(operationType.equals(Constants.OPERATION_DELETE)) {
                long id = data.getLongExtra(Constants.WEIGHT_ID, 0);
                weightViewModel.deleteById(id);
                msg = getString(R.string.delete_success_msg);
            } else {
                //Get the data from the intent
                double weightValue = Double.parseDouble(data.getStringExtra(Constants.WEIGHT));
                int year = data.getIntExtra(Constants.DATE_YEAR, 0);
                int month = data.getIntExtra(Constants.DATE_MONTH, 0);
                int day = data.getIntExtra(Constants.DATE_DAY, 0);

                //Create the date object from data
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                Date date = c.getTime();

                //Check if updating existing row,
                //Otherwise, insert new row
                if(operationType.equals(Constants.OPERATION_UPDATE)) {
                    long id = data.getLongExtra(Constants.WEIGHT_ID, 0);
                    weightViewModel.update(id, date, weightValue);
                    msg = getString(R.string.edit_success_msg);
                } else {
                    Weight weight = new Weight(date, weightValue);
                    weightViewModel.insert(weight);
                    msg = getResources().getString(R.string.save_success_msg);
                }
            }

        }
        else {
            msg = getResources().getString(R.string.error_msg);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onItemClick(int position) {
        List<Weight> weights = weightList;
        Weight weight = weights.get(position);

        Intent intent = new Intent(WeightActivity.this, AddWeightActivity.class);
        intent.putExtra(Constants.WEIGHT_ID, weight.getId());
        intent.putExtra(Constants.WEIGHT, weight.getWeight());

        Calendar c = Calendar.getInstance();
        Date date = weight.getDate();
        c.setTime(date);
        intent.putExtra(Constants.DATE_YEAR, c.get(Calendar.YEAR));
        intent.putExtra(Constants.DATE_MONTH, c.get(Calendar.MONTH));
        intent.putExtra(Constants.DATE_DAY, c.get(Calendar.DAY_OF_MONTH));

        startActivityForResult(intent, Constants.NEW_WEIGHT_ACTIVITY_REQUEST_CODE);
    }

    //Format the labels and boundaries
    private void formatGraph() {
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

        //disable rounding of labels, because of date labels
        graph.getGridLabelRenderer().setHumanRounding(false);
        //set vertical label width, so that whole labels fit in view
        graph.getGridLabelRenderer().setLabelVerticalWidth(90);
    }

    //Method for creating graph data from the database
    private DataPoint[] getDataPointsFromDb() {
        List<Weight> weightsInDb = weightList;

        DataPoint[] data = new DataPoint[weightsInDb.size()];

        double maxWeight = 0;
        double minWeight = 1000;

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
            for (int i = 0; i < weightsInDb.size(); i++) {
                Weight weight = weightsInDb.get(i);
                double weightValue = weight.getWeight();
                Date weightDate = weight.getDate();

                DataPoint d = new DataPoint(weightDate, weightValue);
                data[i] = d;

                //Find the max weight
                if (weightValue > maxWeight) {
                    maxWeight = weightValue;
                }

                //Find the min weight
                if (weightValue < minWeight) {
                    minWeight = weightValue;
                }

                //Find the max date
                if (weightDate.after(maxDate)) {
                    maxDate = weightDate;
                }

                //Find the min date
                if (weightDate.before(minDate)) {
                    minDate = weightDate;
                }
            }
        }

        //Based on the goalType, set the appropriate values as the boundaries
        if(TextUtils.equals(goalType, "Lose weight")) {
            maxY = maxWeight;
            //Check if min weight is not smaller than goal weight
            if(minWeight < goalWeight) {
                //If it is set the minY boundary to the minWeight
                minY = minWeight;
            }
        } else if (TextUtils.equals(goalType, "Gain weight")) {
            minY = minWeight;
            //Check if the max weight is not bigger than goal weight
            if(maxWeight > goalWeight) {
                //If so, set the maxY boundary to the maxweight
                maxY = maxWeight;
            }
        } else if (TextUtils.equals(goalType, "Maintain weight") || !isGoalSet) {
            maxY = maxWeight;
            minY = minWeight;
        }

        //Setting the date boundaries
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