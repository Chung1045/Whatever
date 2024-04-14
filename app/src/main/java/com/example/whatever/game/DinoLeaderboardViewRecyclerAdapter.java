package com.example.whatever.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class DinoLeaderboardViewRecyclerAdapter extends RecyclerView.Adapter<DinoLeaderboardViewRecyclerAdapter.MyViewHolder> {

    private final Context context;
    private List<DinoLeaderboardViewModel> leaderboardData;


    public DinoLeaderboardViewRecyclerAdapter(Context context, List<DinoLeaderboardViewModel> leaderboardData){
        this.context = context;
        this.leaderboardData = leaderboardData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycleview_dinoleaderboard_item_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Assign / update data to views
        DinoLeaderboardViewModel item = leaderboardData.get(position);
        holder.userName.setText(item.getUserName());
        holder.position.setText(String.valueOf(item.getPosition()));
        holder.dinoCount.setText(String.valueOf(item.getDinoCount()));

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

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // Define views for the recycler view item
        private final TextView userName;
        private final TextView position;
        private final TextView dinoCount;
        private final ImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.recycler_dino_item_userName);
            position = itemView.findViewById(R.id.recycler_dino_item_position);
            profileImage = itemView.findViewById(R.id.recycler_dino_item_useravatar);
            dinoCount = itemView.findViewById(R.id.recycler_dino_item_dinoCount);
        }
    }
}
