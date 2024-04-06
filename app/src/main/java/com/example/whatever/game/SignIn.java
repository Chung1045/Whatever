package com.example.whatever.game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class SignIn extends AppCompatActivity {

    private Utils utils;
    private FirebaseAuth mAuth;
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        View v1 = findViewById(android.R.id.content);
        utils = new Utils(this, v1, this);

        setSupportActionBar(findViewById(R.id.view_sign_in_topAppBar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listenerInit();
        mAuth = FirebaseAuth.getInstance();

    }

    private void listenerInit(){
        findViewById(R.id.button_sign_in_create_account).setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this, SignUp.class));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(SignIn.this, ProfileView.class));
                finish();
            }
        });

        findViewById(R.id.button_sign_in).setOnClickListener(v -> {

            findViewById(R.id.progressBar_signin).setVisibility(View.VISIBLE);

            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            TextInputLayout emailLayout = findViewById(R.id.textinput_signin_email_username);
            TextInputLayout passwordLayout = findViewById(R.id.textinput_signin_password);

            emailLayout.setError(null);
            passwordLayout.setError(null);

            EditText emailEditText = emailLayout.getEditText();
            EditText passwordEditText = passwordLayout.getEditText();

            assert emailEditText != null;
            String email = emailEditText.getText().toString().trim();
            assert passwordEditText != null;
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                // Handle the case where EditTexts are empty
                emailLayout.setError("Please fill in this field");
                passwordLayout.setError("Please fill in this field");
                utils.showSnackBarMessage("Please fill all fields");
                findViewById(R.id.progressBar_signin).setVisibility(View.GONE);
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        utils.showSnackBarMessage("Sign in successfully");
                        firebaseHelper.downloadProfileImage(bitmap -> {
                            if (bitmap != null) {
                                utils.saveAvatar(bitmap);
                            }
                        });
                        new Handler().postDelayed(() -> {
                            findViewById(R.id.progressBar_signin).setVisibility(View.GONE);
                            startActivity(new Intent(SignIn.this, ProfileView.class));
                            finish();
                            }, 2000);
                    } else {
                        emailLayout.setError("Please check again");
                        passwordLayout.setError("Please check again");
                        utils.showSnackBarMessage("Incorrect username, email or password");
                        findViewById(R.id.progressBar_signin).setVisibility(View.GONE);
                    }
                });
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(SignIn.this, ProfileView.class));
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