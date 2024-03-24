package com.example.whatever.game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class LevelTemplate extends AppCompatActivity implements View.OnTouchListener {

    private TextView timerTextView;
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private Handler timerHandler = new Handler();
    private long timeInMillis = 0L;
    private long updatedTime = 0L;
    private int seconds;
    private int minutes;
    private int deltaX;
    private int deltaY;
    private boolean isLevelPass = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_template);

        timerTextView = findViewById(R.id.timer_text_view); // Assuming you have a TextView in your layout for displaying the timer

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        listenerInit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updateTimerThread);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerHandler.removeCallbacks(updateTimerThread);
    }

    private void resetState(){
        View textView = findViewById(R.id.timer_text_view);
        textView.setTranslationX(0);
        textView.setTranslationY(0);
    }

    @Override
    protected void onPause() {
        if (!isLevelPass) {
            super.onPause();
            elapsedTime = System.currentTimeMillis() - startTime; // Calculate elapsed time
            timerHandler.removeCallbacks(updateTimerThread); // Stop the timer when the activity enters onPause state
        }
    }

    @Override
    protected void onResume(){
        if (!isLevelPass) {
            super.onResume();
            startTime = System.currentTimeMillis() - elapsedTime; // Adjust start time to account for elapsed time
            timerHandler.postDelayed(updateTimerThread, 0);
        }
    }

    public void onLevelPass(){
        isLevelPass = true;
        timerHandler.removeCallbacks(updateTimerThread);
        TextView timeUsedCount = findViewById(R.id.text_LevelTemplate_TimeUsedText);
        timeUsedCount.setText("" + minutes + ":" + String.format("%02d", seconds));
        findViewById(R.id.view_LevelTemplate_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_LevelTemplate_Pass).startAnimation(fadeInAnimation);

    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMillis = System.currentTimeMillis() - startTime;
            updatedTime = timeInMillis;

            seconds = (int) (updatedTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText("" + minutes + ":" + String.format("%02d", seconds));

            timerHandler.postDelayed(this, 1000);
        }
    };

    private void listenerInit() {
        findViewById(R.id.button_LevelTemplate_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });

        findViewById(R.id.button_LevelTemplate_Bt1).setOnClickListener(view -> {
            findViewById(R.id.button_LevelTemplate_Bt2).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_LevelTemplate_Bt2).setOnClickListener(view -> {
            onLevelPass();
        });

        findViewById(R.id.timer_text_view).setOnTouchListener(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(LevelTemplate.this, LevelSelect.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_LevelTemplate_HomeBt).setOnClickListener(view -> {
            Intent intent = new Intent(LevelTemplate.this, LevelSelect.class);
            startActivity(intent);
        });

        findViewById(R.id.button_LevelTemplate_NextLevelBt).setOnClickListener(view -> {
            Intent intent = new Intent(LevelTemplate.this, Level1.class);
            startActivity(intent);
        });

        findViewById(R.id.button_LevelTemplate_NavigateBackBt).setOnClickListener(view -> {
            Intent intent = new Intent(LevelTemplate.this, LevelSelect.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) { // move elements
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // Capture initial touch position
                deltaX = (int) (view.getX() - event.getRawX());
                deltaY = (int) (view.getY() - event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                // Move the view with the touch
                view.animate()
                        .x(event.getRawX() + deltaX)
                        .y(event.getRawY() + deltaY)
                        .setDuration(0)
                        .start();
                break;
            default:
                return false;
        }
        return true;
    }

}



