package fi.haagahelia.android.fitnessapp;

public final class Constants {

    //DB Operation Type Constants
    public static final String OPERATION_TYPE = "OPERATION_TYPE";
    public static final String OPERATION_UPDATE = "OPERATION_UPDATE";
    public static final String OPERATION_SAVE = "OPERATION_SAVE";
    public static final String OPERATION_DELETE = "OPERATION_DELETE";

    //Other Global Constants
    public static final String DATE_YEAR = "DATE_YEAR";
    public static final String DATE_MONTH = "DATE_MONTH";
    public static final String DATE_DAY = "DATE_DAY";

    //Notification channel id
    public static final String CHANNEL_ID = "1";

    //Request codes
    public static final int NEW_WEIGHT_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_CHALLENGE_REQUEST_CODE = 2;
    public static final int EDIT_CHALLENGE_REQUEST_CODE = 3;
    public static final int ADD_FOOD_REQUEST_CODE = 4;
    public static final int EDIT_FOOD_REQUEST_CODE = 5;
    public static final int ADD_ACTIVITY_REQUEST_CODE = 6;
    public static final int EDIT_ACTIVITY_REQUEST_CODE = 7;

    //Goal Constants
    //Goal Preference
    public static final String GOAL_PREFERENCE_NAME = "GOAL_PREFERENCE";
    public static final String GOAL_TYPE_KEY = "GOAL_TYPE";
    public static final String GOAL_WEIGHT_KEY = "GOAL_WEIGHT";
    public static final String DAILY_KCAL_KEY = "DAILY_KCAL";
    public static final String WEEKLY_MINS_KEY = "WEEKLY_MINS";
    public static final String TEE_KEY = "TEE";
    public static final String LOSE_WEIGHT_GOAL = "Lose weight";
    public static final String GAIN_WEIGHT_GOAL = "Gain weight";
    public static final String MAINTAIN_WEIGHT_GOAL = "Maintain weight";

    //Weight Constants
    public static final String WEIGHT_ID = "WEIGHT_ID";
    public static final String WEIGHT = "WEIGHT";

    //Challenge constants
    public static final String CHALLENGE_ID = "CHALLENGE_ID";
    public static final String CHALLENGE_NAME = "CHALLENGE_NAME";
    public static final String CHALLENGE_DESCRIPTION = "CHALLENGE_DESCRIPTION";
    public static final String CHALLENGE_FREQUENCY = "CHALLENGE_FREQUENCY";
    public static final String CHALLENGE_STATE = "CHALLENGE_STATE";
    public static final String CHALLENGE_COMPLETION = "CHALLENGE_COMPLETION";
    public static final String CHALLENGE_INFO_ID = "CHALLENGE_INFO_ID";
    public static final String CHALLENGE_TYPE = "CHALLENGE_TYPE";

    //Challenge preference
    public static final String CHALLENGE_TRACKING_PREFERENCE = "CHALLENGE_TRACKING";
    public static final String WATER_KEY = "WATER_KEY";
    public static final String WATER_DATE_KEY = "WATER_DATE_KEY";

    //Food constants
    public static final String FOOD_DAY_ID = "REPLY_FOOD_DAY_ID";
    public static final String FOOD_ITEM_ID = "REPLY_FOOD_ITEM_ID";
    public static final String FOOD_JOURNAL_DATE = "FOOD_JOURNAL_DATE";

    public static final String FOOD_NAME = "FOOD_NAME";
    public static final String FOOD_CALORIES = "FOOD_CALORIES";
    public static final String FOOD_GRAMS = "FOOD_GRAMS";
    public static final String FOOD_CARBS = "FOOD_CARBS";
    public static final String FOOD_PROTEIN = "FOOD_PROTEIN";
    public static final String FOOD_FAT = "FOOD_FAT";
    public static final String FOOD_SAT_FAT = "FOOD_SAT_FAT";
    public static final String FOOD_TRANS_FAT = "FOOD_TRANS_FAT";
    public static final String FOOD_SUGAR = "FOOD_SUGAR";
    public static final String FOOD_SALT = "FOOD_SALT";
    public static final String FOOD_FRUIT_VEGGIE = "FOOD_FRUIT_VEGGIE";

    public static final String IS_FOOD_FROM_LIST = "IS_FOOD_FROM_LIST";

    //Food challenge type constants
    public static final String CALORIE_KEY = "calorie";
    public static final String CARBS_KEY = "carbs";
    public static final String PROTEIN_KEY = "protein";
    public static final String FAT_KEY = "fat";
    public static final String SAT_FAT_KEY = "saturated";
    public static final String TRANS_FAT_KEY = "trans";
    public static final String SUGAR_KEY = "sugar";
    public static final String SALT_KEY = "salt";
    public static final String FRUIT_VEGGIE_KEY = "fruit";

    public static final int FAT_KCAL_PER_GRAM = 9;
    public static final int SUGAR_KCAL_PER_GRAM = 4;
    public static final int CARBS_KCAL_PER_GRAM = 4;
    public static final int PROTEIN_KCAL_PER_GRAM = 4;

    //Activity constants
    public static final String ACTIVITY_ID = "ACTIVITY_ID";
    public static final String ACTIVITY_NAME = "ACTIVITY_NAME";
    public static final String ACTIVITY_HOURS = "ACTIVITY_HOURS";
    public static final String ACTIVITY_MINUTES = "ACTIVITY_MINUTES";
    public static final String ACTIVITY_SECONDS = "ACTIVITY_SECONDS";
    public static final String ACTIVITY_INTENSITY = "ACTIVITY_INTENSITY";
    public static final String ACTIVITY_DURATION = "ACTIVITY_DURATION";

    //Activity editing constants
    public static final String ACTIVITY_ORIGINAL_HOURS = "ACTIVITY_ORIGINAL_HOURS";
    public static final String ACTIVITY_ORIGINAL_MINUTES = "ACTIVITY_ORIGINAL_MINUTES";
    public static final String ACTIVITY_ORIGINAL_SECONDS = "ACTIVITY_EDITED_SECONDS";
    public static final String ACTIVITY_ORIGINAL_WEEK_NUMBER = "ACTIVITY_ORIGINAL_WEEK_NUMBER";
    public static final String ACTIVITY_ORIGINAL_WEEK_YEAR = "ACTIVITY_ORIGINAL_WEEK_YEAR";

    public static final boolean EXTRA_REPLY_DELETE = true;

}