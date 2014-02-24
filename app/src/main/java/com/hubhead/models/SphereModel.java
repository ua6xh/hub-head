package com.hubhead.models;


public class SphereModel {
    public int id;
    public int circle_id;
    public String name;
    public int user_id;
    public int sphere_group_id;
    public int archived;
    public int deleted;
    public String followers;
    public String roles;
    public long create_date;
    public long archived_time;
    public long update_time;

    public SphereModel() {
    }

    public SphereModel(int id, int circle_id, String name) {
        this.id = id;
        this.circle_id = circle_id;
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
