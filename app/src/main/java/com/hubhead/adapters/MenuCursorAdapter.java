package com.hubhead.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.hubhead.R;
import com.hubhead.contentprovider.CirclesContentProvider;
import com.hubhead.helpers.TypefacesHelper;

public class MenuCursorAdapter extends CursorAdapter {
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private final Typeface tf;
    private LayoutInflater mInflater;

    public MenuCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tf = TypefacesHelper.get(context, "fonts/exljbris_-_museosanscyrl-300-webfont.ttf");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.item_list_menu, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView contentTV = (TextView) view.findViewById(R.id.text1);
        String name = cursor.getString(cursor.getColumnIndex(CirclesContentProvider.CIRCLE_NAME));
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        contentTV.setText(name);
        contentTV.setTypeface(tf);

        TextView countTV = (TextView) view.findViewById(R.id.text2);
        int count = cursor.getInt(cursor.getColumnIndex(CirclesContentProvider.CIRCLE_COUNT_NOTIFICATIONS));
        String countText = count == 0 ? "" : Integer.toString(count);
        countTV.setText(countText);
        countTV.setTypeface(tf);
    }
}
