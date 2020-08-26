package fi.haagahelia.android.fitnessapp.db;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.LiveData;

public class WeightRepository {

    private WeightDao weightDao;
    private LiveData<List<Weight>> allWeights;

    WeightRepository(Application application) {
        FitnessDatabase db = FitnessDatabase.getDatabase(application);
        weightDao = db.weightDao();
        allWeights = weightDao.getWeights();
    }

    LiveData<List<Weight>> getAllWeights() {
        return allWeights;
    }

    void insert(Weight weight) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                weightDao.insert(weight);
            }
        });
    }
}

