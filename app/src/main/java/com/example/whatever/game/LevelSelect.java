package com.example.whatever.game;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class LevelSelect extends AppCompatActivity implements View.OnClickListener {

    private Utils utils;
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.activity_level_select, null);
        utils = new Utils(this, v, this);

        listenerInit();
    }

    private void listenerInit() {
        findViewById(R.id.button_selectLevel_Level1).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_Level2).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_Level3).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_Level4).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_Level5).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_Level6).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_Level7).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_Level8).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_Level9).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_Level10).setOnClickListener(this);
        findViewById(R.id.button_selectLevel_NavigateBackBt).setOnClickListener(this);
        findViewById(R.id.image_level_selection_account_icon).setOnClickListener(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(LevelSelect.this, MainActivity.class));
                finish();
            }
        });

    }


    @Override
    public void onClick(View view) {
            if (view.getId() == R.id.button_selectLevel_Level1) {
                startActivity(new Intent(this, Level1.class));
            }
            if (view.getId() == R.id.button_selectLevel_Level2) {
                startActivity(new Intent(this, Level2.class));
            }
            if (view.getId() == R.id.button_selectLevel_Level3) {
                startActivity(new Intent(this, Level3.class));
            }
            if (view.getId() == R.id.button_selectLevel_Level4) {
                startActivity(new Intent(this, Level4.class));
            }
            if (view.getId() == R.id.button_selectLevel_Level5) {
                startActivity(new Intent(this, Level5.class));
            }
            if (view.getId() == R.id.button_selectLevel_Level6) {
                startActivity(new Intent(this, Level6.class));
            }
            if (view.getId() == R.id.button_selectLevel_Level7) {
                startActivity(new Intent(this, Level7.class));
            }
            if (view.getId() == R.id.button_selectLevel_Level8) {
                startActivity(new Intent(this, Level8.class));
            }
            if (view.getId() == R.id.button_selectLevel_Level9) {
                startActivity(new Intent(this, Level9.class));
            }
            if (view.getId() == R.id.button_selectLevel_Level10) {
                startActivity(new Intent(this, Level10.class));
            }
            if (view.getId() == R.id.button_selectLevel_NavigateBackBt) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            if (view.getId() == R.id.image_level_selection_account_icon) {
                startActivity(new Intent(this, ProfileView.class));
            }
        }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Replace R.raw.tap_sound with your actual sound resource ID
            utils.playSFX(R.raw.tap_sfx);
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView avatar = findViewById(R.id.image_level_selection_account_icon);

        if (firebaseHelper.isLoggedIn()){
            utils.getBitmapFromByte(output -> {
                if (!(output == null)){
                    avatar.setColorFilter(Color.TRANSPARENT);
                    avatar.setImageBitmap(output);
                } else {
                    avatar.setImageResource(R.drawable.ic_account_circle_24);
                    avatar.setColorFilter(getColor(R.color.foreground));
                }
            });
        } else {
            avatar.setImageResource(R.drawable.ic_account_circle_24);
            avatar.setColorFilter(getColor(R.color.foreground));
        }

        markUnPlayedLevels();

    }

    private void markUnPlayedLevels(){
        Button Level1bt = findViewById(R.id.button_selectLevel_Level1);
        Button Level2bt = findViewById(R.id.button_selectLevel_Level2);
        Button Level3bt = findViewById(R.id.button_selectLevel_Level3);
        Button Level4bt = findViewById(R.id.button_selectLevel_Level4);
        Button Level5bt = findViewById(R.id.button_selectLevel_Level5);
        Button Level6bt = findViewById(R.id.button_selectLevel_Level6);
        Button Level7bt = findViewById(R.id.button_selectLevel_Level7);
        Button Level8bt = findViewById(R.id.button_selectLevel_Level8);
        Button Level9bt = findViewById(R.id.button_selectLevel_Level9);
        Button Level10bt = findViewById(R.id.button_selectLevel_Level10);

        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL1, 0) == 0){
            Level1bt.setText(getText(R.string.string_levelSelect_Level1bt) + "*");
        }
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0) == 0){
            Level2bt.setText(getText(R.string.string_levelSelect_Level2bt) + "*");
        }
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL3, 0) == 0){
            Level3bt.setText(getText(R.string.string_levelSelect_Level3bt) + "*");
        }
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0) == 0){
            Level4bt.setText(getText(R.string.string_levelSelect_Level4bt) + "*");
        }
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0) == 0){
            Level5bt.setText(getText(R.string.string_levelSelect_Level5bt) + "*");
        }
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0) == 0){
            Level6bt.setText(getText(R.string.string_levelSelect_Level6bt) + "*");
        }
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0) == 0){
            Level7bt.setText(getText(R.string.string_levelSelect_Level7bt) + "*");
        }
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0) == 0){
            Level8bt.setText(getText(R.string.string_levelSelect_Level8bt) + "*");
        }
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL9, 0) == 0){
            Level9bt.setText(getText(R.string.string_levelSelect_Level9bt) + "*");
        }
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL10, 0) == 0){
            Level10bt.setText(getText(R.string.string_levelSelect_Level10bt) + "*");
        }
    }
}