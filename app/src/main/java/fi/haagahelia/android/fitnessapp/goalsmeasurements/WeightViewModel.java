package fi.haagahelia.android.fitnessapp.goalsmeasurements;

import android.app.Application;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import fi.haagahelia.android.fitnessapp.db.DataRepository;
import fi.haagahelia.android.fitnessapp.db.Weight;

public class WeightViewModel extends AndroidViewModel {

    private DataRepository repository;

    private LiveData<List<Weight>> allWeights;

    public WeightViewModel (Application application) {
        super(application);

        repository = new DataRepository(application);
        allWeights = repository.getAllWeights();
    }

    public LiveData<List<Weight>> getAllWeights() {
        return allWeights;
    }

    public void insert(Weight weight) {
        repository.insertWeight(weight);
    }

    public void deleteById(long id) {
        repository.deleteWeightById(id);
    }

    public void update(long id, Date date, double weight) {
        repository.updateWeight(id, date, weight);
    }

}