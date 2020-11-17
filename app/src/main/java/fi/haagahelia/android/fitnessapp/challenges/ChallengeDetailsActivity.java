package fi.haagahelia.android.fitnessapp.challenges;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fi.haagahelia.android.fitnessapp.ChallengesActivity;
import fi.haagahelia.android.fitnessapp.Constants;
import fi.haagahelia.android.fitnessapp.GoalsMeasurementsActivity;
import fi.haagahelia.android.fitnessapp.HelperMethods;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.activitytracking.AddActivityActivity;
import fi.haagahelia.android.fitnessapp.activitytracking.PhysicalActivityListAdapter;
import fi.haagahelia.android.fitnessapp.activitytracking.PhysicalActivityViewModel;
import fi.haagahelia.android.fitnessapp.activitytracking.WeekViewModel;
import fi.haagahelia.android.fitnessapp.db.ChallengeDate;
import fi.haagahelia.android.fitnessapp.db.FoodDay;
import fi.haagahelia.android.fitnessapp.db.FoodItem;
import fi.haagahelia.android.fitnessapp.db.FoodJournalItem;
import fi.haagahelia.android.fitnessapp.db.PhysicalActivity;
import fi.haagahelia.android.fitnessapp.db.Week;
import fi.haagahelia.android.fitnessapp.foodtracking.AddFoodActivity;
import fi.haagahelia.android.fitnessapp.foodtracking.FoodDayViewModel;
import fi.haagahelia.android.fitnessapp.foodtracking.FoodJournalActivity;

public class ChallengeDetailsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, PhysicalActivityListAdapter.OnItemListener {

    private BottomNavigationView navigationView;

    private ChallengeViewModel challengeViewModel;
    private ChallengeDateViewModel challengeDateViewModel;
    private FoodDayViewModel foodDayViewModel;
    private PhysicalActivityViewModel physicalActivityViewModel;
    private WeekViewModel weekViewModel;

    private TextView completedDaysTextView;
    private TextView completionTitle;

    private long challengeId;
    private String challengeName;
    private String challengeDescription;
    private int challengeFrequency;
    private int challengeState;
    private int challengeType;
    private int challengeInfoText;

    private int completion;

    private Date currentDate;
    private Week shownWeek;

    private List<PhysicalActivity> shownActivities;
    private ScrollView activityListView;
    private View activityEmptyView;

    private Calendar calendar;
    private ColorDrawable accentColor;
    private CaldroidFragment caldroidFragment;

    private List<ChallengeDate> dateList;
    private int challengeTotalValue = 0;
    private TextView totalValueTextView;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_details);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        currentDate = calendar.getTime();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        completedDaysTextView = findViewById(R.id.completed_textView);

        challengeViewModel = new ViewModelProvider(this).get(ChallengeViewModel.class);
        challengeDateViewModel = new ViewModelProvider(this).get(ChallengeDateViewModel.class);
        foodDayViewModel = new ViewModelProvider(this).get(FoodDayViewModel.class);

        Intent intent = getIntent();

        challengeId = intent.getLongExtra(Constants.CHALLENGE_ID, 0);
        challengeName = intent.getStringExtra(Constants.CHALLENGE_NAME);

        challengeDescription = intent.getStringExtra(Constants.CHALLENGE_DESCRIPTION);
        challengeFrequency = intent.getIntExtra(Constants.CHALLENGE_FREQUENCY, 0);
        challengeState = intent.getIntExtra(Constants.CHALLENGE_STATE, 0);
        completion = intent.getIntExtra(Constants.CHALLENGE_COMPLETION, 0);
        challengeType = intent.getIntExtra(Constants.CHALLENGE_TYPE, 0);
        challengeInfoText = intent.getIntExtra(Constants.CHALLENGE_INFO_ID, 0);

        setTitle(challengeName);

        //Modify the CalendarView to CaldroidFragment
        modifyCalendarView();

        //Change completed days to weeks depending on frequency
        completionTitle = findViewById(R.id.completion_title_textView);
        setCompletionTitle();
        completedDaysTextView.setText(String.valueOf(completion));

        //Show the the fields for tracking the challenge, according to the challenge type
        Button minusBtn = findViewById(R.id.minus_btn);
        TextView totalTitleTextView = findViewById(R.id.tracking_total_textView);
        totalValueTextView = findViewById(R.id.tracking_value_textView);
        Button plusBtn = findViewById(R.id.plus_btn);

        if(challengeType == 0) {

            //Basic challenge hide all the tracking fields
            minusBtn.setVisibility(View.GONE);
            totalTitleTextView.setVisibility(View.GONE);
            totalValueTextView.setVisibility(View.GONE);
            plusBtn.setVisibility(View.GONE);

        } else if (challengeType == 1) {

            //Food challenge, hide only the minus btn
            minusBtn.setVisibility(View.GONE);
            totalTitleTextView.setText(R.string.total_percent);

            if (challengeName.contains(Constants.CALORIE_KEY)) {
                totalTitleTextView.setText(R.string.total_kcal);
            } else if (challengeName.contains(Constants.FRUIT_VEGGIE_KEY) || challengeName.contains(Constants.SALT_KEY)) {
                totalTitleTextView.setText(R.string.total_grams);
            }

            //Get the challenge total
            trackFoodChallengeTotal();

        } else if(challengeType == 2) {

            //If activity challenge, set the layout accordingly
            modifyLayoutForActivityChallenge(minusBtn, totalTitleTextView);

        } else if (challengeType == 3) {
            //Water challenge
            totalTitleTextView.setText(R.string.cups);
            //Create or retrieve existing preference with water tracking saved
            //Check if the date saved in the preferences is the same or different (then reset)
            preferences = getSharedPreferences(Constants.CHALLENGE_TRACKING_PREFERENCE, Context.MODE_PRIVATE);

            if(preferences.contains(Constants.WATER_KEY)) {
                long savedDate = preferences.getLong(Constants.WATER_DATE_KEY, 0);

                if(savedDate == currentDate.getTime()) {
                   challengeTotalValue = preferences.getInt(Constants.WATER_KEY, 0);
                }
            }
            //Get the challenge total
            totalValueTextView.setText(String.valueOf(challengeTotalValue));
        }

        minusBtn.setOnClickListener(view -> {
            if(challengeTotalValue > 0) {
                challengeTotalValue -= 1;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(Constants.WATER_KEY, challengeTotalValue);
                editor.putLong(Constants.WATER_DATE_KEY, currentDate.getTime());
                editor.apply();
                totalValueTextView.setText(String.valueOf(challengeTotalValue));
            }
        });

        plusBtn.setOnClickListener(view -> {
            if(challengeType == 3) {
                //Water challenge, add water
                challengeTotalValue += 1;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(Constants.WATER_KEY, challengeTotalValue);
                editor.putLong(Constants.WATER_DATE_KEY, currentDate.getTime());
                editor.apply();
                totalValueTextView.setText(String.valueOf(challengeTotalValue));

            } else if (challengeType == 1){
                //Food challenge, add food item
                Intent addFoodIntent = new Intent(ChallengeDetailsActivity.this, AddFoodActivity.class);
                startActivityForResult(addFoodIntent, Constants.ADD_FOOD_REQUEST_CODE);

            } else if (challengeType == 2) {
                //Activity challenge, add activity
                Intent addActivityIntent = new Intent(ChallengeDetailsActivity.this, AddActivityActivity.class);
                startActivityForResult(addActivityIntent, Constants.ADD_ACTIVITY_REQUEST_CODE);

            }
        });

        //Get the completed days for the challenge
        challengeDateViewModel.getAllChallengeDates().observe(this, challengeDates -> {

            dateList = new ArrayList<>();

            for (int i = 0; i < challengeDates.size(); i ++) {

                Long id = challengeId;
                Long challengeDateId = challengeDates.get(i).getChallengeId();

                if((id.equals(challengeDateId))) {
                    dateList.add(challengeDates.get(i));
                }
            }

            if(dateList.isEmpty()) {
                caldroidFragment.clearSelectedDates();
                caldroidFragment.refreshView();
            }
            markDates(dateList);
        });
    }

    //Method to set up the right completion title - completed weeks/days
    private void setCompletionTitle() {
        if(challengeFrequency > 0) {
            completionTitle.setText(getResources().getString(R.string.completed_weeks));
        } else {
            completionTitle.setText(getResources().getString(R.string.completed_days));
        }
    }

    //Set up the calendar
    private void modifyCalendarView() {
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CaldroidTheme);
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();

        accentColor = new ColorDrawable(Color.rgb(205, 220, 57));

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {

                boolean dateMarked = checkIfDateMarked(date);
                int offset = 0;

                if(dateMarked) {
                    caldroidFragment.clearBackgroundDrawableForDate(date);
                    challengeDateViewModel.deleteById(challengeId, date);

                } else {
                    challengeDateViewModel.insert(new ChallengeDate(challengeId, date));

                }

                //If frequency is daily
                if(challengeFrequency == 0) {
                    offset = getOffsetForCompletionOfDailyChallenge(date, dateMarked);
                } else {
                    //If frequency is weekly
                    offset = getOffsetForCompletionOfWeeklyChallenge(date, dateMarked);
                }

                completion += offset;
                completedDaysTextView.setText(String.valueOf(completion));
                challengeViewModel.adjustCompletion(challengeId, offset);

            }

            @Override
            public void onLongClickDate(Date date, View view) {
                //On long click, show the food journal activity for that date
                Intent foodJournalIntent = new Intent(ChallengeDetailsActivity.this, FoodJournalActivity.class);
                foodJournalIntent.putExtra(Constants.FOOD_JOURNAL_DATE, date.getTime());
                startActivity(foodJournalIntent);
            }

        };

        caldroidFragment.setCaldroidListener(listener);
    }

    //Method for creating the right layout for activity challenge
    private void modifyLayoutForActivityChallenge(Button minusBtn, TextView totalsTextView) {
        CalendarView calendarView = findViewById(R.id.calendar);
        calendarView.setVisibility(View.GONE);
        caldroidFragment.dismiss();

        totalsTextView.setText(R.string.total_minutes);
        totalValueTextView.setText(String.valueOf(challengeTotalValue));

        RelativeLayout weekIndicatorView = findViewById(R.id.week_indicator_container);
        weekIndicatorView.setVisibility(View.VISIBLE);

        minusBtn.setVisibility(View.GONE);

        activityListView = findViewById(R.id.activity_list_scrollview);
        activityListView.setVisibility(View.VISIBLE);

        activityEmptyView = findViewById(R.id.activity_empty_view);

        //Physical Activities List
        RecyclerView recyclerView = findViewById(R.id.activity_recyclerView);
        final PhysicalActivityListAdapter activityListAdapter = new PhysicalActivityListAdapter(this, this);
        recyclerView.setAdapter(activityListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add the divider to the list
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        physicalActivityViewModel = new ViewModelProvider(this).get(PhysicalActivityViewModel.class);

        //Show the dates of this week
        weekViewModel = new ViewModelProvider(this).get(WeekViewModel.class);
        weekViewModel.getAllWeeks().observe(this, weeks -> {

            boolean weekFound = false;

            for(int i = 0; i < weeks.size(); i++) {
                calendar.setTime(currentDate);

                if(calendar.get(Calendar.WEEK_OF_YEAR) == weeks.get(i).getWeekNumber() && calendar.get(Calendar.YEAR) == weeks.get(i).getWeekYear()) {

                    shownWeek = weeks.get(i);
                    weekFound = true;
                    break;

                }
            }

            //If week has not been found, get the last week in the db
            if(!weekFound && weeks.size() != 0) {
                shownWeek = weeks.get(weeks.size() - 1);
            }
            if(shownWeek != null) {
                //Show the week dates
                showWeekDates();
                //Get the activities for that week
                getPhysicalActivitiesForWeek(shownWeek, activityListAdapter);
                trackActivityChallengeTotal(shownWeek);
            } else {
                //There is no week, so show empty view
                hideOrShowEmptyView(false);
            }

        });

        //On arrow buttons click change the week
        Button nextBtn = findViewById(R.id.button_next_week);
        nextBtn.setOnClickListener(view -> {

            if(shownWeek != null) {
                //shownWeekId += 1;
                shownWeek = getWeekByOffsetFromCurrentWeek(1);
                //Show the week dates
                showWeekDates();
                //Get the activities for that week
                getPhysicalActivitiesForWeek(shownWeek, activityListAdapter);
                trackActivityChallengeTotal(shownWeek);
            }

        });

        Button prevBtn = findViewById(R.id.button_previous_week);
        prevBtn.setOnClickListener(view -> {

            if(shownWeek != null) {
                //shownWeekId -= 1;
                shownWeek = getWeekByOffsetFromCurrentWeek(-1);
                //Show the week dates
                showWeekDates();
                //Get the activities for that week
                getPhysicalActivitiesForWeek(shownWeek, activityListAdapter);
                trackActivityChallengeTotal(shownWeek);
            }

        });
    }

    //Method for getting the week by offset from currently shown week
    private Week getWeekByOffsetFromCurrentWeek(int offset) {

        Week week = shownWeek;

        List<Week> weeks = weekViewModel.getAllWeeks().getValue();

        for (int i = 0; i < weeks.size(); i++){

            if(weeks.get(i).getWeekNumber() == shownWeek.getWeekNumber() && weeks.get(i).getWeekYear() == shownWeek.getWeekYear()) {

                //If searching for previous week and the index of the current week is 0 -> return current week
                if((offset < 0) && (i == 0)) {
                    return week;
                } else if ((offset > 0) && (i == (weeks.size() - 1))) {
                    //If searching for next week and the index of the current week is the last -> return current week
                    return week;
                } else {
                    //Otherwise, find next/previous week
                    week = weeks.get(i + offset);
                }
            }
        }

        return week;
    }

    //Method for showing the week dates "Week 16.11 - 22.11.2020"
    private void showWeekDates() {

        TextView weekDatesTextView = findViewById(R.id.week_dates_textView);

        //Set week start date
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, shownWeek.getWeekNumber());
        String weekStartDate = HelperMethods.getDateInStringFormat(calendar);
        //Cut the year from the start date
        weekStartDate = weekStartDate.substring(0,5);

        //Set week end date
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, shownWeek.getWeekNumber());
        String weekEndDate = HelperMethods.getDateInStringFormat(calendar);

        String weekDatesText = weekStartDate + " - " + weekEndDate;

        weekDatesTextView.setText(weekDatesText);

    }

    //Method for getting the activities for the week that is displayed
    private void getPhysicalActivitiesForWeek(Week week, PhysicalActivityListAdapter activityListAdapter) {

        physicalActivityViewModel.getAllPhysicalActivities().observe(this, physicalActivities -> {

            shownActivities = new ArrayList<>();

            for (int i = 0; i < physicalActivities.size(); i++) {
                PhysicalActivity physicalActivity = physicalActivities.get(i);

                calendar.setTime(physicalActivity.getDate());
                int physicalActivityWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR);

                if(physicalActivityWeekNumber == week.getWeekNumber()) {
                    shownActivities.add(physicalActivity);
                }
            }

            activityListAdapter.setPhysicalActivities(shownActivities);

            if(activityListAdapter.getItemCount() == 0){
                hideOrShowEmptyView(false);
            } else {
                hideOrShowEmptyView(true);
            }

        });

    }

    //Method for hiding or showing activity empty view based it the items are present
    private void hideOrShowEmptyView (boolean areItemsPresent) {
        if(areItemsPresent) {
            //If items are present, hide the empty view and show the list and totals
            activityEmptyView.setVisibility(View.GONE);
            activityListView.setVisibility(View.VISIBLE);
        } else {
            //Otherwise show empty view, and hide the list and totals
            activityEmptyView.setVisibility(View.VISIBLE);
            activityListView.setVisibility(View.GONE);
        }
    }

    private void trackActivityChallengeTotal(Week week) {

        int totalHours = week.getTotalHours();
        int totalMinutes = week.getTotalMinutes();
        int totalSeconds = week.getTotalSeconds();

        //Convert the hours and seconds to mins and add to total
        challengeTotalValue = (totalHours * 60) + totalMinutes + (totalSeconds / 60);

        totalValueTextView.setText(String.valueOf(challengeTotalValue));

    }

    //Activity list onItemClick method
    @Override
    public void onItemClick(int position) {

        PhysicalActivity activity = shownActivities.get(position);

        Intent intent = new Intent(ChallengeDetailsActivity.this, AddActivityActivity.class);
        intent.putExtra(Constants.ACTIVITY_ID, activity.getId());
        intent.putExtra(Constants.ACTIVITY_NAME, activity.getName());

        //Duration
        int hours = activity.getHours();
        int minutes = activity.getMinutes();
        int seconds = activity.getSeconds();

        String duration = HelperMethods.showDurationAsString(hours, minutes, seconds);

        intent.putExtra(Constants.ACTIVITY_HOURS, hours);
        intent.putExtra(Constants.ACTIVITY_MINUTES, minutes);
        intent.putExtra(Constants.ACTIVITY_SECONDS, seconds);
        intent.putExtra(Constants.ACTIVITY_DURATION, duration);
        intent.putExtra(Constants.ACTIVITY_INTENSITY, activity.getIntensity());

        Date date = activity.getDate();

        calendar.setTime(date);
        intent.putExtra(Constants.DATE_YEAR, calendar.get(Calendar.YEAR));
        intent.putExtra(Constants.DATE_MONTH, calendar.get(Calendar.MONTH));
        intent.putExtra(Constants.DATE_DAY, calendar.get(Calendar.DAY_OF_MONTH));

        startActivityForResult(intent, Constants.EDIT_ACTIVITY_REQUEST_CODE);
    }

    private void trackFoodChallengeTotal() {
        //Getting sum of what is being tracked by this challenge
        String challengeTrackingProperty = "";
        if(challengeName.contains(Constants.CALORIE_KEY)) {
            challengeTrackingProperty = Constants.CALORIE_KEY;
        } else if (challengeName.contains(Constants.CARBS_KEY)) {
            challengeTrackingProperty = Constants.CARBS_KEY;
        } else if (challengeName.contains(Constants.PROTEIN_KEY)) {
            challengeTrackingProperty = Constants.PROTEIN_KEY;
        } else if (challengeName.contains(Constants.FAT_KEY)) {

            if (challengeName.contains(Constants.SAT_FAT_KEY)) {
                challengeTrackingProperty = Constants.SAT_FAT_KEY;
            } else if (challengeName.contains(Constants.TRANS_FAT_KEY)) {
                challengeTrackingProperty = Constants.TRANS_FAT_KEY;
            } else {
                challengeTrackingProperty = Constants.FAT_KEY;
            }

        } else if (challengeName.contains(Constants.SUGAR_KEY)) {
            challengeTrackingProperty = Constants.SUGAR_KEY;
        } else if (challengeName.contains(Constants.SALT_KEY)) {
            challengeTrackingProperty = Constants.SALT_KEY;
        } else if (challengeName.contains(Constants.FRUIT_VEGGIE_KEY)) {
            challengeTrackingProperty = Constants.FRUIT_VEGGIE_KEY;
        }

        String finalProperty = challengeTrackingProperty;
        foodDayViewModel.getFoodJournalList().observe(this, foodJournalItems -> {

            List<FoodJournalItem> items = new ArrayList<>();

            for (int i=0; i < foodJournalItems.size(); i++) {

                FoodJournalItem item = foodJournalItems.get(i);
                Date itemDate = item.getFoodDay().getDate();

                if(currentDate.equals(itemDate)) {
                    items.add(item);
                }

            }

            int challengeTotal = getFoodChallengeTotal(items, finalProperty);
            totalValueTextView.setText(String.valueOf(challengeTotal));
        });
    }

    //Based on the operation type, delete or update the challenge
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String msg;

        if (requestCode == Constants.EDIT_CHALLENGE_REQUEST_CODE && resultCode == RESULT_OK) {

            long editChallengeId = data.getLongExtra(Constants.CHALLENGE_ID, 0);

            String editChallengeName = data.getStringExtra(Constants.CHALLENGE_NAME);
            String editChallengeDescription = data.getStringExtra(Constants.CHALLENGE_DESCRIPTION);
            int editChallengeFrequency = data.getIntExtra(Constants.CHALLENGE_FREQUENCY, 0);

            //Check if the description is empty, if so, leave the old value
            if(TextUtils.isEmpty(editChallengeDescription)) {
                editChallengeDescription = this.challengeDescription;
            }

            if(!TextUtils.isEmpty(editChallengeName)) {
                //Update the challenge
                challengeViewModel.update(editChallengeId, editChallengeName, editChallengeDescription, editChallengeFrequency, challengeState);

                //Update the values of the global challenge variables
                challengeName = editChallengeName;
                challengeDescription = editChallengeDescription;
                challengeFrequency = editChallengeFrequency;

                //Update the app bar title
                setTitle(challengeName);

                //Update the completion title, in case the frequency change between daily and weekly
                setCompletionTitle();

                //Reset the completed days/weeks based on new frequency
                recalculateCompletion();
            }

            msg = getString(R.string.edit_success_msg);

        } else if (requestCode == Constants.ADD_FOOD_REQUEST_CODE && resultCode == RESULT_OK) {

            //Check if the item is from the list, is so add only the food size for that day
            boolean isFoodFromList = data.getBooleanExtra(Constants.IS_FOOD_FROM_LIST, false);

            String foodName = data.getStringExtra(Constants.FOOD_NAME);
            double grams = data.getDoubleExtra(Constants.FOOD_GRAMS, 0);

            if(isFoodFromList) {
                long foodItemId = data.getLongExtra(Constants.FOOD_ITEM_ID, 0);
                FoodDay foodDay = new FoodDay(currentDate, foodItemId, grams);
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

                foodDayViewModel.insertFoodItemAndFoodDay(foodItem, currentDate, grams);
            }

            msg = getResources().getString(R.string.save_success_msg);

        } else if (requestCode == Constants.ADD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String activityName = data.getStringExtra(Constants.ACTIVITY_NAME);
            int year = data.getIntExtra(Constants.DATE_YEAR, 0);
            int month = data.getIntExtra(Constants.DATE_MONTH, 0);
            int day = data.getIntExtra(Constants.DATE_DAY, 0);
            int hours = data.getIntExtra(Constants.ACTIVITY_HOURS, 0);
            int minutes = data.getIntExtra(Constants.ACTIVITY_MINUTES, 0);
            int seconds = data.getIntExtra(Constants.ACTIVITY_SECONDS, 0);
            //int intensity = data.getIntExtra(Constants.ACTIVITY_INTENSITY, 0);

            calendar.set(year, month, day);
            Date date = calendar.getTime();

            //Week number from the date
            int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
            int weekYear = calendar.get(Calendar.YEAR);

            boolean weekExists = checkIfWeekExists(weekNumber, weekYear);

            //If week does not exist - add new week to db
            if (!weekExists) {

                Week week = new Week(weekNumber, weekYear, hours, minutes, seconds);

                weekViewModel.insert(week);

                //Check if week totals are equal to 150 mins
                int total = (hours * 60) + minutes + (seconds / 60);
                if (total >= 150) {
                    challengeViewModel.adjustCompletion(challengeId, +1);
                }

            } else {
                //Otherwise update the weeks totals
                Week week = findWeekByWeekNumberAndYear(weekNumber, weekYear);
                weekViewModel.addToWeekTotal(week.getId(), hours, minutes, seconds);

                adjustActivityChallengeCompletion(week, hours, minutes, seconds, false);

            }

            PhysicalActivity physicalActivity = new PhysicalActivity(activityName, date, hours, minutes, seconds, 0);
            physicalActivityViewModel.insert(physicalActivity);

            msg = getResources().getString(R.string.save_success_msg);

        } else if (requestCode == Constants.EDIT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            long activityId = data.getLongExtra(Constants.ACTIVITY_ID, 0);
            boolean isDeleting = data.getBooleanExtra(Constants.OPERATION_TYPE, false);

            //Get the activity that is being deleted/edited
            PhysicalActivity activity = getPhysicalActivityById(activityId);

            // Delete if the user is deleting
            if(isDeleting) {
                //Find the week of this activity
                calendar.setTime(activity.getDate());
                int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
                int weekYear = calendar.get(Calendar.YEAR);

                Week week = findWeekByWeekNumberAndYear(weekNumber, weekYear);

                //Adjust the completion of the challenge and totals in week
                adjustActivityChallengeCompletion(week, activity.getHours(), activity.getMinutes(), activity.getSeconds(), true);
                weekViewModel.subtractFromWeekTotal(week.getId(), activity.getHours(), activity.getMinutes(), activity.getSeconds());

                //Delete the activity
                physicalActivityViewModel.deleteById(activityId);

                msg = getResources().getString(R.string.delete_success_msg);
            } else {
                //Otherwise edit
                //Get the data for updating
                String name = data.getStringExtra(Constants.ACTIVITY_NAME);
                int year = data.getIntExtra(Constants.DATE_YEAR, 0);
                int month = data.getIntExtra(Constants.DATE_MONTH, 0);
                int day = data.getIntExtra(Constants.DATE_DAY, 0);
                int hours = data.getIntExtra(Constants.ACTIVITY_HOURS, 0);
                int minutes = data.getIntExtra(Constants.ACTIVITY_MINUTES, 0);
                int seconds = data.getIntExtra(Constants.ACTIVITY_SECONDS, 0);
                //int intensity = data.getIntExtra(Constants.ACTIVITY_INTENSITY, 0);

                //Get the old values for hours, mins and sec
                int originalHours = data.getIntExtra(Constants.ACTIVITY_ORIGINAL_HOURS, 0);
                int originalMinutes = data.getIntExtra(Constants.ACTIVITY_ORIGINAL_MINUTES, 0);
                int originalSeconds = data.getIntExtra(Constants.ACTIVITY_ORIGINAL_SECONDS, 0);

                //Calculate the difference between the old and new values
                int hoursDiff = originalHours - hours;
                int minutesDiff = originalMinutes - minutes;
                int secondsDiff = originalSeconds - seconds;

                //Get the updated date and week
                calendar.set(year, month, day);
                int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
                int weekYear = calendar.get(Calendar.YEAR);
                
                int originalWeekNumber = data.getIntExtra(Constants.ACTIVITY_ORIGINAL_WEEK_NUMBER, 0);
                int originalWeekYear = data.getIntExtra(Constants.ACTIVITY_ORIGINAL_WEEK_YEAR, 0);
                Week originalWeek = findWeekByWeekNumberAndYear(originalWeekNumber, originalWeekYear);

                boolean weekExists = checkIfWeekExists(weekNumber, weekYear);

                //If week does not exist - add new week to db
                if (!weekExists) {
                    //Add new week with the duration from the activity as totals
                    weekViewModel.insert(new Week(weekNumber, weekYear, hours, minutes, seconds));
                    //Adjust the totals in the original week by the original duration and adjust the totals for the original week
                    weekViewModel.subtractFromWeekTotal(originalWeek.getId(), originalHours, originalMinutes, originalSeconds);
                    adjustActivityChallengeCompletion(originalWeek, originalHours, originalMinutes, originalSeconds, true);
                            

                    //Check if week totals are equal to 150 mins
                    int total = (hours * 60) + minutes + (seconds / 60);
                    if (total >= 150) {
                        challengeViewModel.adjustCompletion(challengeId, +1);
                    }

                } else {
                    //Otherwise get the week
                    Week week = findWeekByWeekNumberAndYear(weekNumber, weekYear);
                    //If the original week and week are the same
                    //Adjust the totals with the difference, set to delete so that the difference is subtracted
                    if(week.equals(originalWeek)) {
                        adjustActivityChallengeCompletion(week, hoursDiff, minutesDiff, secondsDiff, true);
                        weekViewModel.subtractFromWeekTotal(week.getId(), hoursDiff, minutesDiff, secondsDiff);
                    } else {
                        //Otherwise, add the duration to the week and subtract the duration from the original week
                        //adjustActivityChallengeCompletion();
                        weekViewModel.addToWeekTotal(week.getId(), hours, minutes, seconds);
                        weekViewModel.subtractFromWeekTotal(originalWeek.getId(), originalHours, originalMinutes, originalSeconds);
                        //And adjust the completion from both of them
                        //For new week, duration is added
                        adjustActivityChallengeCompletion(week, hours, minutes, seconds, false);
                        //For original week duration is deleted
                        adjustActivityChallengeCompletion(originalWeek, hours, minutes, seconds, true);
                    }

                }

                calendar.set(year, month, day);
                Date date = calendar.getTime();

                physicalActivityViewModel.update(activityId, name, date, hours, minutes, seconds, 0);

                msg = getResources().getString(R.string.edit_success_msg);
            }

        }
        else {
            msg = getResources().getString(R.string.error_msg);
        }

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }

    //Method for getting activity by Id
    private PhysicalActivity getPhysicalActivityById(long id) {

        PhysicalActivity physicalActivity = null;

        List<PhysicalActivity> activities = physicalActivityViewModel.getAllPhysicalActivities().getValue();

        for (int i = 0; i < activities.size(); i++) {
            if(activities.get(i).getId() == id) {
                physicalActivity = activities.get(i);
            }
        }

        return physicalActivity;
    }

    //Method for adjusting the completed weeks for activity challenge
    private void adjustActivityChallengeCompletion(Week week, int hoursFromActivity, int minutesFromActivity, int secondsFromActivity, boolean isDeleting) {

        int totalHours = week.getTotalHours();
        int totalMinutes = week.getTotalMinutes();
        int totalSeconds = week.getTotalSeconds();

        //Save the reference to total before activity to check if this activity was the one that affects completion of week
        int totalBefore = (totalHours * 60) + totalMinutes + (totalSeconds / 60);

        //Add or subtract the activity duration based if the user is deleting or not
        if(isDeleting) {
            totalHours -= hoursFromActivity;
            totalMinutes -= minutesFromActivity;
            totalSeconds -= secondsFromActivity;
        } else {
            totalHours += hoursFromActivity;
            totalMinutes += minutesFromActivity;
            totalSeconds += secondsFromActivity;
        }

        int totalAfter = (totalHours * 60) + totalMinutes + (totalSeconds / 60);

        //The user is deleting and total before was 150 and the total after is smaller than 150 -> subtract one from completed weeks
        if(isDeleting && (totalBefore >= 150) && (totalAfter < 150)) {
            challengeViewModel.adjustCompletion(challengeId, -1);
            completion -= 1;
        } else if ((totalBefore < 150) && totalAfter >= 150) {
            //If the activity is added and before the total was smaller than 150 and after it is bigger -> add one to completed weeks
            challengeViewModel.adjustCompletion(challengeId, +1);
            completion += 1;
        }

        completedDaysTextView.setText(String.valueOf(completion));

    }

    //Check if a week for this week number and year exists in the week table for activities
    private boolean checkIfWeekExists(int weekNumber, int weekYear) {
        boolean weekExists = false;

        List<Week> weekList = weekViewModel.getAllWeeks().getValue();

        for (int i = 0; i <weekList.size(); i++) {
            if(weekList.get(i).getWeekNumber() == weekNumber && weekList.get(i).getWeekYear() == weekYear) {
                weekExists = true;
            }
        }
        return weekExists;
    }

    //Find week by number and year
    private Week findWeekByWeekNumberAndYear(int weekNumber, int weekYear) {

        Week week = null;

        List<Week> weekList = weekViewModel.getAllWeeks().getValue();

        for (int i = 0; i < weekList.size(); i++) {
            if(weekList.get(i).getWeekNumber() == weekNumber && weekList.get(i).getWeekYear() == weekYear) {
                week = weekList.get(i);
            }
        }

        return week;
    }

    //Check if the date has already been marked
    private boolean checkIfDateMarked(Date date) {
        if(!dateList.isEmpty()) {

            for (int i = 0; i < dateList.size(); i++) {
                if (date.equals(dateList.get(i).getDate())) {
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    private int getOffsetForCompletionOfDailyChallenge(Date date, boolean dateMarked) {
        int offset = 0;

        if(dateMarked) {
            offset = -1;
        } else {
            offset = 1;
        }

        return offset;
    }

    private int getOffsetForCompletionOfWeeklyChallenge(Date date, boolean dateIsBeingUnmarked) {
        int offset = 0;

        calendar.setTime(date);

        //Get the week number and year from the date that is being marked/unmarked
        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        int weekYear = calendar.get(Calendar.YEAR);

        //Get the number of days that are completed that week
        int daysCompletedThatWeek = 0;
        daysCompletedThatWeek = getDaysCompletedInAWeek(weekNumber, weekYear);

        //Based on the frequency and marked dates of that week, set the offset to the right value
        //1 - 3xweek, 2-2xweek 3-1xweek
        //If date has been already marked, subtract
        //If marked, subtract
        if(challengeFrequency == 1) {
            if (dateIsBeingUnmarked) {
                if(daysCompletedThatWeek == 3) {
                    offset = -1;
                }
            } else {
                if (daysCompletedThatWeek == 2) {
                    offset = 1;
                }
            }

        } else if(challengeFrequency == 2) {
            if (dateIsBeingUnmarked) {
                if(daysCompletedThatWeek == 2) {
                    offset = -1;
                }
            } else {
                if (daysCompletedThatWeek == 1) {
                    offset = 1;
                }
            }

        } else if(challengeFrequency == 3) {
            if(dateIsBeingUnmarked) {
                if(daysCompletedThatWeek == 1) {
                    offset = -1;
                }
            } else {
                if (daysCompletedThatWeek == 0) {
                    offset = 1;
                }
            }
        }

        return offset;
    }

    //Method for counting how many days have been marked as completed in particular week
    private int getDaysCompletedInAWeek(int weekNumber, int weekYear) {
        int daysCompletedThatWeek = 0;
        //Go through the dates that are already marked and find ones that are marked that week
        for(int i = 0; i < dateList.size(); i++) {
            //Set the calendar to the current marked date
            Date d = dateList.get(i).getDate();
            calendar.setTime(d);
            //Compare week number and year, if they match add to completed days of that week
            if(calendar.get(Calendar.WEEK_OF_YEAR) == weekNumber && calendar.get(Calendar.YEAR) == weekYear) {

                daysCompletedThatWeek += 1;

            }
        }
        return daysCompletedThatWeek;
    }

    //After editing the challenge, recalculate the completed days/weeks
    private void recalculateCompletion() {
        //Reset the completion in db
        challengeViewModel.resetCompletion(challengeId);

        completion = 0;
        int offset = 0;
        //If challenge frequency is now daily, recalculate the completed days
        if(challengeFrequency == 0) {
            offset = recalculateCompletedDays();
        } else {
            //Otherwise recalculated completed weeks
            offset = recalculateCompletedWeeks();
        }
        //Update the challenge completion in the db by the offset
        challengeViewModel.adjustCompletion(challengeId, offset);

        //update the completion and the view showing it (now completion equals offset)
        completion = offset;
        completedDaysTextView.setText(String.valueOf(completion));

    }

    //Method for recalculating completed days for daily challenge
    private int recalculateCompletedDays() {
        int offset = 0;
        for(int i = 0; i < dateList.size(); i++) {
            offset++;
        }
        return offset;
    }

    //Method for recalculating completed weeks for weekly challenge
    private int recalculateCompletedWeeks() {
        int offset = 0;
        //Reference to the current week that the days are calculated for
        int currentWeekNo = 0;
        int daysCompleted = 0;
        //The loop counter equal to the size of the list, in order for going through the offset calculation once again
        for(int i = 0; i <= dateList.size(); i++) {

            int weekNumber;

            //Get the week number and year from the date
            //If the loop is over the list, just equal the week to 0
            if(i == dateList.size()) {
                weekNumber = 0;
            } else {
                calendar.setTime(dateList.get(i).getDate());
                weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
            }

            //Check if the current week does not equals week from the date list, that means the days for new week are being calculated - set currentWeek
            if(currentWeekNo != weekNumber) {
                //Save the current offset from the days completed from the 'previous' week
                if(challengeFrequency == 1) {
                    if (daysCompleted >= 3) {
                        offset += 1;
                    }

                } else if(challengeFrequency == 2) {
                    if (daysCompleted >= 2) {
                        offset += 1;
                    }
                } else if(challengeFrequency == 3) {
                    if (daysCompleted >= 1) {
                        offset += 1;
                    }
                }

                //Reset the days completed
                daysCompleted = 0;

                //Set the current week to the new week
                currentWeekNo = weekNumber;

            }

            //If the current week equals the week from the dateList, add to  days completed
            if(currentWeekNo == weekNumber) {
                daysCompleted++;
            }

        }
        return offset;

    }

    //Mark the dates on the calendar
    private void markDates(List<ChallengeDate> challengeDates) {

        if(!challengeDates.isEmpty()) {

            for (int i = 0; i < challengeDates.size(); i++) {

                Date date = challengeDates.get(i).getDate();

                caldroidFragment.setBackgroundDrawableForDate(accentColor, date);

            }
            caldroidFragment.refreshView();
        }
    }

    private int getFoodChallengeTotal(List<FoodJournalItem> foodJournalItems, String challengeTrackingProperty) {
        double total = 0;
        double trackingPropertyValue = 0;
        int totalCalories = 0;

        for (int i = 0; i < foodJournalItems.size(); i++) {
            FoodItem item = foodJournalItems.get(i).getFoodItem();
            double grams = foodJournalItems.get(i).getFoodDay().getGrams();
            //Count the calories and sum them up for later calculation of %
            totalCalories += (item.getCalories() * grams) / 100;

            //If the item is fruit or veggie and the challenge tracking property is fruit or veggie add the grams to total
            if (item.isFruitVeggie() && challengeTrackingProperty.equals(Constants.FRUIT_VEGGIE_KEY)) {
                total += grams;
            } else {
                //Get the appropriate property
                trackingPropertyValue = getTheAppropriatePropertyValue(item, challengeTrackingProperty);
                //Count how many of the property for that number of grams and add it to total
                total += (trackingPropertyValue * grams) / 100;

            }
        }
        //If tracking calories and salt, fruits return the calculated total
        if(challengeTrackingProperty.equals(Constants.CALORIE_KEY) || challengeTrackingProperty.equals(Constants.SALT_KEY) || challengeTrackingProperty.equals(Constants.FRUIT_VEGGIE_KEY)) {
            return (int) total;
        } else {
            //Other properties need to be in percentage
            //Count the calories that come from property (fat, sugar etc), by multiplying by right value of kcal/gram
            int kcalPerGram = 0;
            if(challengeTrackingProperty.equals(Constants.FAT_KEY) || challengeTrackingProperty.equals(Constants.SAT_FAT_KEY) || challengeTrackingProperty.equals(Constants.TRANS_FAT_KEY)) {
                kcalPerGram = Constants.FAT_KCAL_PER_GRAM;
            } else if (challengeTrackingProperty.equals(Constants.SUGAR_KEY)) {
                kcalPerGram = Constants.SUGAR_KCAL_PER_GRAM;
            } else if (challengeTrackingProperty.equals(Constants.CARBS_KEY)) {
                kcalPerGram = Constants.CARBS_KCAL_PER_GRAM;
            } else if (challengeTrackingProperty.equals(Constants.PROTEIN_KEY)) {
                kcalPerGram = Constants.PROTEIN_KCAL_PER_GRAM;
            }
            double totalKcalFromProperty = total * kcalPerGram;

            int percentage = (int) ((totalKcalFromProperty / totalCalories) * 100);

            return percentage;

        }

    }

    private double getTheAppropriatePropertyValue(FoodItem item, String challengeTrackingProperty) {
        double property = 0;

        switch (challengeTrackingProperty) {
            case (Constants.CALORIE_KEY):
                property = item.getCalories();
                break;
            case (Constants.CARBS_KEY):
                property = item.getCarbs();
                break;
            case (Constants.PROTEIN_KEY):
                property = item.getProtein();
                break;
            case (Constants.FAT_KEY):
                property = item.getFat();
                break;
            case (Constants.SAT_FAT_KEY):
                property = item.getSaturatedFat();
                break;
            case (Constants.TRANS_FAT_KEY):
                property = item.getTransFat();
                break;
            case (Constants.SUGAR_KEY):
                property = item.getSugar();
                break;
            case (Constants.SALT_KEY):
                property = item.getSalt();
                break;
        }

        return property;
    }

    //Method for showing a confirmation dialog for finishing the challenge
    private void showConfirmationDialog(long challengeId, String challengeName) {
        //Create an AlertDialog, set message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.complete_challenge_dialog_title);
        String dialogTitle = getString(R.string.do_you_want_to_complete) + challengeName + getString(R.string.challenge_question_mark);
        builder.setMessage(dialogTitle);

        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            challengeViewModel.updateState(challengeId, 2);
            challengeViewModel.resetCompletion(challengeId);
            challengeDateViewModel.deleteDatesFromChallenge(challengeId);

            finish();
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

    //Create the menu with the action button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu with the right resource based on the challenge type
        if(challengeType == 0) {
            getMenuInflater().inflate(R.menu.basic_challenge_options_menu, menu);
        } else if (challengeType == 1) {
            getMenuInflater().inflate(R.menu.food_challenge_options_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.other_challenge_options_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    //Handle the menu click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.challenge_info) {
            showInfoDialog();
        } else if (id == R.id.edit_challenge) {
            Intent editIntent = new Intent(ChallengeDetailsActivity.this, EditChallengeActivity.class);

            editIntent.putExtra(Constants.CHALLENGE_ID, challengeId);
            editIntent.putExtra(Constants.CHALLENGE_NAME, challengeName);
            editIntent.putExtra(Constants.CHALLENGE_DESCRIPTION, challengeDescription);
            editIntent.putExtra(Constants.CHALLENGE_FREQUENCY, challengeFrequency);

            startActivityForResult(editIntent, Constants.EDIT_CHALLENGE_REQUEST_CODE);
        } else if (id == R.id.food_journal) {
            Intent foodJournalIntent = new Intent(ChallengeDetailsActivity.this, FoodJournalActivity.class);
            startActivity(foodJournalIntent);
        } else if (id == R.id.finish_challenge) {
            showConfirmationDialog(challengeId, challengeName);
        }
        return super.onOptionsItemSelected(item);
    }

    //Method to show dialog with information
    private void showInfoDialog() {
        //Create an AlertDialog, set message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getResources().getString(R.string.challenge_description);
        builder.setTitle(title);

        //Info text displays frequency and the information text/description
        //For the info text, first get the frequency title and get the frequency
        String frequency = getResources().getString(R.string.frequency_input_title) + " ";
        String[] frequencyArray = getResources().getStringArray(R.array.frequency_array);
        frequency += frequencyArray[challengeFrequency];

        //For description check the type of the challenge
        //Description for pre-made challenges is in string.xml
        String description;
        if (challengeInfoText != 0){
            String[] infoTextArray = getResources().getStringArray(R.array.challenge_info_array);
            //Challenge info is saved in db starting from 1 - in array from 0, so need to subtract 1
            description = infoTextArray[challengeInfoText - 1];
        } else {
            description = challengeDescription;
        }

        builder.setMessage(frequency + "\n\n" + description);
        builder.setNeutralButton(R.string.info_dialog_btn, (dialogInterface, i) -> dialogInterface.dismiss());

        //Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //update the state of the bottom navigation tab, to show the right item
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_challenges);
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