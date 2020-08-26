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

@Database(entities = {Weight.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class FitnessDatabase extends androidx.room.RoomDatabase {

    public abstract WeightDao weightDao();

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
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            databaseWriterExecutor.execute(() -> {
                WeightDao dao = INSTANCE.weightDao();
                dao.deleteAll();

            });
        }
    };

}
