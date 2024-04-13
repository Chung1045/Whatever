package com.example.whatever.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SFX_ENABLED = "sfxEnabled";
    public static final String VIBRATION_ENABLED = "vibrationEnabled";
    public static final String BEST_TIME_LEVEL1 = "bestTimeLevel1";
    public static final String BEST_TIME_LEVEL2 = "bestTimeLevel2";
    public static final String BEST_TIME_LEVEL3 = "bestTimeLevel3";
    public static final String BEST_TIME_LEVEL4 = "bestTimeLevel4";
    public static final String BEST_TIME_LEVEL5 = "bestTimeLevel5";
    public static final String BEST_TIME_LEVEL6 = "bestTimeLevel6";
    public static final String BEST_TIME_LEVEL7 = "bestTimeLevel7";
    public static final String BEST_TIME_LEVEL8 = "bestTimeLevel8";
    public static final String BEST_TIME_LEVEL9 = "bestTimeLevel9";
    public static final String BEST_TIME_LEVEL10 = "bestTimeLevel10";
    public static final String CURRENT_RANK = "currentRank";
    public static final String TOTAL_COMPETITORS = "totalCompetitors";
    public static final String DINO_COUNT = "dinoCount";
    public static final String BEST_TIME_LEVEL_TEMPLATE = "bestTimeLevelTemplate";
    public static final String LAST_PLAYED_LEVEL = "lastPlayedLevel";
    public static final String USER_AVATAR = "userAvatar";
    public static final String BEST_TIME_USED_TOTAL = "bestTimeUsedTotal";

    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    public static void init(Activity a) {
        sharedPref = a.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }


}
