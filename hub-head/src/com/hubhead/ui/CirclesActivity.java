package com.hubhead.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.test.IsolatedContext;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hubhead.R;
import com.hubhead.SFBaseActivity;
import com.hubhead.SFServiceCallbackListener;
import com.hubhead.fragments.CircleFragment;
import com.hubhead.handlers.impl.LoadCirclesData;
import com.hubhead.handlers.impl.LoadNotifications;
import com.hubhead.helpers.DBHelper;


public class CirclesActivity extends SFBaseActivity implements SFServiceCallbackListener, ListView.OnItemClickListener {
    private final static String MY_PREF = "MY_PREF";
    private static final String TAG = "CirclesActivity";
    private static final String PROGRESS_DIALOG_LOAD_CIRCLES_DATA = "progress-dialog-load-circles-data";
    private static final String PROGRESS_DIALOG_LOAD_NOTIFICATIONS = "progress-dialog-load-notifications";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mCirclesTitles;
    private int mRequestCirclesDataId = -1;
    private int mRequestNotificationsId = -1;
    private DBHelper mDbHelper;
    private ArrayAdapter<String> mDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circles_activity);
        mDbHelper = new DBHelper(this);
        mCirclesTitles = mDbHelper.getCirclesNames(mDbHelper.getWritableDatabase());

        createNavigationDrawer();


        if (savedInstanceState == null) {
            loadCirclesDataFromServer();
            selectItem(0);
        }
    }

    private void createNavigationDrawer() {
        mTitle = getTitle();
        mDrawerTitle = getResources().getString(R.string.drawer_open);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, mCirclesTitles);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
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
        menu.findItem(R.id.action_add).setVisible(!drawerOpen);
        menu.findItem(R.id.action_sign_out).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                SharedPreferences.Editor editor = this.getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                mDbHelper.truncateDB(mDbHelper.getWritableDatabase());
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectItem(i);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new CircleFragment();
        Bundle args = new Bundle();
        args.putInt(CircleFragment.ARG_CIRCLE_ID, position);
        args.putStringArray(CircleFragment.ARG_CIRCLES_NAMES, mCirclesTitles);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mCirclesTitles[position]);
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
        if (getServiceHelper().check(requestIntent, LoadCirclesData.class)) {
            if (resultCode == LoadCirclesData.RESPONSE_SUCCESS) {
                dismissProgressDialog(PROGRESS_DIALOG_LOAD_CIRCLES_DATA);
                loadNotificationsFromServer();
            } else if (resultCode == LoadCirclesData.RESPONSE_FAILURE) {
                dismissProgressDialog(PROGRESS_DIALOG_LOAD_CIRCLES_DATA);
                сreateAlertDialogSingIn(this.getResources().getString(R.string.alert_dialog_title_error), resultData.getString("error"));
            }
        }

        // Загрузка уведомлений
        if (getServiceHelper().check(requestIntent, LoadNotifications.class)) {
            if (resultCode == LoadNotifications.RESPONSE_SUCCESS) {
                dismissProgressDialog(PROGRESS_DIALOG_LOAD_NOTIFICATIONS);
            } else if (resultCode == LoadNotifications.RESPONSE_FAILURE) {
                dismissProgressDialog(PROGRESS_DIALOG_LOAD_NOTIFICATIONS);
                сreateAlertDialogSingIn(this.getResources().getString(R.string.alert_dialog_title_error), resultData.getString("error"));
            }
        }
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

    private void loadCirclesDataFromServer() {
        ProgressDialogFragment progress = new ProgressDialogFragment(this.getResources().getString(R.string.alert_dialog_message_load_circles_data));
        progress.show(getSupportFragmentManager(), PROGRESS_DIALOG_LOAD_CIRCLES_DATA);
        mRequestCirclesDataId = getServiceHelper().loadCirclesDataFromServer();
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
    }

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

    public String[] getCirclesTitles(){
        return mCirclesTitles;
    }


}
