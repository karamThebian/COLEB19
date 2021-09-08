package com.example.coleb19;

public class UserMap {

    public String Age,Blood_Type,Latitude,Longitude,Name,Phone,date;

    //default constructor
    public UserMap() {

    }

    //constructor


    public UserMap(String age, String blood_Type, String latitude, String longitude, String name, String phone, String date) {
        Age = age;
        Blood_Type = blood_Type;
        Latitude = latitude;
        Longitude = longitude;
        Name = name;
        Phone = phone;
        this.date = date;
    }
}
