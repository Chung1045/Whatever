package com.example.whatever.game;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

public class LevelSelect extends AppCompatActivity implements View.OnClickListener {

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        View v = LayoutInflater.from(this).inflate(R.layout.activity_level_select, null);
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
        findViewById(R.id.button_selectLevel_templateLevel).setOnClickListener(this);
        findViewById(R.id.image_level_selection_account_icon).setOnClickListener(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(LevelSelect.this, MainActivity.class));
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
            }
            if (view.getId() == R.id.button_selectLevel_templateLevel) {
                startActivity(new Intent(this, LevelTemplate.class));
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

}