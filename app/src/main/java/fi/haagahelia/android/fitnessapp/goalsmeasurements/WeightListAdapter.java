package fi.haagahelia.android.fitnessapp.goalsmeasurements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.db.Weight;

public class WeightListAdapter extends RecyclerView.Adapter<WeightListAdapter.WeightViewHolder> {

    public static class WeightViewHolder extends RecyclerView.ViewHolder {
        private final TextView weightTextView;
        private final TextView dateTextView;

        private String kg;
        private String noWeightsInDb;

        private WeightViewHolder(View itemView) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weight_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);

            kg = itemView.getResources().getString(R.string.kilos);
            noWeightsInDb = itemView.getResources().getString(R.string.no_weights_in_db);
        }
    }

    private final LayoutInflater inflater;
    private List<Weight> weightList;

    public WeightListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.weight_recyclerview_item, parent, false);
        return new WeightViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WeightViewHolder holder, int position) {
        if (weightList != null) {
            Weight currentWeight = weightList.get(position);
            holder.weightTextView.setText(currentWeight.getWeight() + holder.kg);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(currentWeight.getDate().getTime());
            holder.dateTextView.setText(getDateInStringFormat(c));
        } else {
            holder.weightTextView.setText(holder.noWeightsInDb);
        }
    }

    //Method for formatting the date in the format XX.XX.XXXX
    private String getDateInStringFormat(Calendar c) {
        String date = "";
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

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
        return date;
    }

    void setWeights(List<Weight> weights) {
        weightList = weights;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (weightList != null) {
            return weightList.size();
        }
        else {
            return 0;
        }
    }

}
