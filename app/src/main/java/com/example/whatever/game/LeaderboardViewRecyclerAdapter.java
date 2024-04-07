package com.example.whatever.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderboardViewRecyclerAdapter extends RecyclerView.Adapter<LeaderboardViewRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<itemModel> names;

    public LeaderboardViewRecyclerAdapter(Context context, ArrayList<itemModel> names){
        this.context = context;
        this.names = names;
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
        holder.userName.setText(names.get(position).getUserName());
        holder.position.setText(String.valueOf(names.get(position).getPosition()));
    }

    @Override
    public int getItemCount() {
        // Return the size of the list
        return names.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // Define views for the recycler view item
        private TextView userName;
        private TextView position;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            position = itemView.findViewById(R.id.position);

        }
    }

}
