package com.hubhead.models;


public class CircleModel {
    public int id;
    public String name;
    public long add_date;
    public int user_id;
    public int contact_id;
    public int status;

    public CircleModel() {
    } // Используется для Jackson

    public CircleModel(int id, String name) {
        this.id = id;
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
