package com.hubhead.helpers;

public class TextHelper {
    public static String getTypeAndModel(long notificationId) {
        String str = Long.toString(notificationId);
        String result;
        result = str.substring(0, 1).equals("1") ? "task" : "sphere";
        result = result.concat("_").concat(str.substring(1, str.length()));
        return result;
    }

    public static String getCookieForSend(String cookie){
        String[] cookieSplit = cookie.split("=");
        return cookieSplit[1].substring(0, cookieSplit[1].length() - 1);
    }
}
