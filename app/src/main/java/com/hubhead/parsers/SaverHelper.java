package com.hubhead.parsers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hubhead.contentprovider.CirclesContentProvider;
import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.helpers.DBHelper;
import com.hubhead.models.CircleModel;
import com.hubhead.models.ContactModel;
import com.hubhead.models.ReminderModel;
import com.hubhead.models.SphereModel;

import java.util.List;

public class SaverHelper {

    private final DBHelper dbHelper;
    public SaverHelper(Context context) {
        mContext = context;
        dbHelper = new DBHelper(mContext);
    }

    private Context mContext;

    public void saveCircles(List<CircleModel> items) {
        ContentValues[] circlesValues;
        circlesValues = new ContentValues[items.size()];
        int i = 0;
        for (CircleModel item : items) {
            ContentValues cv = new ContentValues();
            cv.put("_id", item.getId());
            cv.put("name", item.name);
            cv.put("add_date", item.add_date);
            cv.put("user_id", item.user_id);
            cv.put("contact_id", item.contact_id);
            cv.put("status", item.status);
            circlesValues[i] = cv;
            i++;
            Log.d("SaverHelper", cv.toString() + " iterator: " + i);
        }
        mContext.getContentResolver().bulkInsert(CirclesContentProvider.CIRCLE_CONTENT_URI, circlesValues);
    }

    // todo Передалать на getContentResolver
    public void saveContacts(List<ContactModel> items) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        for (ContactModel item : items) {
            cv.put("_id", item.getId());
            cv.put("circle_id", item.circle_id);
            cv.put("name", item.name);
            cv.put("account_id", item.account_id);
            db.replace("contacts", null, cv);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        dbHelper.close();
    }

    public void saveSpheres(List<SphereModel> items) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        for (SphereModel item : items) {
            cv.put("_id", item.getId());
            cv.put("circle_id", item.circle_id);
            cv.put("name", item.name);
            db.replace("spheres", null, cv);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        dbHelper.close();
    }

    public void saveReminders(List<ReminderModel> items) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        for (ReminderModel item : items) {
            cv.put("_id", item.getId());
            cv.put("circle_id", item.circle_id);
            cv.put("sphere_id", item.sphere_id);
            cv.put("task_name", item.task_name);
            db.replace("reminders", null, cv);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        dbHelper.close();
    }
}
