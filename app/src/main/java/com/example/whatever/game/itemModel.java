package com.example.whatever.game;

public class itemModel {
    private String userName;
    private float bestTime;
    private int position;

    public itemModel(String userName, int position) {
        this.userName = userName;
        //this.bestTime = bestTime;
        this.position = position;
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
}
