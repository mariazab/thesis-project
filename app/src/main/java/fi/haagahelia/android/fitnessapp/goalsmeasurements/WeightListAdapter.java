package fi.haagahelia.android.fitnessapp.goalsmeasurements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import fi.haagahelia.android.fitnessapp.HelperMethods;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.db.Weight;

public class WeightListAdapter extends RecyclerView.Adapter<WeightListAdapter.WeightViewHolder> {

    private OnItemListener mOnItemListener;

    public static class WeightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView weightTextView;
        private final TextView dateTextView;

        OnItemListener onItemListener;

        private String kg;
        private String noWeightsInDb;

        private WeightViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weight_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            this.onItemListener = onItemListener;

            kg = itemView.getResources().getString(R.string.kilos);
            noWeightsInDb = itemView.getResources().getString(R.string.no_weights_in_db);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    private final LayoutInflater inflater;
    private List<Weight> weightList;

    public WeightListAdapter(Context context, OnItemListener onItemListener) {
        inflater = LayoutInflater.from(context);
        this.mOnItemListener = onItemListener;
    }

    @Override
    public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.weight_recyclerview_item, parent, false);

        return new WeightViewHolder(itemView, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(WeightViewHolder holder, int position) {
        if (weightList != null) {
            Weight currentWeight = weightList.get(position);
            String weightString = currentWeight.getWeight() + holder.kg;
            holder.weightTextView.setText(weightString);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(currentWeight.getDate().getTime());
            holder.dateTextView.setText(HelperMethods.getDateInStringFormat(c));

        } else {
            holder.weightTextView.setText(holder.noWeightsInDb);
        }
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

    public interface OnItemListener {
        void onItemClick(int position);
    }
}