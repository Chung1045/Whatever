package com.example.whatever.game;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Utils utils;
    private View v;

    private LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserPreferences.init(this);
        View v = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        utils = new Utils(this, v, this);

        layoutInit();
        listenerInit();
    }

    public void listenerInit(){
        findViewById(R.id.home_selectLevelBt).setOnClickListener(this);
        findViewById(R.id.home_lastPlayedBt).setOnClickListener(this);
        findViewById(R.id.image_home_account_image).setOnClickListener(this);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // end program
                finishAffinity();
            }
        });
    }

    public void layoutInit(){
        int lastPlayedLevel = UserPreferences.sharedPref.getInt("lastPlayedLevel", 0);
        if (lastPlayedLevel == 0){
            findViewById(R.id.home_lastPlayedBt).setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.home_selectLevelBt) {
            startActivity(new Intent(this, LevelSelect.class));
        }

        if (view.getId() == R.id.image_home_account_image) {
            startActivity(new Intent(this, ProfileView.class));
        }

        if(view.getId() == R.id.home_lastPlayedBt){
            int level = UserPreferences.sharedPref.getInt("lastPlayedLevel", 0);
            if (level == 1){
                startActivity(new Intent(this, Level1.class));
            } else if (level == 2){
                startActivity(new Intent(this, Level2.class));
            } else if (level == 3){
                startActivity(new Intent(this, Level3.class));
            } else if (level == 4){
                startActivity(new Intent(this, Level4.class));
            } else if (level == 5){
                startActivity(new Intent(this, Level5.class));
            } else if (level == 6){
                startActivity(new Intent(this, Level6.class));
            } else if (level == 7){
                startActivity(new Intent(this, Level7.class));
            } else if (level == 8){
                startActivity(new Intent(this, Level8.class));
            } else if (level == -1){
                startActivity(new Intent(this, LevelTemplate.class));
            }
        }

    }

    // Touch Sound Effect
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Replace R.raw.tap_sound with your actual sound resource ID
            utils.playSFX(R.raw.tap_sfx);
        }
        return super.dispatchTouchEvent(event);
    }
}