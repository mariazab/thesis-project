package fi.haagahelia.android.fitnessapp.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ChallengeDao {

    @Insert
    void insert(Challenge challenge);

    @Query("SELECT * FROM challenge_table")
    LiveData<List<Challenge>> getChallenges();

    @Query("SELECT * FROM challenge_table WHERE state=0 OR state=2")
    LiveData<List<Challenge>> getInactiveChallenges();

    @Query("SELECT * FROM challenge_table WHERE state=1")
    LiveData<List<Challenge>> getActiveChallenges();

    @Query("SELECT * FROM challenge_table WHERE state=2")
    LiveData<List<Challenge>> getCompletedChallenges();

    @Query("DELETE FROM challenge_table WHERE challenge_id=:id")
    void deleteById(long id);

    @Query("UPDATE challenge_table SET name=:name, description=:description, frequency=:frequency, state=:state WHERE challenge_id=:id")
    void update(long id, String name, String description, int frequency, int state);

    @Query("UPDATE challenge_table SET state=:state WHERE challenge_id=:id")
    void updateState(long id, int state);

    @Query("UPDATE challenge_table SET completion = 0 WHERE challenge_id=:id")
    void resetCompletion(long id);

    @Query("UPDATE challenge_table SET completion = completion + :offset WHERE challenge_id=:id")
    void adjustCompletion(long id, int offset);

}
