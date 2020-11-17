package fi.haagahelia.android.fitnessapp.foodtracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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
import fi.haagahelia.android.fitnessapp.db.FoodDay;
import fi.haagahelia.android.fitnessapp.db.FoodItem;
import fi.haagahelia.android.fitnessapp.db.FoodJournalItem;

public class FoodJournalActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FoodJournalListAdapter.OnItemListener {

    private BottomNavigationView navigationView;

    private FoodDayViewModel foodDayViewModel;

    private List<FoodJournalItem> foodItemsForDate;

    private TextView dateTextView;

    private View emptyView;

    private RecyclerView recyclerView;

    private View totalsContainer;

    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        setTitle(R.string.food_title);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        Calendar c = Calendar.getInstance();

        Intent intent = getIntent();
        if(intent.hasExtra(Constants.FOOD_JOURNAL_DATE)) {
            long dateExtra = intent.getLongExtra(Constants.FOOD_JOURNAL_DATE, 0);
            c.setTimeInMillis(dateExtra);
        }

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        date = c.getTime();

        emptyView = findViewById(R.id.food_journal_empty_view);
        totalsContainer = findViewById(R.id.totals_container);

        //Show the date in right format in the screen app bar
        String dateText = HelperMethods.getDateInStringFormat(c);

        dateTextView = findViewById(R.id.date_textView);
        dateTextView.setText(dateText);

        recyclerView = findViewById(R.id.food_journal_recyclerView);
        final FoodJournalListAdapter adapter = new FoodJournalListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add the divider to the list
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        foodDayViewModel = new ViewModelProvider(this).get(FoodDayViewModel.class);
        getFoodJournalItems(date, adapter);

        //Set up the arrow buttons for changing the dates
        Button prevBtn = findViewById(R.id.button_previous_day);
        prevBtn.setOnClickListener(view -> {
            c.add(Calendar.DAY_OF_MONTH, -1);
            String dateText1 = HelperMethods.getDateInStringFormat(c);
            dateTextView.setText(dateText1);
            date = c.getTime();
            getFoodJournalItems(date, adapter);

        });

        Button nextBtn = findViewById(R.id.button_next_day);
        nextBtn.setOnClickListener(view -> {
            c.add(Calendar.DAY_OF_MONTH, 1);
            String dateText12 = HelperMethods.getDateInStringFormat(c);
            dateTextView.setText(dateText12);
            date = c.getTime();
            getFoodJournalItems(date, adapter);

        });

        //FAB on click AddFood
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent1 = new Intent(FoodJournalActivity.this, AddFoodActivity.class);
            startActivityForResult(intent1, Constants.ADD_FOOD_REQUEST_CODE);
        });
    }

    //Method for hiding or showing empty view based if the items are present
    private void hideOrShowEmptyView (boolean areItemsPresent) {
        if(areItemsPresent) {
            //If items are present, hide the empty view and show the list and totals
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            totalsContainer.setVisibility(View.VISIBLE);
        } else {
            //Otherwise show empty view, and hide the list and totals
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            totalsContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void getFoodJournalItems(Date date, FoodJournalListAdapter adapter) {
        foodDayViewModel.getFoodJournalList().observe(this, foodJournalList -> {

            foodItemsForDate = new ArrayList<>();

            for (int i=0; i < foodJournalList.size(); i++) {

                FoodJournalItem item = foodJournalList.get(i);
                Date itemDate = item.getFoodDay().getDate();

                if(date.equals(itemDate)) {
                    foodItemsForDate.add(item);
                }
            }

            adapter.setFoodJournalItemList(foodItemsForDate);

            //if there is no items, show empty view
            if(adapter.getItemCount() == 0){
                hideOrShowEmptyView(false);
            } else {
                hideOrShowEmptyView(true);
                fillTheTotalTextView();
            }

        });
    }

    @Override
    public void onItemClick(int position) {
        FoodJournalItem item = foodItemsForDate.get(position);

        Intent intent = new Intent(FoodJournalActivity.this, EditFoodActivity.class);
        intent.putExtra(Constants.FOOD_ITEM_ID, item.getFoodItem().getId());
        intent.putExtra(Constants.FOOD_DAY_ID, item.getFoodDay().getId());
        intent.putExtra(Constants.FOOD_NAME, item.getFoodItem().getName());
        intent.putExtra(Constants.FOOD_CALORIES, item.getFoodItem().getCalories());
        intent.putExtra(Constants.FOOD_GRAMS, item.getFoodDay().getGrams());
        intent.putExtra(Constants.FOOD_CARBS, item.getFoodItem().getCarbs());
        intent.putExtra(Constants.FOOD_PROTEIN, item.getFoodItem().getProtein());
        intent.putExtra(Constants.FOOD_FAT, item.getFoodItem().getFat());
        intent.putExtra(Constants.FOOD_SAT_FAT, item.getFoodItem().getSaturatedFat());
        intent.putExtra(Constants.FOOD_TRANS_FAT, item.getFoodItem().getTransFat());
        intent.putExtra(Constants.FOOD_SUGAR, item.getFoodItem().getSugar());
        intent.putExtra(Constants.FOOD_SALT, item.getFoodItem().getSalt());
        intent.putExtra(Constants.FOOD_FRUIT_VEGGIE, item.getFoodItem().isFruitVeggie());

        startActivityForResult(intent, Constants.EDIT_FOOD_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String msg = "";

        if (requestCode == Constants.EDIT_FOOD_REQUEST_CODE && resultCode == RESULT_OK) {

            String operationType = data.getStringExtra(Constants.OPERATION_TYPE);

            //If operation type is delete, delete the row and set an appropriate message
            if(operationType.equals(Constants.OPERATION_DELETE)) {
                long id = data.getLongExtra(Constants.FOOD_DAY_ID, 0);
                foodDayViewModel.deleteById(id);
                msg = getResources().getString(R.string.delete_success_msg);

            } else {
                //Get the data from the intent
                long foodItemId = data.getLongExtra(Constants.FOOD_ITEM_ID, 0);
                long foodDayId = data.getLongExtra(Constants.FOOD_DAY_ID, 0);
                String foodName = data.getStringExtra(Constants.FOOD_NAME);
                double grams = data.getDoubleExtra(Constants.FOOD_GRAMS, 0);
                int calories = data.getIntExtra(Constants.FOOD_CALORIES, 0);
                double carbs = data.getDoubleExtra(Constants.FOOD_CARBS, 0);
                double protein = data.getDoubleExtra(Constants.FOOD_PROTEIN, 0);
                double fat = data.getDoubleExtra(Constants.FOOD_FAT, 0);
                double satFat = data.getDoubleExtra(Constants.FOOD_SAT_FAT, 0);
                double transFat = data.getDoubleExtra(Constants.FOOD_TRANS_FAT, 0);
                double sugar = data.getDoubleExtra(Constants.FOOD_SUGAR, 0);
                double salt =data.getDoubleExtra(Constants.FOOD_SALT, 0);
                boolean isFruitVeggie = data.getBooleanExtra(Constants.FOOD_FRUIT_VEGGIE, false);

                foodDayViewModel.updateFoodItemAndFoodDay(foodItemId, foodName, calories, carbs, protein, fat, satFat, transFat, sugar, salt, isFruitVeggie, foodDayId, grams);
                msg = getResources().getString(R.string.edit_success_msg);

            }

        } else if (requestCode == Constants.ADD_FOOD_REQUEST_CODE && resultCode == RESULT_OK) {

            //Check if the item is from the list, is so add only the food size for that day
            boolean isFoodFromList = data.getBooleanExtra(Constants.IS_FOOD_FROM_LIST, false);

            String foodName = data.getStringExtra(Constants.FOOD_NAME);
            double grams = data.getDoubleExtra(Constants.FOOD_GRAMS, 0);

            if(isFoodFromList) {
                long foodItemId = data.getLongExtra(Constants.FOOD_ITEM_ID, 0);
                FoodDay foodDay = new FoodDay(date, foodItemId, grams);
                foodDayViewModel.insert(foodDay);
            } else {
                int calories = data.getIntExtra(Constants.FOOD_CALORIES, 0);
                double carbs = data.getDoubleExtra(Constants.FOOD_CARBS, 0);
                double protein = data.getDoubleExtra(Constants.FOOD_PROTEIN, 0);
                double fat = data.getDoubleExtra(Constants.FOOD_FAT, 0);
                double satFat = data.getDoubleExtra(Constants.FOOD_SAT_FAT, 0);
                double transFat = data.getDoubleExtra(Constants.FOOD_TRANS_FAT, 0);
                double sugar = data.getDoubleExtra(Constants.FOOD_SUGAR, 0);
                double salt = data.getDoubleExtra(Constants.FOOD_SALT, 0);
                boolean isFruitVeggie = data.getBooleanExtra(Constants.FOOD_FRUIT_VEGGIE, false);

                FoodItem foodItem = new FoodItem(foodName, calories, carbs, protein, fat, satFat, transFat, sugar, salt, isFruitVeggie);

                foodDayViewModel.insertFoodItemAndFoodDay(foodItem, date, grams);
            }

            msg = getResources().getString(R.string.save_success_msg);

        } else {
            msg = getResources().getString(R.string.error_msg);
        }

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }

    //Fill the textviews with the counted totals of kcal, carbs, protein, fat
    private void fillTheTotalTextView() {
        //Get the specific text views for values
        TextView totalKcalTextView = findViewById(R.id.total_kcal_textView);
        TextView totalCarbsTextView = findViewById(R.id.total_carbs_textView);
        TextView totalProteinTextView = findViewById(R.id.total_protein_textView);
        TextView totalFatTextView = findViewById(R.id.total_fat_textView);

        int totalKcal = 0;
        double totalCarbs = 0;
        double totalProtein = 0;
        double totalFat = 0;

        for (int i = 0; i < foodItemsForDate.size(); i++) {
            FoodItem foodItem = foodItemsForDate.get(i).getFoodItem();
            double grams = foodItemsForDate.get(i).getFoodDay().getGrams();

            totalKcal += (foodItem.getCalories() * grams) / 100;
            totalCarbs += (foodItem.getCarbs() * grams) / 100;
            totalProtein += (foodItem.getProtein() * grams) / 100;
            totalFat += (foodItem.getFat() * grams) / 100;

        }

        totalKcalTextView.setText(String.valueOf(totalKcal));
        totalCarbsTextView.setText(String.valueOf(countTotalPercentage(totalCarbs, totalKcal, Constants.CARBS_KCAL_PER_GRAM)));
        totalProteinTextView.setText(String.valueOf(countTotalPercentage(totalProtein, totalKcal, Constants.PROTEIN_KCAL_PER_GRAM)));
        totalFatTextView.setText(String.valueOf(countTotalPercentage(totalFat, totalKcal, Constants.FAT_KCAL_PER_GRAM)));

    }

    //Count percentage of kcal in total kcal of property such as carbs, protein, fat
    private int countTotalPercentage(double totalValue, int totalKcal, int kcalPerGram) {
        int totalPercentage;

        double totalKcalFromProperty = totalValue * kcalPerGram;

        totalPercentage = (int) ((totalKcalFromProperty / totalKcal) * 100);

        return totalPercentage;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //update the state of the bottom navigation tab, to show the right item
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_food);
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