package com.example.whatever.game;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.os.BuildCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ProfileView extends AppCompatActivity {

    private Utils utils;
    private View v;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_view);

        UserPreferences.init(this);
        v = findViewById(android.R.id.content);
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

        findViewById(R.id.button_profile_sign_out).setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Sign Out Confirm")
                    .setMessage("You are about to log out. Are you sure?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        firebaseHelper.logout();
                        utils.showSnackBarMessage("Log out successfully");
                        utils.removeAvatar();
                        updateUI();
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        dialog.cancel();
                    })
                    .show();
        });

        findViewById(R.id.image_profile_profile_icon).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            if (!checkPickerPermission()){
                utils.showSnackBarMessage("You need to grant permission to ne able to select pictures");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    // Android 14 or above
                    requestPermissions(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED}, 1);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13 or above
                    requestPermissions(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO}, 1);
                } else {
                    // Android 12 or below
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 1);
                }
            } else {
                imagePickerResultLauncher.launch(intent);

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

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        TextView userName = findViewById(R.id.text_profile_Username);
        TextView profileDescription = findViewById(R.id.text_profile_description);
        Button signOut = findViewById(R.id.button_profile_sign_out);
        Button signIn = findViewById(R.id.button_profile_sign_in_sign_up);
        ImageView userAvatar = findViewById(R.id.image_profile_profile_icon);
        ImageView edituserNameIcon = findViewById(R.id.image_profile_edit);

        if (firebaseHelper.isLoggedIn()) {
            userName.setText(firebaseHelper.getUserName());
            userName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            signOut.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.GONE);
            edituserNameIcon.setVisibility(View.VISIBLE);

            // Get the avatar bitmap from SharedPreferences
            utils.getBitmapFromByte(output -> {
                if (!(output == null)){
                    userAvatar.setImageBitmap(output);
                } else {
                    userAvatar.setImageResource(R.drawable.ic_account_circle_24);
                }
            });

            profileDescription.setText("Best Total Time: ");

        } else {
            userName.setText(R.string.string_profile_defaultUsername);
            signOut.setVisibility(View.GONE);
            signIn.setVisibility(View.VISIBLE);
            edituserNameIcon.setVisibility(View.GONE);

            // Set a default image or placeholder when the user is not logged in
            userAvatar.setImageResource(R.drawable.ic_account_circle_24);

            profileDescription.setText(R.string.string_profile_signIn_function_description);
        }
    }


    ActivityResultLauncher<Intent> imagePickerResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                ImageView userAvatar = findViewById(R.id.image_profile_profile_icon);
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Uri selectedImageSrc = result.getData().getData();
                    if (selectedImageSrc!= null) {
                        utils.showSnackBarMessage("Image Selected");
                        Bitmap bitmap;
                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImageSrc);
                            bitmap = ImageDecoder.decodeBitmap(source);
                            utils.saveAvatar(imageCrop(bitmap));
                            userAvatar.setImageBitmap(imageCrop(bitmap));
                            firebaseHelper.updateProfileImage(imageCrop(bitmap), success ->{
                                if (success){
                                    utils.showSnackBarMessage("Profile Image Updated");
                                }
                            });
                        } catch (IOException e) {
                            utils.showSnackBarMessage("Oops! Something went wrong, please try again");
                            throw new RuntimeException(e);
                        }

                    }
                }
            });


    private boolean checkPickerPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && (checkSelfPermission(READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                && (checkSelfPermission(READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else return false;
    }

    private Bitmap imageCrop(Bitmap input){
        int size = Math.min(input.getWidth(), input.getHeight());
        int xOffset = (input.getWidth() - size) / 2;
        int yOffset = (input.getHeight() - size) / 2;

        Bitmap croppedBitmap = Bitmap.createBitmap(input, xOffset, yOffset, size, size);

        // Resize the cropped bitmap to 512x512 pixels
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, 512, 512, true);

        return resizedBitmap;
    }


}