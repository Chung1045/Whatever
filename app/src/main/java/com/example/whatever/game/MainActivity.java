package com.example.whatever.game;

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

        listenerInit();
    }

    public void listenerInit(){
        findViewById(R.id.home_selectLevelBt).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, LevelSelect.class);
        startActivity(intent);
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