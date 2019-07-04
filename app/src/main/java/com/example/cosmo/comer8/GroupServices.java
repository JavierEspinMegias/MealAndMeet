package com.example.cosmo.comer8;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupServices extends Service {
    public DatabaseReference firebaseReference, newRef;
    public String userId;
    public SharedPreferences data;
    NotificationManager mNotificationManager;
    Notification.Builder mBuilder;

    public static ArrayList<String> myGroups;



    public GroupServices() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseReference = FirebaseDatabase.getInstance().getReference();
        data = getSharedPreferences("data", Context.MODE_PRIVATE);
        userId = data.getString("userId","");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(getApplicationContext());
        myGroups = new ArrayList<String>();

        this.newRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // CHECK NEW GROUP REQUESTS
        firebaseReference.child("notifications").child("groups").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot group:dataSnapshot.getChildren()){
                    for (DataSnapshot notifi:group.getChildren()){
                        NotificationGroup newNot = new NotificationGroup();
                        newNot = notifi.getValue(NotificationGroup.class);
                        // CHECK IM NOT ADMIN
                        if (!newNot.isNotified() && newNot.getType().equals("apply")){
                            notificationManagement( getResources().getString(R.string.ullBeNotified), getResources().getString(R.string.waiting_response));
                            firebaseReference.child("notifications").child("groups").child(userId).child(group.getKey()).child(notifi.getKey()).child("notified").setValue(true);

                        }else if(!newNot.isNotified() && newNot.getType().equals("request") && newNot.isActive()){
                            notificationManagement(newNot.getMessage(), getResources().getString(R.string.clickToAccept));
                            firebaseReference.child("notifications").child("groups").child(userId).child(group.getKey()).child(notifi.getKey()).child("notified").setValue(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // CHECK USER ACCEPTED
        firebaseReference.child("notifications").child("groups").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroup:dataSnapshot.getChildren()){
                    for (DataSnapshot pojoNoti:pojoGroup.getChildren()){
                        NotificationGroup singleNoti = pojoNoti.getValue(NotificationGroup.class);
                        if (!singleNoti.isNotified() && singleNoti.isActive()){
                            if (singleNoti.getMessage().equals(R.string.userAccepted)){
                                notifyUserAccepted(getResources().getString(R.string.userAccepted), getResources().getString(R.string.clickToCheck));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // LOAD CALENDARS GROUPS
        firebaseReference.child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot group:dataSnapshot.getChildren()){
                    for (DataSnapshot user:group.getChildren()){
                        MealGroup userGroup = user.getValue(MealGroup.class);
                        if (user.getKey().equals(userId) && userGroup.isActive()){
                            myGroups.add(group.getKey());
                        }
                    }
                }


                firebaseReference.child("dates").child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot group : dataSnapshot.getChildren()) {
                            if (myGroups.contains(group.getKey())) {
                                boolean isCreated = false;
                                String format = "";
                                for (final DataSnapshot user : group.getChildren()) {
                                    GroupDate singleDate = user.getValue(GroupDate.class);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


                                    int calendarDate = checkDayWeek(singleDate.getDay());

                                    //CHECK WE HAVE DATE REGISTERED
                                    Calendar now = Calendar.getInstance();
                                    int weekday = now.get(Calendar.DAY_OF_WEEK);
                                    if (weekday != calendarDate) {
                                        // calculate how much to add
                                        // the int number is the difference between Saturday and our day
                                        int days = (Calendar.SATURDAY - weekday + calendarDate) % 7;
                                        now.add(Calendar.DAY_OF_YEAR, days);
                                    }
                                    // now is the date you want
                                    Date nextDate = now.getTime();
                                    format = sdf.format(nextDate).split(" ")[0];

                                    // CHECK WE HAVE NOT WRITEN THIS DATE
                                    if (format.equals(singleDate.getDayDate())){
                                        isCreated = true;
                                    }

                                }

                                // WE NEED CREATE DATE
                                if (isCreated == false){
                                    newRef = newRef.child("dates").child(userId).child(group.getKey()).push();
                                    final String idDate = newRef.getKey();
                                    final String finalFormat = format;
                                    firebaseReference.child("calendars").child(userId).child(group.getKey()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot cal:dataSnapshot.getChildren()){
                                                CalendarGroup calendarPref = cal.getValue(CalendarGroup.class);

                                                GroupDate newDate = new GroupDate(idDate,userId,calendarPref.getCalendarGroup(),calendarPref.getCalendarInfo(),calendarPref.getCalendarDay(),calendarPref.getCalendarMenu(),calendarPref.getCalendarLatitude(),calendarPref.getCalendarLongitude(),calendarPref.getCalendarHour(),calendarPref.getCalendarPrice(), finalFormat, false);

//                                              SAVE DATE
                                                firebaseReference.child("dates").child(userId).child(calendarPref.getCalendarGroup()).child(idDate).setValue(newDate);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // ONE DAY TO NOT EDITED DATE
        firebaseReference.child("dates").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroup:dataSnapshot.getChildren()){
                    for (DataSnapshot pojoDate:pojoGroup.getChildren()){
                        GroupDate singDateEdit = pojoDate.getValue(GroupDate.class);

                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date());
                        c.add(Calendar.DATE, 1);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String tomorrow = sdf.format(c.getTime());
                        if (singDateEdit.getDayDate().equals(tomorrow) && !singDateEdit.isEdited()){
                            notifyDateNotEdited(getResources().getString(R.string.date_no_edited),getResources().getString(R.string.clickToCheck), singDateEdit.getDateId());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public int checkDayWeek(String day){

        switch (day){
            case "MONDAY":
                return Calendar.MONDAY;
            case "TUESDAY":
                return Calendar.TUESDAY;
            case "SATURDAY":
                return Calendar.SATURDAY;
            case "FRIDAY":
                return Calendar.FRIDAY;
            case "THURSDAY":
                return Calendar.THURSDAY;
            case "WEDNESDAY":
                return Calendar.WEDNESDAY;
            case "SUNDAY":
                return Calendar.SUNDAY;
        }
        return -1;
    }

    public void notificationManagement(String title, String text){

        mBuilder.setSmallIcon(R.mipmap.ic_info);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_group_map);
        mBuilder.setLargeIcon(bmp);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setVibrate(new long[] {1000, 1000, 1000, 1000});

        //Abrimos una activity al pulsar sobra la notificación
        //Creamos Intent
        Intent notIntent = new Intent(getApplicationContext(), UsersRequests.class);
        //Creamos PendingIntent al cual le pasamos nuestro Intent
        PendingIntent contIntent = PendingIntent.getActivity(getApplicationContext(), 0,notIntent,0);
        //Añadimos nuestra PendingIntent a la notificación
        mBuilder.setContentIntent(contIntent);
        //Lanzamos la notificación
        mNotificationManager.notify(01, mBuilder.build());
        //Más metodos: https://developer.android.com/reference/android/app/Notification.Builder

    }

    public void notifyUserAccepted(String title, String text){

        mBuilder.setSmallIcon(R.mipmap.ic_info);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_accept);
        mBuilder.setLargeIcon(bmp);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setVibrate(new long[] {1000, 1000, 1000, 1000});

        //Abrimos una activity al pulsar sobra la notificación
        //Creamos Intent
        Intent notIntent = new Intent(getApplicationContext(), CreateMealCal.class);
        //Creamos PendingIntent al cual le pasamos nuestro Intent
        PendingIntent contIntent = PendingIntent.getActivity(getApplicationContext(), 0,notIntent,0);
        //Añadimos nuestra PendingIntent a la notificación
        mBuilder.setContentIntent(contIntent);
        //Lanzamos la notificación
        mNotificationManager.notify(01, mBuilder.build());
        //Más metodos: https://developer.android.com/reference/android/app/Notification.Builder
    }

    public void notifyDateNotEdited(String title, String text, String dateKey){

        mBuilder.setSmallIcon(R.mipmap.ic_info);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_calendar);
        mBuilder.setLargeIcon(bmp);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setVibrate(new long[] {1000, 1000, 1000, 1000});

        //Abrimos una activity al pulsar sobra la notificación
        //Creamos Intent
        Intent notIntent = new Intent(getApplicationContext(), ViewSingleDateActivity.class);
        notIntent.putExtra("dateKey",dateKey);
        //Creamos PendingIntent al cual le pasamos nuestro Intent
        PendingIntent contIntent = PendingIntent.getActivity(getApplicationContext(), 0,notIntent,0);
        //Añadimos nuestra PendingIntent a la notificación
        mBuilder.setContentIntent(contIntent);
        //Lanzamos la notificación
        mNotificationManager.notify(01, mBuilder.build());
        //Más metodos: https://developer.android.com/reference/android/app/Notification.Builder
    }

}