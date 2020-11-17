package fi.haagahelia.android.fitnessapp.db;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weight_table")
public class Weight {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "weight_id")
    private long id;

    private Date date;

    private double weight;

    public Weight(Date date, double weight) {
        this.date = date;
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
