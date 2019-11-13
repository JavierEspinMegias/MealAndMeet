package com.example.cosmo.comer8;

public class NotificationGroup {
    private String notificationId, userId, groupId, message, type, notificationTime;
    private boolean isNotified, isActive;

    public NotificationGroup(){
        this.notificationId = "";
        this.userId = "";
        this.groupId = "";
        this.message = "";
        this.type = "";
        this.notificationTime = "";
        this.isNotified = false;
        this.isActive = true;
    }

    public NotificationGroup(String notificationId, String userId, String groupId, String message, String type, String notificationTime, boolean isNotified, boolean isActive) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.groupId = groupId;
        this.message = message;
        this.type = type;
        this.notificationTime = notificationTime;
        this.isNotified = isNotified;
        this.isActive = isActive;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
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

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }
}
