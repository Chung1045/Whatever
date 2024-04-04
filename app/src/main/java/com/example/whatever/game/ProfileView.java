package com.example.whatever.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.os.BuildCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ProfileView extends AppCompatActivity {

    private Utils utils;
    private View v;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_view);

        UserPreferences.init(this);
        v = LayoutInflater.from(this).inflate(R.layout.activity_profile_view, null);
        utils = new Utils(this, v, this);

        setSupportActionBar(findViewById(R.id.view_profile_topAppBar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutInit();
        listenerInit();

    }

    private void layoutInit(){

        MaterialSwitch sfxSwitch = findViewById(R.id.switch_profile_sfx);
        sfxSwitch.setChecked(UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED,false));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void listenerInit() {

        findViewById(R.id.switchBarLayout_profile_sfx).setOnClickListener(v -> {
            MaterialSwitch sfxSwitch = findViewById(R.id.switch_profile_sfx);
            sfxSwitch.setChecked(!sfxSwitch.isChecked());
            UserPreferences.editor.putBoolean(UserPreferences.SFX_ENABLED, sfxSwitch.isChecked()).commit();
        });

        MaterialSwitch sfxSwitch = findViewById(R.id.switch_profile_sfx);
        sfxSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            UserPreferences.editor.putBoolean(UserPreferences.SFX_ENABLED, sfxSwitch.isChecked()).commit();
        });

        findViewById(R.id.button_profile_sign_in_sign_up).setOnClickListener(v -> {
            startActivity(new Intent(ProfileView.this, SignIn.class));
        });

        findViewById(R.id.button_profile_leaderboard).setOnClickListener(view -> {
            startActivity(new Intent(ProfileView.this, LeaderBoard.class));
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        findViewById(R.id.button_profile_sign_out).setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Sign Out Confirm")
                    .setMessage("You are about to log out. Are you sure?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        FirebaseAuth.getInstance().signOut();
                        updateUI();
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        dialog.cancel();
                    })
                    .show();
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
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        TextView userName = findViewById(R.id.text_profile_Username);
        TextView profileDescription = findViewById(R.id.text_profile_description);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Button signOut = findViewById(R.id.button_profile_sign_out);
        Button signIn = findViewById(R.id.button_profile_sign_in_sign_up);

        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            userName.setText(currentUser.getDisplayName());
            signOut.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.GONE);

            profileDescription.setText("Best Total Time: ");


        } else {
            userName.setText("Guests");
            signOut.setVisibility(View.GONE);
            signIn.setVisibility(View.VISIBLE);
            profileDescription.setText(R.string.string_profile_signIn_function_description);
        }
    }

}