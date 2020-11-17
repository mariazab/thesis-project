package fi.haagahelia.android.fitnessapp.db;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "physical_activity_table")
public class PhysicalActivity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "physical_activity_id")
    public long id;

    private String name;

    private Date date;

    //Duration fields
    private int hours;
    private int minutes;
    private int seconds;

    //Intensity
    //0 - light
    //1 - moderate
    //3 - vigorous
    private int intensity;

    public PhysicalActivity(String name, Date date, int hours, int minutes, int seconds, int intensity) {
        this.name = name;
        this.date = date;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.intensity = intensity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
