package com.example.whatever.game;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class LeaderBoard extends AppCompatActivity {

    private Utils utils;
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    ArrayList<itemModel> recyclerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leader_board);
        View v1 = findViewById(android.R.id.content);
        utils = new Utils(this, v1, this);

        setSupportActionBar(findViewById(R.id.view_leaderboard_topAppBar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listenerInit();
        leaderBoardRecyclerViewInit();

    }

    private void leaderBoardRecyclerViewInit() {

        RecyclerView leaderboard = findViewById(R.id.recyclerview_leaderboard_view);
        recyclerViewItemInit();

        LeaderboardViewRecyclerAdapter adapter = new LeaderboardViewRecyclerAdapter(this, recyclerItems);

        leaderboard.setAdapter(adapter);
        leaderboard.setLayoutManager(new LinearLayoutManager(this));

    }

    private void recyclerViewItemInit() {
        String[] names = getResources().getStringArray(R.array.names);

        for (int i = 0; i < names.length; i++) {
            recyclerItems.add(new itemModel(names[i], i + 1)); // Increment position value by 1 to start counting from 1
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Replace R.raw.tap_sound with your actual sound resource ID
            utils.playSFX(R.raw.tap_sfx);
        }
        return super.dispatchTouchEvent(event);
    }

    private void listenerInit(){

        findViewById(R.id.button_leaderboard_sign_in_sign_up).setOnClickListener(view -> {
            startActivity(new Intent(LeaderBoard.this, SignIn.class));
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(LeaderBoard.this, ProfileView.class));
                finish();
            }
        });
    }

    private void updateUI(){
        TextView userName = findViewById(R.id.text_leaderboard_Username);
        TextView profileDescription = findViewById(R.id.text_leaderboard_description);
        Button signIn = findViewById(R.id.button_leaderboard_sign_in_sign_up);
        ImageView userAvatar = findViewById(R.id.image_leaderboard_leaderboard_icon);

        if (firebaseHelper.isLoggedIn()){
            userName.setText(firebaseHelper.getUserName());
            signIn.setVisibility(View.GONE);
            utils.getBitmapFromByte(output -> {
                if (!(output == null)){
                    userAvatar.setColorFilter(Color.TRANSPARENT);
                    userAvatar.setImageBitmap(output);
                } else {
                    userAvatar.setColorFilter(getColor(R.color.background));
                    userAvatar.setImageResource(R.drawable.ic_account_circle_24);
                }
            });
            profileDescription.setText(utils.getBestTotaltime());
        } else {
            userName.setText("Guests");
            signIn.setVisibility(View.VISIBLE);
            userAvatar.setColorFilter(getColor(R.color.background));
            userAvatar.setImageResource(R.drawable.ic_account_circle_24);
            profileDescription.setText(R.string.string_leaderboard_signIn_function_description);
        }

    }

}