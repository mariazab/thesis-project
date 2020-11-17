package fi.haagahelia.android.fitnessapp.goalsmeasurements;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import fi.haagahelia.android.fitnessapp.Constants;
import fi.haagahelia.android.fitnessapp.HelperMethods;
import fi.haagahelia.android.fitnessapp.R;

public class AddWeightActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText addWeightEditText;
    private Button saveBtn;
    private Button deleteBtn;
    private EditText addDateEditText;

    private int year;
    private int month;
    private int day;

    private long weightId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        Context context = this;

        saveBtn = findViewById(R.id.button_save);
        deleteBtn = findViewById(R.id.button_delete);
        addWeightEditText = findViewById(R.id.add_weight);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        addDateEditText = findViewById(R.id.add_date);
        addDateEditText.setOnClickListener(view -> {
            //Show the DatePicker fragment on click
            DatePickerDialog dialog = new DatePickerDialog(context, AddWeightActivity.this::onDateSet, year, month, day);
            dialog.show();
        });

        //Get the intent that triggered the activity to find out which user action it was
        //If it was the list item click - set the title to Edit and populate the edit fields with the data
        Intent intent = getIntent();
        weightId = intent.getLongExtra(Constants.WEIGHT_ID, 0);

        if(weightId != 0) {
            deleteBtn.setVisibility(View.VISIBLE);
            double weight = intent.getDoubleExtra(Constants.WEIGHT, 0);
            year = intent.getIntExtra(Constants.DATE_YEAR, 0);
            month = intent.getIntExtra(Constants.DATE_MONTH, 0);
            day = intent.getIntExtra(Constants.DATE_DAY, 0);

            Calendar c = Calendar.getInstance();
            c.set(year, month, day);

            String date = HelperMethods.getDateInStringFormat(c);

            setTitle(getString(R.string.edit_weight));
            addWeightEditText.setText(String.valueOf(weight));
            addDateEditText.setText(date);
        }

        //Send the weight with the date back to the WeightActivity on button click
        saveBtn.setOnClickListener(view -> {
            boolean areAllValuesPresent = checkIfAllValuesArePresent();
            //If all values are not present, show toast message
            //Otherwise save
            if (!areAllValuesPresent) {
                Toast.makeText(getApplicationContext(), getString(R.string.insert_error_msg), Toast.LENGTH_LONG).show();
            } else {
                saveWeight();
            }

        });

        deleteBtn.setOnClickListener(view -> showConfirmationDialog());
    }

    //Check if all values are provided by the user
    private boolean checkIfAllValuesArePresent() {
        boolean areValuesPresent = true;

        if (TextUtils.isEmpty(addWeightEditText.getText()) || TextUtils.isEmpty(addDateEditText.getText())) {
            areValuesPresent = false;
        }
        return areValuesPresent;
    }

    private void saveWeight() {

        Intent replyIntent = new Intent();
        //For update or save, send the weight
        String weight = addWeightEditText.getText().toString();
        replyIntent.putExtra(Constants.WEIGHT, weight);

        //If weightId exists, then the user is editing, sent that info with intent
        if (weightId != 0) {
            replyIntent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_UPDATE);
            replyIntent.putExtra(Constants.WEIGHT_ID, weightId);

        } else {
            replyIntent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_SAVE);
        }

        replyIntent.putExtra(Constants.DATE_YEAR, year);
        replyIntent.putExtra(Constants.DATE_MONTH, month);
        replyIntent.putExtra(Constants.DATE_DAY, day);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    //Method to show confirmation dialog when DELETE is clicked
    private void showConfirmationDialog() {
        //Create an AlertDialog, set message and respond to answers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_dialog_msg);
        builder.setMessage(R.string.delete_dialog_submsg);
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            if(weightId != 0) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(addWeightEditText.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    replyIntent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_DELETE);
                    replyIntent.putExtra(Constants.WEIGHT_ID, weightId);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
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

    //OnDateSetListener from DatePicker dialog
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        String date = HelperMethods.getDateInStringFormat(c);

        addDateEditText.setText(date);
    }
}