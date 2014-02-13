package com.hubhead.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "hub-head: DBHelper";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "hubhead";
    private static final String CIRCLES = "circles";
    private static final String SPHERES = "spheres";
    private static final String CONTACTS = "contacts";
    private static final String REMINDERS = "reminders";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "--- onCreate " + CIRCLES + " ---");
            db.execSQL("create table " + CIRCLES + " ("
                    + "_id integer primary key,"
                    + "name text,"
                    + "user_id integer,"
                    + "contact_id integer,"
                    + "status integer"
                    + ");");

            Log.d(TAG, "--- onCreate " + SPHERES + " ---");
            db.execSQL("create table " + SPHERES + " ("
                    + "_id integer primary key,"
                    + "name text,"
                    + "circle_id integer"
                    + ");");

            Log.d(TAG, "--- onCreate " + CONTACTS + " ---");
            db.execSQL("create table " + CONTACTS + " ("
                    + "_id integer primary key,"
                    + "name text,"
                    + "circle_id integer,"
                    + "account_id integer"
                    + ");");

            Log.d(TAG, "--- onCreate " + REMINDERS + " ---");
            db.execSQL("create table " + REMINDERS + " ("
                    + "_id integer primary key,"
                    + "circle_id integer,"
                    + "sphere_id integer,"
                    + "task_name text,"
                    + "start_time integer,"
                    + "deadline integer"
                    + ");");
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void onOpen(SQLiteDatabase database) {
        if (!database.isOpen()) {
            SQLiteDatabase.openDatabase(database.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CIRCLES);
        db.execSQL("DROP TABLE IF EXISTS " + SPHERES);
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + REMINDERS);
        onCreate(db);
    }

    public void truncateDB(SQLiteDatabase db) {

        db.execSQL("DELETE FROM " + CIRCLES);
        db.execSQL("DELETE FROM " + SPHERES);
        db.execSQL("DELETE FROM " + CONTACTS);
        db.execSQL("DELETE FROM " + REMINDERS);
        Log.d(TAG, "--- truncateDB ---");
        db.close();

    }

    public String[] getCirclesNames(SQLiteDatabase db) {
        List<String> result = new ArrayList<String>();
        Cursor c = db.query(CIRCLES, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            do {
                result.add(c.getString(nameColIndex));
            } while (c.moveToNext());
        }
        result.add("test circle");
        c.close();
        String[] names = new String[result.size()];
        return result.toArray(names);
    }
}
