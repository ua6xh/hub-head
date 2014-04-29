package com.hubhead.models;

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

import com.hubhead.contentprovider.RemindersContentProvider;

import java.util.LinkedList;
import java.util.List;

public final class Reminder implements Parcelable {
    /**
     * Alarms start with an invalid id when it hasn't been saved to the database.
     */
    public static final long INVALID_ID = -1;
    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        public Reminder createFromParcel(Parcel p) {
            return new Reminder(p);
        }

        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    // Public fields
    public long id;
    public long circle_id;
    public long sphere_id;
    public long task_id;
    public String task_name;
    public int task_status;
    public int user_id;
    public int user_role;
    public long start_time;
    public long deadline;
    public int type;

    // Creates a default reminder at the current time.
    public Reminder() {
    }

    public Reminder(int id, int circle_id, int sphere_id, String task_name, long start_time, long deadline) {
        this.id = id;
        this.circle_id = circle_id;
        this.sphere_id = sphere_id;
        this.task_name = task_name;
        this.start_time = start_time;
        this.deadline = deadline;
    }

    public Reminder(Cursor c) {
        id = c.getLong(RemindersContentProvider.ID_INDEX);
        circle_id = c.getLong(RemindersContentProvider.CIRCLE_ID_INDEX);
        sphere_id = c.getLong(RemindersContentProvider.SPHERE_ID_INDEX);
        task_id = c.getLong(RemindersContentProvider.TASK_ID_INDEX);
        task_name = c.getString(RemindersContentProvider.TASK_NAME_INDEX);
        task_status = c.getInt(RemindersContentProvider.TASK_STATUS_INDEX);
        start_time = c.getLong(RemindersContentProvider.START_TIME_INDEX);
        deadline = c.getLong(RemindersContentProvider.DEADLINE_INDEX);
        type = c.getInt(RemindersContentProvider.TYPE_INDEX);
    }


    Reminder(Parcel p) {
        id = p.readLong();
        circle_id = p.readLong();
        sphere_id = p.readLong();
        task_id = p.readLong();
        task_name = p.readString();
        task_status = p.readInt();
        start_time = p.readLong();
        deadline = p.readLong();
        type = p.readInt();
    }

    /**
     * The default sort order for this table
     */
    public static ContentValues createContentValues(Reminder reminder) {
        ContentValues values = new ContentValues(RemindersContentProvider.COLUMN_COUNT);
        if (reminder.id != INVALID_ID) {
            values.put(RemindersContentProvider._ID, reminder.id);
        }

        values.put(RemindersContentProvider.CIRCLE_ID, reminder.circle_id);
        values.put(RemindersContentProvider.SPHERE_ID, reminder.sphere_id);
        values.put(RemindersContentProvider.TASK_ID, reminder.task_id);
        values.put(RemindersContentProvider.TASK_NAME, reminder.task_name);
        values.put(RemindersContentProvider.START_TIME, reminder.start_time);
        values.put(RemindersContentProvider.DEADLINE, reminder.deadline);
        values.put(RemindersContentProvider.TYPE, reminder.type);

        return values;
    }

    public static Intent createIntent(String action, long reminderId) {
        return new Intent(action).setData(getUri(reminderId));
    }

    public static Intent createIntent(Context context, Class<?> cls, long reminderId) {
        return new Intent(context, cls).setData(getUri(reminderId));
    }

    public static Uri getUri(long reminderId) {
        return ContentUris.withAppendedId(RemindersContentProvider.CONTENT_URI, reminderId);
    }

    public static long getId(Uri contentUri) {
        return ContentUris.parseId(contentUri);
    }

    /**
     * Get reminder cursor loader for all reminders.
     *
     * @param context to query the database.
     * @return cursor loader with all the reminders.
     */
    public static CursorLoader getRemindersCursorLoader(Context context) {
        return new CursorLoader(context, RemindersContentProvider.CONTENT_URI, RemindersContentProvider.QUERY_COLUMNS, null, null, RemindersContentProvider.DEFAULT_SORT_ORDER);
    }

    /**
     * Get reminder by id.
     *
     * @param contentResolver to perform the query on.
     * @param reminderId      for the desired reminder.
     * @return reminder if found, null otherwise
     */
    public static Reminder getReminder(ContentResolver contentResolver, long reminderId) {
        Cursor cursor = contentResolver.query(getUri(reminderId), RemindersContentProvider.QUERY_COLUMNS, null, null, null);
        Reminder result = null;
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                result = new Reminder(cursor);
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    /**
     * Get all reminders given conditions.
     *
     * @param contentResolver to perform the query on.
     * @param selection       A filter declaring which rows to return, formatted as an
     *                        SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                        return all rows for the given URI.
     * @param selectionArgs   You may include ?s in selection, which will be
     *                        replaced by the values from selectionArgs, in the order that they
     *                        appear in the selection. The values will be bound as Strings.
     * @return list of reminders matching where clause or empty list if none found.
     */
    public static List<Reminder> getReminders(ContentResolver contentResolver, String selection, String... selectionArgs) {
        Cursor cursor = contentResolver.query(RemindersContentProvider.CONTENT_URI, RemindersContentProvider.QUERY_COLUMNS,
                selection, selectionArgs, null);
        List<Reminder> result = new LinkedList<Reminder>();
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    result.add(new Reminder(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    public static Reminder addReminder(ContentResolver contentResolver, Reminder reminder) {
        ContentValues values = createContentValues(reminder);
        Uri uri = contentResolver.insert(RemindersContentProvider.CONTENT_URI, values);
        reminder.id = getId(uri);
        return reminder;
    }

    public static boolean updateReminder(ContentResolver contentResolver, Reminder reminder) {
        if (reminder.id == Reminder.INVALID_ID) return false;
        ContentValues values = createContentValues(reminder);
        long rowsUpdated = contentResolver.update(getUri(reminder.id), values, null, null);
        return rowsUpdated == 1;
    }

    public static boolean deleteReminder(ContentResolver contentResolver, long reminderId) {
        if (reminderId == INVALID_ID) return false;
        int deletedRows = contentResolver.delete(getUri(reminderId), "", null);
        return deletedRows == 1;
    }

    public long getId() {
        return id;
    }

    public void writeToParcel(Parcel p, int flags) {
        p.writeLong(id);
        p.writeLong(circle_id);
        p.writeLong(sphere_id);
        p.writeLong(task_id);
        p.writeString(task_name);
        p.writeInt(task_status);
        p.writeLong(start_time);
        p.writeLong(deadline);
        p.writeInt(type);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reminder)) return false;
        final Reminder other = (Reminder) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public String toString() {
        return "Reminder{" +
                ", id=" + id +
                ", type=" + type +
                ", circle_id=" + circle_id +
                ", sphere_id=" + sphere_id +
                ", task_id='" + task_id + "'" +
                ", task_name=" + task_name +
                ", task_status='" + task_status + "'" +
                ", start_time=" + start_time +
                ", deadline='" + deadline + "'" +
                '}';
    }
}
