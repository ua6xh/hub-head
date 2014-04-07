package com.hubhead.helpers;


import com.hubhead.models.CircleModel;
import com.hubhead.models.ContactModel;
import com.hubhead.models.ReminderModel;
import com.hubhead.models.SphereModel;

import java.util.List;

public class AllDataStructureJson {
    public String type;
    public Data data;

    public static class Data {
        public List<CircleModel> circles;
        public List<ContactModel> contacts;
        public List<SphereModel> spheres;
        public List<ReminderModel> reminders;
        public Long last_get_time;
    }
}