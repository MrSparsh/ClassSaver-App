package com.example.comp.timetableapp;

/**
 * Created by Mayank on 07-10-2017.
 */

public class DataModel {

    String subject_name;
    String time;
    String venue_name;

    public DataModel(String subject_name, String time, String venue_name) {
        this.subject_name=subject_name;
        this.time=time;
        this.venue_name=venue_name;

    }

    public String getName() {
        return subject_name;
    }

    public String getType() {
        return time;
    }

    public String getVersion_number() {
        return venue_name;
    }

}
