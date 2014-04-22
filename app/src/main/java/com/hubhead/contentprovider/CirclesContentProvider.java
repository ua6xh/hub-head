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

public class CirclesContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.hubhead.contentproviders.CirclesContentProvider";

    // path
    static final String CIRCLES_PATH = "circles";

    // Общий Uri
    public static final Uri CIRCLE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CIRCLES_PATH);

    // Типы данных
    // набор строк
    static final String CIRCLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + CIRCLES_PATH;

    // одна строка
    static final String CIRCLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + CIRCLES_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_CIRCLES = 1;

    // Uri с указанным ID
    static final int URI_CIRCLES_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CIRCLES_PATH, URI_CIRCLES);
        uriMatcher.addURI(AUTHORITY, CIRCLES_PATH + "/#", URI_CIRCLES_ID);
    }

    private final String TAG = ((Object) this).getClass().getCanonicalName();
    public static final String CIRCLE_TABLE = "circles";

    public static final String CIRCLE_ID = "_id";
    public static final String CIRCLE_NAME = "name";
    public static final String CIRCLE_ADD_DATE = "add_date";
    public static final String CIRCLE_COUNT_NOTIFICATIONS = "count_notifications";

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int ADD_DATE_INDEX = 2;
    public static final int COUNT_NOTIFICATIONS_INDEX = 3;

    public static final String[] QUERY_COLUMNS = {
            CIRCLE_ID,
            CIRCLE_NAME,
            "(SELECT COUNT(*) FROM notifications n WHERE circles._id = n.circle_id  AND n._id NOT IN (SELECT _id FROM notifications WHERE messages_count = 0 AND groups_count = 0)) as " + CIRCLE_COUNT_NOTIFICATIONS,
            CIRCLE_ADD_DATE
    };
    //private final String[] mProjection = new String[]{CIRCLE_ID, CIRCLE_NAME, "0 as " + CIRCLE_COUNT_NOTIFICATIONS, CIRCLE_ADD_DATE};

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_CIRCLES: { // общий Uri
                // если сортировка не указана, ставим свою - по времени добавления
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CIRCLE_ADD_DATE + " ASC, " + CIRCLE_ID + " ASC";
                }
                break;
            }
            case URI_CIRCLES_ID: { // Uri с ID
                String id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = CIRCLE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + CIRCLE_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, sortOrder);
        Cursor cursor = db.query(CIRCLE_TABLE, QUERY_COLUMNS, selection, selectionArgs, null, null, sortOrder);
        //  Cursor cursor = db.rawQuery("SELECT , name, _id FROM circles c;", null);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в CIRCLE_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(), CIRCLE_CONTENT_URI);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_CIRCLES) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(CIRCLE_TABLE, null, values);

        Uri resultUri = ContentUris.withAppendedId(CIRCLE_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_CIRCLES: {
                break;
            }
            case URI_CIRCLES_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = CIRCLE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + CIRCLE_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(CIRCLE_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_CIRCLES: {
                break;
            }
            case URI_CIRCLES_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = CIRCLE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + CIRCLE_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(CIRCLE_TABLE, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (values.length > 0) {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(CIRCLE_TABLE, null, null);
                for (ContentValues value : values) {
                    db.insert(CIRCLE_TABLE, null, value);
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

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_CIRCLES: {
                return CIRCLE_CONTENT_TYPE;
            }
            case URI_CIRCLES_ID: {
                return CIRCLE_CONTENT_ITEM_TYPE;
            }
        }
        return null;
    }
}
