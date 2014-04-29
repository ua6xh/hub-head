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

import com.hubhead.helpers.DBHelper;

public class RemindersContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.hubhead.contentproviders.RemindersContentProvider";
    public static final String _ID = "_id";
    public static final String CIRCLE_ID = "circle_id";
    public static final String SPHERE_ID = "sphere_id";
    public static final String TASK_ID = "task_id";
    public static final String TASK_NAME = "task_name";
    public static final String TASK_STATUS = "task_status";
    public static final String START_TIME = "start_time";
    public static final String DEFAULT_SORT_ORDER = START_TIME + " DESC";
    public static final String DEADLINE = "deadline";
    public static final String TYPE = "type";
    public static final String[] QUERY_COLUMNS = {
            _ID,
            CIRCLE_ID,
            SPHERE_ID,
            TASK_ID,
            TASK_NAME,
            TASK_STATUS,
            START_TIME,
            DEADLINE,
            TYPE
    };
    public static final int ID_INDEX = 0;
    public static final int CIRCLE_ID_INDEX = 1;
    public static final int SPHERE_ID_INDEX = 2;
    public static final int TASK_ID_INDEX = 3;
    public static final int TASK_NAME_INDEX = 4;
    public static final int TASK_STATUS_INDEX = 5;
    public static final int START_TIME_INDEX = 6;
    public static final int DEADLINE_INDEX = 7;
    public static final int TYPE_INDEX = 8;
    public static final int COLUMN_COUNT = TYPE_INDEX + 1;
    // path
    static final String REMINDERS_PATH = "reminders";
    // Общий Uri
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + REMINDERS_PATH);
    // Типы данных
    // набор строк
    static final String REMINDER_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + REMINDERS_PATH;
    // одна строка
    static final String REMINDER_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + REMINDERS_PATH;
    //// UriMatcher
    // общий Uri
    static final int URI_REMINDERS = 1;
    // Uri с указанным ID
    static final int URI_REMINDERS_ID = 2;
    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, REMINDERS_PATH, URI_REMINDERS);
        uriMatcher.addURI(AUTHORITY, REMINDERS_PATH + "/#", URI_REMINDERS_ID);
    }

    private static final String TABLE = "reminders";
    private final String TAG = ((Object) this).getClass().getCanonicalName();
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
                    sortOrder = DEFAULT_SORT_ORDER;
                }
                break;
            case URI_REMINDERS_ID: { // Uri с ID
                String id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = _ID + " = " + id;
                } else {
                    selection = selection + " AND " + _ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        Log.d(TAG, selection);

        Cursor cursor = db.query(TABLE, QUERY_COLUMNS, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_REMINDERS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (rowID == -1) {
            Log.e(TAG, "db.insertWithOnConflict: -1");
        } else {
            Log.d(TAG, "db.insertWithOnConflict:" + rowID);

        }
        Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        getContext().getContentResolver().notifyChange(CirclesContentProvider.CONTENT_URI, null);

        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_REMINDERS: {
                break;
            }
            case URI_REMINDERS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = _ID + " = " + id;
                } else {
                    selection = selection + " AND " + _ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(CirclesContentProvider.CONTENT_URI, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_REMINDERS: {
                break;
            }
            case URI_REMINDERS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = _ID + " = " + id;
                } else {
                    selection = selection + " AND " + _ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] valueses) {
        if (valueses.length > 0) {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(TABLE, null, null);
                for (ContentValues values : valueses) {
                    db.insert(TABLE, null, values);
                }
                db.setTransactionSuccessful();
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage(), e);
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
                return REMINDER_CONTENT_TYPE;
            }
            case URI_REMINDERS_ID: {
                return REMINDER_CONTENT_ITEM_TYPE;
            }
        }
        return null;
    }
}
