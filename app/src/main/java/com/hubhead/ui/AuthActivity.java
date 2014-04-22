package com.hubhead.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.test.IsolatedContext;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.hubhead.R;
import com.hubhead.SFBaseActivity;
import com.hubhead.handlers.impl.AuthActionCommand;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.List;


public class AuthActivity extends SFBaseActivity implements OnEditorActionListener, View.OnClickListener {

    private static final String PROGRESS_DIALOG_SIGN_IN = "progress-dialog-sign-in";
    private static final String PROGRESS_DIALOG_SIGN_IN_GOOGLE = "progress-dialog-sign-in-google";
    private static final String MY_PREF = "MY_PREF";

    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private static final int REQUEST_AVAILABILITY_ERROR = 48123;
    private static final int REQUEST_PICK_ACCOUNT = 48125;
    private static final int REQUEST_AUTH = 48127;


    private String mAccountName;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mButtonSignIn;
    private int mRequestAuthId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String cookie = getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).getString("cookies", "");
        if (cookie.isEmpty()) {
            setContentView(R.layout.auth_form);
            mEditEmail = (EditText) findViewById(R.id.edit_email);
            mEditEmail.addTextChangedListener(mTextWatcher);

            mEditPassword = (EditText) findViewById(R.id.edit_password);
            mEditPassword.setOnEditorActionListener(this);
            mEditPassword.addTextChangedListener(mTextWatcher);

            SignInButton mButtonSignInGoogle = (SignInButton) findViewById(R.id.button_sign_in_google);
            mButtonSignInGoogle.setSize(SignInButton.SIZE_WIDE);
            mButtonSignInGoogle.setOnClickListener(this);

            mButtonSignIn = (Button) findViewById(R.id.button_sign_in);
            mButtonSignIn.setOnClickListener(this);
            checkFieldsForEmptyValues();
        } else {
            startMainActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestAuthId != -1 && !getServiceHelper().isPending(mRequestAuthId)) {
            dismissProgressDialog(PROGRESS_DIALOG_SIGN_IN);
            cancelCommand();
        }
    }

    private void singInToServer() {
        ProgressDialogFragment progress = new ProgressDialogFragment(this.getResources().getString(R.string.alert_dialog_message_signing_process));
        progress.show(getSupportFragmentManager(), PROGRESS_DIALOG_SIGN_IN);
        mRequestAuthId = getServiceHelper().signInAction(mEditEmail.getText().toString(), mEditPassword.getText().toString());
    }

    private void startMainActivity() {
        Intent i = new Intent(this, CirclesActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        super.onServiceCallback(requestId, requestIntent, resultCode, resultData);

        // Простая авторизация
        if (getServiceHelper().check(requestIntent, AuthActionCommand.class)) {
            if (resultCode == AuthActionCommand.RESPONSE_SUCCESS) {
                dismissProgressDialog(PROGRESS_DIALOG_SIGN_IN);
                startMainActivity();
            } else if (resultCode == AuthActionCommand.RESPONSE_FAILURE) {
                dismissProgressDialog(PROGRESS_DIALOG_SIGN_IN);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_in: {
                singInToServer();
                break;
            }
            case R.id.button_sign_in_google: {
                int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if (result != ConnectionResult.SUCCESS) {
                    GooglePlayServicesUtil.getErrorDialog(result, this, REQUEST_AVAILABILITY_ERROR).show();
                    return;
                }
                Intent pickAccount = AccountPicker.newChooseAccountIntent(null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
                startActivityForResult(pickAccount, REQUEST_PICK_ACCOUNT);
                break;
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE && checkFieldsForEmptyValues()) {
            singInToServer();
        }
        return false;
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
            ((AuthActivity) getActivity()).cancelCommand();
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
        if (mRequestAuthId != -1) {
            getServiceHelper().cancelCommand(mRequestAuthId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if (requestCode == REQUEST_AVAILABILITY_ERROR && result == Activity.RESULT_OK) {
            onClick(null);
        } else if (requestCode == REQUEST_PICK_ACCOUNT) {
            if (result == Activity.RESULT_OK) {
                mAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                new SignInTask().execute();
            }
        } else if (requestCode == REQUEST_AUTH) {
            if (result == Activity.RESULT_OK) {
                new SignInTask().execute();
            }
        }
    }

    private class SignInTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            ProgressDialogFragment progress = new ProgressDialogFragment(getApplicationContext().getResources().getString(R.string.alert_dialog_message_signing_google_process));
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(progress, PROGRESS_DIALOG_SIGN_IN_GOOGLE);
            transaction.commitAllowingStateLoss();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.email" + " " + "https://www.googleapis.com/auth/userinfo.profile";
                String token = GoogleAuthUtil.getToken(getApplicationContext(), mAccountName, scopes);
                String url = "http://tm.dev-lds.ru/auth/gglogin/?account_name=" + mAccountName + "&token=" + token;
                Log.d("hub-head", url);

                DefaultHttpClient httpClient = new DefaultHttpClient();
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Accept", "application/json");
                String response = httpClient.execute(httpPost, responseHandler);

                List<Cookie> cookiesReq = httpClient.getCookieStore().getCookies();
                if (!cookiesReq.isEmpty()) {
                    String cookieStr = "";
                    for (Cookie cookie : cookiesReq) {
                        cookieStr += " " + cookie.getName() + "=" + cookie.getValue() + ";";
                    }
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).edit();
                    editor.putString("cookies", cookieStr);
                    editor.commit();
                    Log.d(TAG, "RESPONSE:" + response);
                    return 1;
                } else {
                    return 2;
                }
                //TODO if you reach this point the account is authenticated to use the scopes, so save the accountname somewhere
            } catch (UserRecoverableAuthException recoverableException) {
                recoverableException.getLocalizedMessage();
                Intent recoveryIntent = recoverableException.getIntent();
                startActivityForResult(recoveryIntent, REQUEST_AUTH);
            } catch (GoogleAuthException e) {
                e.getLocalizedMessage();
                return 3;
            } catch (IOException e) {
                e.getLocalizedMessage();
                return 4;
            }
            return 5;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG, "onPostExecute:RESULT:" + result);
            switch (result) {
                case 1: {
                    dismissProgressDialog(PROGRESS_DIALOG_SIGN_IN_GOOGLE);
                    startMainActivity();
                    break;
                }
                case 2:
                case 3: {
                    dismissProgressDialog(PROGRESS_DIALOG_SIGN_IN_GOOGLE);
                    сreateAlertDialogSingIn(
                            getApplicationContext().getResources().getString(R.string.alert_dialog_title_notice),
                            getApplicationContext().getResources().getString(R.string.alert_dialog_message_invalid_google_auth));
                    break;
                }
                case 4: {
                    dismissProgressDialog(PROGRESS_DIALOG_SIGN_IN_GOOGLE);
                    сreateAlertDialogSingIn(
                            getApplicationContext().getResources().getString(R.string.alert_dialog_title_error),
                            getApplicationContext().getResources().getString(R.string.error_undefined_error_network));
                    break;
                }
                case 5: {
                    Log.d(TAG, "return 5 code");
                    break;
                }
                default: {
                    dismissProgressDialog(PROGRESS_DIALOG_SIGN_IN_GOOGLE);
                }

            }
        }
    }

    private void dismissProgressDialog(String tag) {
        ProgressDialogFragment progress = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (progress != null) {
            progress.dismiss();
        }
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues();
        }
    };

    private boolean checkFieldsForEmptyValues() {
        String s1 = mEditEmail.getText().toString();
        String s2 = mEditPassword.getText().toString();
        boolean check = !(s1.equals("") || s2.equals(""));
        mButtonSignIn.setEnabled(check);
        return check;
    }
}