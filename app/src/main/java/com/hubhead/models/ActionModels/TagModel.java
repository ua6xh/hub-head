package com.hubhead.models.ActionModels;



public class TagModel{
    public int id;
    public String name;
    public String color;

    public TagModel(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }
}
