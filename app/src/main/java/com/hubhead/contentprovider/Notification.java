package com.hubhead.contentprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

public final class Notification implements Parcelable {
    /**
     * Alarms start with an invalid id when it hasn't been saved to the database.
     */
    public static final long INVALID_ID = -1;

    /**
     * The default sort order for this table
     */
    public static final String _ID = "_id";
    public static final String TYPE_NOTIFICATION = "type_notification";
    public static final String MESSAGES_COUNT = "messages_count";
    public static final String CIRCLE_ID = "circle_id";
    public static final String SPHERE_ID = "sphere_id";
    public static final String MODEL_NAME = "model_name";
    public static final String GROUPS = "groups";
    public static final String CREATE_DATE = "create_date";
    public static final String DT = "dt";

    public static final int ID_INDEX = 0;
    public static final int TYPE_NOTIFICATION_INDEX = 1;
    public static final int MESSAGES_COUNT_INDEX = 2;
    public static final int CIRCLE_ID_INDEX = 3;
    public static final int SPHERE_ID_INDEX = 4;
    public static final int MODEL_NAME_INDEX = 5;
    public static final int GROUPS_INDEX = 6;
    public static final int CREATE_DATE_INDEX = 7;
    public static final int DT_INDEX = 8;

    private static final String[] QUERY_COLUMNS = {
            _ID,
            TYPE_NOTIFICATION,
            MESSAGES_COUNT,
            CIRCLE_ID,
            SPHERE_ID,
            MODEL_NAME,
            GROUPS,
            CREATE_DATE,
            DT
    };

    private static final int COLUMN_COUNT = DT_INDEX + 1;

    public static ContentValues createContentValues(Notification notification) {
        ContentValues values = new ContentValues(COLUMN_COUNT);
        if (notification.id != INVALID_ID) {
            values.put(_ID, notification.id);
        }

        values.put(TYPE_NOTIFICATION, notification.type_notification);
        values.put(MESSAGES_COUNT, notification.messages_count);
        values.put(CIRCLE_ID, notification.circle_id);
        values.put(SPHERE_ID, notification.sphere_id);
        values.put(MODEL_NAME, notification.model_name);
        values.put(GROUPS, notification.groups);
        values.put(CREATE_DATE, notification.create_date);
        values.put(DT, notification.dt);

        return values;
    }

    public static Intent createIntent(String action, long notificationId) {
        return new Intent(action).setData(getUri(notificationId));
    }

    public static Intent createIntent(Context context, Class<?> cls, long notificationId) {
        return new Intent(context, cls).setData(getUri(notificationId));
    }

    public static Uri getUri(long notificationId) {
        return ContentUris.withAppendedId(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, notificationId);
    }

    public static long getId(Uri contentUri) {
        return ContentUris.parseId(contentUri);
    }

    /**
     * Get notification cursor loader for all notifications.
     *
     * @param context to query the database.
     * @return cursor loader with all the notifications.
     */
    public static CursorLoader getNotificationsCursorLoader(Context context) {
        return new CursorLoader(context, NotificationsContentProvider.NOTIFICATION_CONTENT_URI, QUERY_COLUMNS, null, null, NotificationsContentProvider.DEFAULT_SORT_ORDER);
    }

    /**
     * Get notification by id.
     *
     * @param contentResolver to perform the query on.
     * @param notificationId  for the desired notification.
     * @return notification if found, null otherwise
     */
    public static Notification getNotification(ContentResolver contentResolver, long notificationId) {
        Cursor cursor = contentResolver.query(getUri(notificationId), QUERY_COLUMNS, null, null, null);
        Notification result = null;
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                result = new Notification(cursor);
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    /**
     * Get all notifications given conditions.
     *
     * @param contentResolver to perform the query on.
     * @param selection       A filter declaring which rows to return, formatted as an
     *                        SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                        return all rows for the given URI.
     * @param selectionArgs   You may include ?s in selection, which will be
     *                        replaced by the values from selectionArgs, in the order that they
     *                        appear in the selection. The values will be bound as Strings.
     * @return list of notifications matching where clause or empty list if none found.
     */
    public static List<Notification> getNotifications(ContentResolver contentResolver,
                                                      String selection, String... selectionArgs) {
        Cursor cursor = contentResolver.query(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, QUERY_COLUMNS,
                selection, selectionArgs, null);
        List<Notification> result = new LinkedList<Notification>();
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    result.add(new Notification(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    public static Notification addNotification(ContentResolver contentResolver, Notification notification) {
        ContentValues values = createContentValues(notification);
        Uri uri = contentResolver.insert(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, values);
        notification.id = getId(uri);
        return notification;
    }

    public static boolean updateNotification(ContentResolver contentResolver, Notification notification) {
        if (notification.id == Notification.INVALID_ID) return false;
        ContentValues values = createContentValues(notification);
        long rowsUpdated = contentResolver.update(getUri(notification.id), values, null, null);
        return rowsUpdated == 1;
    }

    public static boolean deleteNotification(ContentResolver contentResolver, long notificationId) {
        if (notificationId == INVALID_ID) return false;
        int deletedRows = contentResolver.delete(getUri(notificationId), "", null);
        return deletedRows == 1;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        public Notification createFromParcel(Parcel p) {
            return new Notification(p);
        }

        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    // Public fields
    // TODO: Refactor instance names
    public long id;
    public int type_notification;
    public int messages_count;
    public long circle_id;
    public long sphere_id;
    public String model_name;
    public String groups;
    public long create_date;
    public long dt;

    // Creates a default notification at the current time.
    public Notification() {
    }

    public Notification(Cursor c) {
        id = c.getLong(ID_INDEX);
        type_notification = c.getInt(TYPE_NOTIFICATION_INDEX);
        messages_count = c.getInt(MESSAGES_COUNT_INDEX);
        circle_id = c.getLong(CIRCLE_ID_INDEX);
        sphere_id = c.getLong(SPHERE_ID_INDEX);
        model_name = c.getString(MODEL_NAME_INDEX);
        groups = c.getString(GROUPS_INDEX);
        create_date = c.getLong(CREATE_DATE_INDEX);
        dt = c.getLong(DT_INDEX);
    }

    Notification(Parcel p) {
        id = p.readLong();
        type_notification = p.readInt();
        messages_count = p.readInt();
        circle_id = p.readLong();
        sphere_id = p.readLong();
        model_name = p.readString();
        groups = p.readString();
        create_date = p.readLong();
        dt = p.readLong();
    }

    public void writeToParcel(Parcel p, int flags) {
        p.writeLong(id);
        p.writeInt(type_notification);
        p.writeInt(messages_count);
        p.writeLong(circle_id);
        p.writeLong(sphere_id);
        p.writeString(model_name);
        p.writeString(groups);
        p.writeLong(create_date);
        p.writeLong(dt);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Notification)) return false;
        final Notification other = (Notification) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public String toString() {
        return "Notification{" +
                ", id=" + id +
                ", type_notification=" + type_notification +
                ", messages_count=" + messages_count +
                ", circle_id=" + circle_id +
                ", sphere_id=" + sphere_id +
                ", model_name=" + model_name +
                ", groups='" + groups + "'" +
                ", create_date=" + create_date +
                ", dt='" + dt + "'" +
                '}';
    }
}
