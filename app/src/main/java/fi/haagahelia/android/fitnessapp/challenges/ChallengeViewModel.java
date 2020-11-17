package fi.haagahelia.android.fitnessapp.challenges;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import fi.haagahelia.android.fitnessapp.db.Challenge;
import fi.haagahelia.android.fitnessapp.db.DataRepository;

public class ChallengeViewModel extends AndroidViewModel {

    private DataRepository repository;

    private LiveData<List<Challenge>> allChallenges;
    private LiveData<List<Challenge>> inactiveChallenges;
    private LiveData<List<Challenge>> activeChallenges;
    private LiveData<List<Challenge>> completedChallenges;

    public ChallengeViewModel (Application application) {
        super(application);

        repository = new DataRepository(application);
        allChallenges = repository.getAllChallenges();
        inactiveChallenges = repository.getInactiveChallenges();
        activeChallenges = repository.getActiveChallenges();
        completedChallenges = repository.getCompletedChallenges();
    }

    public LiveData<List<Challenge>> getAllChallenges() {
        return allChallenges;
    }

    public LiveData<List<Challenge>> getInactiveChallenges() {
        return inactiveChallenges;
    }

    public LiveData<List<Challenge>> getActiveChallenges() {
        return activeChallenges;
    }

    public LiveData<List<Challenge>> getCompletedChallenges() {
        return completedChallenges;
    }

    public void insert(Challenge challenge) {
        repository.insertChallenge(challenge);
    }

    public void deleteById(long id) {
        repository.deleteChallengeById(id);
    }

    public void update(long id, String name, String description, int frequency, int state) {
        repository.updateChallenge(id, name, description, frequency, state);
    }

    public void updateState(long id, int state) {
        repository.updateChallengeState(id, state);
    }

    public void resetCompletion(long id) {
        repository.resetChallengeCompletion(id);
    }

    public void adjustCompletion(long id, int offset) {
        repository.adjustChallengeCompletion(id, offset);
    }
}