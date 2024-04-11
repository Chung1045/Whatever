package com.example.whatever.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Level4 extends AppCompatActivity implements View.OnTouchListener {
    private Utils utils;
    private long startTime = 0L, elapsedTime = 0L;
    private final Handler timerHandler = new Handler();
    private int minutes, seconds, milliseconds, deltaX, deltaY;
    private long timeUsedInMilliseconds;
    private boolean isLevelPass = false;
    private final String[] levelPassMessage = new String[]{"You must be good at math isnt it?", "I wonder how big it can be......", "Can we count through aleph null?"};
    private final String levelHint = "It's a fibonacci sequence, just google it";
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level4);
        View v = findViewById(R.id.view_LevelLayout_Level4);
        onLevelStart(v);
    }

    private void onLevelStart(View v){

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        UserPreferences.init(this);

        // change accordingly to the current level
        UserPreferences.editor.putInt(UserPreferences.LAST_PLAYED_LEVEL, 4).commit();

        topBarInit();
        utils = new Utils(this, v, this);
        utils.playEnterLevelSFX();
        listenerInit();
    }

    private void topBarInit(){
        boolean sfx = UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, true);
        ImageView soundBt = findViewById(R.id.button_Level4_SoundBt);
        if (!sfx) {
            soundBt.setImageResource(R.drawable.ic_volume_muted_24);
        }

        findViewById(R.id.button_Level4_SettingsBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(500);
            fadeout.setDuration(500);

            findViewById(R.id.button_Level4_SettingsBt).startAnimation(fadeout);
            findViewById(R.id.button_Level4_ResetBt).startAnimation(fadeout);
            findViewById(R.id.button_Level4_HintBt).startAnimation(fadeout);
            findViewById(R.id.button_Level4_SettingsBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level4_ResetBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level4_HintBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level4_CloseBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level4_SoundBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level4_CloseBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level4_SoundBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level4_CloseBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(500);
            fadeout.setDuration(500);
            findViewById(R.id.button_Level4_CloseBt).startAnimation(fadeout);
            findViewById(R.id.button_Level4_CloseBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level4_SoundBt).startAnimation(fadeout);
            findViewById(R.id.button_Level4_SoundBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level4_SettingsBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level4_ResetBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level4_HintBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level4_SettingsBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level4_ResetBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level4_HintBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level4_SoundBt).setOnClickListener(view -> {
            ImageView soundButton = findViewById(R.id.button_Level4_SoundBt);
            UserPreferences.editor.putBoolean(UserPreferences.SFX_ENABLED,!UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED,false)).commit();

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
                startActivity(new Intent(Level4.this, LevelSelect.class));
                finish();
            }
        });

        // go back to level selection (Top arrow back icon)
        findViewById(R.id.button_Level4_NavigateBackBt).setOnClickListener(view -> {
            startActivity(new Intent(Level4.this, LevelSelect.class));
            finish();
        });

        findViewById(R.id.button_Level4_HomeBt).setOnClickListener(view -> {
            startActivity(new Intent(Level4.this, LevelSelect.class));
            finish();
        });

        findViewById(R.id.button_Level4_NextLevelBt).setOnClickListener(view -> {
            startActivity(new Intent(Level4.this, Level5.class));
            finish();
        });

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

    // Show the level pass screen
    public void onLevelPass(){
        isLevelPass = true;
        timerHandler.removeCallbacks(updateTimerThread);
        TextView timeUsedCount = findViewById(R.id.text_Level4_TimeUsedText);
        TextView passMessage = findViewById(R.id.text_LevelTemPlate_PassMessage);
        timeUsedCount.setText(minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds));
        passMessage.setText(levelPassMessage[random.nextInt(levelPassMessage.length)]);

        updateBestTime();

        // Random level pass message to be display
        findViewById(R.id.view_Level4_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_Level4_Pass).startAnimation(fadeInAnimation);
        utils.playCorrectSFX();
    }

    private void updateBestTime(){
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0L) == 0L ||
                timeUsedInMilliseconds < UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0L)){
            UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL4, timeUsedInMilliseconds).commit();
            TextView timeUsedTitle = findViewById(R.id.text_Level4_TimeUsedTitle);
            timeUsedTitle.setText("New best time : ");
            findViewById(R.id.text_Level4_BestTimeUsedTitle).setVisibility(View.GONE);
            findViewById(R.id.text_Level4_Best_TimeUsedText).setVisibility(View.GONE);
        } else {
            Long bestTime = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL4, 0L);
            TextView bestTimeUsed = findViewById(R.id.text_Level4_Best_TimeUsedText);

            long seconds = bestTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            milliseconds = (int) (bestTime % 1000);

            bestTimeUsed.setText(String.format("%02d:%02d:%03d", minutes, seconds, milliseconds));

        }
    }

    public void onWrongAttempt(){
        EditText answer = findViewById(R.id.text_Level4_Answer);
        answer.setError("Wrong");
        utils.playWrongSFX();
    }

    // AKA Stopwatch Timer
    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeUsedInMilliseconds = System.currentTimeMillis() - startTime;

            seconds = (int) (timeUsedInMilliseconds / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            milliseconds = (int) (timeUsedInMilliseconds % 1000); // Calculate milliseconds part
            timerHandler.postDelayed(this, 10); // Update interval changed to 50ms for smoother milliseconds display
        }
    };

    // Listener for Level elements
    private void listenerInit() {
        findViewById(R.id.button_Level4_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });

        EditText answer = findViewById(R.id.text_Level4_Answer);
        findViewById(R.id.button_Level4_ResetAnswerBt).setOnClickListener(v -> answer.setText(""));

        findViewById(R.id.button_Level4_Enter2Bt).setOnClickListener(view -> {
            answer.setError(null);
            if (!answer.getText().toString().isEmpty()){
                if(Integer.parseInt(answer.getText().toString()) == 13){
                    closeKeyboard();
                    onLevelPass();
                } else {
                    onWrongAttempt();
                }
            } else {
                onWrongAttempt();
            }
        });

        findViewById(R.id.button_Level4_HintBt).setOnClickListener(view -> utils.showSnackBarMessage(levelHint));

        // place your element listener here
    }

    private void resetState(){
        View textView = findViewById(R.id.timer_text_view);
        textView.setTranslationX(0);
        textView.setTranslationY(0);
        // add you move-able / state changeable elements here
    }

    // Listener for dragging move-able elements
    @SuppressLint("ClickableViewAccessibility")
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

    // Touch Sound Effect
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Replace R.raw.tap_sound with your actual sound resource ID
            utils.playSFX(R.raw.tap_sfx);
        }
        return super.dispatchTouchEvent(event);
    }
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}