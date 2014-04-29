package com.hubhead.models;


import android.content.ContentResolver;
import android.database.Cursor;

import com.hubhead.contentprovider.ContactsContentProvider;

import java.util.HashMap;
import java.util.Map;

public class ContactModel {
    public long id;
    public long create_date;
    public long update_time;
    public String name;
    public long circle_id;
    public int position;
    public String[] email;
    public long account_id;
    public int account_role;
    public String last_invite_time;
    public int status;
    public String company;
    public String url;
    public String im;
    public String phone;
    public String address;
    public String birthday;
    public String relationship;
    public String other;
    public String note;
    public int user_id;
    private String TAG = ((Object) this).getClass().getCanonicalName();

    public ContactModel() {
    }

    public ContactModel(int id, int circle_id, int account_id, String name) {
        this.id = id;
        this.circle_id = circle_id;
        this.account_id = account_id;
        this.name = name;
    }

    public ContactModel(Cursor c) {
        id = c.getLong(ContactsContentProvider.ID_INDEX);
        name = c.getString(ContactsContentProvider.NAME_INDEX);
        circle_id = c.getLong(ContactsContentProvider.CIRCLE_ID_INDEX);
        account_id = c.getLong(ContactsContentProvider.ACCOUNT_ID_INDEX);
    }

    public static Map<String, ContactModel> getMap(ContentResolver contentResolver, String selection, String... selectionArgs) {
        Cursor cursor = contentResolver.query(ContactsContentProvider.CONTENT_URI, ContactsContentProvider.QUERY_COLUMNS, selection, selectionArgs, null);
        Map<String, ContactModel> result = new HashMap<String, ContactModel>();
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    ContactModel contactModel = new ContactModel(cursor);
                    result.put(contactModel.getIdForMap(), contactModel);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    public long getId() {
        return this.id;
    }

    public String getIdForMap() {
        return circle_id + "_" + account_id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
