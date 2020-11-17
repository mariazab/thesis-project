package fi.haagahelia.android.fitnessapp.activitytracking;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import fi.haagahelia.android.fitnessapp.db.DataRepository;
import fi.haagahelia.android.fitnessapp.db.Week;

public class WeekViewModel extends AndroidViewModel {

    private DataRepository repository;

    private LiveData<List<Week>> allWeeks;

    public WeekViewModel (Application application) {
        super(application);

        repository = new DataRepository(application);
        allWeeks = repository.getAllWeeks();
    }

    public LiveData<List<Week>> getAllWeeks() {
        return allWeeks;
    }

    public void insert(Week week) {
        repository.insertWeek(week);
    }

    public void deleteById(long id) {
        repository.deleteWeekById(id);
    }

    public void addToWeekTotal(long id, int totalHours, int totalMinutes, int totalSeconds) {
        repository.addToWeekTotal(id, totalHours, totalMinutes, totalSeconds);
    }

    public void subtractFromWeekTotal(long id, int totalHours, int totalMinutes, int totalSeconds) {
        repository.subtractFromWeekTotal(id, totalHours, totalMinutes, totalSeconds);
    }
}