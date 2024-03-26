package com.example.whatever.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Objects;

public class ProfileView extends AppCompatActivity {

    private Utils utils;
    private View v;

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