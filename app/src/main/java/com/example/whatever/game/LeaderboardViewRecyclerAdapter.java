package com.example.whatever.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LeaderboardViewRecyclerAdapter extends RecyclerView.Adapter<LeaderboardViewRecyclerAdapter.MyViewHolder> {

    private final Context context;
    private List<LeaderboardViewModel> leaderboardData;
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_CURRENT_USER = 1;


    public LeaderboardViewRecyclerAdapter(Context context, List<LeaderboardViewModel> leaderboardData){
        this.context = context;
        this.leaderboardData = leaderboardData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycleview_leaderboard_item_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Assign / update data to views
        LeaderboardViewModel item = leaderboardData.get(position);
        holder.userName.setText(item.getUserName());
        holder.position.setText(String.valueOf(item.getPosition()));
        holder.bestTime.setText(calculateTimeFromFloat(item.getBestTime()));

        // Load profile image using Picasso
        if (item.getProfilePicURL() == null) {
            Log.d("ProfilePicURL", "Null image");
            holder.profileImage.setColorFilter(R.color.background);
        } else {
            Log.d("ProfilePicURL", "Loading image");
            holder.profileImage.setColorFilter(Color.TRANSPARENT);
            Picasso.get().load(item.getProfilePicURL()).into(holder.profileImage);
        }
    }


    @Override
    public int getItemCount() {
        // Return the size of the list
        return leaderboardData.size();
    }

    @SuppressLint("DefaultLocale")
    private String calculateTimeFromFloat(float time){
        long seconds = (long) (time / 1000);
        long minutes = seconds / 60;
        seconds = seconds % 60;
        int milliseconds = (int) (time % 1000);

        return String.format("%02d:%02d:%03d", minutes, seconds, (long) milliseconds);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // Define views for the recycler view item
        private final TextView userName;
        private final TextView position;
        private final TextView bestTime;
        private final ImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            position = itemView.findViewById(R.id.position);
            profileImage = itemView.findViewById(R.id.recycler_item_useravatar);
            bestTime = itemView.findViewById(R.id.recycler_item_bestTime);
        }
    }
}
