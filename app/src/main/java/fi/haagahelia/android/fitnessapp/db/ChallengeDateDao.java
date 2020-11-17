package fi.haagahelia.android.fitnessapp.db;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ChallengeDateDao {

    @Insert
    void insert(ChallengeDate challengeDate);

    @Query("SELECT * FROM challenge_date_table ORDER BY date")
    LiveData<List<ChallengeDate>> getAllChallengeDates();

    @Query("SELECT * FROM challenge_date_table WHERE challengeId=:challengeId")
    LiveData<List<ChallengeDate>> getChallengeDatesByChallengeId(long challengeId);

    @Query("DELETE FROM challenge_date_table WHERE challengeId=:challengeId AND date=:date")
    void deleteOne(long challengeId, Date date);

    @Query("DELETE FROM challenge_date_table WHERE challengeId=:challengeId")
    void deleteDatesFromChallenge(long challengeId);
}
