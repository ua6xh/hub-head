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

public class NotificationsContentProvider extends ContentProvider {
    static final String AUTHORITY = "com.hubhead.contentproviders.NotificationsContentProvider";

    // path
    static final String NOTIFICATIONS_PATH = "notifications";

    // Общий Uri
    public static final Uri NOTIFICATION_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + NOTIFICATIONS_PATH);

    // Типы данных
    // набор строк
    static final String NOTIFICATION_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + NOTIFICATIONS_PATH;

    // одна строка
    static final String NOTIFICATION_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + NOTIFICATIONS_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_NOTIFICATIONS = 1;

    // Uri с указанным ID
    static final int URI_NOTIFICATIONS_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, NOTIFICATIONS_PATH, URI_NOTIFICATIONS);
        uriMatcher.addURI(AUTHORITY, NOTIFICATIONS_PATH + "/#", URI_NOTIFICATIONS_ID);
    }

    private static final String TAG = "NotificationsContentProvider";
    private static final String NOTIFICATION_NAME = "model_name";
    private static final String NOTIFICATION_ID = "_id";
    private static final String NOTIFICATION_TABLE = "notifications";

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
            case URI_NOTIFICATIONS: // общий Uri
                Log.d(TAG, "URI_NOTIFICATIONS");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = NOTIFICATION_NAME + " ASC";
                }
                break;
            case URI_NOTIFICATIONS_ID: // Uri с ID
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_NOTIFICATIONS_ID, " + id);
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = NOTIFICATION_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NOTIFICATION_ID + " = " + id;
                }
                break;
            default:{
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(NOTIFICATION_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в NOTIFICATION_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(), NOTIFICATION_CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_NOTIFICATIONS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(NOTIFICATION_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(NOTIFICATION_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_NOTIFICATIONS: {
                Log.d(TAG, "URI_NOTIFICATIONS");
                break;
            }
            case URI_NOTIFICATIONS_ID: {
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_NOTIFICATIONS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = NOTIFICATION_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NOTIFICATION_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(NOTIFICATION_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_NOTIFICATIONS: {
                Log.d(TAG, "URI_NOTIFICATIONS");
                break;
            }
            case URI_NOTIFICATIONS_ID: {
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_NOTIFICATIONS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = NOTIFICATION_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NOTIFICATION_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(NOTIFICATION_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public String getType(Uri uri) {
        Log.d(TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_NOTIFICATIONS: {
                return NOTIFICATION_CONTENT_TYPE;
            }
            case URI_NOTIFICATIONS_ID: {
                return NOTIFICATION_CONTENT_ITEM_TYPE;
            }
        }
        return null;
    }
}
