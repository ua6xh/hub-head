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

import com.hubhead.handlers.SFHttpCommand;
import com.hubhead.parsers.ParseHelper;

import java.util.HashMap;

public class RefreshNotificationsActionCommand extends SFHttpCommand {

    private static final String TAG = "RefreshNotificationsActionCommand";
    String url = DOMAINE + "/api/get-notifications";

    @Override
    public void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle data = new Bundle();

        String response = sendHttpQuery(url, new HashMap<String, String>(), context);

        if (response.isEmpty()) {
            data.putString("error", "Empty notifications");
            notifyFailure(data);
        } else {
            data.putString("data", "ok");
            data.putString("response", response);
            ParseHelper parseHelper = new ParseHelper(context);
            parseHelper.parseNotifications(response);
            notifySuccess(data);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Creator<RefreshNotificationsActionCommand> CREATOR = new Creator<RefreshNotificationsActionCommand>() {
        public RefreshNotificationsActionCommand createFromParcel(Parcel in) {
            return new RefreshNotificationsActionCommand(in);
        }

        public RefreshNotificationsActionCommand[] newArray(int size) {
            return new RefreshNotificationsActionCommand[size];
        }
    };

    private RefreshNotificationsActionCommand(Parcel in) {
    }

    public RefreshNotificationsActionCommand() {
    }


}