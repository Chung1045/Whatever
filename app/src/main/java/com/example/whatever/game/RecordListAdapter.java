package com.example.whatever.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Map;

public class RecordListAdapter extends BaseAdapter {
    private Context mContext;
    private Map<String, Object> mRecordHolder;

    public RecordListAdapter(Context context, Map<String, Object> recordHolder) {
        mContext = context;
        mRecordHolder = recordHolder;
    }

    @Override
    public int getCount() {
        return mRecordHolder.size(); // Number of records
    }

    @Override
    public Object getItem(int position) {
        // Get the record at the specified position
        // For simplicity, returning null as we don't need this method
        return null;
    }

    @Override
    public long getItemId(int position) {
        // Return the ID of the record at the specified position
        // For simplicity, returning 0 as we don't need this method
        return 0;
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
        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) mRecordHolder.entrySet().toArray()[position];
        String key = entry.getKey();
        Object value = entry.getValue();

        // Set the text of TextViews with the key-value pair
        textTitle.setText(key);
        textResult.setText(value.toString());

        return convertView;
    }

}

