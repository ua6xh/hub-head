package com.hubhead.handlers.impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.test.IsolatedContext;
import android.util.Log;

import com.hubhead.R;
import com.hubhead.handlers.SFHttpCommand;

import java.util.HashMap;


public class AuthGoogleActionCommand extends SFHttpCommand {
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private static final String MY_PREF = "MY_PREF";
    private final String mToken;
    private String mAccountName;

    public void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle data = new Bundle();
        HashMap<String, String> postData = new HashMap<String, String>();
        String response = "";

        postData.put("token", mToken);
        postData.put("account_name", mAccountName);
        response = sendHttpQuery(DOMAINE + "/auth/gglogin", postData, context);
        Log.d(TAG, "RESPONSE:" + response);
        if (response.equals("")) {
            data.putString("error", context.getResources().getString(R.string.error_undefined_error_network));
            notifyFailure(data);
        } else if (checkResponse(response)) {
            data.putString("data", "ok");
            data.putString("response", response);
            notifySuccess(data);
        } else {
            data.putString("error", context.getResources().getString(R.string.error_invalid_email_or_pass));
            SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();
            notifyFailure(data);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAccountName);
        dest.writeString(mToken);
    }

    public static final Parcelable.Creator<AuthGoogleActionCommand> CREATOR = new Parcelable.Creator<AuthGoogleActionCommand>() {
        public AuthGoogleActionCommand createFromParcel(Parcel in) {

            return new AuthGoogleActionCommand(in);
        }

        public AuthGoogleActionCommand[] newArray(int size) {

            return new AuthGoogleActionCommand[size];
        }
    };

    public AuthGoogleActionCommand(Parcel in) {
        mAccountName = in.readString();
        mToken = in.readString();
    }

    public AuthGoogleActionCommand(String accountName, String token) {
        mAccountName = accountName;
        mToken = token;
    }

}
