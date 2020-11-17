package fi.haagahelia.android.fitnessapp.activitytracking;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import fi.haagahelia.android.fitnessapp.R;

public class DurationPickerFragment extends DialogFragment {

    private int hours;
    private int minutes;
    private int seconds;

    public DurationPickerFragment(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public interface DurationPickerListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    DurationPickerFragment.DurationPickerListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (DurationPickerFragment.DurationPickerListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.duration_picker_dialog, null);
        builder.setView(view);

        //Text view that show the picked numbers
        TextView hourValueTextView = view.findViewById(R.id.hours_value_textView);

        TextView minutesValueTextView = view.findViewById(R.id.minutes_value_textView);

        TextView secondsValueTextView = view.findViewById(R.id.seconds_value_textView);

        NumberPicker hourPicker = view.findViewById(R.id.hour_picker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(hours);
        hourValueTextView.setText(String.valueOf(hours));

        NumberPicker minutePicker = view.findViewById(R.id.minute_picker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(minutes);
        minutesValueTextView.setText(String.valueOf(minutes));

        NumberPicker secondsPicker = view.findViewById(R.id.second_picker);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);
        secondsPicker.setValue(seconds);
        secondsValueTextView.setText(String.valueOf(seconds));

        hourPicker.setOnValueChangedListener((numberPicker, i, i1) ->
                hourValueTextView.setText(String.valueOf(i1))
        );

        minutePicker.setOnValueChangedListener((numberPicker, i, i1) ->
                minutesValueTextView.setText(String.valueOf(i1))
        );

        secondsPicker.setOnValueChangedListener((numberPicker, i, i1) ->
                secondsValueTextView.setText(String.valueOf(i1))
        );

        builder.setPositiveButton(R.string.ok_btn, (dialog, id) ->
                listener.onDialogPositiveClick(DurationPickerFragment.this)
        )
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                    dialog.dismiss();
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}