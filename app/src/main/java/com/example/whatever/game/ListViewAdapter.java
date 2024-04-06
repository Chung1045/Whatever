package com.example.whatever.game;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<String> {

    private ArrayList<String> dataSet;
    private Context mContext;

    public ListViewAdapter(ArrayList<String> data, Context context) {
        super(context, R.layout.listview_item_layout, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.textView2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String item = dataSet.get(position);
        if (item != null) {
            viewHolder.textView.setText(item);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
