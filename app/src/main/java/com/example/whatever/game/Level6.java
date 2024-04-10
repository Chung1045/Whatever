package com.example.whatever.game;

import android.content.Intent;
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

import com.airbnb.lottie.LottieAnimationView;

import java.util.Random;

public class Level6 extends AppCompatActivity implements View.OnTouchListener {
    private Utils utils;
    private long startTime = 0L, elapsedTime = 0L;
    private final Handler timerHandler = new Handler();
    private int minutes, seconds, milliseconds, deltaX, deltaY;
    private long timeUsedInMilliseconds;
    private boolean isLevelPass = false;
    private final String[] levelPassMessage = new String[]{"Are ya winning son?", "That was quite easy", "As expected"};
    private final String levelHint = "Can you steal the weapon?";
    private final Random random = new Random();

    LottieAnimationView win, spearLose, throwLose;
    ImageView lv6_original_archer, lv6_original_player, lv6_after_archer, lv6_after_player, lv6_vs, lv6_pass, lv6_spear, lv6_throw, lv6_spearselect, lv6_throwselect;
    TextView lv6_you, lv6_enemy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level6);
        View v = findViewById(R.id.view_LevelLayout_Level6);
        onLevelStart(v);

        lv6_pass = findViewById(R.id.pass);
        lv6_spear = findViewById(R.id.image_Level6_spear);
        lv6_throw = findViewById(R.id.image_Level6_throw);
        lv6_original_archer = findViewById(R.id.image_Level6_original_archer);
        lv6_original_player = findViewById(R.id.image_Level6_original_player);
        lv6_after_archer = findViewById(R.id.image_Level6_after_archer);
        lv6_after_player = findViewById(R.id.image_Level6_after_player);
        lv6_vs = findViewById(R.id.image_Level6_vs);
        lv6_spearselect = findViewById(R.id.image_Level6_select_spear);
        lv6_throwselect = findViewById(R.id.image_Level6_select_throw);
        lv6_you = findViewById(R.id.tv_you);
        lv6_enemy = findViewById(R.id.tv_enemy);
        spearLose = findViewById(R.id.animation_Level6spear_lose);
        throwLose = findViewById(R.id.animation_Level6throw_lose);
        win = findViewById(R.id.animation_Level6win);
    }

    private void lottieAnimation(int id){
        switch (id){
            case 0:
                win.setVisibility(View.VISIBLE);
                win.animate().setStartDelay(100);
                break;

            case 1:
                spearLose.setVisibility(View.VISIBLE);
                spearLose.animate().setStartDelay(100);
                break;

            case 2:
                throwLose.setVisibility(View.VISIBLE);
                throwLose.animate().setStartDelay(100);
                break;


        }
    }
    private void onLevelStart(View v){

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        UserPreferences.init(this);

        // change accordingly to the current level
        UserPreferences.editor.putInt(UserPreferences.LAST_PLAYED_LEVEL, 6).commit();

        topBarInit();
        utils = new Utils(this, v, this);
        utils.playEnterLevelSFX();
        listenerInit();
    }

    private void topBarInit(){
        boolean sfx = UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, true);
        ImageView soundBt = findViewById(R.id.button_Level6_SoundBt);
        if (!sfx) {
            soundBt.setImageResource(R.drawable.ic_volume_muted_24);
        }

        findViewById(R.id.button_Level6_SettingsBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);

            findViewById(R.id.button_Level6_SettingsBt).startAnimation(fadeout);
            findViewById(R.id.button_Level6_ResetBt).startAnimation(fadeout);
            findViewById(R.id.button_Level6_HintBt).startAnimation(fadeout);
            findViewById(R.id.button_Level6_SettingsBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level6_ResetBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level6_HintBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level6_CloseBt).startAnimation(fadein);
            findViewById(R.id.button_Level6_SoundBt).startAnimation(fadein);
            findViewById(R.id.button_Level6_CloseBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level6_SoundBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level6_CloseBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);
            findViewById(R.id.button_Level6_CloseBt).startAnimation(fadeout);
            findViewById(R.id.button_Level6_CloseBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level6_SoundBt).startAnimation(fadeout);
            findViewById(R.id.button_Level6_SoundBt).setVisibility(View.GONE);
            findViewById(R.id.button_Level6_SettingsBt).startAnimation(fadein);
            findViewById(R.id.button_Level6_ResetBt).startAnimation(fadein);
            findViewById(R.id.button_Level6_HintBt).startAnimation(fadein);
            findViewById(R.id.button_Level6_SettingsBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level6_ResetBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level6_HintBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level6_SoundBt).setOnClickListener(view -> {
            ImageView soundButton = findViewById(R.id.button_Level6_SoundBt);
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
                startActivity(new Intent(Level6.this, LevelSelect.class));
            }
        });

        // go back to level selection (Top arrow back icon)
        findViewById(R.id.button_Level6_NavigateBackBt).setOnClickListener(view -> {
            startActivity(new Intent(Level6.this, LevelSelect.class));
        });

        findViewById(R.id.button_Level6_HomeBt).setOnClickListener(view -> {
            startActivity(new Intent(Level6.this, LevelSelect.class));
        });

        findViewById(R.id.button_Level6_NextLevelBt).setOnClickListener(view -> {
            startActivity(new Intent(Level6.this, Level7.class));
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
        TextView timeUsedCount = findViewById(R.id.text_Level6_TimeUsedText);
        TextView passMessage = findViewById(R.id.text_LevelTemPlate_PassMessage);
        timeUsedCount.setText("" + minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds));
        passMessage.setText(levelPassMessage[random.nextInt(levelPassMessage.length)]);

        updateBestTime();

        // Random level pass message to be display
        findViewById(R.id.view_Level6_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_Level6_Pass).startAnimation(fadeInAnimation);
        utils.playCorrectSFX();
    }

    private void updateBestTime(){
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0L) == 0L ||
                timeUsedInMilliseconds < UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0L)){
            UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL6, timeUsedInMilliseconds).commit();
            TextView timeUsedTitle = findViewById(R.id.text_Level6_TimeUsedTitle);
            timeUsedTitle.setText("New best time : ");
            findViewById(R.id.text_Level6_BestTimeUsedTitle).setVisibility(View.GONE);
            findViewById(R.id.text_Level6_Best_TimeUsedText).setVisibility(View.GONE);
        } else {
            Long bestTime = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL6, 0L);
            TextView bestTimeUsed = findViewById(R.id.text_Level6_Best_TimeUsedText);

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
        findViewById(R.id.button_Level6_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });

        findViewById(R.id.button_Level6_HintBt).setOnClickListener(view -> {
            utils.showSnackBarMessage(levelHint);
        });

        findViewById(R.id.pass).setOnClickListener(view -> {
            removeItem();
            timerHandler.removeCallbacks(updateTimerThread);
            lv6_after_archer.setVisibility(View.VISIBLE);
            lv6_after_player.setVisibility(View.VISIBLE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    lv6_after_archer.setVisibility(View.GONE);
                    lv6_after_player.setVisibility(View.GONE);
                    lottieAnimation(0);
                }
            }, 500);

            handler.postDelayed(new Runnable() {
                public void run() {
                    onLevelPass();
                }
            }, 3000);
        });

        findViewById(R.id.image_Level6_spear).setOnClickListener(view -> {
            removeItem();
            lv6_spearselect.setVisibility(View.VISIBLE);
            lv6_original_archer.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    lv6_spearselect.setVisibility(View.GONE);
                    lv6_original_archer.setVisibility(View.GONE);
                    lottieAnimation(1);
                }
            }, 500);

            handler.postDelayed(new Runnable() {
                public void run() {
                    resetState();
                }
            }, 3000);

        });

        findViewById(R.id.image_Level6_throw).setOnClickListener(view -> {
            removeItem();
            lv6_throwselect.setVisibility(View.VISIBLE);
            lv6_original_archer.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    lv6_throwselect.setVisibility(View.GONE);
                    lv6_original_archer.setVisibility(View.GONE);
                    lottieAnimation(2);
                }
            }, 500);

            handler.postDelayed(new Runnable() {
                public void run() {
                    resetState();
                }
            }, 3000);
        });
        // place your element listener here
    }

    private void removeItem(){
        lv6_vs.setVisibility(View.GONE);
        lv6_pass.setVisibility(View.GONE);
        lv6_original_archer.setVisibility(View.GONE);
        lv6_original_player.setVisibility(View.GONE);
        lv6_spear.setVisibility(View.GONE);
        lv6_throw.setVisibility(View.GONE);
        lv6_you.setVisibility(View.GONE);
        lv6_enemy.setVisibility(View.GONE);
    }
    private void resetState(){
        lv6_you.setVisibility(View.VISIBLE);
        lv6_enemy.setVisibility(View.VISIBLE);
        lv6_original_archer.setVisibility(View.VISIBLE);
        lv6_original_player.setVisibility(View.VISIBLE);
        lv6_pass.setVisibility(View.VISIBLE);
        lv6_vs.setVisibility(View.VISIBLE);
        lv6_spear.setVisibility(View.VISIBLE);
        lv6_throw.setVisibility(View.VISIBLE);
        win.setVisibility(View.GONE);
        throwLose.setVisibility(View.GONE);
        spearLose.setVisibility(View.GONE);
        lv6_after_archer.setVisibility(View.GONE);
        lv6_after_player.setVisibility(View.GONE);
        lv6_throwselect.setVisibility(View.GONE);
        lv6_spearselect.setVisibility(View.GONE);
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