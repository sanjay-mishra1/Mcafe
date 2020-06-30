package com.sanproject.mcafe;

/**
 * Created by sanjay on 26/03/2018.
 */

public class account_info {
    String Email,phone,collegeid,user;
    account_info(){
            }

    public account_info(String email, String phone, String collegeid, String user) {
        Email = email;
        this.phone = phone;
        this.collegeid = collegeid;
        this.user = user;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCollegeid() {
        return collegeid;
    }

    public void setCollegeid(String collegeid) {
        this.collegeid = collegeid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
