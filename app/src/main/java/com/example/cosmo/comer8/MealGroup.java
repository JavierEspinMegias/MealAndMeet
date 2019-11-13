package com.example.cosmo.comer8;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

public class MealGroup{

    private String idGroup, userMaster, infoGroup, latitude, longitude, radiusMap, price, idCalendar, title, snippet;
    private boolean isActive;


    public MealGroup(){
        this.idGroup = "";
        this.userMaster = "";
        this.latitude = "";
        this.longitude = "";
        this.radiusMap = "";
        this.price = "";
        this.idCalendar = "";
        this.infoGroup = "";
        this.isActive = true;
        this.title = "";
        this.snippet = "";
    }


    public MealGroup(String idGroup, String latitude, String longitude, String radiusMap){
        this.idGroup = idGroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusMap = radiusMap;
    }


    public MealGroup(String idGroup, String userMaster, String infoGroup, String latitude, String longitude, String radiusMap, String price, String idCalendar, boolean isActive, String pTitle, String pSnippet) {
        this.idGroup = idGroup;
        this.userMaster = userMaster;
        this.idCalendar = idCalendar;
        this.infoGroup = infoGroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusMap = radiusMap;
        this.price = price;
        this.isActive = isActive;
        this.title = pTitle;
        this.snippet = pSnippet;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getUserMaster() {
        return userMaster;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String gerUserMaster() {
        return userMaster;
    }

    public void setUserMaster(String userMaster) {
        this.userMaster = userMaster;
    }

    public String getInfoGroup() {
        return infoGroup;
    }

    public void setInfoGroup(String infoGroup) {
        this.infoGroup = infoGroup;
    }

    public String getidCalendar() {
        return idCalendar;
    }

    public void setidCalendar(String idCalendar) {
        this.idCalendar = idCalendar;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getRadiusMap() {
        return radiusMap;
    }

    public void setRadiusMap(String radiusMap) {
        this.radiusMap = radiusMap;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getidUserMasterGroup() {
        return userMaster;
    }

    public void setidUserMasterGroup(String idUserMasterGroup) {
        this.userMaster = idUserMasterGroup;
    }

}
