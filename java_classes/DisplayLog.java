package com.example.insulinpump;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

// Note: This is an activity and is included in the AndroidManifest.xml as:
// <activity android:name=".DisplayLog"></activity>
// Also requires this in the <application .../> :
// android:theme="@style/Theme.AppCompat"

public class DisplayLog extends AppCompatActivity {
    private static final String TAG = "DisplayLog.java";

    database_configuration db;

    private ListView lstLog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_display);
        lstLog = (ListView) findViewById(R.id.lstLog);
        db = new database_configuration(this);

        generateListView();
    }

    private void generateListView() {
        Log.d(TAG, "Generating data for the ListView");

        Cursor data = db.getData(); // receives contents of the table
        ArrayList<String> listData = new ArrayList<>();

        while(data.moveToNext()) {
            // Lists data: 99:99AM 9999 99 BASAL *IF FAIL
            listData.add(data.getString(6) + " " + data.getString(7) + " " +
                    data.getString(1) + " " + data.getString(2) + " " +
                    data.getString(3) + " " + data.getString(5));
        }

        // Creating a list adapter to view data
        ListAdapter adapter = new ArrayAdapter<>(this, R.layout.log_row_format, listData);
        lstLog.setAdapter(adapter);
    }

    private void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // Returns to MainActivity.java
    public void goHome(android.view.View View) {
        Intent intent = new Intent(DisplayLog.this, MainActivity.class);
        startActivity(intent);
    }

}
