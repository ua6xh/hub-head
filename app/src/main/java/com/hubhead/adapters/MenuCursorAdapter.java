package com.hubhead.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.hubhead.R;
import com.hubhead.contentprovider.CirclesContentProvider;

public class MenuCursorAdapter extends CursorAdapter {
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private LayoutInflater mInflater;

    public MenuCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.drawer_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView contentTV = (TextView) view.findViewById(R.id.text1);
        contentTV.setText(cursor.getString(cursor.getColumnIndex(CirclesContentProvider.CIRCLE_NAME)));

        TextView countTV = (TextView) view.findViewById(R.id.text2);
        int count = cursor.getInt(cursor.getColumnIndex(CirclesContentProvider.CIRCLE_COUNT_NOTIFICATIONS));
        String countText = count == 0 ? "" : Integer.toString(count);
        countTV.setText(countText);
    }
}
