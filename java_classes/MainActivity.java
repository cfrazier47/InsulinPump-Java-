package com.example.insulinpump;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import com.example.insulinpump.calculate;

// Main activity is the interface handler (and activity_main.xml is the default display)

public class MainActivity extends WearableActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();
    }

    // all xml:

    // Display functions:
    public void goHome(android.view.View View) {
        setContentView(R.layout.activity_main);
    }
    public void percentView(android.view.View View) {
        setContentView(R.layout.percent);
    }
    public void logView(android.view.View View) {
        setContentView(R.layout.log_display);
    }
    public void insulinView(android.view.View View) {
        setContentView(R.layout.insulin);
    }

    // insulin.xml :
    public void calculate(android.view.View View) {
        // refers to calculate.java
        calculate.calculate(View);
    }
}
