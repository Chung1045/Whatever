package com.example.whatever.game;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class Level10 extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {
    private Utils utils;
    private long startTime = 0L, elapsedTime = 0L;
    private final Handler timerHandler = new Handler();
    private int minutes, seconds, milliseconds, deltaX, deltaY;
    private long timeUsedInMilliseconds;
    private boolean isLevelPass = false;
    private String[] levelPassMessage;
    private String[] levelHint;
    private final Random random = new Random();
    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private long touchStartTime = 0L;
    private boolean isTouchingUnlockIndicator = false, canVibrate = false, isVibrate1 = false, isVibrate2 = false;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level10);
        View v = findViewById(android.R.id.content);
        onLevelStart(v);

        levelPassMessage = getResources().getStringArray(R.array.string_level10_level_pass_messages_array);
        levelHint = getResources().getStringArray(R.array.string_Level10_hints_array);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

    }


    private void onLevelStart(View v){

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        UserPreferences.init(this);

        // change accordingly to the current level
        UserPreferences.editor.putInt(UserPreferences.LAST_PLAYED_LEVEL, 10).commit();

        topBarInit();
        utils = new Utils(this, v, this);
        utils.playEnterLevelSFX();
        listenerInit();
    }

    private void topBarInit(){
        boolean sfx = UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, true);
        ImageView soundBt = findViewById(R.id.button_Level10_SoundBt);
        if (!sfx) {
            soundBt.setImageResource(R.drawable.ic_volume_muted_24);
        }

        findViewById(R.id.button_Level10_SettingsBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(500);
            fadeout.setDuration(500);

            findViewById(R.id.button_Level10_SettingsBt).startAnimation(fadeout);
            findViewById(R.id.button_Level10_ResetBt).startAnimation(fadeout);
            findViewById(R.id.button_Level10_HintBt).startAnimation(fadeout);
            findViewById(R.id.button_Level10_SettingsBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level10_ResetBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level10_HintBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level10_CloseBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level10_SoundBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level10_CloseBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level10_SoundBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level10_CloseBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(500);
            fadeout.setDuration(500);
            findViewById(R.id.button_Level10_CloseBt).startAnimation(fadeout);
            findViewById(R.id.button_Level10_CloseBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level10_SoundBt).startAnimation(fadeout);
            findViewById(R.id.button_Level10_SoundBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level10_SettingsBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level10_ResetBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level10_HintBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level10_SettingsBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level10_ResetBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level10_HintBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level10_SoundBt).setOnClickListener(view -> {
            ImageView soundButton = findViewById(R.id.button_Level10_SoundBt);
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
                startActivity(new Intent(Level10.this, LevelSelect.class));
                finish();
            }
        });

        // go back to level selection (Top arrow back icon)
        findViewById(R.id.button_Level10_NavigateBackBt).setOnClickListener(view -> {
            startActivity(new Intent(Level10.this, LevelSelect.class));
            finish();
        });

        findViewById(R.id.button_Level10_HomeBt).setOnClickListener(view -> {
            startActivity(new Intent(Level10.this, LevelSelect.class));
            finish();
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isLevelPass) {
            elapsedTime = System.currentTimeMillis() - startTime; // Calculate elapsed time
            timerHandler.removeCallbacks(updateTimerThread); // Stop the timer when the activity enters onPause state
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (!isLevelPass) {
            startTime = System.currentTimeMillis() - elapsedTime; // Adjust start time to account for elapsed time
            timerHandler.postDelayed(updateTimerThread, 0);
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerHandler.removeCallbacks(updateTimerThread);
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updateTimerThread);
        sensorManager.unregisterListener(this);
    }

    // Show the level pass screen
    public void onLevelPass(){
        isLevelPass = true;

        TextView timeUsedCount = findViewById(R.id.text_Level10_TimeUsedText);
        TextView passMessage = findViewById(R.id.text_LevelTemPlate_PassMessage);
        timeUsedCount.setText(minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds));
        passMessage.setText(levelPassMessage[random.nextInt(levelPassMessage.length)]);

        updateBestTime();

        // Random level pass message to be display
        findViewById(R.id.view_Level10_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_Level10_Pass).startAnimation(fadeInAnimation);
        utils.playCorrectSFX();
    }

    private void updateBestTime(){
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL10, 0L) == 0L ||
                timeUsedInMilliseconds < UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL10, 0L)){
            UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL10, timeUsedInMilliseconds).commit();
            TextView timeUsedTitle = findViewById(R.id.text_Level10_TimeUsedTitle);
            timeUsedTitle.setText("New best time : ");
            findViewById(R.id.text_Level10_BestTimeUsedTitle).setVisibility(View.GONE);
            findViewById(R.id.text_Level10_Best_TimeUsedText).setVisibility(View.GONE);
        } else {
            long bestTime = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL10, 0L);
            TextView bestTimeUsed = findViewById(R.id.text_Level10_Best_TimeUsedText);

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
    @SuppressLint("ClickableViewAccessibility")
    private void listenerInit() {
        findViewById(R.id.button_Level10_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });

        findViewById(R.id.button_Level10_HintBt).setOnClickListener(view -> utils.showSnackBarMessage(levelHint[random.nextInt(levelHint.length)]));
        // place your element listener here

    }

    private void resetState(){
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        ImageView knobSwitch = findViewById(R.id.image_level10_knob_rotation_indicator);
        ImageView unlockIndicator = findViewById(R.id.image_level10_knob_unlock_indicator);
        TextView countdown = findViewById(R.id.text_Level10_Countdown);

        knobSwitch.setPivotX(knobSwitch.getWidth() / 2);
        knobSwitch.setPivotY(knobSwitch.getHeight());

        // Check if the sensor type is rotation vector
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Get rotation values from the sensor event
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            float[] orientationValues = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationValues);

            // Calculate rotation angle (in degrees) around the z-axis
            float rotationAngle = (float) Math.toDegrees(orientationValues[0]);

            // Rotate the ImageView based on the rotation angle
            knobSwitch.setRotation(rotationAngle);

            if (Math.floor(rotationAngle) % 5 == 0 && canVibrate) {
                utils.vibrateTick();
                //utils.playSFX(R.raw.sfx_level10_safe_knob_click);
                canVibrate = false;
            } else if (Math.floor(rotationAngle) % 5 != 0) {
                canVibrate = true;
            }

            // Check if the knob switch touches the unlock indicator
            Rect knobRect = new Rect();
            knobSwitch.getHitRect(knobRect);
            Rect unlockRect = new Rect();
            unlockIndicator.getHitRect(unlockRect);
            if (knobRect.intersect(unlockRect)) {
                if (!isTouchingUnlockIndicator) {
                    // Start timer when knob switch starts touching unlock indicator
                    isTouchingUnlockIndicator = true;
                    touchStartTime = System.currentTimeMillis();
                    handler.postDelayed(unlockCheckRunnable, 0);
                    countdown.setText("3");
                    utils.vibrateHeavyTick();
                }
            } else {
                // Knob switch is not touching the unlock indicator
                isTouchingUnlockIndicator = false;
                handler.removeCallbacks(unlockCheckRunnable);
                countdown.setText(null);
                isVibrate1 = false;
                isVibrate2 = false;
            }

        }
    }

    // Runnable to check if knob switch continues to touch unlock indicator for 3 seconds
    private final Runnable unlockCheckRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            TextView countdown = findViewById(R.id.text_Level10_Countdown);
            if (currentTime - touchStartTime >= 1000 && !isVibrate1) {
                countdown.setText("2");
                utils.vibrateHeavyTick();
                isVibrate1 = true;
            }
            if (currentTime - touchStartTime >= 2000 &&!isVibrate2) {
                countdown.setText("1");
                utils.vibrateHeavyTick();
                isVibrate2 = true;
            }
            if (currentTime - touchStartTime >= 3000) {
                // Knob switch has been touching unlock indicator for 3 seconds
                // Trigger unlock action here
                countdown.setText(null);
                onUnlockAction();
            } else {
                // Continue checking if knob switch is touching unlock indicator
                handler.postDelayed(this, 100); // Check every 100 milliseconds
            }
        }
    };

    // Method to perform unlock action
    private void onUnlockAction() {
        handler.removeCallbacks(unlockCheckRunnable);
        sensorManager.unregisterListener(this);
        timerHandler.removeCallbacks(updateTimerThread);

        ConstraintLayout safePanel = findViewById(R.id.view_level10_safe_panel_layout);
        safePanel.setPivotX(0);
        safePanel.setPivotY((float) safePanel.getHeight() / 2);

        Animator doorOpenAnimationPart1 = AnimatorInflater.loadAnimator(this, R.animator.animator_level10_safe_open_part1);
        doorOpenAnimationPart1.setTarget(safePanel);

        Animator doorOpenAnimationPart2 = AnimatorInflater.loadAnimator(this, R.animator.animator_level10_safe_open_part2);
        doorOpenAnimationPart2.setTarget(safePanel);

        ImageView unlockIndicator = findViewById(R.id.image_level10_safe_unlock_status);

        unlockIndicator.setColorFilter(R.color.safe_unlocked);
        utils.playSFX(R.raw.sfx_level10_safe_unlock);
        new Handler().postDelayed(() -> {
            utils.playSFX(R.raw.sfx_level10_safe_door_open);
            doorOpenAnimationPart1.start();
            new Handler().postDelayed(() -> {
                unlockIndicator.setVisibility(View.GONE);
                findViewById(R.id.image_level10_knob_background).setVisibility(View.GONE);
                findViewById(R.id.image_level10_knob_unlock_indicator).setVisibility(View.GONE);
                findViewById(R.id.image_level10_knob_switch).setVisibility(View.GONE);
                findViewById(R.id.image_level10_knob_rotation_indicator).setVisibility(View.GONE);
                doorOpenAnimationPart2.start();
                new Handler().postDelayed(this::onLevelPass, 2000);
            }, 500);
        },1500);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Empty
    }

}