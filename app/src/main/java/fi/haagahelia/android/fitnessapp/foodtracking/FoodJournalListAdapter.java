package fi.haagahelia.android.fitnessapp.foodtracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.db.FoodDay;
import fi.haagahelia.android.fitnessapp.db.FoodItem;
import fi.haagahelia.android.fitnessapp.db.FoodJournalItem;


public class FoodJournalListAdapter extends RecyclerView.Adapter<FoodJournalListAdapter.FoodJournalViewHolder> {

    private String TAG = "Food Adapter";
    private OnItemListener mOnItemListener;

    public static class FoodJournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView foodNameTextView;
        private final TextView foodGramsTextView;
        private final TextView foodKcalTextView;

        OnItemListener onItemListener;

        private FoodJournalViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);

            foodNameTextView = itemView.findViewById(R.id.title_textView);
            foodGramsTextView = itemView.findViewById(R.id.subtitle_grams_textView);
            foodKcalTextView = itemView.findViewById(R.id.subtitle_kcal_textView);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    private final LayoutInflater inflater;
    private List<FoodJournalItem> foodJournalItemList;

    public FoodJournalListAdapter(Context context, OnItemListener onItemListener) {
        inflater = LayoutInflater.from(context);
        this.mOnItemListener = onItemListener;
    }

    @Override
    public FoodJournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.food_recyclervew_item, parent, false);

        return new FoodJournalViewHolder(itemView, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(FoodJournalViewHolder holder, int position) {
        if (foodJournalItemList != null) {
            FoodDay foodDay = foodJournalItemList.get(position).getFoodDay();
            FoodItem foodItem = foodJournalItemList.get(position).getFoodItem();

            holder.foodNameTextView.setText(foodItem.getName());

            String textGrams = foodDay.getGrams() + holder.itemView.getResources().getString(R.string.grams);
            String textKcal = foodItem.getCalories() + holder.itemView.getResources().getString(R.string.kcal);

            holder.foodGramsTextView.setText(textGrams);
            holder.foodKcalTextView.setText(textKcal);
        }
    }

    void setFoodJournalItemList(List<FoodJournalItem> foodJournalList) {
        foodJournalItemList = foodJournalList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (foodJournalItemList != null) {
            return foodJournalItemList.size();
        } else {
            return 0;
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}




