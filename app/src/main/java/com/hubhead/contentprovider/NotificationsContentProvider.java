package com.hubhead.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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
    private static final String TABLE = "notifications";
    public static final String _ID = "_id";
    public static final String TYPE_NOTIFICATION = "type_notification";
    public static final String MESSAGES_COUNT = "messages_count";
    public static final String CIRCLE_ID = "circle_id";
    public static final String SPHERE_ID = "sphere_id";
    public static final String MODEL_NAME = "model_name";
    public static final String GROUPS = "groups";
    public static final String GROUPS_COUNT = "groups_count";
    public static final String CREATE_DATE = "create_date";
    public static final String DT = "dt";
    public static final String LAST_ACTION_USER_ID = "last_action_user_id";
    public static final String LAST_ACTION_DT = "last_action_dt";
    public static final String LAST_ACTION_TEXT = "last_action_text";
    public static final String LAST_ACTION_AUTHOR = "last_action_author";


    public static final int ID_INDEX = 0;
    public static final int TYPE_NOTIFICATION_INDEX = 1;
    public static final int MESSAGES_COUNT_INDEX = 2;
    public static final int CIRCLE_ID_INDEX = 3;
    public static final int SPHERE_ID_INDEX = 4;
    public static final int MODEL_NAME_INDEX = 5;
    public static final int GROUPS_INDEX = 6;
    public static final int GROUPS_COUNT_INDEX = 7;
    public static final int CREATE_DATE_INDEX = 8;
    public static final int DT_INDEX = 9;
    public static final int LAST_ACTION_USER_ID_INDEX = 10;
    public static final int LAST_ACTION_DT_INDEX = 11;
    public static final int LAST_ACTION_TEXT_INDEX = 12;
    public static final int LAST_ACTION_AUTHOR_INDEX = 13;

    public static final int COLUMN_COUNT = LAST_ACTION_AUTHOR_INDEX + 1;

    public static final String[] QUERY_COLUMNS = {
            _ID,
            TYPE_NOTIFICATION,
            MESSAGES_COUNT,
            CIRCLE_ID,
            SPHERE_ID,
            MODEL_NAME,
            GROUPS,
            GROUPS_COUNT,
            CREATE_DATE,
            DT,
            LAST_ACTION_USER_ID,
            LAST_ACTION_DT,
            LAST_ACTION_TEXT,
            LAST_ACTION_AUTHOR
    };

    public static final String DEFAULT_SORT_ORDER = DT + " DESC";

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
                    sortOrder = DEFAULT_SORT_ORDER;
                }
                break;
            case URI_NOTIFICATIONS_ID: { // Uri с ID
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
        cursor.setNotificationUri(getContext().getContentResolver(), NOTIFICATION_CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_NOTIFICATIONS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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
            Cursor cursor = db.query(TABLE, null, null, null, null, null, null);
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
                    db.replace(TABLE, null, cv);
                    update = true;
                }
                for (String deleteId : deleteNotifications) {
                    db.delete(TABLE, "_id = ?", new String[]{deleteId});
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
