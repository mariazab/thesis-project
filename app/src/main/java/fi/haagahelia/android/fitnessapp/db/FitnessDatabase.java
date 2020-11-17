package fi.haagahelia.android.fitnessapp.db;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Weight.class, Challenge.class, ChallengeDate.class, FoodItem.class, FoodDay.class, PhysicalActivity.class, Week.class}, version = 42)
@TypeConverters({Converters.class})
public abstract class FitnessDatabase extends androidx.room.RoomDatabase {

    public abstract WeightDao weightDao();
    public abstract ChallengeDao challengeDao();
    public abstract ChallengeDateDao challengeDateDao();
    public abstract FoodItemDao foodItemDao();
    public abstract FoodDayDao foodDayDao();
    public abstract PhysicalActivityDao physicalActivityDao();
    public abstract WeekDao weekDao();

    private static volatile FitnessDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static FitnessDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (FitnessDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), FitnessDatabase.class, "fitness_database")
                            .fallbackToDestructiveMigration().addCallback(roomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //On create prepopulate the db with the challenges

            databaseWriterExecutor.execute(() -> {
                ChallengeDao challengeDao = INSTANCE.challengeDao();
                //Pre-made challenges

                //1. Eat 400g of fruits and veggies every day
                challengeDao.insert(new Challenge("Eat 400g of fruits and veggies every day", " ", 0, 0, 0, 1, 1));
                //2. Include more green veggies in your daily meals
                challengeDao.insert(new Challenge("Include more green veggies in your daily meals", " ", 0, 0, 0, 2, 0));
                //3. Include whole grains instead and starchy foods in your day (potatoes, oats, bread, rice)
                challengeDao.insert(new Challenge("Include whole grains instead and starchy foods in your day (potatoes, oats, bread, rice)", " ", 0, 0, 0, 3, 0));
                //4. Limit the intake of cow, pig and sheep meat (instead chicken, turkey, fish)
                challengeDao.insert(new Challenge("Limit the intake of cow, pig and sheep meat (instead chicken, turkey, fish)", " ", 0, 0, 0, 4, 0));
                //5.   Include carbs as 50% of your total kcal
                challengeDao.insert(new Challenge("Include carbs as 50% of your total kcal", " ", 0, 0, 0, 5, 1));
                //6.  Include protein as 30% of your total kcal
                challengeDao.insert(new Challenge("Include protein as 30% of your total kcal", " ", 0, 0, 0, 6, 1));
                //7. Limit intake of fats to 30% of your total kcal
                challengeDao.insert(new Challenge("Limit intake of fats to 30% of your total kcal", " ", 0, 0, 0, 7, 1));
                //8. Limit the intake of saturated fats to 10% of your total kcal
                challengeDao.insert(new Challenge("Limit the intake of saturated fats to 10% of your total kcal", " ", 0, 0, 0, 8, 1));
                //9. Limit the intake of trans-fats to lower than 1% of your total kcal
                challengeDao.insert(new Challenge("Limit the intake of trans-fats to lower than 1% of your total kcal", " ", 0, 0, 0, 9, 1));
                //10.  Limit the intake of sugar to 10% of your total kcal
                challengeDao.insert(new Challenge("Limit the intake of sugar to 10% of your total kcal", " ", 0, 0, 0, 10, 1));
                //11.  Limit the intake of sugar to 5% of your total kcal
                challengeDao.insert(new Challenge("Limit the intake of sugar to 5% of your total kcal", " ", 0, 0, 0, 11, 1));
                //12. Limit the intake of salt to 5g
                challengeDao.insert(new Challenge("Limit the intake of salt to 5g", " ", 0, 0, 0, 12, 1));
                //13.  Drink 8 cups of water
                challengeDao.insert(new Challenge("Drink 8 cups of water", " ", 0, 0, 0, 13, 3));
                //14. Eat within your calorie goal
                challengeDao.insert(new Challenge("Eat within your calorie goal", " ", 0, 0, 0, 14, 1));
                //15. Engage in physical activities for at least 150 mins per week
                challengeDao.insert(new Challenge("Engage in physical activities for at least 150 mins per week", " ", 3, 0, 0, 15, 2));
                //16. Stop smoking
                challengeDao.insert(new Challenge("Stop smoking", " ", 0, 0, 0, 16, 0));
                //17. Lower your alcohol intake
                challengeDao.insert(new Challenge("Lower your alcohol intake", " ", 0, 0, 0, 17, 0));

            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            databaseWriterExecutor.execute(() -> {

//                Calendar calendar = Calendar.getInstance();
//                Date date = calendar.getTime();
//
//                List<Week> weekList = weekDao.getAllWeeks().getValue();
//                boolean weekFound = false;
//                int i = 0;
//
//                if(weekList != null){
//                    //Check if there is a week matching current week
//                    while(!weekFound) {
//                        Log.d("FitnessDB", "onOpen: i " + i);
//                        if(weekList.get(i).getWeekNumber() == calendar.get(Calendar.WEEK_OF_YEAR) && weekList.get(i).getWeekYear() == calendar.get(Calendar.YEAR)) {
//                            weekFound = true;
//                            Log.d("FitnessDB", "onOpen: week found ");
//                        } else {
//                            Log.d("FitnessDB", "onOpen: week not found");
//                            i++;
//                        }
//                    }
//                }
//
//                //If there is no week matching the current date, create one
//                if(!weekFound) {
//                    Week week = new Week(calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.YEAR), 0, 0, 0);
//                    weekDao.insert(week);
//                }
            });
        }
    };

}
