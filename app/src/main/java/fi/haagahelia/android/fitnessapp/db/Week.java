package fi.haagahelia.android.fitnessapp.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "week_table")
public class Week {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "week_id")
    private long id;

    private int weekNumber;

    private int weekYear;

    //Week Total fields
    private int totalHours;
    private int totalMinutes;
    private int totalSeconds;

    public Week(int weekNumber, int weekYear, int totalHours, int totalMinutes, int totalSeconds) {
        this.weekNumber = weekNumber;
        this.weekYear = weekYear;
        this.totalHours = totalHours;
        this.totalMinutes = totalMinutes;
        this.totalSeconds = totalSeconds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getWeekYear() {
        return weekYear;
    }

    public void setWeekYear(int weekYear) {
        this.weekYear = weekYear;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}
