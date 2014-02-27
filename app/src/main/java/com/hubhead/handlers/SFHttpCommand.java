package com.hubhead.handlers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.test.IsolatedContext;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SFHttpCommand extends SFBaseCommand {

    private static final String MY_PREF = "MY_PREF";
    protected String TAG = ((Object) this).getClass().getCanonicalName();
    protected static String DOMAINE = "http://tm.dev-lds.ru";

    @Override
    public void doExecute(Intent intent, Context context, ResultReceiver callback) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected String sendHttpQuery(String url, Map<String, String> postData, Context context) {
        String response = "";
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json");
            httpPost.addHeader("Cookie", getCookiesPreference(context));
            if (!postData.isEmpty()) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(postData.size());
                for (Map.Entry<String, String> entry : postData.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }

            response = httpClient.execute(httpPost, responseHandler);

            List<Cookie> cookiesReq = httpClient.getCookieStore().getCookies();
            if (!cookiesReq.isEmpty()) {
                Log.d(TAG + " : test cookie", url);
                String cookieStr = "";
                for (Cookie cookie : cookiesReq) {
                    cookieStr += " " + cookie.getName() + "=" + cookie.getValue() + ";";
                    Log.d(TAG, cookieStr);
                }
                if (!cookieStr.equals("")) {
                    setCookiesPreference(cookieStr, context);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException: " +  e.getLocalizedMessage());
            response =  "";
        } finally {
            Log.d(TAG + ": url:" + url + ":", response);
        }
        return response;
    }

    protected boolean checkResponse(String response) {
        try {
            JSONObject jsonAuth = new JSONObject(response);
            String type = jsonAuth.getString("type");
            return type.equals("ok");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public String getCookiesPreference(Context context) {
        return context.getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).getString("cookies", "");
    }

    protected void setCookiesPreference(String value, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).edit();
        editor.putString("cookies", value);
        editor.commit();
    }
}
