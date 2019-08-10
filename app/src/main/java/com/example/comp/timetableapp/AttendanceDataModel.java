package com.example.comp.timetableapp;

/**
 * Created by Mayank on 08-10-2017.
 */

public class AttendanceDataModel {

    String subject_name;
    String percentange;
    String total;
    String present;
    String absent;

    public AttendanceDataModel(String subject_name,String percentange, String total, String present,String absent) {
        this.subject_name=subject_name;
        this.percentange=percentange;
        this.total=total;
        this.present=present;
        this.absent=absent;

    }

    public String getSubject_name() {
        return subject_name;
    }

    public String getPercentange() {
        return percentange;
    }

    public String getTotal() {
        return total;
    }

    public String getPresent()
    {
        return present;
    }

    public String getAbsent(){
        return absent;
    }
}
