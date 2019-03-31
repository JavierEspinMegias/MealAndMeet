package com.example.cosmo.comer8;

public class User {

    private String name, email, pass, surname, address, id, nick;
    private int age;
    private double latitud, longitud;


    public User(){
        this.name = "";
        this.email = "";
        this.pass = "";
        this.surname = "";
        this.address = "";
        this.age = 0;
        this.id = "";
        this.nick = "";
        this.latitud = 0.0;
        this.longitud = 0.0;
    }

    public User(String name, String email, String pass, String surname, String address, int age, String id, String nick, double latitud, double longitud) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.surname = surname;
        this.address = address;
        this.age = age;
        this.id = id;
        this.nick = nick;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
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