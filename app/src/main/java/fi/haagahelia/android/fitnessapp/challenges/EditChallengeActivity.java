package fi.haagahelia.android.fitnessapp.challenges;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import fi.haagahelia.android.fitnessapp.ChallengesActivity;
import fi.haagahelia.android.fitnessapp.Constants;
import fi.haagahelia.android.fitnessapp.R;

public class EditChallengeActivity extends AppCompatActivity {

    private EditText challengeNameEditText;
    private EditText challengeDescriptionEditText;
    private int frequencySelected;

    private Bundle intentExtras;
    private long challengeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_challenge);

        //Get the editing challenge values from the intent
        Intent intent = getIntent();
        //Extras bundle for when back is pressed
        intentExtras = intent.getExtras();

        challengeId = intent.getLongExtra(Constants.CHALLENGE_ID, 0);
        String challengeName = intent.getStringExtra(Constants.CHALLENGE_NAME);
        String challengeDescription = intent.getStringExtra(Constants.CHALLENGE_DESCRIPTION);
        int challengeFrequency = intent.getIntExtra(Constants.CHALLENGE_FREQUENCY, 0);

        //Set the edit text with the challenge values
        challengeNameEditText = findViewById(R.id.add_challenge_name);
        challengeNameEditText.setText(challengeName);

        challengeDescriptionEditText = findViewById(R.id.add_challenge_description);
        challengeDescriptionEditText.setText(challengeDescription);

        Spinner frequencySpinner = findViewById(R.id.frequency_spinner);

        //Populate the spinner
        String[] frequencyItems = getResources().getStringArray(R.array.frequency_array);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, frequencyItems);
        frequencySpinner.setAdapter(spinnerAdapter);
        //Set the spinner at the right challenge value
        frequencySpinner.setSelection(challengeFrequency);
        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                frequencySelected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //On button click save the challenge
        Button saveBtn = findViewById(R.id.button_save);
        saveBtn.setOnClickListener(view -> {

            boolean areRequiredValuesPresent = checkIfRequiredValuesArePresent();

            //If the required values are missing, show a toast msg
            if (!areRequiredValuesPresent) {
                Toast.makeText(getApplicationContext(), getString(R.string.challenge_insert_error_msg), Toast.LENGTH_LONG).show();
            } else {
                editChallenge(challengeId);
            }
        });

        //On button click show the confirmation dialog before deleting
        Button deleteBtn = findViewById(R.id.button_delete);
        deleteBtn.setOnClickListener(view -> {
            showConfirmationDialog(challengeId);
        });

    }

    //When editing send the result code and data to the ChallengeDetails
    private void editChallenge(long challengeId) {
        Intent replyIntent = new Intent();
        String challengeName = challengeNameEditText.getText().toString();
        challengeName = challengeName.trim();
        String challengeDescription = challengeDescriptionEditText.getText().toString();
        challengeDescription = challengeDescription.trim();

        //replyIntent.putExtra(EXTRA_REPLY_OPERATION_TYPE, EXTRA_REPLY_UPDATE);
        replyIntent.putExtra(Constants.CHALLENGE_ID, challengeId);
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

    //Method to show confirmation dialog when DELETE is clicked and send the result code and data to ChallengesActivity
    private void showConfirmationDialog(long challengeId) {
        //Create an AlertDialog, set message and respond to answers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_dialog_msg);
        builder.setMessage(R.string.delete_challenge_dialog_submsg);
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            if (challengeId != 0) {
                Intent replyIntent = new Intent(EditChallengeActivity.this, ChallengesActivity.class);
                if (TextUtils.isEmpty(challengeNameEditText.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    replyIntent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_DELETE);
                    replyIntent.putExtra(Constants.CHALLENGE_ID, challengeId);
                    setResult(RESULT_OK, replyIntent);
                    startActivity(replyIntent);
                }
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }
}