package com.example.insulinpump;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.insulinpump.database_configuration;

// Main activity is the interface handler (and activity_main.xml is the default display)
// Main activity is now also calculate.java

public class MainActivity extends WearableActivity {
    private static final String TAG = "MainActivity.java";

    // opens database access
    database_configuration db = new database_configuration(this);

    // for insulin calculations (was calculate.java)
    int insulin = 0;
    int glucose = -1;

    // insulin.xml, activity_main.xml:
    TextView txtGlucose;
    Button btnBasal, btnBolus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // insulin.xml, activity_main.xml:
        txtGlucose = (TextView) findViewById(R.id.txtGlucose);

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
        displayData();
    }
    public void insulinView(android.view.View View) {
        setContentView(R.layout.insulin);
        btnBasal = (Button) findViewById(R.id.btnBasal);
        btnBolus = (Button) findViewById(R.id.btnBolus);

        btnBasal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                calculate(true);
            }
        });
        btnBolus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                calculate(false);
            }
        });
    }

    // log_display.xml:
    // Shows log data in ListView
    public void displayData() {
        Intent intent = new Intent(MainActivity.this, DisplayLog.class);
        startActivity(intent); // adds contents from table to a list and puts in current view
        finish();
    }

    // insulin.xml:
    // Receives glucose value
    public int getGlucose() {
        int glucose = -1;
        try {
            glucose = Integer.parseInt(txtGlucose.getText().toString());
            if (glucose > 4000 || glucose < 0) {
                throw new ArithmeticException("getGlucose(): Glucose value is invalid. Must be a positive number no greater than 4000.");
            }
        } catch (ArithmeticException ae) {
            Log.d(TAG, ae.getMessage());
        } catch (Exception e) {
            Log.d(TAG, "getGlucose(): Value inserted was not an Integer.");
        }

        return glucose;
    }
    // Calculates insulin dosage
    public void calculate(boolean ifBasal) {
        try {
            glucose = getGlucose();

            if (glucose >= 349) {
                // very high alert
                insulin = 8;
            } else if (glucose >= 300) {
                insulin = 7;
            } else if (glucose >= 250) {
                insulin = 5;
            } else if (glucose >= 200) {
                insulin = 3;
            } else if (glucose >= 150) {
                insulin = 1;
            } else if (glucose <= 50) {
                // very low alert
            }

            // Calculating insulin-sensitivity or resistance
            String dosage = ""; // *****************************************************************get preferences here
            if (dosage == "sensitive") {
                insulin -= Math.floor(insulin * 1.45 - insulin);
            } else if (dosage == "resistant") {
                insulin = (int) Math.floor(insulin * 1.33) + 1;
                if (insulin == 11) {
                    insulin = 12;
                } else if (insulin == 1) {
                    insulin = 0;
                }
            }

            administerInsulin(insulin, ifBasal);

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
    // Insulin goes to needle and data is logged
    //**********************************************************************************************insulin reservoir needs to be implemented (needle.java may become reservoir.java)
    public void administerInsulin(int insulinDose, boolean isBasal) {
        boolean ifFail = false;
        String description = " ";

        try {
            if (isBasal) { // constant insulin supply

            } else { // instant insulin supply

            }
        } catch (Exception e) {
            ifFail = true;
            description = e.getMessage().substring(0, 255);
        }

        // logging data
        Log.d(TAG, "administerInsulin(...) : ma.getData(" + glucose + ", " + insulinDose + ", " + isBasal + ", " + ifFail + ", " + description + ")");
        db.addData(glucose, insulinDose, isBasal, ifFail, description);
        // Resets values
        glucose = -1;
        insulin = 0;
    }
}
