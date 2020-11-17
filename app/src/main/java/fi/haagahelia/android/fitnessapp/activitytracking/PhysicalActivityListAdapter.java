package fi.haagahelia.android.fitnessapp.activitytracking;

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
import fi.haagahelia.android.fitnessapp.db.PhysicalActivity;

public class PhysicalActivityListAdapter extends RecyclerView.Adapter<PhysicalActivityListAdapter.PhysicalActivityViewHolder> {

    private OnItemListener mOnItemListener;

    public static class PhysicalActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nameTextView;
        private final TextView dateTextView;
        private final TextView minutesTextView;

        OnItemListener onItemListener;

        private PhysicalActivityViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            minutesTextView = itemView.findViewById(R.id.duration_textView);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    private final LayoutInflater inflater;
    private List<PhysicalActivity> physicalActivityList;

    public PhysicalActivityListAdapter(Context context, OnItemListener onItemListener) {
        inflater = LayoutInflater.from(context);
        this.mOnItemListener = onItemListener;
    }

    @Override
    public PhysicalActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.activities_recyclerview_item, parent, false);

        return new PhysicalActivityViewHolder(itemView, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(PhysicalActivityViewHolder holder, int position) {

        if(physicalActivityList != null) {
            PhysicalActivity currentActivity = physicalActivityList.get(position);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentActivity.getDate());

            String dateString = HelperMethods.getDateInStringFormat(calendar);

            holder.nameTextView.setText(currentActivity.getName());
            holder.dateTextView.setText(dateString);

            String duration = HelperMethods.showDurationAsString(currentActivity.getHours(), currentActivity.getMinutes(), currentActivity.getSeconds());

            holder.minutesTextView.setText(duration);
        }
    }

    public void setPhysicalActivities(List<PhysicalActivity> physicalActivities) {
        physicalActivityList = physicalActivities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(physicalActivityList != null) {
            return  physicalActivityList.size();
        } else {
            return 0;
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

}