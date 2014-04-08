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

public class ContactsContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.hubhead.contentproviders.ContactsContentProvider";

    // path
    static final String CONTACTS_PATH = "contacts";

    // Общий Uri
    public static final Uri CONTACT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTACTS_PATH);

    // Типы данных
    // набор строк
    static final String CONTACT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + CONTACTS_PATH;

    // одна строка
    static final String CONTACT_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + CONTACTS_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_CONTACTS = 1;

    // Uri с указанным ID
    static final int URI_CONTACTS_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTACTS_PATH, URI_CONTACTS);
        uriMatcher.addURI(AUTHORITY, CONTACTS_PATH + "/#", URI_CONTACTS_ID);
    }

    private final String TAG = ((Object) this).getClass().getCanonicalName();
    public static final String CONTACT_ID = "_id";
    public static final String CONTACT_NAME = "name";
    public static final String CONTACT_CIRCLE_ID = "circle_id";
    public static final String CONTACT_ACCOUNT_ID = "account_id";

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int CIRCLE_ID_INDEX = 2;
    public static final int ACCOUNT_ID_INDEX = 3;

    public static final String CONTACT_TABLE = "contacts";
    public static final String[] QUERY_COLUMNS = {
            CONTACT_ID,
            CONTACT_NAME,
            CONTACT_CIRCLE_ID,
            CONTACT_ACCOUNT_ID
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
            case URI_CONTACTS: { // общий Uri
                // если сортировка не указана, ставим свою - по времени добавления
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CONTACT_CIRCLE_ID + " ASC, " + CONTACT_ACCOUNT_ID + " ASC" ;
                }
                break;
            }
            case URI_CONTACTS_ID: { // Uri с ID
                String id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = CONTACT_ID + " = " + id;
                } else {
                    selection = selection + " AND " + CONTACT_ID + " = " + id;
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
        Cursor cursor = null;
        try {

            cursor = db.query(CONTACT_TABLE, QUERY_COLUMNS, selection, selectionArgs, null, null, sortOrder);
            //  Cursor cursor = db.rawQuery("SELECT , name, _id FROM contacts c;", null);
            // просим ContentResolver уведомлять этот курсор
            // об изменениях данных в CONTACT_CONTENT_URI
            cursor.setNotificationUri(getContext().getContentResolver(), CONTACT_CONTENT_URI);
        } catch (NullPointerException e){
            Log.e(TAG, "NullPointerException in ContactsContentProvider", e);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_CONTACTS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(CONTACT_TABLE, null, values);

        Uri resultUri = ContentUris.withAppendedId(CONTACT_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS: {
                break;
            }
            case URI_CONTACTS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = CONTACT_ID + " = " + id;
                } else {
                    selection = selection + " AND " + CONTACT_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(CONTACT_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS: {
                break;
            }
            case URI_CONTACTS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = CONTACT_ID + " = " + id;
                } else {
                    selection = selection + " AND " + CONTACT_ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(CONTACT_TABLE, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valueses) {
        if (valueses.length > 0) {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(CONTACT_TABLE, null, null);
                for (ContentValues values : valueses) {
                    db.insert(CONTACT_TABLE, null, values);
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
            case URI_CONTACTS: {
                return CONTACT_CONTENT_TYPE;
            }
            case URI_CONTACTS_ID: {
                return CONTACT_CONTENT_ITEM_TYPE;
            }
        }
        return null;
    }
}