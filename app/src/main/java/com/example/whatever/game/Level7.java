package com.example.whatever.game;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
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
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Calendar;
import java.util.Random;

public class Level7 extends AppCompatActivity implements View.OnTouchListener {

    private Utils utils;
    private long startTime = 0L, elapsedTime = 0L;
    private final Handler timerHandler = new Handler();
    private final Handler darkEnvironmentHandler = new Handler();
    private int minutes, seconds, milliseconds, deltaX, deltaY;
    private int curtainState = 0;
    private long timeUsedInMilliseconds;
    private boolean isLevelPass = false;
    private String[] levelPassMessage;
    private String[] levelHint;
    private final Random random = new Random();
    private boolean isSwitchOn = true, isLampOn = true;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private View layoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level7);
        layoutView = findViewById(R.id.view_LevelLayout_Level7);

        levelPassMessage = getResources().getStringArray(R.array.string_level7_level_pass_messages_array);
        levelHint = getResources().getStringArray(R.array.string_Level7_hints_array);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        onLevelStart(layoutView);

    }

    private void onLevelStart(View v){

        startTime = System.currentTimeMillis();

        UserPreferences.init(this);

        // change accordingly to the current level
        UserPreferences.editor.putInt(UserPreferences.LAST_PLAYED_LEVEL, 7).commit();

        topBarInit();
        utils = new Utils(this, v, this);
        utils.playEnterLevelSFX();
        listenerInit();
        windowView();
    }

    private void topBarInit(){
        boolean sfx = UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, true);
        ImageView soundBt = findViewById(R.id.button_Level7_SoundBt);
        if (!sfx) {
            soundBt.setImageResource(R.drawable.ic_volume_muted_24);
        }

        findViewById(R.id.button_Level7_SettingsBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);

            findViewById(R.id.button_Level7_SettingsBt).startAnimation(fadeout);
            findViewById(R.id.button_Level7_ResetBt).startAnimation(fadeout);
            findViewById(R.id.button_Level7_HintBt).startAnimation(fadeout);
            findViewById(R.id.button_Level7_SettingsBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level7_ResetBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level7_HintBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level7_CloseBt).startAnimation(fadein);
            findViewById(R.id.button_Level7_SoundBt).startAnimation(fadein);
            findViewById(R.id.button_Level7_CloseBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level7_SoundBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level7_CloseBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(500);
            fadeout.setDuration(500);
            findViewById(R.id.button_Level7_CloseBt).startAnimation(fadeout);
            findViewById(R.id.button_Level7_CloseBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level7_SoundBt).startAnimation(fadeout);
            findViewById(R.id.button_Level7_SoundBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level7_SettingsBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level7_ResetBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level7_HintBt).startAnimation(fadeIn);
            findViewById(R.id.button_Level7_SettingsBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level7_ResetBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level7_HintBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level7_SoundBt).setOnClickListener(view -> {
            ImageView soundButton = findViewById(R.id.button_Level7_SoundBt);
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
                startActivity(new Intent(Level7.this, LevelSelect.class));
            }
        });

        // go back to level selection (Top arrow back icon)
        findViewById(R.id.button_Level7_NavigateBackBt).setOnClickListener(view -> startActivity(new Intent(Level7.this, LevelSelect.class)));

        findViewById(R.id.button_Level7_HomeBt).setOnClickListener(view -> startActivity(new Intent(Level7.this, LevelSelect.class)));

        findViewById(R.id.button_Level7_NextLevelBt).setOnClickListener(view -> startActivity(new Intent(Level7.this, Level8.class)));

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isLevelPass) {
            elapsedTime = System.currentTimeMillis() - startTime; // Calculate elapsed time
            timerHandler.removeCallbacks(updateTimerThread); // Stop the timer when the activity enters onPause state
        }
        timerHandler.removeCallbacks(updateTimerThread);
        darkEnvironmentHandler.removeCallbacks(checkDarkEnvironment);
        sensorManager.unregisterListener(lightSensorListener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (!isLevelPass) {
            startTime = System.currentTimeMillis() - elapsedTime; // Adjust start time to account for elapsed time
            timerHandler.postDelayed(updateTimerThread, 0);
            darkEnvironmentHandler.post(checkDarkEnvironment);
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerHandler.removeCallbacks(updateTimerThread);
        darkEnvironmentHandler.removeCallbacks(checkDarkEnvironment);
        sensorManager.unregisterListener(lightSensorListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updateTimerThread);
        darkEnvironmentHandler.removeCallbacks(checkDarkEnvironment);
        sensorManager.unregisterListener(lightSensorListener);
    }

    // Show the level pass screen
    public void onLevelPass(){
        isLevelPass = true;
        timerHandler.removeCallbacks(updateTimerThread);
        darkEnvironmentHandler.removeCallbacks(checkDarkEnvironment);
        sensorManager.unregisterListener(lightSensorListener);

        TextView timeUsedCount = findViewById(R.id.text_Level7_TimeUsedText);
        TextView passMessage = findViewById(R.id.text_LevelTemPlate_PassMessage);
        timeUsedCount.setText(minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds));
        passMessage.setText(levelPassMessage[random.nextInt(levelPassMessage.length)]);

        updateBestTime();

        // Random level pass message to be display
        findViewById(R.id.view_Level7_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_Level7_Pass).startAnimation(fadeInAnimation);
        utils.playCorrectSFX();
    }

    private void updateBestTime(){
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0L) == 0L ||
                timeUsedInMilliseconds < UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0L)){
            UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL7, timeUsedInMilliseconds).commit();
            TextView timeUsedTitle = findViewById(R.id.text_Level7_TimeUsedTitle);
            timeUsedTitle.setText("New best time : ");
            findViewById(R.id.text_Level7_BestTimeUsedTitle).setVisibility(View.GONE);
            findViewById(R.id.text_Level7_Best_TimeUsedText).setVisibility(View.GONE);
        } else {
            long bestTime = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL7, 0L);
            TextView bestTimeUsed = findViewById(R.id.text_Level7_Best_TimeUsedText);

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

    private Runnable checkDarkEnvironment = () -> {
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        ImageView lightSwitch = findViewById(R.id.image_Level7_Switch);
        ImageView light = findViewById(R.id.image_Level7_Light);
        ImageView darkOverlay = findViewById(R.id.image_level7_dark_overlay);
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                isSwitchOn = false;

                lightSwitch.setImageResource(R.drawable.image_level7_switch_off);
                light.setImageResource(R.drawable.image_level7_light_off);
                darkOverlay.setVisibility(View.VISIBLE);
                utils.playSFX(R.raw.sfx_level7_switch_toggle);

                if (!isLampOn) {
                    ImageView lamp = findViewById(R.id.image_Level7_Lamp);
                    lamp.setImageResource(R.drawable.image_level7_lamp_off);
                }

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                isSwitchOn = true;

                lightSwitch.setImageResource(R.drawable.image_level7_switch_on);
                light.setImageResource(R.drawable.image_level7_light_on);
                darkOverlay.setVisibility(View.GONE);
                utils.playSFX(R.raw.sfx_level7_switch_toggle);

                if (!isLampOn) {
                    ImageView lamp = findViewById(R.id.image_Level7_Lamp);
                    lamp.setImageResource(R.drawable.image_level7_lamp);
                }

                break;


        }

    };

    private void windowView(){
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        ImageView window = findViewById(R.id.image_Level7_Window);

        if (currentHour >= 6 && currentHour < 12) {
            window.setImageResource(R.drawable.image_level7_window_morning);
        } else if (currentHour >= 12 && currentHour < 18) {
            window.setImageResource(R.drawable.image_level7_window_noon);
        } else if (currentHour >= 18 && currentHour < 21) {
            window.setImageResource(R.drawable.image_level7_window_evening);
        } else {
            window.setImageResource(R.drawable.image_level7_window_night);
        }
    }

    // Listener for Level elements
    private void listenerInit() {
        findViewById(R.id.button_Level7_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });

        findViewById(R.id.button_Level7_HintBt).setOnClickListener(view -> utils.showSnackBarMessage(levelHint[random.nextInt(levelHint.length)]));

        // place your element listener here

        findViewById(R.id.image_Level7_Cabinet).setOnClickListener(view -> onWrongAttempt());

        findViewById(R.id.image_Level7_Light).setOnClickListener(view -> onWrongAttempt());

        findViewById(R.id.image_Level7_Switch).setOnClickListener(view -> onWrongAttempt());

        findViewById(R.id.image_Level7_Bed).setOnClickListener(view -> {
            if (!isSwitchOn && !isLampOn && curtainState == 2) {
                onLevelPass();
            } else onWrongAttempt();
        });

        findViewById(R.id.image_Level7_Lamp).setOnClickListener(view -> {
            ImageView lamp = findViewById(R.id.image_Level7_Lamp);
            ImageView lampGlow = findViewById(R.id.image_Level7_LampGlow);
            isLampOn =!isLampOn;
            if (isLampOn) {
                lampGlow.setVisibility(View.VISIBLE);
                lamp.setImageResource(R.drawable.image_level7_lamp);
                utils.playSFX(R.raw.sfx_level7_lamp_on);
            } else {
                lampGlow.setVisibility(View.GONE);
                if (isSwitchOn){
                    lamp.setImageResource(R.drawable.image_level7_lamp);
                } else lamp.setImageResource(R.drawable.image_level7_lamp_off);
                utils.playSFX(R.raw.sfx_level7_lamp_off);
            }
        });

        findViewById(R.id.image_Level7_Curtain).setOnClickListener(view -> {
            ImageView curtain = findViewById(R.id.image_Level7_Curtain);

            if (curtainState < 2){
                curtainState += 1;
            } else curtainState = 0;

            switch (curtainState) {
                case 0:
                    curtain.setImageResource(R.drawable.image_level7_curtain_open);
                    break;
                case 1:
                    curtain.setImageResource(R.drawable.image_level7_curtain_half_open);
                    break;
                case 2:
                    curtain.setImageResource(R.drawable.image_level7_curtain_closed);
                    break;
            }

            utils.playSFX(R.raw.sfx_level7_curtain_move);

        });
    }

    private void resetState(){
        // add you move-able / state changeable elements here
        windowView();
        curtainState = 0;
        ImageView curtain = findViewById(R.id.image_Level7_Curtain);
        curtain.setImageResource(R.drawable.image_level7_curtain_open);
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

    private SensorEventListener lightSensorListener = new SensorEventListener() {

        boolean isSFXOnPlayed = false;
        boolean isSFXOffPlayed = false;
        @Override
        public void onSensorChanged(SensorEvent event) {
            float lightValue = event.values[0];
            ImageView lampGlow = findViewById(R.id.image_Level7_LampGlow);
            ImageView lamp = findViewById(R.id.image_Level7_Lamp);
            // Handle changes in light sensor value here
            if (lightValue < 100 ) {
                isLampOn = false;
                lampGlow.setVisibility(View.GONE);
                if (isSwitchOn) {
                    lamp.setImageResource(R.drawable.image_level7_lamp);
                } else lamp.setImageResource(R.drawable.image_level7_lamp_off);
                if (!isSFXOnPlayed) {
                    utils.playSFX(R.raw.sfx_level7_lamp_off);
                    isSFXOnPlayed = true;
                    isSFXOffPlayed = false;
                }
            } else {
                isLampOn = true;
                lampGlow.setVisibility(View.VISIBLE);
                lamp.setImageResource(R.drawable.image_level7_lamp);
                if (!isSFXOffPlayed) {
                    utils.playSFX(R.raw.sfx_level7_lamp_on);
                    isSFXOffPlayed = true;
                    isSFXOnPlayed = false;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes if needed
        }

    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ConstraintLayout topAppBar = findViewById(R.id.view_topbar_Level7);
        ImageView lightSwitch = findViewById(R.id.image_Level7_Switch);
        ImageView light = findViewById(R.id.image_Level7_Light);
        ImageView darkOverlay = findViewById(R.id.image_level7_dark_overlay);

        int nightModeFlags = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                isSwitchOn = false;

                layoutView.setBackgroundColor(Color.BLACK);
                topAppBar.setBackgroundColor(Color.BLACK);
                toggleTheme(Color.WHITE);

                lightSwitch.setImageResource(R.drawable.image_level7_switch_off);
                light.setImageResource(R.drawable.image_level7_light_off);
                darkOverlay.setVisibility(View.VISIBLE);
                utils.playSFX(R.raw.sfx_level7_switch_toggle);

                if (!isLampOn) {
                    ImageView lamp = findViewById(R.id.image_Level7_Lamp);
                    lamp.setImageResource(R.drawable.image_level7_lamp_off);
                }

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                isSwitchOn = true;

                layoutView.setBackgroundColor(Color.WHITE);
                topAppBar.setBackgroundColor(Color.WHITE);
                toggleTheme(Color.BLACK);

                lightSwitch.setImageResource(R.drawable.image_level7_switch_on);
                light.setImageResource(R.drawable.image_level7_light_on);
                darkOverlay.setVisibility(View.GONE);
                utils.playSFX(R.raw.sfx_level7_switch_toggle);

                if (!isLampOn) {
                    ImageView lamp = findViewById(R.id.image_Level7_Lamp);
                    lamp.setImageResource(R.drawable.image_level7_lamp);
                }

                break;

        }

    }

    private void toggleTheme(int colorResource){
        ImageView backBt = findViewById(R.id.button_Level7_NavigateBackBt);
        ImageView hintBt = findViewById(R.id.button_Level7_HintBt);
        ImageView resetBt = findViewById(R.id.button_Level7_ResetBt);
        ImageView settingsBt = findViewById(R.id.button_Level7_SettingsBt);
        ImageView closeSettingBt = findViewById(R.id.button_Level7_CloseBt);
        ImageView soundBt = findViewById(R.id.button_Level7_SoundBt);
        TextView title = findViewById(R.id.text_Level7_Title);

        backBt.setColorFilter(colorResource);
        hintBt.setColorFilter(colorResource);
        resetBt.setColorFilter(colorResource);
        settingsBt.setColorFilter(colorResource);
        closeSettingBt.setColorFilter(colorResource);
        soundBt.setColorFilter(colorResource);
        title.setTextColor(colorResource);
    }

}