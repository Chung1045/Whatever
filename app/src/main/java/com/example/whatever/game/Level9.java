package com.example.whatever.game;

import static android.view.View.GONE;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

public class Level9 extends AppCompatActivity implements View.OnTouchListener {

    private Utils utils;
    private long startTime = 0L, elapsedTime = 0L;
    private final Handler timerHandler = new Handler();
    private int minutes, seconds, milliseconds, deltaX, deltaY;
    private long timeUsedInMilliseconds;
    private boolean isLevelPass = false;
    private final String[] levelPassMessage = new String[]{"Are ya winning son?", "That was quite easy", "As expected"};
    private final String levelHint = "No Help Lol";
    private final Random random = new Random();
    private ProgressBar progressBar;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private Activity.ScreenCaptureCallback screenCaptureCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level9);
        View v = findViewById(R.id.view_LevelLayout_Level9);
        onLevelStart(v);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            screenCaptureCallback =
                    new Activity.ScreenCaptureCallback() {
                        @Override
                        public void onScreenCaptured() {
                            View backOnlineView = findViewById(R.id.view_Level9_variableLayout_backOnline);
                            Log.d("Screencapture", "Screen capture started");
                            if (backOnlineView.getVisibility() == View.VISIBLE) {
                                utils.showSnackBarMessage("Heyyy! How dare you take the screenshot with my username and password???");
                            }
                        }
                    };
            registerScreenCaptureCallback(executor, screenCaptureCallback);
        }

    }

    private void onLevelStart(View v) {

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        UserPreferences.init(this);

        // change accordingly to the current level
        UserPreferences.editor.putInt(UserPreferences.LAST_PLAYED_LEVEL, 9).commit();
        progressBar = findViewById(R.id.progressBar_Level9_web);


        topBarInit();
        utils = new Utils(this, v, this);
        utils.playEnterLevelSFX();
        uiInit();
        listenerInit();
        swipeRefreshLayoutInit();
    }

    private void uiInit() {
        TextView dinoCount = findViewById(R.id.text_level9_dino_count);
        int count = UserPreferences.sharedPref.getInt(UserPreferences.DINO_COUNT, 0);

        if (count != 0) {
            dinoCount.setVisibility(View.VISIBLE);
            dinoCount.setText(String.valueOf(count));
        }
    }

    private void swipeRefreshLayoutInit() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_Level9_web);
        ProgressBar progressBar = findViewById(R.id.progressBar_Level9_web);
        ConstraintLayout noInternetLayout = findViewById(R.id.view_Level9_variableLayout_noInternet);
        ConstraintLayout backOnlineLayout = findViewById(R.id.view_Level9_variableLayout_backOnline);

        // Set the color scheme for the indicator
        swipeRefreshLayout.setColorSchemeColors(getColor(R.color.foreground));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getColor(R.color.background));

        // Set the listener for the refresh operation
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            dialogueOne(isContinue -> {
                if (isContinue) {
                    progressBar.setProgress(10);
                    dialogueTwo(isContinue2 -> {
                        progressBar.setProgress(20);
                        if (isContinue2) {
                            dialogueThree(isContinue3 -> {
                                progressBar.setProgress(30);
                                if (isContinue3) {
                                    dialogueFour(isContinue4 -> {
                                        progressBar.setProgress(40);
                                        if (isContinue4) {
                                            dialogueFive(isContinue5 -> {
                                                progressBar.setProgress(50);
                                                if (isContinue5) {
                                                    dialogueSix(isContinue6 -> {
                                                        progressBar.setProgress(60);
                                                        if (isContinue6) {
                                                            dialogueSeven(isContinue7 -> {
                                                                progressBar.setProgress(70);
                                                                if (isContinue7) {
                                                                    dialogueEight(isContinue8 -> {
                                                                        progressBar.setProgress(80);
                                                                        if (isContinue8) {
                                                                            dialogueNine(isContinue9 -> {
                                                                                progressBar.setProgress(90);
                                                                                if (isContinue9) {
                                                                                    dialogueTen(isContinue10 -> {
                                                                                        progressBar.setProgress(95);
                                                                                        if (isContinue10) {
                                                                                            dialogueEleven(isContinue11 -> {
                                                                                                if (isContinue11) {
                                                                                                    progressBar.setProgress(100);
                                                                                                    noInternetLayout.setVisibility(View.GONE);
                                                                                                    backOnlineLayout.setVisibility(View.VISIBLE);
                                                                                                    new Handler().postDelayed(() -> {
                                                                                                        progressBar.setVisibility(View.GONE);
                                                                                                        progressBar.setProgress(0);
                                                                                                    }, 2000);
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });

            // Your refresh operation code here
            // Once the refresh operation is complete, hide the progress bar
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 5000);
        });
    }


    private void topBarInit() {
        boolean sfx = UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED, true);
        ImageView soundBt = findViewById(R.id.button_Level9_SoundBt);
        if (!sfx) {
            soundBt.setImageResource(R.drawable.ic_volume_muted_24);
        }

        findViewById(R.id.button_Level9_SettingsBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);

            findViewById(R.id.button_Level9_SettingsBt).startAnimation(fadeout);
            findViewById(R.id.button_Level9_ResetBt).startAnimation(fadeout);
            findViewById(R.id.button_Level9_HintBt).startAnimation(fadeout);
            findViewById(R.id.button_Level9_SettingsBt).setVisibility(GONE);
            findViewById(R.id.button_Level9_ResetBt).setVisibility(GONE);
            findViewById(R.id.button_Level9_HintBt).setVisibility(GONE);
            findViewById(R.id.button_Level9_CloseBt).startAnimation(fadein);
            findViewById(R.id.button_Level9_SoundBt).startAnimation(fadein);
            findViewById(R.id.button_Level9_CloseBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level9_SoundBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level9_CloseBt).setOnClickListener(view -> {
            Animation fadeout = new AlphaAnimation(1, 0);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(500);
            fadeout.setDuration(500);
            findViewById(R.id.button_Level9_CloseBt).startAnimation(fadeout);
            findViewById(R.id.button_Level9_CloseBt).setVisibility(GONE);
            findViewById(R.id.button_Level9_SoundBt).startAnimation(fadeout);
            findViewById(R.id.button_Level9_SoundBt).setVisibility(GONE);
            findViewById(R.id.button_Level9_SettingsBt).startAnimation(fadein);
            findViewById(R.id.button_Level9_ResetBt).startAnimation(fadein);
            findViewById(R.id.button_Level9_HintBt).startAnimation(fadein);
            findViewById(R.id.button_Level9_SettingsBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level9_ResetBt).setVisibility(View.VISIBLE);
            findViewById(R.id.button_Level9_HintBt).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.button_Level9_SoundBt).setOnClickListener(view -> {
            ImageView soundButton = findViewById(R.id.button_Level9_SoundBt);
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
                startActivity(new Intent(Level9.this, LevelSelect.class));
                finish();
            }
        });

        // go back to level selection (Top arrow back icon)
        findViewById(R.id.button_Level9_NavigateBackBt).setOnClickListener(view -> {
            startActivity(new Intent(Level9.this, LevelSelect.class));
            finish();
        });

        findViewById(R.id.button_Level9_HomeBt).setOnClickListener(view -> {
            startActivity(new Intent(Level9.this, LevelSelect.class));
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
        TextView timeUsedCount = findViewById(R.id.text_Level9_TimeUsedText);
        TextView passMessage = findViewById(R.id.text_LevelTemPlate_PassMessage);
        timeUsedCount.setText(minutes + ":" + String.format("%02d", seconds) + ":" + String.format("%03d", milliseconds));
        passMessage.setText(levelPassMessage[random.nextInt(levelPassMessage.length)]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            unregisterScreenCaptureCallback(screenCaptureCallback);
        }

        updateBestTime();

        // Random level pass message to be display
        findViewById(R.id.view_Level9_Pass).setVisibility(View.VISIBLE);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000); // Duration in milliseconds (adjust as needed)

        // Apply the animation to the result screen view
        findViewById(R.id.view_Level9_Pass).startAnimation(fadeInAnimation);
        utils.playCorrectSFX();
    }

    private void updateBestTime() {
        if (UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL_TEMPLATE, 0L) != 0L ||
                timeUsedInMilliseconds < UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL9, 0L)) {
            UserPreferences.editor.putLong(UserPreferences.BEST_TIME_LEVEL9, timeUsedInMilliseconds).commit();
            TextView timeUsedTitle = findViewById(R.id.text_Level9_TimeUsedTitle);
            timeUsedTitle.setText("New best time : ");
            findViewById(R.id.text_Level9_BestTimeUsedTitle).setVisibility(GONE);
            findViewById(R.id.text_Level9_Best_TimeUsedText).setVisibility(GONE);
        } else {
            long bestTime = UserPreferences.sharedPref.getLong(UserPreferences.BEST_TIME_LEVEL9, 0L);
            TextView bestTimeUsed = findViewById(R.id.text_Level9_Best_TimeUsedText);

            long seconds = bestTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            milliseconds = (int) (bestTime % 1000);

            bestTimeUsed.setText(String.format("%02d:%02d:%03d", minutes, seconds, milliseconds));

        }
    }

    public void onWrongAttempt() {
        utils.playSFX(R.raw.sfx_level9_wrong);
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

        findViewById(R.id.image_level9_dino).setOnClickListener(view -> {
            ImageView dinoImageView = findViewById(R.id.image_level9_dino);
            TextView dinoCount = findViewById(R.id.text_level9_dino_count);
            dinoCount.setVisibility(View.VISIBLE);

            // Play the sound effect for the dino jump


            UserPreferences.editor.putInt(UserPreferences.DINO_COUNT,
                    UserPreferences.sharedPref.getInt(UserPreferences.DINO_COUNT, 0) + 1).commit();
            int count = UserPreferences.sharedPref.getInt(UserPreferences.DINO_COUNT, 0);
            dinoCount.setText(String.valueOf(count));

            if (count % 100 == 0) {
                dinoCountBlink();
            } else {
                utils.playSFX(R.raw.sfx_level9_jump_dino);
            }
            // Define the vertical displacement for the jump (60dp)
            int jumpHeight = (int) getResources().getDimension(R.dimen.dino_jump_height);

            // Create a translate animation to move the SVG upward
            TranslateAnimation jumpAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, -jumpHeight);

            // Set the duration of the jump animation
            jumpAnimation.setDuration(250); // Adjust the duration as needed

            // Set an interpolator to make the jump animation smoother
            jumpAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

            // Set a listener to handle the end of the jump animation
            jumpAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // After the jump, create a new animation to move the SVG back to its original position
                    TranslateAnimation returnAnimation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, -jumpHeight,
                            Animation.RELATIVE_TO_SELF, 0);

                    // Set the duration of the return animation
                    returnAnimation.setDuration(250); // Adjust the duration as needed

                    // Set an interpolator for the return animation
                    returnAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

                    // Apply the return animation to the SVG
                    dinoImageView.startAnimation(returnAnimation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            // Apply the jump animation to the SVG
            dinoImageView.startAnimation(jumpAnimation);
        });

        findViewById(R.id.button_Level9_backOnline_nextBt).setOnClickListener(view -> {
            timerHandler.removeCallbacks(updateTimerThread);

            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                findViewById(R.id.button_Level9_backOnline_nextBt).setVisibility(GONE);
                findViewById(R.id.textinput_Level9_username).setVisibility(GONE);
                findViewById(R.id.textinput_Level9_password).setVisibility(GONE);

                findViewById(R.id.text_Level9_However_Login_Message).setVisibility(View.VISIBLE);

                new Handler().postDelayed(() -> {
                    progressBar.setVisibility(GONE);
                    new Handler().postDelayed(this::onLevelPass, 1000);
                }, 1000);

            }, 2000);


        });

        findViewById(R.id.button_Level9_ResetBt).setOnClickListener(view -> {
            startTime = System.currentTimeMillis();
            elapsedTime = 0L;
            timerHandler.postDelayed(updateTimerThread, 0);
            resetState();
        });


        findViewById(R.id.button_Level9_HintBt).setOnClickListener(view -> {
            utils.showSnackBarMessage(levelHint);
        });


    }

    private void resetState() {
        ConstraintLayout noInternetLayout = findViewById(R.id.view_Level9_variableLayout_noInternet);
        ConstraintLayout backOnlineLayout = findViewById(R.id.view_Level9_variableLayout_backOnline);

        noInternetLayout.setVisibility(View.VISIBLE);
        backOnlineLayout.setVisibility(View.GONE);

        findViewById(R.id.button_Level9_backOnline_nextBt).setVisibility(View.VISIBLE);
        findViewById(R.id.textinput_Level9_username).setVisibility(View.VISIBLE);
        findViewById(R.id.textinput_Level9_password).setVisibility(View.VISIBLE);

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

    private void dialogueOne(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("Are you sure?")
                .setMessage("Changes you made will be lost.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    isContinue.accept(true);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    isContinue.accept(false);
                    progressBar.setVisibility(GONE);
                })
                .show();
    }

    private void dialogueTwo(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("Really?")
                .setMessage("Changes you made will be lost.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    isContinue.accept(true);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    isContinue.accept(false);
                    progressBar.setVisibility(GONE);
                })
                .show();
    }

    private void dialogueThree(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("Think again?")
                .setMessage("Changes you made will be lost.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    isContinue.accept(true);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    isContinue.accept(false);
                    progressBar.setVisibility(GONE);
                })
                .show();
    }

    private void dialogueFour(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("I'm sorry, but are you really sure?...")
                .setMessage("Changes you made will be lost.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                            isContinue.accept(true);
                        }
                )
                .setNegativeButton("No", (dialog, id) -> {
                    isContinue.accept(false);
                    progressBar.setVisibility(GONE);
                })
                .show();
    }

    private void dialogueFive(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("I'm sorry, but are you really sure?...")
                .setMessage("Changes you made will be lost.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    isContinue.accept(true);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    isContinue.accept(false);
                    progressBar.setVisibility(GONE);
                })
                .show();
    }

    private void dialogueSix(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("...")
                .setMessage("Changes you made will be lost.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    isContinue.accept(true);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    isContinue.accept(false);
                    progressBar.setVisibility(GONE);
                })
                .show();
    }

    private void dialogueSeven(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("Okay then...")
                .setMessage("What about we play a game?\n If you win I let you reconnect to the internet.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    isContinue.accept(true);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    isContinue.accept(false);
                    progressBar.setVisibility(GONE);
                })
                .show();
    }

    private void dialogueEight(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("Ready?")
                .setMessage("Remember this color \n #FF0000")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    isContinue.accept(true);
                })
                .show();
    }

    private void dialogueNine(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("What is the last color?")
                .setMessage("Remember this color Yellow")
                .setCancelable(false)
                .setPositiveButton("Red", (dialog, id) -> {
                    isContinue.accept(true);
                })
                .setNegativeButton("Blue", (dialog, id) -> {
                    progressBar.setVisibility(GONE);
                    isContinue.accept(false);
                })
                .setNeutralButton("Green", (dialog, id) -> {
                    progressBar.setVisibility(GONE);
                    isContinue.accept(false);
                })
                .show();
    }

    private void dialogueTen(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("What is the last color?")
                .setMessage("Remember this color Green")
                .setCancelable(false)
                .setPositiveButton("#FFFF00", (dialog, id) -> {
                    isContinue.accept(true);
                })
                .setNegativeButton("#00FFFF", (dialog, id) -> {
                    progressBar.setVisibility(GONE);
                    isContinue.accept(false);
                })
                .setNeutralButton("#FF00FF", (dialog, id) -> {
                    progressBar.setVisibility(GONE);
                    isContinue.accept(false);
                })
                .show();
    }

    private void dialogueEleven(Consumer<Boolean> isContinue) {
        new MaterialAlertDialogBuilder(this).setTitle("What would be the color?")
                .setMessage("What is the color when the first color mixed with the third color?")
                .setCancelable(false)
                .setPositiveButton("Topaz", (dialog, id) -> {
                    progressBar.setVisibility(GONE);
                    isContinue.accept(true);
                })
                .setNegativeButton("Calamansi", (dialog, id) -> {
                    progressBar.setVisibility(GONE);
                    isContinue.accept(false);
                })
                .setNeutralButton("Pale", (dialog, id) -> {
                    progressBar.setVisibility(GONE);
                    isContinue.accept(false);
                })
                .show();
    }

    private void dinoCountBlink() {

        TextView dinoCount = findViewById(R.id.text_level9_dino_count);
        utils.playSFX(R.raw.sfx_level9_point);

        new Handler().postDelayed(() -> {
            dinoCount.setVisibility(GONE);
            new Handler().postDelayed(() -> {
                dinoCount.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> {
                    dinoCount.setVisibility(GONE);
                    new Handler().postDelayed(() -> {
                        dinoCount.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> {
                            dinoCount.setVisibility(GONE);
                            new Handler().postDelayed(() -> {
                                dinoCount.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(() -> {
                                    dinoCount.setVisibility(GONE);
                                    new Handler().postDelayed(() -> {
                                        dinoCount.setVisibility(View.VISIBLE);
                                    }, 500);
                                }, 500);
                            }, 500);
                        }, 500);
                    }, 500);
                }, 500);
            }, 500);
        }, 500);


    }
}



