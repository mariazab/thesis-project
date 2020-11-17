package fi.haagahelia.android.fitnessapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fi.haagahelia.android.fitnessapp.challenges.AddChallengeActivity;
import fi.haagahelia.android.fitnessapp.challenges.ChallengeDetailsActivity;
import fi.haagahelia.android.fitnessapp.challenges.ChallengeListAdapter;
import fi.haagahelia.android.fitnessapp.challenges.ChallengeViewModel;
import fi.haagahelia.android.fitnessapp.db.Challenge;
import fi.haagahelia.android.fitnessapp.foodtracking.FoodJournalActivity;

public class ChallengesActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ChallengeListAdapter.OnItemListener {

    private BottomNavigationView navigationView;

    private ChallengeViewModel challengeViewModel;

    private View activeChallengesEmptyView;
    private View completedChallengesEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        setTitle(R.string.challenges_title);

        createNotificationChannel();

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        activeChallengesEmptyView = findViewById(R.id.active_challenges_empty_view);
        completedChallengesEmptyView = findViewById(R.id.completed_challenges_empty_view);

        RecyclerView activeChallengesRecyclerView = findViewById(R.id.active_challenges_recyclerView);
        final ChallengeListAdapter activeChallengesAdapter = new ChallengeListAdapter(this, this);
        activeChallengesRecyclerView.setAdapter(activeChallengesAdapter);
        activeChallengesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add the divider to the list
        activeChallengesRecyclerView.addItemDecoration(new DividerItemDecoration(activeChallengesRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        challengeViewModel = new ViewModelProvider(this).get(ChallengeViewModel.class);

        challengeViewModel.getActiveChallenges().observe(this, challenges -> {
            activeChallengesAdapter.setChallenges(challenges, true);

            //If there is no items in the adapter, show the empty view and hide the graph
            if(activeChallengesAdapter.getItemCount() == 0) {
                activeChallengesEmptyView.setVisibility(View.VISIBLE);
                activeChallengesRecyclerView.setVisibility(View.GONE);
            } else {
                activeChallengesEmptyView.setVisibility(View.GONE);
                activeChallengesRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        RecyclerView completedChallengesRecyclerView = findViewById(R.id.completed_challenges_recyclerView);
        final ChallengeListAdapter completedChallengesAdapter = new ChallengeListAdapter(this, this);
        completedChallengesRecyclerView.setAdapter(completedChallengesAdapter);
        completedChallengesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Add the divider to the list
        completedChallengesRecyclerView.addItemDecoration(new DividerItemDecoration(completedChallengesRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        challengeViewModel.getCompletedChallenges().observe(this, challenges -> {
            completedChallengesAdapter.setChallenges(challenges, false);

            //If there is no items in the adapter, show the empty view and hide the graph
            if(completedChallengesAdapter.getItemCount() == 0) {
                completedChallengesEmptyView.setVisibility(View.VISIBLE);
                completedChallengesRecyclerView.setVisibility(View.GONE);
            } else {
                completedChallengesEmptyView.setVisibility(View.GONE);
                completedChallengesRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        //FAB on click go to Challenges List
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ChallengesActivity.this, AddChallengeActivity.class);
            startActivityForResult(intent, Constants.NEW_CHALLENGE_REQUEST_CODE);
        });

    }

    @Override
    public void onItemClick(int position, boolean challengesActive) {
        List<Challenge> challenges;

        if(challengesActive) {
            challenges = challengeViewModel.getActiveChallenges().getValue();
            Challenge challenge = challenges.get(position);

            Intent intent = new Intent(ChallengesActivity.this, ChallengeDetailsActivity.class);
            intent.putExtra(Constants.CHALLENGE_ID, challenge.getId());
            intent.putExtra(Constants.CHALLENGE_NAME, challenge.getName());
            intent.putExtra(Constants.CHALLENGE_DESCRIPTION, challenge.getDescription());
            intent.putExtra(Constants.CHALLENGE_FREQUENCY, challenge.getFrequency());
            intent.putExtra(Constants.CHALLENGE_STATE, challenge.getState());
            intent.putExtra(Constants.CHALLENGE_COMPLETION, challenge.getCompletion());
            intent.putExtra(Constants.CHALLENGE_INFO_ID, challenge.getInformationTextId());
            intent.putExtra(Constants.CHALLENGE_TYPE, challenge.getType());
            startActivity(intent);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String msg;

        if (requestCode == Constants.NEW_CHALLENGE_REQUEST_CODE && resultCode == RESULT_OK) {

            //Check if there is challenge name in the extras -> user created their own challenge, save to the db
            String challengeName = data.getStringExtra(Constants.CHALLENGE_NAME);
            if (!(TextUtils.isEmpty(challengeName))) {
                String challengeDesc = data.getStringExtra(Constants.CHALLENGE_DESCRIPTION);
                int challengeFrequency = data.getIntExtra(Constants.CHALLENGE_FREQUENCY, 0);

                Challenge challenge = new Challenge(challengeName, challengeDesc, challengeFrequency, 1, 0, 0,0);
                challengeViewModel.insert(challenge);
            }

            msg = getResources().getString(R.string.challenge_started_msg);

        } else if (requestCode == Constants.EDIT_CHALLENGE_REQUEST_CODE && resultCode == RESULT_OK) {
            long challengeId = data.getLongExtra(Constants.CHALLENGE_ID, 0);
            challengeViewModel.deleteById(challengeId);
            msg = getString(R.string.delete_success_msg);

        }
        else {
            msg = getResources().getString(R.string.error_msg);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("notification");
            String description = ("challenge notification");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setRecurringAlarm() {

        Intent notificationIntent = new Intent(ChallengesActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ChallengesActivity.this, 0, notificationIntent, 0);

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 20);
        time.set(Calendar.MINUTE, 0);

        //If the time is already after 20:00, add one more day so the next notification is next day
        if(Calendar.getInstance().after(time)) {
            time.add(Calendar.DATE, 1);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //update the state of the bottom navigation tab, to show the right item
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_challenges);
        item.setChecked(true);
        setRecurringAlarm();
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