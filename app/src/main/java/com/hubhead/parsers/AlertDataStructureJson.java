package com.hubhead.parsers;


import com.hubhead.models.CircleModel;
import com.hubhead.models.ContactModel;
import com.hubhead.models.ReminderModel;
import com.hubhead.models.SphereModel;

import java.util.List;

public class AlertDataStructureJson {
    public String event;
    public String model;
    public int model_id;
    public String model_name;
    public int circle_id;
    public String sphere_id;
    public String sphere_name;
    public int who;
    public String who_name;
    public Object value;
}