package com.example.cosmo.comer8;

public class GroupDate {
    private String dateId, userId, groupId, info, day, menu, latitude, longitude, hour, price, dayDate;
    private boolean edited;

    public GroupDate(){
        this.dateId = "";
        this.userId = "";
        this.groupId = "";
        this.info = "";
        this.day = "";
        this.menu = "";
        this.latitude = "";
        this.longitude ="";
        this.hour ="";
        this.price = "";
        this.dayDate = "";
        this.edited = false;
    }

    public GroupDate(String dateId, String userId, String groupId, String info, String day, String menu, String latitude, String longitude, String hour, String price, String dayDate, boolean edited) {
        this.dateId = dateId;
        this.userId = userId;
        this.groupId = groupId;
        this.info = info;
        this.day = day;
        this.menu = menu;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hour = hour;
        this.price = price;
        this.dayDate = dayDate;
        this.edited = edited;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
