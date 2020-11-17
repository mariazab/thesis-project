package fi.haagahelia.android.fitnessapp.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "challenge_table")
public class Challenge {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "challenge_id")
    public long id;

    private String name;

    private String description;

    /* Frequency:
    0 - daily
    1 - 3xweek
    2 - 2xweek
    3 - 1xweek
     */
    private int frequency;

    //State 0 - inactive, 1 - active, 2 - completed
    private int state;

    private int completion;

    private int informationTextId;

    //0 -basic
    //1 - food
    //2 - activity
    //3 - water
    private int type;

    public Challenge(String name, String description, int frequency, int state, int completion, int informationTextId, int type) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.state = state;
        this.completion = completion;
        this.informationTextId = informationTextId;
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(int completion) {
        this.completion = completion;
    }

    public int getInformationTextId() {
        return informationTextId;
    }

    public void setInformationTextId(int informationTextId) {
        this.informationTextId = informationTextId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
