package com.example.whatever.game;

import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class Level8 extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {
    private Utils utils;
    private long startTime = 0L, elapsedTime = 0L;
    private final Handler timerHandler = new Handler();
    private int minutes, seconds, milliseconds, deltaX, deltaY;
    private long timeUsedInMilliseconds;
    private boolean isLevelPass = false, holdMode = false;
    private final String[] levelPassMessage = new String[]{"That was hard isn't it?", "Ever played Rotaneo?", "Shh, there a better way to do it"};
    private final String[] levelHint = new String[]{"It seems theres a better way to do it", "If you can't reach the goal, then why not try to let the goal reach you?", "Why work hard when you can work smart?"};
    private final Random random = new Random();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ImageView[] walls;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level8);
        View v = findViewById(R.id.view_LevelLayout_Level8);
        wallsInit();
        onLevelStart(v);
        handler.post(interactMode);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    private void wallsInit() {
        walls = new ImageView[26];
        for (int i = 0; i < 26; i++) {
            int resId = getResources().getIdentifier("image_level8_wall" + (i + 1), "id", getPackageName());
            walls[i] = findViewById(resId);
        }
    }

    private void onLevelStart(View v){

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        UserPreferences.init(this);

        // change accordingly to the current level
        UserPreferences.editor.putInt(UserPreferences.LAST_PLAYED_LEVEL, 8).commit();

        topBarInit();
        utils = new Utils(this, v, this);
        utils.playEnterLevelSFX();
        listenerInit();
    }

    private void topBarInit(){
        boolean sfx = UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, true);
        ImageView soundBt = findViewById(R.id.button_Level8_SoundBt);
        if (!sfx) {
            soundBt.setImageResource(R.drawable.ic_volume_muted_24);
        }

        findViewById(R.id.button_Level8_SettingsBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);

            findViewById(R.id.button_Level8_SettingsBt).startAnimation(fadeout);
            findViewById(R.id.button_Level8_ResetBt).startAnimation(fadeout);
            findViewById(R.id.button_Level8_HintBt).startAnimation(fadeout);
            findViewById(R.id.button_Level8_SettingsBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level8_ResetBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level8_HintBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level8_CloseBt).startAnimation(fadein);
            findViewById(R.id.button_Level8_SoundBt).startAnimation(fadein);
            findViewById(R.id.button_Level8_CloseBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level8_SoundBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level8_CloseBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);
            findViewById(R.id.button_Level8_CloseBt).startAnimation(fadeout);
            findViewById(R.id.button_Level8_CloseBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level8_SoundBt).startAnimation(fadeout);
            findViewById(R.id.button_Level8_SoundBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level8_SettingsBt).startAnimation(fadein);
            findViewById(R.id.button_Level8_ResetBt).startAnimation(fadein);
            findViewById(R.id.button_Level8_HintBt).startAnimation(fadein);
            findViewById(R.id.button_Level8_SettingsBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level8_ResetBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level8_HintBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level8_SoundBt).setOnClickListener(view -> {
            ImageView soundButton = findViewById(R.id.button_Level8_SoundBt);
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
                startActivity(new Intent(Level8.this, LevelSelect.class));
            }
        });

        // go back to level selection (Top arrow back icon)
        findViewById(R.id.button_Level8_NavigateBackBt).setOnClickListener(view -> {
            startActivity(new Intent(Level8.this, LevelSelect.class));
        });

        findViewById(R.id.button_Level8_HomeBt).setOnClickListener(view -> {
            startActivity(new Intent(Level8.this, LevelSelect.class));
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isLevelPass) {
            elapsedTime = System.currentTimeMillis() - startTime; // Calculate elapsed time
            timerHandler.removeCallbacks(updateTimerThread); // Stop the timer when the activity enters onPause state
            sensorManager.unregisterListener(this);
            handler.removeCallbacks(interactMode);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (!isLevelPass) {
            startTime = System.currentTimeMillis() - elapsedTime; // Adjust start time to account for elapsed time
            timerHandler.postDelayed(updateTimerThread, 0);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            handler.post(interactMode);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerHandler.removeCallbacks(updateTimerThread);
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(interactMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updateTimerThread);
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(interactMode);
    }

    // Show the level pass screen
    public void onLevelPass(){
        isLevelPass = true;
        timerHandler.removeCallbacks(updateTimerThread);
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(interactMode);

        TextView timeUsedCount = findViewById(R.id.text_Level8_TimeUsedText);
        TextView passMessage = findViewById(R.id.text_LevelTemPlate_PassMessage);
        timeUsedCount.setText("" + minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds));
        passMessage.setText(levelPassMessage[random.nextInt(levelPassMessage.length)]);

        updateBestTime();

        // Random level pass message to be display
        findViewById(R.id.view_Level8_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_Level8_Pass).startAnimation(fadeInAnimation);
        utils.playCorrectSFX();
    }

    private void updateBestTime(){
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0L) == 0L ||
                timeUsedInMilliseconds < UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0L)){
            UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL8, timeUsedInMilliseconds).commit();
            TextView timeUsedTitle = findViewById(R.id.text_Level8_TimeUsedTitle);
            timeUsedTitle.setText("New best time : ");
            findViewById(R.id.text_Level8_BestTimeUsedTitle).setVisibility(View.GONE);
            findViewById(R.id.text_Level8_Best_TimeUsedText).setVisibility(View.GONE);
        } else {
            Long bestTime = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL8, 0L);
            TextView bestTimeUsed = findViewById(R.id.text_Level8_Best_TimeUsedText);

            long seconds = bestTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            milliseconds = (int) (bestTime % 1000);

            bestTimeUsed.setText(String.format("%02d:%02d:%03d", minutes, seconds, milliseconds));

        }
    }

    public void onWrongAttempt(){
        utils.playWrongSFX();
    }

    // AKA Stopwatch Timer
    private Runnable updateTimerThread = new Runnable() {
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
        findViewById(R.id.button_Level8_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });

        findViewById(R.id.button_Level8_HintBt).setOnClickListener(view -> {
            utils.showSnackBarMessage(levelHint[random.nextInt(levelHint.length)]);
        });

        findViewById(R.id.image_level8_exit).setOnTouchListener(this);
        // place your element listener here

        findViewById(R.id.image_level8_ball).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        holdMode = true;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // Move the view with the touch
                        ImageView pinball = findViewById(R.id.image_level8_ball);
                        pinball.setX(pinball.getX() + event.getX());
                        pinball.setY(pinball.getY() + event.getY());

                        if (isTouchWalls(pinball, walls)){
                            pinball.setTranslationX(0);
                            pinball.setTranslationY(0);
                            onWrongAttempt();
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        holdMode = false;
                        break;
                }
                return true;
            }
        });

    }

    private void resetState(){
        ImageView exit = findViewById(R.id.image_level8_exit);
        exit.setTranslationX(0);
        exit.setTranslationY(0);

        ImageView pinball = findViewById(R.id.image_level8_ball);
        pinball.setTranslationX(0);
        pinball.setTranslationY(0);
        // add you move-able / state changeable elements here
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
    public void onSensorChanged(SensorEvent sensorEvent) {
        ImageView pinball = findViewById(R.id.image_level8_ball);

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];

            // Scale down accelerometer values for translation
            float scaleFactor = 0.6f; // Adjust as needed
            float translationX = x * scaleFactor;
            float translationY = y * scaleFactor;

            // Update ball position
            pinball.setX(pinball.getX() - translationX);
            pinball.setY(pinball.getY() + translationY);


            if (isTouchWalls(pinball, walls)){
                pinball.setTranslationX(0);
                pinball.setTranslationY(0);
                onWrongAttempt();
            }

            if (isTouchExit(pinball)){
                onLevelPass();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public boolean isTouchWalls(View targetView, View[] views) {
        Rect targetRect = new Rect();
        targetView.getGlobalVisibleRect(targetRect);

        for (View view : views) {
            if (view.equals(targetView)) {
                continue; // Skip checking against itself
            }

            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);

            if (rect.intersects(targetRect.left, targetRect.top, targetRect.right, targetRect.bottom)) {
                return true; // If any view intersects with the target, return true
            }
        }

        return false; // None of the views intersect with the target
    }

    public boolean isTouchExit(View targetView) {
        Rect targetRect = new Rect();
        targetView.getGlobalVisibleRect(targetRect);

        Rect exitRect = new Rect();
        findViewById(R.id.image_level8_exit).getGlobalVisibleRect(exitRect);

        return targetRect.intersect(exitRect.left, exitRect.top, exitRect.right, exitRect.bottom);
    }

    private Runnable interactMode = new Runnable() {
        @Override
        public void run() {
            if (holdMode) {
                sensorManager.unregisterListener(Level8.this);
            }  else sensorManager.registerListener(Level8.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
    };

}