package com.example.whatever.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RecordListAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<Map.Entry<String, Object>> mRecordEntries;

    public RecordListAdapter(Context context, Map<String, Object> recordHolder) {
        mContext = context;
        mRecordEntries = new ArrayList<>(recordHolder.entrySet());
        // Sort the entries based on their keys
        Collections.sort(mRecordEntries, Map.Entry.comparingByKey());
    }

    @Override
    public int getCount() {
        return mRecordEntries.size(); // Number of records
    }

    @Override
    public Object getItem(int position) {
        return mRecordEntries.get(position); // Get the record entry at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position; // Return the position as the ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            // Inflate the row layout if it's not recycled
            convertView = inflater.inflate(R.layout.list_item_record, parent, false);
        }

        // Get the TextViews from the row layout
        TextView textTitle = convertView.findViewById(R.id.record_itemView_text_title);
        TextView textResult = convertView.findViewById(R.id.record_itemView_text_result);

        // Get the key-value pair at the specified position
        Map.Entry<String, Object> entry = mRecordEntries.get(position);
        String key = entry.getKey();
        Object value = entry.getValue();

        // Set the text of TextViews with the key-value pair
        textTitle.setText(key);
        textResult.setText(value.toString());

        return convertView;
    }
}
