package fi.haagahelia.android.fitnessapp.activitytracking;

import android.app.Application;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import fi.haagahelia.android.fitnessapp.db.DataRepository;
import fi.haagahelia.android.fitnessapp.db.PhysicalActivity;

public class PhysicalActivityViewModel extends AndroidViewModel {

    private DataRepository repository;

    private LiveData<List<PhysicalActivity>> allPhysicalActivities;

    public PhysicalActivityViewModel (Application application) {
        super(application);

        repository = new DataRepository(application);
        allPhysicalActivities = repository.getAllPhysicalActivities();
    }

    public LiveData<List<PhysicalActivity>> getAllPhysicalActivities() {
        return allPhysicalActivities;
    }

    public void insert(PhysicalActivity physicalActivity) {
        repository.insertPhysicalActivity(physicalActivity);
    }

    public void deleteById(long id) {
        repository.deletePhysicalActivity(id);
    }

    public void update(long id, String name, Date date, int hours, int minutes, int seconds, int intensity) {
        repository.updatePhysicalActivity(id, name, date, hours, minutes, seconds, intensity);
    }
}