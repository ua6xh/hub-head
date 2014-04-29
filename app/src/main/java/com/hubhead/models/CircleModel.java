package com.hubhead.models;


import android.content.ContentResolver;
import android.database.Cursor;

import com.hubhead.contentprovider.CirclesContentProvider;

import java.util.HashMap;
import java.util.Map;

public class CircleModel {
    public long id;
    public String name;
    public long add_date;
    public int user_id;
    public int contact_id;
    public int status;
    private String TAG = ((Object) this).getClass().getCanonicalName();

    public CircleModel() {
    } // Используется для Jackson

    public CircleModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public CircleModel(Cursor c) {
        id = c.getLong(CirclesContentProvider.ID_INDEX);
        name = c.getString(CirclesContentProvider.NAME_INDEX);
    }

    public static Map<Long, CircleModel> getMap(ContentResolver contentResolver, String selection, String... selectionArgs) {
        Cursor cursor = contentResolver.query(CirclesContentProvider.CONTENT_URI, CirclesContentProvider.QUERY_COLUMNS, selection, selectionArgs, null);
        Map<Long, CircleModel> result = new HashMap<Long, CircleModel>();
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    CircleModel circleModel = new CircleModel(cursor);
                    result.put(circleModel.getId(), circleModel);
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

    @Override
    public String toString() {
        return this.name;
    }
}
