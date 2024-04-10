package com.example.whatever.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LeaderboardViewModel {
    private String userName;
    private float bestTime;
    private int position;
    private String profilePicURL;

    public LeaderboardViewModel(HashMap<String, Object> leaderboardData) {
        this.userName = (String) leaderboardData.get("username");
        this.bestTime = ((Number) leaderboardData.get("bestTime")).floatValue();
        this.position = ((Number) leaderboardData.get("position")).intValue();
        this.profilePicURL = (String) leaderboardData.get("profilePicURL");
    }

    public String getUserName() {
        return userName;
    }

    public float getBestTime() {
        return bestTime;
    }

    public int getPosition() {
        return position;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }
}

