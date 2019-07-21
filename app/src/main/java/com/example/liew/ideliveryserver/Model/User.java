package com.example.liew.ideliveryserver.Model;

import com.google.firebase.database.DatabaseReference;

public class User {

    private String name;
    private String password;
    private String phone;
    private String isstaff;
    private String isadmin;


    public User(){

    }

    public User(String Pname, String Ppassword){
        name = Pname;
        password = Ppassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsstaff() {
        return isstaff;
    }

    public void setIsstaff(String isstaff) {
        this.isstaff = isstaff;
    }

    public String getIsadmin() {
        return isadmin;
    }

    public void setIsadmin(String isadmin) {
        this.isadmin = isadmin;
    }
}
