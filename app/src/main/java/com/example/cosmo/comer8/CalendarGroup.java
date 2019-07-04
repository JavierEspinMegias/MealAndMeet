package com.example.cosmo.comer8;

public class CalendarGroup {
    private String calendarKey, calendarUser, calendarGroup, calendarInfo, calendarDay, calendarMenu, calendarLatitude, calendarLongitude, calendarHour, calendarPrice;

    public CalendarGroup(){
        this.calendarKey = "";
        this.calendarUser = "";
        this.calendarGroup = "";
        this.calendarInfo = "";
        this.calendarDay = "";
        this.calendarMenu = "";
        this.calendarHour = "";
        this.calendarLatitude = "";
        this.calendarLongitude = "";
        this.calendarPrice = "";
    }

    public CalendarGroup(String calendarKey, String calendarUser, String calendarGroup, String calendarInfo, String calendarDay, String calendarMenu, String calendarLatitude, String calendarLongitude, String calendarHour, String calendarPrice) {
        this.calendarKey = calendarKey;
        this.calendarUser = calendarUser;
        this.calendarGroup = calendarGroup;
        this.calendarInfo = calendarInfo;
        this.calendarDay = calendarDay;
        this.calendarMenu = calendarMenu;
        this.calendarLatitude = calendarLatitude;
        this.calendarLongitude = calendarLongitude;
        this.calendarHour = calendarHour;
        this.calendarPrice = calendarPrice;
    }

    public String getCalendarInfo() {
        return calendarInfo;
    }

    public void setCalendarInfo(String calendarInfo) {
        this.calendarInfo = calendarInfo;
    }

    public String getCalendarKey() {
        return calendarKey;
    }

    public void setCalendarKey(String calendarKey) {
        this.calendarKey = calendarKey;
    }

    public String getCalendarGroup() {
        return calendarGroup;
    }

    public void setCalendarGroup(String calendarGroup) {
        this.calendarGroup = calendarGroup;
    }

    public String getCalendarUser() {
        return calendarUser;
    }

    public void setCalendarUser(String calendarUser) {
        this.calendarUser = calendarUser;
    }

    public String getCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(String calendarDay) {
        this.calendarDay = calendarDay;
    }

    public String getCalendarMenu() {
        return calendarMenu;
    }

    public void setCalendarMenu(String calendarMenu) {
        this.calendarMenu = calendarMenu;
    }

    public String getCalendarLatitude() {
        return calendarLatitude;
    }

    public void setCalendarLatitude(String calendarLatitude) {
        this.calendarLatitude = calendarLatitude;
    }

    public String getCalendarLongitude() {
        return calendarLongitude;
    }

    public void setCalendarLongitude(String calendarLongitude) {
        this.calendarLongitude = calendarLongitude;
    }

    public String getCalendarHour() {
        return calendarHour;
    }

    public void setCalendarHour(String calendarHour) {
        this.calendarHour = calendarHour;
    }

    public String getCalendarPrice() {
        return calendarPrice;
    }

    public void setCalendarPrice(String calendarPrice) {
        this.calendarPrice = calendarPrice;
    }
}
