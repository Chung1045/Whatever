package com.example.whatever.game;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private View v;
    private Utils utils;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        v = findViewById(android.R.id.content);
        utils = new Utils(this, v, this);

        setSupportActionBar(findViewById(R.id.view_sign_up_topAppBar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listenerInit();

    }

    private void listenerInit() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        findViewById(R.id.button_sign_up_createAccount).setOnClickListener(view -> {
            TextInputLayout userNameLayout = findViewById(R.id.textinput_signup_username_layout);
            TextInputLayout emailLayout = findViewById(R.id.textinput_signup_email_layout);
            TextInputLayout passwordLayout = findViewById(R.id.textinput_signup_password_layout);
            TextInputLayout passwordConfirmLayout = findViewById(R.id.textinput_signup_password_check_layout);

            String userName = String.valueOf(userNameLayout.getEditText().getText());
            String email = String.valueOf(emailLayout.getEditText().getText());
            String password = String.valueOf(passwordLayout.getEditText().getText());
            String passwordConfirm = String.valueOf(passwordConfirmLayout.getEditText().getText());

            emailLayout.setError(null);
            passwordLayout.setError(null);
            passwordLayout.setError(null);
            passwordConfirmLayout.setError(null);

            if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || userName.isEmpty()) {
                utils.showSnackBarMessage("Please fill all fields");

                if (email.isEmpty()) {
                    emailLayout.setError("Field required");
                }
                if (password.isEmpty()) {
                    passwordLayout.setError("Field required");
                }
                if (passwordConfirm.isEmpty()) {
                    passwordConfirmLayout.setError("Field required");
                }
                if (userName.isEmpty()) {
                    userNameLayout.setError("Field required");
                }
            } else if (password.length() < 6) {
                utils.showSnackBarMessage("Password must be at least 6 characters");
                passwordLayout.setError("Password must be at least 6 characters");
            } else if (!password.equals(passwordConfirm)) {
                utils.showSnackBarMessage("Passwords do not match");
                passwordLayout.setError("Password do not match");
                passwordConfirmLayout.setError("Password do not match");
            } else {
                firebaseHelper.isUserNameOccupied(userName, occupied ->{
                    if (occupied) {
                        utils.showSnackBarMessage("User Name already taken, please choose another");
                        userNameLayout.setError("User Name already taken");
                    } else {
                        utils.showSnackBarMessage("Creating account");
                        // create account
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user = FirebaseAuth.getInstance().getCurrentUser();

                                // Set username
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userName)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(e -> {
                                            if (e.isSuccessful()) {
                                                // Profile updated successfully
                                                // You can handle the success here
                                            } else {
                                                // Failed to update profile
                                                // You can handle the failure here
                                            }
                                        });

                                Map<String, Object> userProfile = new HashMap<>();
                                userProfile.put("email", email);
                                userProfile.put("uid", user.getUid());

                                mDatabase.child("UserProfile").child(userName).setValue(userProfile).addOnSuccessListener(e ->{
                                    utils.showSnackBarMessage("Account synced successfully");
                                }).addOnFailureListener(e -> {
                                    utils.showSnackBarMessage("Failed to sync");
                                });

                                new Handler().postDelayed(() -> {
                                    startActivity(new Intent(SignUp.this, ProfileView.class));
                                    finish();
                                }, 2000);

                            } else {
                                utils.showSnackBarMessage("Failed to create account");
                            }
                        });
                    }

                });

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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