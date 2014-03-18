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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    public static final String NOTIFICATION_NAME = "model_name";
    public static final String NOTIFICATION_ID = "_id";
    public static final String NOTIFICATION_CIRCLE_ID = "circle_id";
    public static final String NOTIFICATION_MESSAGES_COUNT = "messages_count";
    private static final String NOTIFICATION_TABLE = "notifications";
    private final String[] mProjection = new String[]{NOTIFICATION_ID, NOTIFICATION_NAME, NOTIFICATION_CIRCLE_ID, NOTIFICATION_MESSAGES_COUNT};

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
            case URI_NOTIFICATIONS: // общий Uri
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = "dt DESC";
                }
                break;
            case URI_NOTIFICATIONS_ID: { // Uri с ID
                String id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
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
        Cursor cursor = db.query(NOTIFICATION_TABLE, mProjection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), NOTIFICATION_CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_NOTIFICATIONS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insertWithOnConflict(NOTIFICATION_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (rowID == -1) {
            Log.e(TAG, "db.insertWithOnConflict: -1");
        } else {
            Log.d(TAG, "db.insertWithOnConflict:" + rowID);

        }
        Uri resultUri = ContentUris.withAppendedId(NOTIFICATION_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        getContext().getContentResolver().notifyChange(CirclesContentProvider.CIRCLE_CONTENT_URI, null);

        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_NOTIFICATIONS: {
                break;
            }
            case URI_NOTIFICATIONS_ID: {
                String id = uri.getLastPathSegment();
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
        getContext().getContentResolver().notifyChange(CirclesContentProvider.CIRCLE_CONTENT_URI, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_NOTIFICATIONS: {
                break;
            }
            case URI_NOTIFICATIONS_ID: {
                String id = uri.getLastPathSegment();
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

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valueses) {
        if (valueses.length > 0) {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(NOTIFICATION_TABLE, null, null, null, null, null, null);
            Map<String, ContentValues> valuesNotifications = new HashMap<String, ContentValues>();
            ArrayList<String> deleteNotifications = new ArrayList<String>();
            for (ContentValues values : valueses) {
                valuesNotifications.put(values.get("_id").toString(), values);
            }
            int notificationId;
            int dtIndex = cursor.getColumnIndex("dt");
            while (cursor.moveToNext()) {
                notificationId = cursor.getInt(0);
                if (valuesNotifications.containsKey(Integer.toString(notificationId))) {
                    long dt = cursor.getLong(dtIndex);
                    ContentValues v = valuesNotifications.get(Integer.toString(notificationId));
                    if (dt == v.getAsLong("dt")) {
                        valuesNotifications.remove(Integer.toString(notificationId));
                    }
                } else {
                    deleteNotifications.add(Integer.toString(notificationId));
                }
            }
            db.beginTransaction();
            boolean update = false;
            try {
                Iterator it = valuesNotifications.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    ContentValues cv = (ContentValues) pairs.getValue();
                    db.replace(NOTIFICATION_TABLE, null, cv);
                    update = true;
                }
                for (String deleteId : deleteNotifications) {
                    db.delete(NOTIFICATION_TABLE, "_id = ?", new String[]{deleteId});
                    update = true;
                }
                db.setTransactionSuccessful();
            } catch (NullPointerException e) {
                Log.e(TAG, "NullPointerException:" + e.getLocalizedMessage());
            } finally {
                db.endTransaction();
                db.close();
                if (update) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(CirclesContentProvider.CIRCLE_CONTENT_URI, null);
                }
            }
        }
        return 0;
    }

    public String getType(Uri uri) {
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
