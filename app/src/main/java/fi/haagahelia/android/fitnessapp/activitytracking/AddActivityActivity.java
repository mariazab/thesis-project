package fi.haagahelia.android.fitnessapp.activitytracking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import fi.haagahelia.android.fitnessapp.Constants;
import fi.haagahelia.android.fitnessapp.HelperMethods;
import fi.haagahelia.android.fitnessapp.R;

public class AddActivityActivity extends AppCompatActivity implements DurationPickerFragment.DurationPickerListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "Add activity";

    private EditText nameEditText;
    private EditText dateEditText;
    private EditText durationEditText;

    //For now the intensity does not do anything so it is kept out
    //private Spinner intensitySpinner;
    //private int intensitySelected;

    private String durationSelected = "";
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;

    private int originalHours;
    private int originalMinutes;
    private int originalSeconds;

    private int year;
    private int month;
    private int day;

    private int originalWeekNumber;
    private int originalWeekYear;

    private long activityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        Context context = this;

        nameEditText = findViewById(R.id.add_activity_name);
        dateEditText = findViewById(R.id.add_date);
        durationEditText = findViewById(R.id.add_duration);
        //intensitySpinner = findViewById(R.id.intensity_spinner);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Populate the spinner
//        String[] intensityLevels = getResources().getStringArray(R.array.activity_levels);
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, intensityLevels);
//        intensitySpinner.setAdapter(spinnerAdapter);
//
//        //Spinner on Item Selected
//        intensitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                intensitySelected = i;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        //Date on click show DatePicker
        dateEditText.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(context, AddActivityActivity.this::onDateSet, year, month, day);
            dialog.show();
        });

        //Duration onClick show time picker
        durationEditText.setOnClickListener(view -> {
            DialogFragment fragment = new DurationPickerFragment(originalHours, originalMinutes, originalSeconds);
            fragment.show(getSupportFragmentManager(), TAG);
        });

        Button saveBtn = findViewById(R.id.button_save);
        saveBtn.setOnClickListener(view -> {
            boolean areValuesPresent = checkIfAllValuesArePresent();
            //If all values are not present, show toast message
            //Otherwise save
            if (!areValuesPresent) {
                Toast.makeText(getApplicationContext(), getString(R.string.insert_error_msg), Toast.LENGTH_LONG).show();
            } else {
                addActivity();
            }
        });

        Button deleteBtn = findViewById(R.id.button_delete);
        deleteBtn.setOnClickListener(view -> showConfirmationDialog(activityId));

        //Get the intent that triggered the activity to find out which user action it was
        //If it was the list item click - set the title to Edit and populate the edit fields with the data
        Intent intent = getIntent();
        activityId = intent.getLongExtra(Constants.ACTIVITY_ID, 0);

        if (activityId != 0) {
            deleteBtn.setVisibility(View.VISIBLE);
            String activityName = intent.getStringExtra(Constants.ACTIVITY_NAME);
            //Save the duration in two ways: as editedHours etc to keep track of the old values and as hours etc (in case the user does not edit the duration, then the hours etc stil contain the right values
            originalHours = intent.getIntExtra(Constants.ACTIVITY_HOURS, 0);
            originalMinutes = intent.getIntExtra(Constants.ACTIVITY_MINUTES, 0);
            originalSeconds = intent.getIntExtra(Constants.ACTIVITY_SECONDS, 0);

            hours = intent.getIntExtra(Constants.ACTIVITY_HOURS, 0);
            minutes = intent.getIntExtra(Constants.ACTIVITY_MINUTES, 0);
            seconds = intent.getIntExtra(Constants.ACTIVITY_SECONDS, 0);

            durationSelected = intent.getStringExtra(Constants.ACTIVITY_DURATION);
            //int activityIntensity = intent.getIntExtra(Constants.ACTIVITY_INTENSITY, 0);
            year = intent.getIntExtra(Constants.DATE_YEAR, 0);
            month = intent.getIntExtra(Constants.DATE_MONTH, 0);
            day = intent.getIntExtra(Constants.DATE_DAY, 0);


            Calendar c = Calendar.getInstance();
            c.set(year, month, day);

            //Save the originalWeekNumber and originalWeekYear to check if the user edited the week number
            originalWeekNumber = c.get(Calendar.WEEK_OF_YEAR);
            originalWeekYear = c.get(Calendar.YEAR);

            String date = HelperMethods.getDateInStringFormat(c);

            setTitle(getString(R.string.edit_activity_title));
            nameEditText.setText(String.valueOf(activityName));
            dateEditText.setText(date);
            durationEditText.setText(durationSelected);
            //intensitySpinner.setSelection(activityIntensity);
        }

    }

    private void addActivity() {
        Intent replyIntent = new Intent();

        //If there is activity id from intent, send it with the reply for editing
        //Send also the old values for hours, mins and sec
        if (activityId != 0) {
            replyIntent.putExtra(Constants.ACTIVITY_ID, activityId);
            replyIntent.putExtra(Constants.ACTIVITY_ORIGINAL_HOURS, originalHours);
            replyIntent.putExtra(Constants.ACTIVITY_ORIGINAL_MINUTES, originalMinutes);
            replyIntent.putExtra(Constants.ACTIVITY_ORIGINAL_SECONDS, originalSeconds);
            replyIntent.putExtra(Constants.ACTIVITY_ORIGINAL_WEEK_NUMBER, originalWeekNumber);
            replyIntent.putExtra(Constants.ACTIVITY_ORIGINAL_WEEK_YEAR, originalWeekYear);
        }

        String activityName = nameEditText.getText().toString();
        activityName = activityName.trim();

        replyIntent.putExtra(Constants.ACTIVITY_NAME, activityName);
        replyIntent.putExtra(Constants.DATE_YEAR, year);
        replyIntent.putExtra(Constants.DATE_MONTH, month);
        replyIntent.putExtra(Constants.DATE_DAY, day);
        replyIntent.putExtra(Constants.ACTIVITY_HOURS, hours);
        replyIntent.putExtra(Constants.ACTIVITY_MINUTES, minutes);
        replyIntent.putExtra(Constants.ACTIVITY_SECONDS, seconds);
        //replyIntent.putExtra(Constants.ACTIVITY_INTENSITY, intensitySelected);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    //Check if all values are provided by the user
    private boolean checkIfAllValuesArePresent() {
        boolean areValuesPresent = true;

        if (TextUtils.isEmpty(nameEditText.getText().toString().trim()) || TextUtils.isEmpty(dateEditText.getText().toString().trim()) || TextUtils.isEmpty(durationEditText.getText().toString().trim())) {
            areValuesPresent = false;
        }
        return areValuesPresent;
    }

    //Method for handling the duration dialog positive button click
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //Reset the duration string
        durationSelected = "";
        TextView hoursValueTextView = dialog.getDialog().findViewById(R.id.hours_value_textView);
        TextView minutesValueTextView = dialog.getDialog().findViewById(R.id.minutes_value_textView);
        TextView secondsValueTextView = dialog.getDialog().findViewById(R.id.seconds_value_textView);

        String hoursText = String.valueOf(hoursValueTextView.getText());
        String minutesText = String.valueOf(minutesValueTextView.getText());
        String secondsText = String.valueOf(secondsValueTextView.getText());

        if (hoursText.isEmpty()) {
            hours = 0;
        } else {
            hours = Integer.parseInt(hoursText);
        }

        if (minutesText.isEmpty()) {
            minutes = 0;
        } else {
            minutes = Integer.parseInt(minutesText);
        }

        if (secondsText.isEmpty()) {
            seconds = 0;
        } else {
            seconds = Integer.parseInt(secondsText);
        }

        durationSelected = HelperMethods.showDurationAsString(hours, minutes, seconds);
        durationEditText.setText(durationSelected);

    }

    //Method to show confirmation dialog when DELETE is clicked and send the result code and data to ChallengesActivity
    private void showConfirmationDialog(long activityId) {
        //Create an AlertDialog, set message and respond to answers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_dialog_msg);
        builder.setMessage(R.string.delete_dialog_submsg);
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            if (activityId != 0) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(nameEditText.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    replyIntent.putExtra(Constants.OPERATION_TYPE, Constants.EXTRA_REPLY_DELETE);
                    replyIntent.putExtra(Constants.ACTIVITY_ID, activityId);
                    setResult(RESULT_OK, replyIntent);
                    finish();
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

    //OnDateSetListener from DatePicker dialog
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        String date = HelperMethods.getDateInStringFormat(c);

        dateEditText.setText(date);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

}