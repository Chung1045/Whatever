package com.example.whatever.game;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Level5 extends AppCompatActivity implements View.OnTouchListener {
    private Utils utils;
    private long startTime = 0L, elapsedTime = 0L;
    private final Handler timerHandler = new Handler();
    private int minutes, seconds, milliseconds, deltaX, deltaY;
    private long timeUsedInMilliseconds;
    private boolean isLevelPass = false;
    private final String[] levelPassMessage = new String[]{"U can do better", "Try hard?", "As expected"};
    private final String levelHint = "Is there only 4 gears?";
    private final Random random = new Random();

    ImageView lv5_gear3, lv5_gear4, lv5_gear5, lv5_gear6, lv5_setting, lv5_waterdrop, lv5_waterdrop1, lv5_waterdrop2, lv5_drag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level5);
        View v = findViewById(R.id.view_LevelLayout_Level5);
        onLevelStart(v);
        lv5_gear3 = (ImageView) findViewById(R.id.image_Level5_gear3);
        lv5_gear4 = (ImageView) findViewById(R.id.image_Level5_gear4);
        lv5_gear5 = (ImageView) findViewById(R.id.image_Level5_gear5);
        lv5_gear6 = (ImageView) findViewById(R.id.image_Level5_gear6);
        lv5_setting = (ImageView) findViewById(R.id.button_Level5_SettingsBt);
        lv5_waterdrop = (ImageView) findViewById(R.id.image_Level5_waterdrop);
        lv5_waterdrop1 = (ImageView) findViewById(R.id.image_Level5_waterdrop1);
        lv5_waterdrop2 = (ImageView) findViewById(R.id.image_Level5_waterdrop2);
        lv5_drag = (ImageView) findViewById(R.id.Level5_drag);

        lv5_gear3.setOnLongClickListener(onclickListener);
        lv5_gear4.setOnLongClickListener(onclickListener);
        lv5_gear5.setOnLongClickListener(onclickListener);
        lv5_gear6.setOnLongClickListener(onclickListener);
        lv5_setting.setOnLongClickListener(onclickListener);

        lv5_drag.setOnDragListener(dragListener);
    }

    View.OnLongClickListener onclickListener = (v) -> {
        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
        v.startDragAndDrop(data, shadowBuilder, v, 0);
        return true;
    };


    View.OnDragListener dragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();

            if (dragEvent == DragEvent.ACTION_DRAG_ENDED) {
                final View view = (View) event.getLocalState();

                if (view.getId() == R.id.button_Level5_SettingsBt) {
                    lv5_drag = (ImageView) findViewById(R.id.Level5_drag);
                    lv5_drag.setImageResource(R.drawable.baseline_settings_24);

                    View view1 = findViewById(R.id.image_Level5_gear1);
                    View view2 = findViewById(R.id.image_Level5_gear2);
                    View view3 = findViewById(R.id.image_Level5_gear7);
                    View view4 = findViewById(R.id.Level5_drag);

                    PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 360f, 0f);
                    PropertyValuesHolder rotation2 = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 360f);

                    ObjectAnimator animator1 = ObjectAnimator.ofPropertyValuesHolder(view3, rotation);
                    animator1.setDuration(1000);

                    ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(view4, rotation);
                    animator2.setDuration(1000);

                    ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view1, rotation2);
                    animator3.setDuration(1000);

                    ObjectAnimator animator4 = ObjectAnimator.ofPropertyValuesHolder(view2, rotation2);
                    animator4.setDuration(1000);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animator1, animator2, animator3, animator4);
                    animatorSet.start();

                    lv5_waterdrop.setVisibility(View.VISIBLE);
                    lv5_waterdrop1.setVisibility(View.VISIBLE);
                    lv5_waterdrop2.setVisibility(View.VISIBLE);
                    timerHandler.removeCallbacks(updateTimerThread);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            onLevelPass();
                        }
                    }, 1000);
                } else if (view.getId() == R.id.image_Level5_gear3 || view.getId() == R.id.image_Level5_gear4 || view.getId() == R.id.image_Level5_gear5 || view.getId() == R.id.image_Level5_gear6) {
                    onWrongAttempt();
                }
            }

            return true;
        }
    };

    private void onLevelStart(View v){

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        UserPreferences.init(this);

        // change accordingly to the current level
        UserPreferences.editor.putInt(UserPreferences.LAST_PLAYED_LEVEL, 5).commit();

        topBarInit();
        utils = new Utils(this, v, this);
        utils.playEnterLevelSFX();
        listenerInit();
    }

    private void topBarInit(){
        boolean sfx = UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, true);
        ImageView soundBt = findViewById(R.id.button_Level5_SoundBt);
        if (!sfx) {
            soundBt.setImageResource(R.drawable.ic_volume_muted_24);
        }

        findViewById(R.id.button_Level5_SettingsBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);

            findViewById(R.id.button_Level5_SettingsBt).startAnimation(fadeout);
            findViewById(R.id.button_Level5_ResetBt).startAnimation(fadeout);
            findViewById(R.id.button_Level5_HintBt).startAnimation(fadeout);
            findViewById(R.id.button_Level5_SettingsBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level5_ResetBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level5_HintBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level5_CloseBt).startAnimation(fadein);
            findViewById(R.id.button_Level5_SoundBt).startAnimation(fadein);
            findViewById(R.id.button_Level5_CloseBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level5_SoundBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level5_CloseBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);
            findViewById(R.id.button_Level5_CloseBt).startAnimation(fadeout);
            findViewById(R.id.button_Level5_CloseBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level5_SoundBt).startAnimation(fadeout);
            findViewById(R.id.button_Level5_SoundBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level5_SettingsBt).startAnimation(fadein);
            findViewById(R.id.button_Level5_ResetBt).startAnimation(fadein);
            findViewById(R.id.button_Level5_HintBt).startAnimation(fadein);
            findViewById(R.id.button_Level5_SettingsBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level5_ResetBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level5_HintBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level5_SoundBt).setOnClickListener(view -> {
            ImageView soundButton = findViewById(R.id.button_Level5_SoundBt);
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
                startActivity(new Intent(Level5.this, LevelSelect.class));
            }
        });

        // go back to level selection (Top arrow back icon)
        findViewById(R.id.button_Level5_NavigateBackBt).setOnClickListener(view -> {
            startActivity(new Intent(Level5.this, LevelSelect.class));
        });

        findViewById(R.id.button_Level5_HomeBt).setOnClickListener(view -> {
            startActivity(new Intent(Level5.this, LevelSelect.class));
        });

        findViewById(R.id.button_Level5_NextLevelBt).setOnClickListener(view -> {
            startActivity(new Intent(Level5.this, Level6.class));
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
        TextView timeUsedCount = findViewById(R.id.text_Level5_TimeUsedText);
        TextView passMessage = findViewById(R.id.text_LevelTemPlate_PassMessage);
        timeUsedCount.setText("" + minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds));
        passMessage.setText(levelPassMessage[random.nextInt(levelPassMessage.length)]);

        updateBestTime();

        // Random level pass message to be display
        findViewById(R.id.view_Level5_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_Level5_Pass).startAnimation(fadeInAnimation);
        utils.playCorrectSFX();
    }

    private void updateBestTime(){
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0L) == 0L ||
                timeUsedInMilliseconds < UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0L)){
            UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL5, timeUsedInMilliseconds).commit();
            TextView timeUsedTitle = findViewById(R.id.text_Level5_TimeUsedTitle);
            timeUsedTitle.setText("New best time : ");
            findViewById(R.id.text_Level5_BestTimeUsedTitle).setVisibility(View.GONE);
            findViewById(R.id.text_Level5_Best_TimeUsedText).setVisibility(View.GONE);
        } else {
            Long bestTime = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL5, 0L);
            TextView bestTimeUsed = findViewById(R.id.text_Level5_Best_TimeUsedText);

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
        findViewById(R.id.button_Level5_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });

        findViewById(R.id.button_Level5_HintBt).setOnClickListener(view -> {
            utils.showSnackBarMessage(levelHint);
        });

        // place your element listener here
    }

    private void resetState(){
        View textView = findViewById(R.id.timer_text_view);
        textView.setTranslationX(0);
        textView.setTranslationY(0);
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
}