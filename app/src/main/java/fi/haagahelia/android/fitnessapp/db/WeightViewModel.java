package fi.haagahelia.android.fitnessapp.db;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class WeightViewModel extends AndroidViewModel {

    private WeightRepository repository;

    private LiveData<List<Weight>> allWeights;

    public WeightViewModel (Application application) {
        super(application);

        repository = new WeightRepository(application);
        allWeights = repository.getAllWeights();
    }

    public LiveData<List<Weight>> getAllWeights() {
        return allWeights;
    }

    public void insert(Weight weight) {
        repository.insert(weight);
    }
}
