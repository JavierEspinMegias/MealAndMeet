package com.example.cosmo.comer8;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {

    private String name, email, pass, surname, address, id, nick, phone;
    private int age;
    private double latitude, longitude, area;

    private boolean isActive;

    protected FirebaseDatabase database;
    protected DatabaseReference refe;


    public User(){
        this.name = "";
        this.email = "";
        this.pass = "";
        this.surname = "";
        this.address = "";
        this.age = 0;
        this.id = "";
        this.nick = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.phone = "";
        this.area = 0.0;
        this.isActive = true;
    }

    public User(String name, String email, String pass, String surname, String address, int age, String id, String nick, double latitude, double longitude, String phone, Double area, boolean isActive) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.surname = surname;
        this.address = address;
        this.age = age;
        this.id = id;
        this.nick = nick;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.area = area;
        this.isActive = true;
    }


    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {this.area = area; }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}