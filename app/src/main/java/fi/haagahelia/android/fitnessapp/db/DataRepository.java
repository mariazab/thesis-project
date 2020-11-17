package fi.haagahelia.android.fitnessapp.db;

import android.app.Application;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;

public class DataRepository {

    //Weight
    private WeightDao weightDao;
    private LiveData<List<Weight>> allWeights;

    //Challenges
    private ChallengeDao challengeDao;
    private LiveData<List<Challenge>> allChallenges;
    private LiveData<List<Challenge>> inactiveChallenges;
    private LiveData<List<Challenge>> activeChallenges;
    private LiveData<List<Challenge>> completedChallenges;

    //ChallengeDate
    private ChallengeDateDao challengeDateDao;
    private LiveData<List<ChallengeDate>> allChallengeDates;
    private LiveData<List<ChallengeDate>> challengeDatesByChallengeId;

    //FoodItem
    private FoodItemDao foodItemDao;
    private LiveData<List<FoodItem>> allFoodItems;

    //FoodDay
    private FoodDayDao foodDayDao;
    private LiveData<List<FoodDay>> foodDayList;
    private LiveData<List<FoodJournalItem>> foodJournalList;

    //PhysicalActivity
    private PhysicalActivityDao physicalActivityDao;
    private LiveData<List<PhysicalActivity>> allPhysicalActivities;

    //Week
    private WeekDao weekDao;
    private LiveData<List<Week>> allWeeks;

    public DataRepository(Application application) {
        FitnessDatabase db = FitnessDatabase.getDatabase(application);

        weightDao = db.weightDao();
        allWeights = weightDao.getWeights();

        challengeDao = db.challengeDao();
        allChallenges = challengeDao.getChallenges();
        inactiveChallenges = challengeDao.getInactiveChallenges();
        activeChallenges = challengeDao.getActiveChallenges();
        completedChallenges = challengeDao.getCompletedChallenges();

        challengeDateDao = db.challengeDateDao();
        allChallengeDates = challengeDateDao.getAllChallengeDates();

        foodItemDao = db.foodItemDao();
        allFoodItems = foodItemDao.getFoodItems();

        foodDayDao = db.foodDayDao();
        foodDayList = foodDayDao.getAllFoodDays();
        foodJournalList = foodDayDao.getFoodJournal();

        physicalActivityDao = db.physicalActivityDao();
        allPhysicalActivities = physicalActivityDao.getAllPhysicalActivities();

        weekDao = db.weekDao();
        allWeeks = weekDao.getAllWeeks();

    }

    //Weight methods
    public LiveData<List<Weight>> getAllWeights() {
        return allWeights;
    }

    public void insertWeight(Weight weight) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                weightDao.insert(weight);
            }
        });
    }

    public void deleteWeightById(long id) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                weightDao.deleteById(id);
            }
        });
    }

    public void updateWeight(long id, Date date, double weight) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                weightDao.update(id, date, weight);
            }
        });
    }

    //Challenge methods
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

    public void insertChallenge(Challenge challenge) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                challengeDao.insert(challenge);
            }
        });
    }

    public void deleteChallengeById(long id) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                challengeDao.deleteById(id);
            }
        });
    }

    public void updateChallenge(long id, String name, String description, int frequency, int state) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                challengeDao.update(id, name, description, frequency, state);
            }
        });
    }

    public void updateChallengeState(long id, int state) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                challengeDao.updateState(id, state);
            }
        });
    }

    public void resetChallengeCompletion(long id) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                challengeDao.resetCompletion(id);
            }
        });
    }

    public void adjustChallengeCompletion(long id, int offset) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                challengeDao.adjustCompletion(id, offset);
            }
        });
    }

    //ChallengeDate methods
    public LiveData<List<ChallengeDate>> getAllChallengeDates() {
        return allChallengeDates;
    }

    public LiveData<List<ChallengeDate>> getChallengeDatesByChallengeId(long challengeId) {
        challengeDatesByChallengeId = challengeDateDao.getChallengeDatesByChallengeId(challengeId);

        return challengeDatesByChallengeId;
    }

    public void insertChallengeDate(ChallengeDate challengeDate) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                challengeDateDao.insert(challengeDate);
            }
        });
    }

    public void deleteChallengeDate(long challengeId, Date date) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                challengeDateDao.deleteOne(challengeId, date);
            }
        });
    }

    public void deleteDatesFromChallenge(long challengeId) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                challengeDateDao.deleteDatesFromChallenge(challengeId);
            }
        });
    }

    //FoodItem methods
    public LiveData<List<FoodItem>> getAllFoodItems() {
        return allFoodItems;
    }

    public void insertFoodItem(FoodItem foodItem) {
        FitnessDatabase.databaseWriterExecutor.execute(() -> {
            foodItemDao.insert(foodItem);
        });
    }

    public void deleteFoodItem(long id) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                foodItemDao.deleteById(id);
            }
        });
    }

    //FoodDay methods
    public LiveData<List<FoodDay>> getFoodDayList() {
        return foodDayList;
    }

    public LiveData<List<FoodJournalItem>> getFoodJournalList() {
        return foodJournalList;
    }

    public void insertFoodDay(FoodDay foodDay) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                foodDayDao.insert(foodDay);
            }
        });
    }

    public void deleteFoodDayItem(long id) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                foodDayDao.deleteById(id);
            }
        });
    }

    //FoodItem and FoodDay
    public void insertFoodItemAndFoodDay(FoodItem foodItem, Date date, double grams) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long id = foodItemDao.insertWithReturn(foodItem);
                FoodDay foodDay = new FoodDay(date, id, grams);
                foodDayDao.insert(foodDay);
            }
        });
    }

    public void updateFoodItemAndFoodDay(long foodItemId, String name, int calories, double carbs, double protein, double fat, double saturatedFat, double transFat, double sugar, double salt, boolean fruitVeggie, long foodDayId, double grams) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                foodItemDao.updateFoodItem(foodItemId, name, calories, carbs, protein, fat, saturatedFat, transFat, sugar, salt, fruitVeggie);
                foodDayDao.updateFoodDayGrams(foodDayId, grams);
            }
        });
    }

    //PhysicalActivity
    public LiveData<List<PhysicalActivity>> getAllPhysicalActivities() {
        return allPhysicalActivities;
    }

    public void insertPhysicalActivity(PhysicalActivity physicalActivity) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                physicalActivityDao.insert(physicalActivity);
            }
        });
    }

    public void deletePhysicalActivity(long id) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                physicalActivityDao.deleteById(id);
            }
        });
    }

    public void updatePhysicalActivity(long id, String name, Date date, int hours, int minutes, int seconds, int intensity) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                physicalActivityDao.update(id, name, date, hours, minutes, seconds, intensity);
            }
        });
    }

    //Week
    public LiveData<List<Week>> getAllWeeks() {
        return allWeeks;
    }

    public void insertWeek(Week week) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                weekDao.insert(week);
            }
        });
    }

    public void deleteWeekById(long id) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                weekDao.deleteById(id);
            }
        });
    }

    public void addToWeekTotal(long id, int totalHours, int totalMinutes, int totalSeconds) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                weekDao.addToWeekTotal(id, totalHours, totalMinutes, totalSeconds);
            }
        });
    }

    public void subtractFromWeekTotal(long id, int totalHours, int totalMinutes, int totalSeconds) {
        FitnessDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                weekDao.subtractFromWeekTotal(id, totalHours, totalMinutes, totalSeconds);
            }
        });
    }

    public Week getWeekById(long id) {
        return weekDao.getWeekById(id);
    }
}