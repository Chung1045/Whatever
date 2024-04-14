package com.example.whatever.game;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class LeaderBoard extends AppCompatActivity {

    private Utils utils;
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private ProgressBar progress;

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

        firebaseHelper.updateBestTime(this, successful ->{});
        progress = findViewById(R.id.progressBar_leaderboard);

        updateUI();
        listenerInit();
        leaderBoardRecyclerViewInit();

    }

    private void recyclerViewItemInit(Consumer<List<LeaderboardViewModel>> callback) {
        progress.setVisibility(View.VISIBLE);

        ConstraintLayout emptyView = findViewById(R.id.view_leaderboard_empty);
        RecyclerView leaderboard = findViewById(R.id.recyclerview_leaderboard_view);

        firebaseHelper.getLeaderboard(newLeaderboardData -> {
            if (newLeaderboardData != null) {
                if (newLeaderboardData.isEmpty()) {
                    leaderboard.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                } else {
                    List<LeaderboardViewModel> convertedData = convertToLeaderboardViewModels(newLeaderboardData);
                    callback.accept(convertedData);
                }
            } else {
                utils.showToastMessage("Try again");
                progress.setVisibility(View.GONE);
            }
        });
    }

    private List<LeaderboardViewModel> convertToLeaderboardViewModels(List<HashMap<String, Object>> data) {
        List<LeaderboardViewModel> convertedData = new ArrayList<>();
        for (HashMap<String, Object> userData : data) {
            LeaderboardViewModel viewModel = new LeaderboardViewModel(userData);
            convertedData.add(viewModel);
        }
        return convertedData;
    }

    private void leaderBoardRecyclerViewInit() {
        RecyclerView leaderboard = findViewById(R.id.recyclerview_leaderboard_view);
        recyclerViewItemInit(newLeaderboardData -> {
            LeaderboardViewRecyclerAdapter adapter = new LeaderboardViewRecyclerAdapter(this, newLeaderboardData);
            leaderboard.setAdapter(adapter);
            leaderboard.setLayoutManager(new LinearLayoutManager(this));

            new Handler().postDelayed(() -> progress.setVisibility(View.GONE), 2000);

        });
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
                finish();
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.view_leaderboard_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            leaderBoardRecyclerViewInit();
            swipeRefreshLayout.setRefreshing(false);
        });

        findViewById(R.id.button_leaderboard_record).setOnClickListener(view ->
                startActivity(new Intent().setClass(LeaderBoard.this, Records.class)));
    }

    private void updateUI(){
        TextView userName = findViewById(R.id.text_leaderboard_Username);
        TextView profileDescription = findViewById(R.id.text_leaderboard_description);
        Button signIn = findViewById(R.id.button_leaderboard_sign_in_sign_up);
        ImageView userAvatar = findViewById(R.id.image_leaderboard_leaderboard_icon);
        LinearLayout rankLayout = findViewById(R.id.view_leaderboard_rank_layout);
        TextView rankText = findViewById(R.id.text_leaderboard_rankResult);

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
            if (utils.isAllLevelPassed()){
                firebaseHelper.updateBestTime(this, successful ->{
                    if (successful){
                        rankLayout.setVisibility(View.VISIBLE);
                        String currentRank = String.valueOf(UserPreferences.sharedPref.getInt(UserPreferences.CURRENT_RANK, 0));
                        String totalCompetors = String.valueOf(UserPreferences.sharedPref.getInt(UserPreferences.TOTAL_COMPETITORS, 0));
                        rankText.setText(currentRank + " / " + totalCompetors);
                    } else {
                        utils.showSnackBarMessage("Error: Unable to fetch leaderboard");
                    }
                });

            }
        } else {
            userName.setText(getText(R.string.string_profile_defaultUsername));
            signIn.setVisibility(View.VISIBLE);
            userAvatar.setColorFilter(getColor(R.color.background));
            userAvatar.setImageResource(R.drawable.ic_account_circle_24);
            profileDescription.setText(R.string.string_leaderboard_signIn_function_description);
        }

    }



}