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
    static final String AUTHORITY = "com.hubhead.contentproviders.CirclesContentProvider";

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

    private static final String TAG = "CirclesContentProvider";
    private static final String CIRCLE_ID = "_id";
    private static final String CIRCLE_NAME = "name";
    private static final String CIRCLE_COUNT_NOTIFICATIONS = "count_notifications";
    private static final String CIRCLE_TABLE = "circles";
    private final String[] mProjection = new String[]{CIRCLE_ID, CIRCLE_NAME, CIRCLE_COUNT_NOTIFICATIONS};

    DBHelper dbHelper;
    SQLiteDatabase db;

    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    // чтение
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query, " + uri.toString());
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_CIRCLES: // общий Uri
                Log.d(TAG, "URI_CIRCLES");

                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CIRCLE_NAME + " ASC";
                }
                break;
            case URI_CIRCLES_ID: // Uri с ID
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_CIRCLES_ID, " + id);
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = CIRCLE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + CIRCLE_ID + " = " + id;
                }
                break;
            default:{
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(CIRCLE_TABLE, mProjection, selection, selectionArgs, null, null, sortOrder);
        //Cursor cursor = db.rawQuery("SELECT _id, name FROM circles", null);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в CIRCLE_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(), CIRCLE_CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_CIRCLES) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(CIRCLE_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(CIRCLE_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_CIRCLES: {
                Log.d(TAG, "URI_CIRCLES");
                break;
            }
            case URI_CIRCLES_ID: {
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_CIRCLES_ID, " + id);
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

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_CIRCLES: {
                Log.d(TAG, "URI_CIRCLES");
                break;
            }
            case URI_CIRCLES_ID: {
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_CIRCLES_ID, " + id);
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

    public String getType(Uri uri) {
        Log.d(TAG, "getType, " + uri.toString());
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
