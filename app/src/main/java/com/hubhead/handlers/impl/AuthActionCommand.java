package com.hubhead.handlers.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.hubhead.R;
import com.hubhead.handlers.SFHttpCommand;

import java.util.HashMap;


public class AuthActionCommand extends SFHttpCommand {

    private static final String TAG = "AuthActionCommand";

    private String mEmail;
    private String mPassword;


    @Override
    public void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle data = new Bundle();
        HashMap<String, String> postData = new HashMap<String, String>();
        String response = "";

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)) {
            data.putString("error", context.getResources().getString(R.string.error_invalid_email_or_pass));
            notifyFailure(data);
        }

        postData.put("login", mEmail);
        postData.put("mPassword", mPassword);
        response = sendHttpQuery(DOMAINE + "/auth/auth", postData, context);
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
            notifyFailure(data);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEmail);
        dest.writeString(mPassword);
    }

    public static final Parcelable.Creator<AuthActionCommand> CREATOR = new Parcelable.Creator<AuthActionCommand>() {
        public AuthActionCommand createFromParcel(Parcel in) {
            return new AuthActionCommand(in);
        }

        public AuthActionCommand[] newArray(int size) {
            return new AuthActionCommand[size];
        }
    };

    private AuthActionCommand(Parcel in) {
        mEmail = in.readString();
        mPassword = in.readString();
    }

    public AuthActionCommand(String arg1, String arg2) {
        this.mEmail = arg1;
        this.mPassword = arg2;
    }

}
