package com.hubhead.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.hubhead.helpers.DBHelper;
import com.hubhead.models.SphereModel;

import java.util.HashMap;
import java.util.Map;

public class SpheresContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.hubhead.contentproviders.SpheresContentProvider";

    // path
    static final String SPHERES_PATH = "spheres";

    // Общий Uri
    public static final Uri SPHERE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + SPHERES_PATH);

    // Типы данных
    // набор строк
    static final String SPHERE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + SPHERES_PATH;

    // одна строка
    static final String SPHERE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + SPHERES_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_SPHERES = 1;

    // Uri с указанным ID
    static final int URI_SPHERES_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, SPHERES_PATH, URI_SPHERES);
        uriMatcher.addURI(AUTHORITY, SPHERES_PATH + "/#", URI_SPHERES_ID);
    }

    private final String TAG = ((Object) this).getClass().getCanonicalName();
    public static final String SPHERE_ID = "_id";
    public static final String SPHERE_NAME = "name";
    public static final String SPHERE_CIRCLE_ID = "circle_id";

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int CIRCLE_ID_INDEX = 2;

    public static final String SPHERE_TABLE = "spheres";
    public static final String[] QUERY_COLUMNS = {
            SPHERE_ID,
            SPHERE_NAME,
            SPHERE_CIRCLE_ID
    };

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
            case URI_SPHERES: { // общий Uri
                // если сортировка не указана, ставим свою - по времени добавления
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = SPHERE_CIRCLE_ID + " ASC";
                }
                break;
            }
            case URI_SPHERES_ID: { // Uri с ID
                String id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = SPHERE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + SPHERE_ID + " = " + id;
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
            Log.e(TAG, e.getMessage(), e);
        }
        Cursor cursor = db.query(SPHERE_TABLE, QUERY_COLUMNS, selection, selectionArgs, null, null, sortOrder);
        //  Cursor cursor = db.rawQuery("SELECT , name, _id FROM spheres c;", null);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в SPHERE_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(), SPHERE_CONTENT_URI);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_SPHERES) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(SPHERE_TABLE, null, values);

        Uri resultUri = ContentUris.withAppendedId(SPHERE_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_SPHERES: {
                break;
            }
            case URI_SPHERES_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = SPHERE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + SPHERE_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(SPHERE_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_SPHERES: {
                break;
            }
            case URI_SPHERES_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = SPHERE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + SPHERE_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(SPHERE_TABLE, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valueses) {
        if (valueses.length > 0) {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(SPHERE_TABLE, null, null);
                for (ContentValues values : valueses) {
                    db.insert(SPHERE_TABLE, null, values);
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
            case URI_SPHERES: {
                return SPHERE_CONTENT_TYPE;
            }
            case URI_SPHERES_ID: {
                return SPHERE_CONTENT_ITEM_TYPE;
            }
        }
        return null;
    }


}
