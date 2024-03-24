package com.example.whatever.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SFX_ENABLED = "sfxEnabled";
    public static final String UNLOCKED_LEVELS = "unlockedLevels";
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    public static void init(Activity a) {
        sharedPref = a.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }
}
