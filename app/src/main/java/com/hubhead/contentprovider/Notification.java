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
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public final class Notification implements Parcelable {
    /**
     * Alarms start with an invalid id when it hasn't been saved to the database.
     */
    public static final long INVALID_ID = -1;
    private final String TAG = ((Object) this).getClass().getCanonicalName();

    /**
     * The default sort order for this table
     */



    public static ContentValues createContentValues(Notification notification) {
        ContentValues values = new ContentValues(NotificationsContentProvider.COLUMN_COUNT);
        if (notification.id != INVALID_ID) {
            values.put(NotificationsContentProvider._ID, notification.id);
        }

        values.put(NotificationsContentProvider.TYPE_NOTIFICATION, notification.type_notification);
        values.put(NotificationsContentProvider.MESSAGES_COUNT, notification.messages_count);
        values.put(NotificationsContentProvider.CIRCLE_ID, notification.circle_id);
        values.put(NotificationsContentProvider.SPHERE_ID, notification.sphere_id);
        values.put(NotificationsContentProvider.MODEL_NAME, notification.model_name);
        values.put(NotificationsContentProvider.GROUPS, notification.groups);
        values.put(NotificationsContentProvider.CREATE_DATE, notification.create_date);
        values.put(NotificationsContentProvider.DT, notification.dt);

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
        return new CursorLoader(context, NotificationsContentProvider.NOTIFICATION_CONTENT_URI, NotificationsContentProvider.QUERY_COLUMNS, null, null, NotificationsContentProvider.DEFAULT_SORT_ORDER);
    }

    /**
     * Get notification by id.
     *
     * @param contentResolver to perform the query on.
     * @param notificationId  for the desired notification.
     * @return notification if found, null otherwise
     */
    public static Notification getNotification(ContentResolver contentResolver, long notificationId) {
        Cursor cursor = contentResolver.query(getUri(notificationId), NotificationsContentProvider.QUERY_COLUMNS, null, null, null);
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
        Cursor cursor = contentResolver.query(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, NotificationsContentProvider.QUERY_COLUMNS,
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
    public int groups_count;
    public long create_date;
    public long dt;
    public int last_action_user_id = 0;
    public long last_action_dt = 0;
    public String last_action_text = "";
    public String last_action_author = "";

    // Creates a default notification at the current time.
    public Notification() {
    }

    public Notification(Cursor c) {
        id = c.getLong(NotificationsContentProvider.ID_INDEX);
        type_notification = c.getInt(NotificationsContentProvider.TYPE_NOTIFICATION_INDEX);
        messages_count = c.getInt(NotificationsContentProvider.MESSAGES_COUNT_INDEX);
        circle_id = c.getLong(NotificationsContentProvider.CIRCLE_ID_INDEX);
        sphere_id = c.getLong(NotificationsContentProvider.SPHERE_ID_INDEX);
        model_name = c.getString(NotificationsContentProvider.MODEL_NAME_INDEX);
        groups = c.getString(NotificationsContentProvider.GROUPS_INDEX);
        groups_count = c.getInt(NotificationsContentProvider.GROUPS_COUNT_INDEX);
        create_date = c.getLong(NotificationsContentProvider.CREATE_DATE_INDEX);
        dt = c.getLong(NotificationsContentProvider.DT_INDEX);
        last_action_user_id = c.getInt(NotificationsContentProvider.LAST_ACTION_USER_ID_INDEX);
        last_action_dt = c.getLong(NotificationsContentProvider.LAST_ACTION_DT_INDEX);
        last_action_text = c.getString(NotificationsContentProvider.LAST_ACTION_TEXT_INDEX);
        last_action_author = c.getString(NotificationsContentProvider.LAST_ACTION_AUTHOR_INDEX);
    }

    Notification(Parcel p) {
        id = p.readLong();
        type_notification = p.readInt();
        messages_count = p.readInt();
        circle_id = p.readLong();
        sphere_id = p.readLong();
        model_name = p.readString();
        groups = p.readString();
        groups_count = p.readInt();
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
        p.writeInt(groups_count);
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
                ", groups_count='" + groups_count + "'" +
                ", create_date=" + create_date +
                ", dt='" + dt + "'" +
                '}';
    }
}
