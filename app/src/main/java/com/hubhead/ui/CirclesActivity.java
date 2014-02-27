package com.hubhead.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.test.IsolatedContext;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hubhead.R;
import com.hubhead.SFBaseActivity;
import com.hubhead.SFServiceCallbackListener;
import com.hubhead.contentprovider.CirclesContentProvider;
import com.hubhead.fragments.CircleFragment;
import com.hubhead.handlers.impl.LoadCirclesDataActionCommand;
import com.hubhead.handlers.impl.LoadNotificationsActionCommand;
import com.hubhead.helpers.DBHelper;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;


public class CirclesActivity extends SFBaseActivity implements SFServiceCallbackListener, ListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private final static String MY_PREF = "MY_PREF";
    private final String TAG =  ((Object) this).getClass().getCanonicalName();
    private static final String PROGRESS_DIALOG_LOAD_CIRCLES_DATA = "progress-dialog-load-circles-data";
    private static final String PROGRESS_DIALOG_LOAD_NOTIFICATIONS = "progress-dialog-load-notifications";
    private static final String F_CIRCLES = "CirclesFragment";
    private static final int CIRCLE_LOADER_ID = 1;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private int mRequestCirclesDataId = -1;
    private int mRequestNotificationsId = -1;

    private SimpleCursorAdapter mDrawerAdapter;
    private int mCircleId = -1;
    private Bundle mSavedInstanceState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        setContentView(R.layout.circles_activity);

        createNavigationDrawer();

        getApp().getWampClient();

        if (savedInstanceState == null) {
            loadCirclesDataFromServer("");
        }
    }

    private void createNavigationDrawer() {
        mTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerAdapter = new SimpleCursorAdapter(this, R.layout.drawer_list_item, null, new String[]{"name", "count_notifications"}, new int[]{R.id.text1, R.id.text2}, 0);
        getSupportLoaderManager().initLoader(CIRCLE_LOADER_ID, null, this);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_sign_out).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
//            case R.id.action_add_circle: {
//
//                Random r = new Random();
//                int i1 = r.nextInt(1000);
//
//                ContentValues cv = new ContentValues();
//                cv.put("name", "name Circle " + i1);
//                cv.put("_id", i1);
//                Uri newUri2 = getContentResolver().insert(CirclesContentProvider.CIRCLE_CONTENT_URI, cv);
//
//                Toast.makeText(this, "Add actions:" + newUri2.toString(), Toast.LENGTH_SHORT).show();
//                return true;
//            }
//            case R.id.action_add_notification: {
//                ContentValues cv = new ContentValues();
//                cv.put("model_name", "name Notify " + mCircleId);
//                cv.put("circle_id", mCircleId);
//                Uri newUri = getContentResolver().insert(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, cv);
//                Toast.makeText(this, "Add actions:" + newUri.toString(), Toast.LENGTH_SHORT).show();
//
//                cv = new ContentValues();
//                cv.put("count_notifications", ++mCircleCountNotifications);
//                Uri itemUri = ContentUris.withAppendedId(CirclesContentProvider.CIRCLE_CONTENT_URI, mCircleId);
//                int cnt = getContentResolver().update(itemUri, cv, null, null);
//                return true;
//            }
            case R.id.action_sign_out: {
                signOutAction();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOutAction() {
        SharedPreferences.Editor editor = this.getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        DBHelper mDbHelper = new DBHelper(this);
        mDbHelper.truncateDB(mDbHelper.getWritableDatabase());
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        selectItem(position);
    }

    private void selectItem(int position) {
        Cursor cursor = (Cursor) mDrawerAdapter.getItem(position);
        int columnIndexId = cursor.getColumnIndex("_id");
        int columnIndexName = cursor.getColumnIndex("name");
        // update the main content by replacing fragments
        mCircleId = cursor.getInt(columnIndexId);//
        String circleName = cursor.getString(columnIndexName);
        CircleFragment circleFragment = new CircleFragment();
        Bundle args = new Bundle();
        args.putInt(CircleFragment.ARG_CIRCLE_ID, mCircleId);
        args.putString(CircleFragment.ARG_CIRCLE_NAME, circleName);
        circleFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, circleFragment).addToBackStack(F_CIRCLES).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(circleName);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        // Загрузка кругов
        if (getServiceHelper().check(requestIntent, LoadCirclesDataActionCommand.class)) {
            if (resultCode == LoadCirclesDataActionCommand.RESPONSE_SUCCESS) {
                dismissProgressDialog(PROGRESS_DIALOG_LOAD_CIRCLES_DATA);
                loadNotificationsFromServer();
            } else if (resultCode == LoadCirclesDataActionCommand.RESPONSE_FAILURE) {
                dismissProgressDialog(PROGRESS_DIALOG_LOAD_CIRCLES_DATA);
                Toast.makeText(this, resultData.getString("error"), Toast.LENGTH_SHORT).show();
            }
        }

        // Загрузка уведомлений
        if (getServiceHelper().check(requestIntent, LoadNotificationsActionCommand.class)) {
            if (resultCode == LoadNotificationsActionCommand.RESPONSE_SUCCESS) {
                dismissProgressDialog(PROGRESS_DIALOG_LOAD_NOTIFICATIONS);
            } else if (resultCode == LoadNotificationsActionCommand.RESPONSE_FAILURE) {
                dismissProgressDialog(PROGRESS_DIALOG_LOAD_NOTIFICATIONS);
                Toast.makeText(this, resultData.getString("error"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setSharedPrefUpdateTime(String update_time) {
        SharedPreferences.Editor editor = this.getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).edit();
        editor.putString("update_time", update_time);
        editor.commit();
    }

    private String getSharedPrefUpdateTime() {
        return getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).getString("update_time", "");
    }


    private void сreateAlertDialogSingIn(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(this.getResources().getString(R.string.alert_dialog_close_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadCirclesDataFromServer(String updateTime) {
        ProgressDialogFragment progress = new ProgressDialogFragment(this.getResources().getString(R.string.alert_dialog_message_load_circles_data));
        progress.show(getSupportFragmentManager(), PROGRESS_DIALOG_LOAD_CIRCLES_DATA);
        mRequestCirclesDataId = getServiceHelper().loadCirclesDataFromServer(updateTime);
    }

    private void loadNotificationsFromServer() {
        ProgressDialogFragment progress = new ProgressDialogFragment(this.getResources().getString(R.string.alert_dialog_message_load_notifications));
        progress.show(getSupportFragmentManager(), PROGRESS_DIALOG_LOAD_NOTIFICATIONS);
        mRequestNotificationsId = getServiceHelper().loadNotificationsFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRequestCirclesDataId != -1 && !getServiceHelper().isPending(mRequestCirclesDataId)) {
            dismissProgressDialog(PROGRESS_DIALOG_LOAD_CIRCLES_DATA);
        }
        if (mRequestNotificationsId != -1 && !getServiceHelper().isPending(mRequestNotificationsId)) {
            dismissProgressDialog(PROGRESS_DIALOG_LOAD_NOTIFICATIONS);
        }

        getSupportLoaderManager().restartLoader(CIRCLE_LOADER_ID, null, CirclesActivity.this);
        invalidateOptionsMenu();
    }

    /*------------------------------LoaderCallbacks Override---------------------------*/
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(this, CirclesContentProvider.CIRCLE_CONTENT_URI, new String[]{"name"}, null, null, null);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mDrawerAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        mDrawerAdapter.swapCursor(cursor);
        if (cursor.getCount() > 0 && mCircleId == -1) {
            handler.sendEmptyMessage(2);
        }
    }
    /*------------------------------End LoaderCallbacks---------------------------*/


    public static class ProgressDialogFragment extends DialogFragment {
        private String mMessage = "";

        public ProgressDialogFragment() {

        }

        public ProgressDialogFragment(String message) {
            mMessage = message;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(mMessage);
            return progressDialog;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            ((CirclesActivity) getActivity()).cancelCommand();
        }

        @Override
        public void onDestroyView() {
            if (getDialog() != null && getRetainInstance()) {
                getDialog().setOnDismissListener(null);
            }
            super.onDestroyView();
        }
    }

    public void cancelCommand() {
        if (mRequestCirclesDataId != -1) {
            getServiceHelper().cancelCommand(mRequestCirclesDataId);
        }
        if (mRequestNotificationsId != -1) {
            getServiceHelper().cancelCommand(mRequestNotificationsId);
        }
    }

    private void dismissProgressDialog(String tag) {
        ProgressDialogFragment progress = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (progress != null) {
            progress.dismiss();
        }
    }

    private Handler handler = new Handler()  // handler for commiting fragment after data is loaded
    {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                if (mSavedInstanceState == null) {
                    selectItem(0);
                }
                invalidateOptionsMenu();
            }
        }
    };
}
