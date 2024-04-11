package com.example.whatever.game;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Records extends AppCompatActivity {

    private Utils utils;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_records);
        v = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        utils = new Utils(this, v, this);

        listInit();
    }

    private void listInit() {

        ListView mListView = findViewById(R.id.view_record_listView);

        // Get all records
        utils.localGetAllRecords(recordHolder -> {
            // Create custom adapter with the obtained records
            RecordListAdapter adapter = new RecordListAdapter(this, recordHolder);

            // Set the adapter for the ListView
            mListView.setAdapter(adapter);
        });

    }


}