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
    public static final String AUTHORITY = "com.hubhead.contentproviders.NotificationsContentProvider";
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

    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private static final String NOTIFICATION_NAME = "model_name";
    private static final String NOTIFICATION_ID = "_id";
    private static final String NOTIFICATION_TABLE = "notifications";
    private final String[] mProjection = new String[]{NOTIFICATION_ID, NOTIFICATION_NAME};

    DBHelper dbHelper;
    SQLiteDatabase db;

    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    // чтение
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_NOTIFICATIONS: // общий Uri
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = "dt DESC";
                }
                break;
            case URI_NOTIFICATIONS_ID: // Uri с ID
                String id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = NOTIFICATION_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NOTIFICATION_ID + " = " + id;
                }
                break;
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(NOTIFICATION_TABLE, mProjection, selection, selectionArgs, null, null, sortOrder);
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
        long rowID = db.insertWithOnConflict(NOTIFICATION_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (rowID == -1) {
            Log.e(TAG, "db.insertWithOnConflict: -1");
        }
        Uri resultUri = ContentUris.withAppendedId(NOTIFICATION_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        Log.d(TAG, "Insert notification, update circles");
        getContext().getContentResolver().notifyChange(CirclesContentProvider.CIRCLE_CONTENT_URI, null);

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
        Log.d(TAG, NOTIFICATION_TABLE + " " + selection + " " + selectionArgs);
        int cnt = db.delete(NOTIFICATION_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "Delete notification, update circles");
        getContext().getContentResolver().notifyChange(CirclesContentProvider.CIRCLE_CONTENT_URI, null);
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
        Log.d(TAG, "change cursor " + NOTIFICATION_TABLE + " cnt:" + cnt);
        return cnt;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valueses) {
        if (valueses.length > 0) {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(NOTIFICATION_TABLE, null, null);
                for (ContentValues values : valueses) {
                    db.insert(NOTIFICATION_TABLE, null, values);
                }
                db.setTransactionSuccessful();
            } catch (NullPointerException e) {
                Log.e(TAG, "NullPointerException:" + e.getLocalizedMessage());
            } finally {
                db.endTransaction();
                db.close();
                getContext().getContentResolver().notifyChange(uri, null);
                getContext().getContentResolver().notifyChange(CirclesContentProvider.CIRCLE_CONTENT_URI, null);
            }
        }
        return 0;
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
