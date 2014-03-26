package com.hubhead.models.ActionModels;

import android.content.Context;
import android.util.Log;

import com.hubhead.R;

public class UserModel{
    private Context context = null;
    public int id;
    public String name = null;
    public int role;

    public UserModel(int id, Context context) {
        this.id = id;
        this.context = context;
    }

    public UserModel(int id, int role, Context context) {
        this.id = id;
        this.role = role;
        this.context = context;
    }

    @Override
    public String toString() {
        if (name != null) {
            return name;
        } else {
            return "user " + getId();
        }
    }

    public int getId() {
        return this.id;
    }

    public String getRole() {
        try {
            switch (this.role) {
                case 1:
                    return context.getResources().getString(R.string.user_role_guest);
                case 2:
                    return context.getResources().getString(R.string.user_role_member);
                case 3:
                    return context.getResources().getString(R.string.user_role_admin);
                default:
                    return context.getResources().getString(R.string.user_role_undefined);
            }
        } catch (Exception e) {
            Log.d("hub-head", "Error, resource not found, context empty");
        }
        return "empty";
    }
}
