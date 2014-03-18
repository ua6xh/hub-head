package com.hubhead.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CursorAdapter;

import com.hubhead.helpers.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OverviewContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.hubhead.contentproviders.OverviewContentProvider";
    // path
    static final String OVERVIEW_PATH = "overview";

    // Общий Uri
    public static final Uri OVERVIEW_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OVERVIEW_PATH);

    // Типы данных
    // набор строк
    static final String OVERVIEW_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + OVERVIEW_PATH;

    // одна строка
    static final String OVERVIEW_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + OVERVIEW_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_REMINDERS = 1;

    // Uri с указанным ID
    static final int URI_REMINDER_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, OVERVIEW_PATH, URI_REMINDERS);
        uriMatcher.addURI(AUTHORITY, OVERVIEW_PATH + "/#", URI_REMINDER_ID);
    }

    private final String TAG = ((Object) this).getClass().getCanonicalName();
    public static final String REMINDER_ID = "_id";
    public static final String REMINDER_CIRCLE_ID = "circle_id";
    public static final String REMINDER_SPHERE_ID = "sphere_id";
    public static final String REMINDER_TASK_ID = "task_id";
    public static final String REMINDER_TASK_NAME = "task_name";
    public static final String REMINDER_TASK_STATUS = "task_status";
    public static final String REMINDER_START_TIME = "start_time";
    public static final String REMINDER_DEADLINE = "deadline";
    public static final String REMINDER_TYPE = "type_reminder";
    private static final String REMINDERS_TABLE = "reminders";


    private final String[] mProjection = new String[]{
            REMINDER_ID,
            REMINDER_TASK_NAME,

            REMINDER_DEADLINE
    };

    DBHelper dbHelper;
    SQLiteDatabase db;

    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    // чтение
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_REMINDERS: // общий Uri
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = REMINDER_START_TIME;
                }
                break;
            case URI_REMINDER_ID: { // Uri с ID
                String id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = REMINDER_ID + " = " + id;
                } else {
                    selection = selection + " AND " + REMINDER_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(REMINDERS_TABLE, mProjection, selection, selectionArgs, REMINDER_TASK_ID, null, sortOrder, null);
        cursor.setNotificationUri(getContext().getContentResolver(), OVERVIEW_CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_REMINDERS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insertWithOnConflict(REMINDERS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (rowID == -1) {
            Log.e(TAG, "db.insertWithOnConflict: -1");
        } else {
            Log.d(TAG, "db.insertWithOnConflict:" + rowID);

        }
        Uri resultUri = ContentUris.withAppendedId(OVERVIEW_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        //getContext().getContentResolver().notifyChange(CirclesContentProvider.CIRCLE_CONTENT_URI, null);

        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_REMINDERS: {
                break;
            }
            case URI_REMINDER_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = REMINDER_ID + " = " + id;
                } else {
                    selection = selection + " AND " + REMINDER_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(REMINDERS_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        //getContext().getContentResolver().notifyChange(CirclesContentProvider.CIRCLE_CONTENT_URI, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_REMINDERS: {
                break;
            }
            case URI_REMINDER_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = REMINDER_ID + " = " + id;
                } else {
                    selection = selection + " AND " + REMINDER_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(REMINDERS_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valueses) {
        if (valueses.length > 0) {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(REMINDERS_TABLE, null, null);
                for (ContentValues values : valueses) {
                    db.insert(REMINDERS_TABLE, null, values);
                }
                db.setTransactionSuccessful();
            } catch (NullPointerException e) {
                Log.d(TAG, "NullPointerException bulkInsert: " + e.getMessage());
            } finally {
                db.endTransaction();
                db.close();
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return 0;
    }

    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_REMINDERS: {
                return OVERVIEW_CONTENT_TYPE;
            }
            case URI_REMINDER_ID: {
                return OVERVIEW_CONTENT_ITEM_TYPE;
            }
        }
        return null;
    }
}
