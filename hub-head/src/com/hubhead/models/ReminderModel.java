package com.hubhead.models;

public class ReminderModel {
    public int id;
    public int circle_id;
    public int sphere_id;
    public int task_id;
    public String task_name;
    public int user_id;
    public int user_role;
    public long start_time;
    public long deadline;
    public int type;
    public int task_status;

    public ReminderModel() {
    }

    public ReminderModel(int id, int circle_id, int sphere_id, String task_name, long start_time, long deadline) {
        this.id = id;
        this.circle_id = circle_id;
        this.sphere_id = sphere_id;
        this.task_name = task_name;
        this.start_time = start_time;
        this.deadline = deadline;
    }


    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.task_name + ":" + deadline + ":" + start_time;
    }


}
