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
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "hubhead";
    private static final String CIRCLES_TABLE_NAME = "circles";
    private static final String SPHERES_TABLE_NAME = "spheres";
    private static final String CONTACTS_TABLE_NAME = "contacts";
    private static final String REMINDERS_TABLE_NAME = "reminders";
    private static final String NOTIFICATIONS_TABLE_NAME = "notifications";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "--- onCreate " + CIRCLES_TABLE_NAME + " ---");
            db.execSQL("CREATE TABLE " + CIRCLES_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "name TEXT,"
                    + "count_notifications INTEGER,"
                    + "user_id INTEGER,"
                    + "contact_id INTEGER,"
                    + "status INTEGER"
                    + ");");

            Log.d(TAG, "--- onCreate " + SPHERES_TABLE_NAME + " ---");
            db.execSQL("CREATE TABLE " + SPHERES_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "name TEXT,"
                    + "circle_id INTEGER"
                    + ");");

            Log.d(TAG, "--- onCreate " + CONTACTS_TABLE_NAME + " ---");
            db.execSQL("CREATE TABLE " + CONTACTS_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "name TEXT,"
                    + "circle_id INTEGER,"
                    + "account_id INTEGER"
                    + ");");

            Log.d(TAG, "--- onCreate " + REMINDERS_TABLE_NAME + " ---");
            db.execSQL("CREATE TABLE " + REMINDERS_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "circle_id INTEGER,"
                    + "sphere_id INTEGER,"
                    + "task_name TEXT,"
                    + "start_time INTEGER,"
                    + "deadline INTEGER"
                    + ");");

            Log.d(TAG, "--- onCreate " + NOTIFICATIONS_TABLE_NAME + " ---");
            db.execSQL("CREATE TABLE " + NOTIFICATIONS_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "type_notification INTEGER,"
                    + "messages_count INTEGER,"
                    + "circle_id INTEGER,"
                    + "sphere_id INTEGER,"
                    + "model_name TEXT,"
                    + "groups TEXT,"
                    + "create_date INTEGER,"
                    + "dt INTEGER"
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
        db.execSQL("DROP TABLE IF EXISTS " + CIRCLES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SPHERES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + REMINDERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATIONS_TABLE_NAME);
        onCreate(db);
    }

    public void truncateDB(SQLiteDatabase db) {

        db.execSQL("DELETE FROM " + CIRCLES_TABLE_NAME);
        db.execSQL("DELETE FROM " + SPHERES_TABLE_NAME);
        db.execSQL("DELETE FROM " + CONTACTS_TABLE_NAME);
        db.execSQL("DELETE FROM " + REMINDERS_TABLE_NAME);
        db.execSQL("DELETE FROM " + NOTIFICATIONS_TABLE_NAME);
        Log.d(TAG, "--- truncateDB ---");
        db.close();

    }

    public Cursor getAllCircles(SQLiteDatabase db) {
        Cursor c = db.query(CIRCLES_TABLE_NAME, null, null, null, null, null, null);
        return c;

    }
}
