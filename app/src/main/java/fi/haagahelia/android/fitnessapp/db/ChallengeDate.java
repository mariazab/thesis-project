package fi.haagahelia.android.fitnessapp.db;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "challenge_date_table", primaryKeys = {"challengeId", "date"}, foreignKeys = @ForeignKey(entity = Challenge.class, parentColumns = "challenge_id", childColumns = "challengeId", onDelete = ForeignKey.CASCADE))
public class ChallengeDate {

    public long challengeId;

    @NonNull
    private Date date;

    public ChallengeDate(long challengeId, Date date) {
        this.challengeId = challengeId;
        this.date = date;
    }

    public long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(long challengeId) {
        this.challengeId = challengeId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
