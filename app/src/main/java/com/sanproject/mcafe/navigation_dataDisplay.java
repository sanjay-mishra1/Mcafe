package com.sanproject.mcafe;

/**
 * Created by sanjay on 14/04/2018.
 */

public class navigation_dataDisplay {
private String Name,Mobile_number,College_id;

    public navigation_dataDisplay(String name, String mobile_number, String college_id) {
        Name = name;
        Mobile_number = mobile_number;
        College_id = college_id;
    }

    public String getName() {
        return Name;
    }

    public String getMobile_number() {
        return Mobile_number;
    }

    public navigation_dataDisplay() {
    }

    public String getCollege_id() {
        return College_id;
    }
}
