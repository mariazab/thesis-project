package fi.haagahelia.android.fitnessapp.challenges;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fi.haagahelia.android.fitnessapp.Constants;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.db.Challenge;

public class AddChallengeActivity extends AppCompatActivity implements ChallengeListAdapter.OnItemListener {

    private ChallengeViewModel challengeViewModel;

    private EditText challengeNameEditText;
    private EditText challengeDescriptionEditText;
    private int frequencySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge);

        challengeNameEditText = findViewById(R.id.add_challenge_name);
        challengeDescriptionEditText = findViewById(R.id.add_challenge_description);
        Spinner frequencySpinner = findViewById(R.id.frequency_spinner);

        //Populate the spinner
        String[] frequencyItems = getResources().getStringArray(R.array.frequency_array);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, frequencyItems);
        frequencySpinner.setAdapter(spinnerAdapter);

        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                frequencySelected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //List with the pre-made challenges
        RecyclerView challengesListRecyclerView = findViewById(R.id.new_challenges_recyclerView);
        final ChallengeListAdapter newChallengeAdapter = new ChallengeListAdapter(this, this);
        challengesListRecyclerView.setAdapter(newChallengeAdapter);
        challengesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add the divider to the list
        challengesListRecyclerView.addItemDecoration(new DividerItemDecoration(challengesListRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        challengeViewModel = new ViewModelProvider(this).get(ChallengeViewModel.class);
        challengeViewModel.getInactiveChallenges().observe(this, challenges -> {
            newChallengeAdapter.setChallenges(challenges, false);
        });

        //On button click save the challenge
        Button saveBtn = findViewById(R.id.button_save);
        saveBtn.setOnClickListener(view -> {

            boolean areRequiredValuesPresent = checkIfRequiredValuesArePresent();

            //If the required values are missing, show a toast msg
            if (!areRequiredValuesPresent) {
                Toast.makeText(getApplicationContext(), getString(R.string.challenge_insert_error_msg), Toast.LENGTH_LONG).show();
            } else {
                addChallenge();
            }

        });

        Button itemFromListBtn = findViewById(R.id.button_choose_from_list);
        itemFromListBtn.setVisibility(View.VISIBLE);
        itemFromListBtn.setOnClickListener(view ->
                challengesListRecyclerView.setVisibility(View.VISIBLE)
        );
    }

    @Override
    public void onItemClick(int position, boolean challengesActive) {
        List<Challenge> challenges = challengeViewModel.getInactiveChallenges().getValue();
        Challenge challenge = challenges.get(position);
        showChallengeDetails(challenge);
    }

    //Send the data and finish the activity
    private void addChallenge() {
        Intent replyIntent = new Intent();
        String challengeName = challengeNameEditText.getText().toString();
        challengeName = challengeName.trim();
        String challengeDescription = challengeDescriptionEditText.getText().toString();
        challengeDescription = challengeDescription.trim();

        replyIntent.putExtra(Constants.CHALLENGE_NAME, challengeName);
        replyIntent.putExtra(Constants.CHALLENGE_DESCRIPTION, challengeDescription);
        replyIntent.putExtra(Constants.CHALLENGE_FREQUENCY, frequencySelected);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    //Check if all required values are provided by the user
    private boolean checkIfRequiredValuesArePresent() {
        boolean areValuesPresent = true;

        if (TextUtils.isEmpty(challengeNameEditText.getText().toString().trim())) {
            areValuesPresent = false;
        }

        return areValuesPresent;
    }

    //Method for showing a dialog with challenges information
    private void showChallengeDetails(Challenge challenge) {
        //Create an AlertDialog, set message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(challenge.getName());
        String challengeFrequency = "";
        String[] frequencyLevels = getResources().getStringArray(R.array.frequency_array);

        switch (challenge.getFrequency()) {
            case 0:
                challengeFrequency = frequencyLevels[0];
                break;
            case 1:
                challengeFrequency = frequencyLevels[1];
                break;
            case 2:
                challengeFrequency = frequencyLevels[2];
                break;
            case 3:
                challengeFrequency = frequencyLevels[3];
                break;
        }

        String state = "";

        if (challenge.getState() == 2) {
            state = getString(R.string.challenge_already_was_completed);
            state += "\n\n";
        }

        String frequency = getResources().getString(R.string.frequency_input_title) + " " + challengeFrequency;
        String description = getResources().getString(R.string.challenge_description_input_title) + "\n";

        //For description check the type of the challenge
        //Description for pre-made challenges is in string.xml
        if (challenge.getInformationTextId() != 0){
            String[] infoTextArray = getResources().getStringArray(R.array.challenge_info_array);
            //Challenge info is saved in db starting from 1 - in array from 0, so need to subtract 1
            description += infoTextArray[challenge.getInformationTextId() - 1];
        } else {
            description += challenge.getDescription();
        }

        builder.setMessage(state + frequency + "\n\n" + description);

        builder.setPositiveButton(R.string.start_challenge, (dialogInterface, i) -> {
            challengeViewModel.updateState(challenge.getId(), 1);

            Intent replyIntent = new Intent();
            setResult(RESULT_OK, replyIntent);
            finish();
        });

        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
            //CANCEL clicked, so dismiss the dialog
            if (dialogInterface != null) {
                dialogInterface.dismiss();
            }
        });

        //Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}