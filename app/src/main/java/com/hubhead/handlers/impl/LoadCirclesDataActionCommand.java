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
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import com.hubhead.R;
import com.hubhead.handlers.SFHttpCommand;
import com.hubhead.parsers.AllDataStructureJson;
import com.hubhead.parsers.ParseHelper;
import com.hubhead.parsers.SaverHelper;

import java.util.HashMap;

public class LoadCirclesDataActionCommand extends SFHttpCommand {

    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private String mUpdateTime;


    @Override
    public void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle data = new Bundle();
        HashMap<String, String> postData = new HashMap<String, String>();
        String response = "";

        if (!mUpdateTime.isEmpty()) {
            postData.put("update_time", mUpdateTime);
        }
        response = sendHttpQuery(DOMAINE + "/api/get-all-data", postData, context);
        Log.d(TAG, "RESPONSE:" + response);
        if (response.equals("")) {
            data.putString("error", context.getResources().getString(R.string.error_loading_data_fail));
            notifyFailure(data);
        } else if (checkResponse(response)) {
            data.putString("data", "ok");
            data.putString("response", response);
            AllDataStructureJson allDataStructureJson = ParseHelper.parseAllData(response);
            SaverHelper saverHelper = new SaverHelper(context);
            saverHelper.saveCircles(allDataStructureJson.data.circles);
            saverHelper.saveSpheres(allDataStructureJson.data.spheres);
            saverHelper.saveContacts(allDataStructureJson.data.contacts);

            saverHelper.saveReminders(allDataStructureJson.data.reminders);
//            setSharedPrefUpdateTime(Long.toString(allDataStructureJson.data.last_get_time));
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
        dest.writeString(mUpdateTime);
    }

    public static final Parcelable.Creator<LoadCirclesDataActionCommand> CREATOR = new Parcelable.Creator<LoadCirclesDataActionCommand>() {
        public LoadCirclesDataActionCommand createFromParcel(Parcel in) {

            return new LoadCirclesDataActionCommand(in);
        }

        public LoadCirclesDataActionCommand[] newArray(int size) {

            return new LoadCirclesDataActionCommand[size];
        }
    };

    private LoadCirclesDataActionCommand(Parcel in) {
        mUpdateTime = in.readString();
    }

    public LoadCirclesDataActionCommand(String arg1) {
        this.mUpdateTime = arg1;
    }

}
