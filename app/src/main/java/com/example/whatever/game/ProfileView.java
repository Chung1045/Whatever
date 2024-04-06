package com.example.whatever.game;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileView extends AppCompatActivity {

    private Utils utils;
    private View v;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();
    private ProgressBar progressBar;

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

        progressBar = findViewById(R.id.progressBar_profile);

        layoutInit();
        listenerInit();

    }

    private void layoutInit(){

        MaterialSwitch sfxSwitch = findViewById(R.id.switch_profile_sfx);
        sfxSwitch.setChecked(UserPreferences.sharedPref.getBoolean(UserPreferences.SFX_ENABLED,false));

        ListView listView = findViewById(R.id.listView_profile_references);

        // Sample data
        String[] data = getResources().getStringArray(R.array.string_profile_references_array);
        ArrayList<String> dataList = new ArrayList<>(Arrays.asList(data));

        // Create and set the custom adapter
        ListViewAdapter adapter = new ListViewAdapter(dataList, this);
        listView.setAdapter(adapter);

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
            if (firebaseHelper.isLoggedIn()) {

                if (!checkPickerPermission()) {
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

                    //uses new photo picker
                    imagePickerResultLauncher.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    progressBar.setVisibility(View.VISIBLE);
                }
            } else {
                utils.showSnackBarMessage("Log in to set your user avatar");
            }
        });

        findViewById(R.id.text_profile_Username).setOnClickListener(view -> {
            if (firebaseHelper.isLoggedIn()){
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialogue_profile_editusername_layout, null);
                TextInputLayout usernameLayout = dialogView.findViewById(R.id.textinput_dialog_username);
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Edit username")
                        .setCancelable(false)
                        .setView(dialogView) // Set dialog view to the inflated layout
                        .setPositiveButton("Change", (dialog, which) -> {

                            progressBar.setVisibility(View.VISIBLE);
                            String changeUsername = usernameLayout.getEditText().getText().toString();
                            firebaseHelper.isUserNameOccupied(changeUsername, isOccupied ->{
                                if (isOccupied){
                                    utils.showSnackBarMessage("Username is already taken");
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(firebaseHelper.getUID());

                                    Map<String, Object> changeInfo = new HashMap<>();
                                    changeInfo.put("username", changeUsername);

                                    ref.updateChildren(changeInfo).addOnCompleteListener(task ->{});

                                    firebaseHelper.updateUserName(changeUsername, isSuccess ->{
                                        updateUI();
                                        progressBar.setVisibility(View.GONE);
                                    });
                                    utils.showSnackBarMessage("Username updated");
                                }
                            });
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            progressBar.setVisibility(View.GONE);
                        }).show();

                usernameLayout.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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
                    userAvatar.setColorFilter(Color.TRANSPARENT);
                    userAvatar.setImageBitmap(output);
                } else {
                    userAvatar.setColorFilter(getColor(R.color.background));
                    userAvatar.setImageResource(R.drawable.ic_account_circle_24);
                }
            });

            profileDescription.setText("Best Total Time: ");

        } else {
            userName.setText(R.string.string_profile_defaultUsername);
            userName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            signOut.setVisibility(View.GONE);
            signIn.setVisibility(View.VISIBLE);
            edituserNameIcon.setVisibility(View.GONE);

            // Set a default image or placeholder when the user is not logged in
            userAvatar.setColorFilter(getColor(R.color.background));
            userAvatar.setImageResource(R.drawable.ic_account_circle_24);

            profileDescription.setText(R.string.string_profile_signIn_function_description);
        }
    }


    ActivityResultLauncher<PickVisualMediaRequest> imagePickerResultLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            result -> {
                ImageView userAvatar = findViewById(R.id.image_profile_profile_icon);
                ContentResolver cr = this.getContentResolver();
                    if (result!= null) {
                        Bitmap bitmap;
                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), result);
                            bitmap = ImageDecoder.decodeBitmap(source);
                            utils.saveAvatar(imageCrop(bitmap));
                            userAvatar.setImageBitmap(imageCrop(bitmap));
                            firebaseHelper.updateProfileImage(imageCrop(bitmap), success ->{
                                if (success){
                                    utils.showSnackBarMessage("Profile Image Updated");
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } catch (IOException e) {
                            utils.showSnackBarMessage("Oops! Something went wrong, please try again");
                            progressBar.setVisibility(View.GONE);
                            throw new RuntimeException(e);
                        }

                    } else {
                        progressBar.setVisibility(View.GONE);
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