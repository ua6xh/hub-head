package com.hubhead.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hubhead.R;
import com.hubhead.SFBaseActivity;
import com.hubhead.SFServiceCallbackListener;
import com.hubhead.adapters.MenuCursorAdapter;
import com.hubhead.contentprovider.CirclesContentProvider;
import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.fragments.CircleFragment;
import com.hubhead.fragments.EmptyFragment;
import com.hubhead.handlers.impl.LoadCirclesDataActionCommand;
import com.hubhead.handlers.impl.LoadNotificationsActionCommand;
import com.hubhead.helpers.DBHelper;
import com.hubhead.models.NotificationModel;
import com.hubhead.service.WampService;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class CirclesActivity extends SFBaseActivity implements SFServiceCallbackListener, ListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private final static String MY_PREF = "MY_PREF";
    private static final int LOCAL_NOTIFICATION_ID = 1;
    private final String TAG = ((Object) this).getClass().getCanonicalName();
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

    private MenuCursorAdapter mDrawerAdapter;
    private int mCircleId = -1;
    private Bundle mSavedInstanceState = null;
    private int selectItemMenu = 0;
    private int mNotificationFlag = 0;
    private boolean mIsBound = false;

    /*----- GCM -----*/
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "685083954794";
    /*------ GCM end ----*/


    public void sendNotificationSetReaded(long notificationId) {
        Log.d(TAG, "call sendNotificationSetReaded");
        mBoundService.sendNotificationSetReaded(notificationId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "call onCreate");
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        setContentView(R.layout.circles_activity);
        mCircleId = getIntent().getIntExtra("circle_id", -1);
        mNotificationFlag = getIntent().getIntExtra("notification", 0);

        createNavigationDrawer();

        Intent i = new Intent(this, WampService.class);
        startService(i);
        doBindService();

        if (savedInstanceState == null && mCircleId == -1) {
            loadCirclesDataFromServer(1);
            EmptyFragment emptyFragment = new EmptyFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, emptyFragment).commit();
        } else if (savedInstanceState == null && mNotificationFlag == 1) {
            loadCirclesDataFromServer(0);
        }
        if (mNotificationFlag == 1) {
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(LOCAL_NOTIFICATION_ID);
        }


        context = getApplicationContext();
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }


    }

    private void createNavigationDrawer() {
        Log.d(TAG, "call createNavigationDrawer");
        mTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerAdapter = new MenuCursorAdapter(this, null, 0);
        getSupportLoaderManager().initLoader(CIRCLE_LOADER_ID, null, this);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                Log.d(TAG, "call onDrawerClosed");
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.d(TAG, "call onDrawerOpened");
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "call onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "call onPrepareOptionsMenu");
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_sign_out).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "call onOptionsItemSelected");
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_sign_out: {
                signOutAction();
                return true;
            }
            case R.id.action_add: {
                NotificationModel n = new NotificationModel();
                n.circle_id = 5;
                Random r = new Random();
                int i1 = r.nextInt(5000) + 65;
                n.model_name = "ADD TEST" + i1;
                n.dt = (long) i1;

                n.id = Integer.parseInt(Integer.toString(1) + Integer.toString(i1));
                getContentResolver().insert(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, n.getContentValues());
                //getContentResolver().notifyChange(CirclesContentProvider.CIRCLE_CONTENT_URI, null);
                return true;
            }
            case R.id.action_send: {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        Log.d(TAG, "call doInBackground");
                        String msg = "";
                        try {
                            Bundle data = new Bundle();
                            data.putString("my_message", "Hello World");
                            data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
                            String id = Integer.toString(msgId.incrementAndGet());
                            gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                            msg = "Sent message";
                        } catch (IOException ex) {
                            msg = "Error :" + ex.getMessage();
                        }
                        return msg;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        Log.d(TAG, "call onPostExecute");
                        Toast.makeText(getApplicationContext(), "ACTION_SEND: " + msg + "\n", Toast.LENGTH_LONG).show();
                    }
                }.execute(null, null, null);
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOutAction() {
        Log.d(TAG, "call signOutAction");
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
        Log.d(TAG, "call onItemClick");
        selectItem(position);
    }

    private void selectItem(int position) {
        Log.d(TAG, "call selectItem");
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
        Log.d(TAG, "call setTitle");
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d(TAG, "call onPostCreate");
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "call onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        Log.d(TAG, "call onServiceCallback");
        // Загрузка кругов
        if (getServiceHelper().check(requestIntent, LoadCirclesDataActionCommand.class)) {
            if (resultCode == LoadCirclesDataActionCommand.RESPONSE_SUCCESS) {
                dismissProgressDialog(PROGRESS_DIALOG_LOAD_CIRCLES_DATA);
                //loadNotificationsFromServer(mNotificationFlag);
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

    private void сreateAlertDialogSingIn(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(this.getResources().getString(R.string.alert_dialog_close_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "call onClick");
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadCirclesDataFromServer(int mode) {
        Log.d(TAG, "call loadCirclesDataFromServer");
        if (mode == 1) {
            ProgressDialogFragment progress = new ProgressDialogFragment(this.getResources().getString(R.string.alert_dialog_message_load_circles_data));
            progress.show(getSupportFragmentManager(), PROGRESS_DIALOG_LOAD_CIRCLES_DATA);
        }
        mRequestCirclesDataId = getServiceHelper().loadCirclesDataFromServer("");
        mRequestNotificationsId = getServiceHelper().loadNotificationsFromServer();
    }

    private void loadNotificationsFromServer(int mode) {
        Log.d(TAG, "call loadNotificationsFromServer");
        if (mode == 0) {
            ProgressDialogFragment progress = new ProgressDialogFragment(this.getResources().getString(R.string.alert_dialog_message_load_notifications));
            progress.show(getSupportFragmentManager(), PROGRESS_DIALOG_LOAD_NOTIFICATIONS);
        }
        mRequestNotificationsId = getServiceHelper().loadNotificationsFromServer();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "call onResume");
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
        Log.d(TAG, "call onLoaderReset");
        mDrawerAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "call onLoadFinished");
        mDrawerAdapter.swapCursor(cursor);

//        todo Из-за этого кода, дергается список при перестроении
//        if (cursor.getCount() > 0 && mCircleId == -1) {
//            handler.sendEmptyMessage(2);
//        } else if (cursor.getCount() > 0 && mCircleId != -1) {
//            while (cursor.moveToNext()) {
//                if (cursor.getLong(0) == mCircleId) {
//                    selectItemMenu = cursor.getPosition();
//                    break;
//                }
//            }
//            handler.sendEmptyMessage(2);
//        }
    }

    private Handler handler = new Handler()  // handler for commiting fragment after data is loaded
    {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "call handleMessage");
            if (msg.what == 2) {
                if (mSavedInstanceState == null) {
                    selectItem(selectItemMenu);
                }
                invalidateOptionsMenu();
            }
        }
    };
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
        Log.d(TAG, "call cancelCommand");
        if (mRequestCirclesDataId != -1) {
            getServiceHelper().cancelCommand(mRequestCirclesDataId);
        }
        if (mRequestNotificationsId != -1) {
            getServiceHelper().cancelCommand(mRequestNotificationsId);
        }
    }

    private void dismissProgressDialog(String tag) {
        Log.d(TAG, "call dismissProgressDialog");
        ProgressDialogFragment progress = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (progress != null) {
            progress.dismiss();
        }
    }

    /*------------------ BindService ------------------*/
    private WampService mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "call onServiceConnected");
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((WampService.LocalBinder) service).getService();

            // Tell the user about this for our demo.
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "call onServiceDisconnected");
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
        }
    };

    void doBindService() {
        Log.d(TAG, "call doBindService");
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(CirclesActivity.this, WampService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        Log.d(TAG, "call doUnbindService");
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "call onDestroy");
        super.onDestroy();
        doUnbindService();
    }

    /*---------------------- GCM Start -----------------------*/

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        Log.d(TAG, "call checkPlayServices");
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        Log.d(TAG, "call storeRegistrationId");
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        Log.d(TAG, "call getRegistrationId");
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        Log.d(TAG, "call registerInBackground");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.d(TAG, "call doInBackground");
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, "call onPostExecute");
                Toast.makeText(getApplicationContext(), "registerInBackground: " + msg + "\n", Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        Log.d(TAG, "call getGcmPreferences");
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        Log.d(TAG, "call sendRegistrationIdToBackend");
        // Your implementation here.
    }
    /*----------------------- GCM End ------------------------*/

}
