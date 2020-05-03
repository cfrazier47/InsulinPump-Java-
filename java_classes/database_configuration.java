package com.example.insulinpump;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class database_configuration extends SQLiteOpenHelper {
    private static final String TAG = "database_configuration.java";

    public static final String TABLE_NAME = "Log";
    public static final String COLUMN_0 = "ID";
    public static final String COLUMN_1 = "glucose";
    public static final String COLUMN_2 = "insulin";
    public static final String COLUMN_3 = "basal";
    public static final String COLUMN_4 = "flag";
    public static final String COLUMN_5 = "description";
    public static final String COLUMN_6 = "date";
    public static final String COLUMN_7 = "time";

    public database_configuration(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    // Creates table below
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_0 + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_1 + " INTEGER NOT NULL, " +
                COLUMN_2 + " INTEGER NOT NULL, " +
                COLUMN_3 + " BOOLEAN, " +
                COLUMN_4 + " BOOLEAN, " +
                COLUMN_5 + " VARCHAR(255), " +
                COLUMN_6 + " DATE NOT NULL, " +
                COLUMN_7 + " VARCHAR(10) NOT NULL)";
        db.execSQL(createTable);
    }

    // Deletes and recreates when updated
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Used to insert data into table
    // addData(data) : ifSucceeded
    public boolean addData(int glucose, int insulin, boolean basal, boolean flag, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        limitRows(100); // deletes old rows if past 100 records

        ContentValues contentValues = new ContentValues();
        // ID is auto-generated (COLUMN_0)
        contentValues.put(COLUMN_1, glucose);
        contentValues.put(COLUMN_2, insulin);
        contentValues.put(COLUMN_3, basal);
        contentValues.put(COLUMN_4, flag);
        contentValues.put(COLUMN_5, description);
        Date currentDate = new java.sql.Date(System.currentTimeMillis()); // gets current date
        contentValues.put(COLUMN_6, currentDate.toString());
        SimpleDateFormat time24 = new SimpleDateFormat("HH:mm:ss");
        String time = time24.format(new java.util.Date());
        contentValues.put(COLUMN_7, time);

        // Temp logs data inserted
        String data = glucose + ", " + insulin + ", " + basal + ", " + flag + ", " + currentDate;
        Log.d(TAG, "addData: Adding " + data + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues); // adds value(s)

        // reports if column addition was a success:
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    // Returns table data
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // Deletes rows past limit
    public void limitRows(int limit) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Getting total number of rows
        int rowCount = -1;
        String rowCountQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;
        rowCount = Integer.parseInt(db.rawQuery(rowCountQuery, null).toString());

        // Getting minimum ID
        int minId = -1;
        String minIdQuery = "SELECT MIN(" + COLUMN_0 + ") FROM " + TABLE_NAME;
        minId = Integer.parseInt(db.rawQuery(minIdQuery, null).toString());

        // Deleting oldest records
        while ((rowCount > limit) && (rowCount != -1) && (minId != -1)) {
            String deleteQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_0 + " = '" + minId + "'";
            db.execSQL(deleteQuery);
            rowCount--;
            minId++;
        }
    }

    // Resets Log table (for editor-use only)
    public void resetLog() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_0 + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_1 + " INTEGER NOT NULL, " +
                COLUMN_2 + " INTEGER NOT NULL, " +
                COLUMN_3 + " BOOLEAN, " +
                COLUMN_4 + " BOOLEAN, " +
                COLUMN_5 + " VARCHAR(255), " +
                COLUMN_6 + " DATE NOT NULL, " +
                COLUMN_7 + " VARCHAR(10) NOT NULL)";
        db.execSQL(createTable);
    }
}
