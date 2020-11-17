package fi.haagahelia.android.fitnessapp.challenges;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import fi.haagahelia.android.fitnessapp.R;
import fi.haagahelia.android.fitnessapp.db.Challenge;

public class ChallengeListAdapter extends RecyclerView.Adapter<ChallengeListAdapter.ChallengeViewHolder> {

    private OnItemListener mOnItemListener;

    private boolean areChallengesActive;

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleTextView;
        private final TextView subtitleTextView;

        OnItemListener onItemListener;
        boolean challengesActive;

        private ChallengeViewHolder(View itemView, OnItemListener onItemListener, boolean challengesActive) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title_textView);
            subtitleTextView = itemView.findViewById(R.id.subtitle_textView);
            this.onItemListener = onItemListener;
            this.challengesActive = challengesActive;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition(), challengesActive);
        }
    }

    private final LayoutInflater inflater;
    private List<Challenge> challengeList;
    //private boolean challengesActive;

    public ChallengeListAdapter(Context context, OnItemListener onItemListener) {
        inflater = LayoutInflater.from(context);
        this.mOnItemListener = onItemListener;
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.challenges_recyclerview_item, parent, false);

        return new ChallengeViewHolder(itemView, mOnItemListener, areChallengesActive);
    }

    @Override
    public void onBindViewHolder(ChallengeViewHolder holder, int position) {

        if(challengeList != null) {
            String completed;
            Challenge currentChallenge = challengeList.get(position);

            if(currentChallenge.getFrequency() > 0) {
                completed = holder.itemView.getResources().getString(R.string.weeks_complete);
            } else {
                completed = holder.itemView.getResources().getString(R.string.days_complete);
            }

            holder.titleTextView.setText(currentChallenge.getName());
            if(areChallengesActive) {
                String fullCompletedString = currentChallenge.getCompletion() + completed;
                holder.subtitleTextView.setText(fullCompletedString);
            } else {
                holder.subtitleTextView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setChallenges(List<Challenge> challenges, boolean challengesActive) {
        challengeList = challenges;
        //this.challengesActive = challengesActive;
        areChallengesActive = challengesActive;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(challengeList != null) {
            return  challengeList.size();
        } else {
            return 0;
        }
    }

    public interface OnItemListener {
        void onItemClick(int position, boolean challengesActive);
    }

}