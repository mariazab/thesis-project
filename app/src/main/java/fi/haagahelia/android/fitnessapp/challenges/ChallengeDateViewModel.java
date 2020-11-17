package fi.haagahelia.android.fitnessapp.challenges;

import android.app.Application;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import fi.haagahelia.android.fitnessapp.db.ChallengeDate;
import fi.haagahelia.android.fitnessapp.db.DataRepository;

public class ChallengeDateViewModel extends AndroidViewModel {

    private DataRepository repository;

    private LiveData<List<ChallengeDate>> allChallengeDates;

    private LiveData<List<ChallengeDate>> challengeDatesByChallengeId;

    public ChallengeDateViewModel(Application application) {
        super(application);

        repository = new DataRepository(application);
        allChallengeDates = repository.getAllChallengeDates();
    }

    public LiveData<List<ChallengeDate>> getAllChallengeDates() {
        return allChallengeDates;
    }

    public void insert(ChallengeDate challengeDate) {
        repository.insertChallengeDate(challengeDate);
    }

    public void deleteById(long challengeId, Date date) {
        repository.deleteChallengeDate(challengeId, date);
    }

    public void deleteDatesFromChallenge(long challengeId) {
        repository.deleteDatesFromChallenge(challengeId);
    }
}