package fi.haagahelia.android.fitnessapp.goalsmeasurements;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import fi.haagahelia.android.fitnessapp.R;

public class AddWeightActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY_WEIGHT = "fi.haagahelia.android.fitnessapp.REPLY_WEIGHT";
    public static final String EXTRA_REPLY_DATE = "fi.haagahelia.android.fitnessapp.REPLY_DATE";
    public static final String EXTRA_REPLY_DATE_YEAR = "fi.haagahelia.android.fitnessapp.REPLY_DATE_YEAR";
    public static final String EXTRA_REPLY_DATE_MONTH = "fi.haagahelia.android.fitnessapp.REPLY_DATE_MONTH";
    public static final String EXTRA_REPLY_DATE_DAY = "fi.haagahelia.android.fitnessapp.REPLY_DATE_DAY";

    EditText addWeightEditText;
    Button saveBtn;
    static EditText addDateEditText;
    static int year;
    static int month;
    static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        saveBtn = findViewById(R.id.button_save);
        addWeightEditText = findViewById(R.id.add_weight);

        addDateEditText = findViewById(R.id.add_date);
        addDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show the DatePicker fragment on click
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        //Send the weight with the date back to the WeightActivity on button click
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(addWeightEditText.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String weight = addWeightEditText.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY_WEIGHT, weight);
                    replyIntent.putExtra(EXTRA_REPLY_DATE_YEAR, year);
                    replyIntent.putExtra(EXTRA_REPLY_DATE_MONTH, month);
                    replyIntent.putExtra(EXTRA_REPLY_DATE_DAY, day);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Save the date chosen by the user and show it in the date EditText

            AddWeightActivity.year = year;
            AddWeightActivity.month = month;
            AddWeightActivity.day = day;

            month += 1;
            String date = "";
            if (day < 10 && month < 10) {
                date += "0" + day + ".0" + month;
            } else if (day < 10){
                date += "0" + day + "." + month;
            } else if (month < 10) {
                date += day + ".0" + month;
            } else {
                date += day + "." + month;
            }

            date += "." + year;
            addDateEditText.setText(date);

        }
    }
}
