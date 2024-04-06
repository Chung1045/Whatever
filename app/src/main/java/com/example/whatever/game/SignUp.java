package com.example.whatever.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private Utils utils;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        View v1 = findViewById(android.R.id.content);
        utils = new Utils(this, v1, this);

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

            findViewById(R.id.progressBar_signup).setVisibility(View.VISIBLE);
            utils.hideKeyboard();

            TextInputLayout userNameLayout = findViewById(R.id.textinput_signup_username_layout);
            TextInputLayout emailLayout = findViewById(R.id.textinput_signup_email_layout);
            TextInputLayout passwordLayout = findViewById(R.id.textinput_signup_password_layout);
            TextInputLayout passwordConfirmLayout = findViewById(R.id.textinput_signup_password_check_layout);

            String userName = String.valueOf(Objects.requireNonNull(userNameLayout.getEditText()).getText());
            String email = String.valueOf(Objects.requireNonNull(emailLayout.getEditText()).getText());
            String password = String.valueOf(Objects.requireNonNull(passwordLayout.getEditText()).getText());
            String passwordConfirm = String.valueOf(Objects.requireNonNull(passwordConfirmLayout.getEditText()).getText());

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

                findViewById(R.id.progressBar_signup).setVisibility(View.GONE);

            } else if (password.length() < 6) {
                utils.showSnackBarMessage("Password must be at least 6 characters");
                passwordLayout.setError("Password must be at least 6 characters");
                findViewById(R.id.progressBar_signup).setVisibility(View.GONE);
            } else if (!password.equals(passwordConfirm)) {
                utils.showSnackBarMessage("Passwords do not match");
                passwordLayout.setError("Password do not match");
                passwordConfirmLayout.setError("Password do not match");
                findViewById(R.id.progressBar_signup).setVisibility(View.GONE);
            } else {
                firebaseHelper.isUserNameOccupied(userName, occupied ->{
                    if (occupied) {
                        utils.showSnackBarMessage("User Name already taken, please choose another");
                        userNameLayout.setError("User Name already taken");
                        findViewById(R.id.progressBar_signup).setVisibility(View.GONE);
                    } else {
                        utils.showSnackBarMessage("Creating account");
                        // create account
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user = FirebaseAuth.getInstance().getCurrentUser();

                                // set username
                                firebaseHelper.updateUserName(userName, isSuccess ->{});

                                Map<String, Object> userProfile = new HashMap<>();
                                userProfile.put("email", email);
                                userProfile.put("username", userName);

                                mDatabase.child("UserProfile").child(user.getUid()).setValue(userProfile).addOnSuccessListener(e -> utils.showSnackBarMessage("Account created and synced")).addOnFailureListener(e -> utils.showSnackBarMessage("Failed to sync"));

                                new Handler().postDelayed(() -> {
                                    findViewById(R.id.progressBar_signup).setVisibility(View.GONE);
                                    startActivity(new Intent(SignUp.this, ProfileView.class));
                                    finish();
                                }, 2000);

                            } else {
                                utils.showSnackBarMessage("Failed to create account");
                                findViewById(R.id.progressBar_signup).setVisibility(View.GONE);
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