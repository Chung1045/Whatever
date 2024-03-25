package com.example.whatever.game;

import java.util.Random;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class LevelTemplate extends AppCompatActivity implements View.OnTouchListener {

    private TextView timerTextView;
    private Utils utils;
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private final Handler timerHandler = new Handler();
    private int seconds;
    private int minutes;
    private int deltaX;
    private int deltaY;
    private boolean isLevelPass = false;
    private final String[] levelPassMessage = new String[]{"Are ya winning son?", "That was quite easy", "As expected"};
    private final String levelHint = "Try tapping the buttons";
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_template);
        View v = findViewById(R.id.view_LevelLayout_LevelTemplate);
        onLevelStart(v);
    }

    private void onLevelStart(View v){
        timerTextView = findViewById(R.id.timer_text_view); // Assuming you have a TextView in your layout for displaying the timer

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        UserPreferences.init(this);

        // change accordingly to the current level
        UserPreferences.editor.putInt(UserPreferences.LAST_PLAYED_LEVEL, -1).commit();

        topBarInit();
        utils = new Utils(this, v, this);
        utils.playEnterLevelSFX();
        listenerInit();
    }

    private void topBarInit(){
        boolean sfx = UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, true);
        ImageView soundBt = findViewById(R.id.button_LevelTemplate_SoundBt);
        if (!sfx) {
            soundBt.setImageResource(R.drawable.ic_volume_muted_24);
        }

        findViewById(R.id.button_LevelTemplate_SettingsBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);

            findViewById(R.id.button_LevelTemplate_SettingsBt).startAnimation(fadeout);
            findViewById(R.id.button_LevelTemplate_ResetBt).startAnimation(fadeout);
            findViewById(R.id.button_LevelTemplate_HintBt).startAnimation(fadeout);
            findViewById(R.id.button_LevelTemplate_SettingsBt).setVisibility(View.GONE);
            findViewById(R.id.button_LevelTemplate_ResetBt).setVisibility(View.GONE);
            findViewById(R.id.button_LevelTemplate_HintBt).setVisibility(View.GONE);
            findViewById(R.id.button_LevelTemplate_CloseBt).startAnimation(fadein);
            findViewById(R.id.button_LevelTemplate_SoundBt).startAnimation(fadein);
            findViewById(R.id.button_LevelTemplate_CloseBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_LevelTemplate_SoundBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_LevelTemplate_CloseBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);
            findViewById(R.id.button_LevelTemplate_CloseBt).startAnimation(fadeout);
            findViewById(R.id.button_LevelTemplate_CloseBt).setVisibility(View.GONE);
            findViewById(R.id.button_LevelTemplate_SoundBt).startAnimation(fadeout);
            findViewById(R.id.button_LevelTemplate_SoundBt).setVisibility(View.GONE);
            findViewById(R.id.button_LevelTemplate_SettingsBt).startAnimation(fadein);
            findViewById(R.id.button_LevelTemplate_ResetBt).startAnimation(fadein);
            findViewById(R.id.button_LevelTemplate_HintBt).startAnimation(fadein);
            findViewById(R.id.button_LevelTemplate_SettingsBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_LevelTemplate_ResetBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_LevelTemplate_HintBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_LevelTemplate_SoundBt).setOnClickListener(view -> {
            ImageView soundButton = findViewById(R.id.button_LevelTemplate_SoundBt);
            UserPreferences.editor.putBoolean(UserPreferences.SFX_ENABLED,!UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED,false));
            UserPreferences.editor.commit();

            if (!UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, false)) {
                soundButton.setImageResource(R.drawable.ic_volume_muted_24);
            } else {
                soundButton.setImageResource(R.drawable.ic_volume_on_24);
            }
        });

        // go back to level selection (android system navigation bar is pressed / back gesture)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(LevelTemplate.this, LevelSelect.class));
            }
        });

        // go back to level selection (Top arrow back icon)
        findViewById(R.id.button_LevelTemplate_NavigateBackBt).setOnClickListener(view -> {
            startActivity(new Intent(LevelTemplate.this, LevelSelect.class));
        });

        findViewById(R.id.button_LevelTemplate_HomeBt).setOnClickListener(view -> {
            startActivity(new Intent(LevelTemplate.this, LevelSelect.class));
        });

        findViewById(R.id.button_LevelTemplate_NextLevelBt).setOnClickListener(view -> {
            startActivity(new Intent(LevelTemplate.this, Level1.class));
        });

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

    @Override
    protected void onPause() {
        super.onPause();
        if (!isLevelPass) {
            elapsedTime = System.currentTimeMillis() - startTime; // Calculate elapsed time
            timerHandler.removeCallbacks(updateTimerThread); // Stop the timer when the activity enters onPause state
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (!isLevelPass) {
            startTime = System.currentTimeMillis() - elapsedTime; // Adjust start time to account for elapsed time
            timerHandler.postDelayed(updateTimerThread, 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerHandler.removeCallbacks(updateTimerThread);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updateTimerThread);
    }

    private void resetState(){
        View textView = findViewById(R.id.timer_text_view);
        textView.setTranslationX(0);
        textView.setTranslationY(0);
        // add you move-able / state changeable elements here
    }

    // Show the level pass screen
    public void onLevelPass(){
        isLevelPass = true;
        timerHandler.removeCallbacks(updateTimerThread);
        TextView timeUsedCount = findViewById(R.id.text_LevelTemplate_TimeUsedText);
        TextView passMessage = findViewById(R.id.text_LevelTemPlate_PassMessage);
        timeUsedCount.setText("" + minutes + ":" + String.format("%02d", seconds));
        passMessage.setText(levelPassMessage[random.nextInt(levelPassMessage.length)]);

        // Random level pass message to be display
        findViewById(R.id.view_LevelTemplate_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_LevelTemplate_Pass).startAnimation(fadeInAnimation);
        utils.playCorrectSFX();
    }

    public void onWrongAttempt(){
        utils.playWrongSFX();
    }

    // AKA Stopwatch Timer
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            long timeInMillis = System.currentTimeMillis() - startTime;

            seconds = (int) (timeInMillis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText("" + minutes + ":" + String.format("%02d", seconds));

            timerHandler.postDelayed(this, 1000);
        }
    };

    // Listener for Level elements
    private void listenerInit() {
        findViewById(R.id.button_LevelTemplate_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });

        findViewById(R.id.button_LevelTemplate_Bt1).setOnClickListener(view -> {
            onWrongAttempt();
        });

        findViewById(R.id.button_LevelTemplate_Bt2).setOnClickListener(view -> {
            onLevelPass();
        });

        findViewById(R.id.button_LevelTemplate_HintBt).setOnClickListener(view -> {
            utils.showSnackBarMessage(levelHint);
        });

        findViewById(R.id.timer_text_view).setOnTouchListener(this);
    }

    // Listener for dragging move-able elements
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



