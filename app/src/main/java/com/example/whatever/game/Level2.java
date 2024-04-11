package com.example.whatever.game;

import android.annotation.SuppressLint;
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

import java.util.Random;

public class Level2 extends AppCompatActivity implements View.OnTouchListener {
    private Utils utils;
    private long startTime = 0L, elapsedTime = 0L;
    private final Handler timerHandler = new Handler();
    private int minutes, seconds, milliseconds, deltaX, deltaY;
    private long timeUsedInMilliseconds;
    private boolean isLevelPass = false;
    private final String[] levelPassMessage = new String[]{"\"Totally\" Correct", "Can it become as big as the universe?", "Yes Cuz why not?"};
    private final String levelHint = "Who is the \"biggest?\"";
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level2);
        View v = findViewById(R.id.view_LevelLayout_Level2);
        onLevelStart(v);
    }

    private void onLevelStart(View v) {

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        UserPreferences.init(this);

        // change accordingly to the current level
        UserPreferences.editor.putInt(UserPreferences.LAST_PLAYED_LEVEL, 2).commit();

        topBarInit();
        utils = new Utils(this, v, this);
        utils.playEnterLevelSFX();
        listenerInit();
    }

    private void topBarInit() {
        boolean sfx = UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, true);
        ImageView soundBt = findViewById(R.id.button_Level2_SoundBt);
        if (!sfx) {
            soundBt.setImageResource(R.drawable.ic_volume_muted_24);
        }

        findViewById(R.id.button_Level2_SettingsBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);

            findViewById(R.id.button_Level2_SettingsBt).startAnimation(fadeout);
            findViewById(R.id.button_Level2_ResetBt).startAnimation(fadeout);
            findViewById(R.id.button_Level2_HintBt).startAnimation(fadeout);
            findViewById(R.id.button_Level2_SettingsBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level2_ResetBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level2_HintBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level2_CloseBt).startAnimation(fadein);
            findViewById(R.id.button_Level2_SoundBt).startAnimation(fadein);
            findViewById(R.id.button_Level2_CloseBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level2_SoundBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level2_CloseBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);
            findViewById(R.id.button_Level2_CloseBt).startAnimation(fadeout);
            findViewById(R.id.button_Level2_CloseBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level2_SoundBt).startAnimation(fadeout);
            findViewById(R.id.button_Level2_SoundBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level2_SettingsBt).startAnimation(fadein);
            findViewById(R.id.button_Level2_ResetBt).startAnimation(fadein);
            findViewById(R.id.button_Level2_HintBt).startAnimation(fadein);
            findViewById(R.id.button_Level2_SettingsBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level2_ResetBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level2_HintBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level2_SoundBt).setOnClickListener(view -> {
            ImageView soundButton = findViewById(R.id.button_Level2_SoundBt);
            UserPreferences.editor.putBoolean(UserPreferences.SFX_ENABLED, !UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, false)).commit();

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
                startActivity(new Intent(com.example.whatever.game.Level2.this, LevelSelect.class));
                finish();
            }
        });

        // go back to level selection (Top arrow back icon)
        findViewById(R.id.button_Level2_NavigateBackBt).setOnClickListener(view -> {
            startActivity(new Intent(com.example.whatever.game.Level2.this, LevelSelect.class));
            finish();
        });

        findViewById(R.id.button_Level2_HomeBt).setOnClickListener(view -> {
            startActivity(new Intent(com.example.whatever.game.Level2.this, LevelSelect.class));
            finish();
        });

        findViewById(R.id.button_Level2_NextLevelBt).setOnClickListener(view -> {
            startActivity(new Intent(com.example.whatever.game.Level2.this, Level3.class));
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
    protected void onResume() {
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
    public void onLevelPass() {
        isLevelPass = true;
        timerHandler.removeCallbacks(updateTimerThread);
        TextView timeUsedCount = findViewById(R.id.text_Level2_TimeUsedText);
        TextView passMessage = findViewById(R.id.text_LevelTemPlate_PassMessage);
        timeUsedCount.setText(minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds));
        passMessage.setText(levelPassMessage[random.nextInt(levelPassMessage.length)]);

        updateBestTime();

        // Random level pass message to be display
        findViewById(R.id.view_Level2_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_Level2_Pass).startAnimation(fadeInAnimation);
        utils.playCorrectSFX();
    }

    private void updateBestTime() {
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0L) == 0L ||
                timeUsedInMilliseconds < UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0L)) {
            UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL2, timeUsedInMilliseconds).commit();
            TextView timeUsedTitle = findViewById(R.id.text_Level2_TimeUsedTitle);
            timeUsedTitle.setText("New best time : ");
            findViewById(R.id.text_Level2_BestTimeUsedTitle).setVisibility(View.GONE);
            findViewById(R.id.text_Level2_Best_TimeUsedText).setVisibility(View.GONE);
        } else {
            Long bestTime = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL2, 0L);
            TextView bestTimeUsed = findViewById(R.id.text_Level2_Best_TimeUsedText);

            long seconds = bestTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            milliseconds = (int) (bestTime % 1000);

            bestTimeUsed.setText(String.format("%02d:%02d:%03d", minutes, seconds, milliseconds));

        }
    }

    public void onWrongAttempt() {
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

            // Update the timer string to include minutes, seconds, and milliseconds
            timerHandler.postDelayed(this, 10); // Update interval changed to 50ms for smoother milliseconds display
        }
    };

    // Listener for Level elements
    private void listenerInit() {
        findViewById(R.id.button_Level2_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });

        findViewById(R.id.button_Level2_HintBt).setOnClickListener(view -> {
            utils.showSnackBarMessage(levelHint);
        });

        findViewById(R.id.image_Level2_fifth).setOnClickListener(view -> {
            onLevelPass();
        });

        findViewById(R.id.image_Level2_forth).setOnClickListener(view -> {
            startActivity(new Intent(Level2.this, LevelSelect.class));
            finish();
        });

        findViewById(R.id.image_Level2_third).setOnClickListener(view -> {
            onWrongAttempt();
        });

        findViewById(R.id.image_Level2_second).setOnClickListener(view -> {
            onWrongAttempt();
        });

        findViewById(R.id.image_Level2_first).setOnClickListener(view -> {
            onWrongAttempt();
        });

    }

    private void resetState() {
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
}