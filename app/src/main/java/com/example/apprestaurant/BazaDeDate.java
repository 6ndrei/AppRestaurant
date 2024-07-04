package com.example.apprestaurant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BazaDeDate extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final String TABLE_NAME = "user_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "USERNAME";
    private static final String COL_3 = "PASSWORD";
    private static final String COL_4 = "RANK";

    public BazaDeDate(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERNAME TEXT, PASSWORD TEXT, RANK TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUser(String username, String password, String rank) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, password);
        contentValues.put(COL_4, rank);
        long result = db.insert(TABLE_NAME, null, contentValues);
        Log.d("BazaDeDate", "Insert result: " + result);
        return result != -1;
    }


    // Metodă pentru verificarea utilizatorului.
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE USERNAME = ? AND PASSWORD = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Metodă pentru a obține rangul utilizatorului
   /* public String getUserRank(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String rank = null;
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_4}, "USERNAME = ? AND PASSWORD = ?", new String[]{username, password}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            rank = cursor.getString(cursor.getColumnIndex(COL_4));
            cursor.close();
        }
        return rank;
    } */
}

