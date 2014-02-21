package com.hubhead.models;


public class ContactModel {
    public int id;
    public long update_time;
    public String name;
    public int circle_id;
    public int position;
    public String[] email;
    public int account_id;
    public int account_role;
    public String last_invite_time;
    public int status;
    public String company;
    public String url;
    public String im;
    public String phone;
    public String address;
    public String birthday;
    public String relationship;
    public String other;
    public String note;
    public int user_id;

    public ContactModel() {
    }

    public ContactModel(int id, int circle_id, int account_id, String name) {
        this.id = id;
        this.circle_id = circle_id;
        this.account_id = account_id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
