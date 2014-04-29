package com.hubhead.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.hubhead.R;
import com.hubhead.contentprovider.RemindersContentProvider;
import com.hubhead.helpers.TypefacesHelper;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RemindersCursorAdapter extends CursorAdapter {
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private final Typeface tf;
    private LayoutInflater mInflater;

    public RemindersCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tf = TypefacesHelper.get(context, "fonts/exljbris_-_museosanscyrl-300-webfont.ttf");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.item_list_reminder, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView contentTV = (TextView) view.findViewById(R.id.text1);
        contentTV.setText(cursor.getString(cursor.getColumnIndex(RemindersContentProvider.TASK_NAME)));
        //contentTV.setTypeface(tf);

        TextView countTV = (TextView) view.findViewById(R.id.text2);
        long startTime = cursor.getLong(RemindersContentProvider.START_TIME_INDEX);
        countTV.setText(new SimpleDateFormat("d MMM, HH:mm").format(new Date(startTime * 1000)));
        //countTV.setTypeface(tf);
    }
}