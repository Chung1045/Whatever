package com.example.whatever.game;

import java.util.HashMap;

public class DinoLeaderboardViewModel {
    private final String userName;
    private final int dinoCount;
    private final int position;
    private final String profilePicURL;

    public DinoLeaderboardViewModel(HashMap<String, Object> leaderboardData) {
        this.userName = (String) leaderboardData.get("username");
        this.dinoCount = Integer.parseInt((String) leaderboardData.get("dinoCount"));
        this.position = ((Number) leaderboardData.get("position")).intValue();
        this.profilePicURL = (String) leaderboardData.get("profilePicURL");
    }

    public String getUserName() {
        return userName;
    }

    public int getDinoCount() {
        return dinoCount;
    }

    public int getPosition() {
        return position;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }
}

