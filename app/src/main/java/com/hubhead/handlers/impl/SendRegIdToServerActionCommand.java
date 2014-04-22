/*
 * Copyright (C) 2013 Alexander Osmanov (http://perfectear.educkapps.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.hubhead.handlers.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.util.Log;

import com.hubhead.R;
import com.hubhead.handlers.SFHttpCommand;

import java.util.HashMap;
import java.util.Map;

public class SendRegIdToServerActionCommand extends SFHttpCommand {

    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private String mRegId;


    @Override
    public void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle data = new Bundle();
        HashMap<String, String> postData = new HashMap<String, String>();
        Map result;
        String response = "";
        String cookie = "";

        if (!mRegId.isEmpty()) {
            postData.put("token", mRegId);
        }
        result = sendHttpQuery(DOMAINE + "/api/add-android-token", postData, context);
        response = (String) result.get("response");
        if (result.containsKey("cookie")) {
            cookie = (String) result.get("cookie");
        }
        Log.d(TAG, "RESPONSE:" + response);
        if (checkResponse(response)) {
            data.putString("data", "token-ok");
            data.putString("response", response);
            setCookiesPreference(cookie, context);
            notifySuccess(data);
        } else {
            data.putString("error", context.getResources().getString(R.string.error_loading_data_fail));
            notifyFailure(data);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRegId);
    }

    public static final Creator<SendRegIdToServerActionCommand> CREATOR = new Creator<SendRegIdToServerActionCommand>() {
        public SendRegIdToServerActionCommand createFromParcel(Parcel in) {
            return new SendRegIdToServerActionCommand(in);
        }

        public SendRegIdToServerActionCommand[] newArray(int size) {
            return new SendRegIdToServerActionCommand[size];
        }
    };

    private SendRegIdToServerActionCommand(Parcel in) {
        mRegId = in.readString();
    }

    public SendRegIdToServerActionCommand(String arg1) {
        this.mRegId = arg1;
    }

}
