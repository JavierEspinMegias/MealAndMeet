package com.example.cosmo.comer8;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    public FloatingActionButton main;
    public SharedPreferences saved;
    public ArrayList<GroupDate> myDates;
    public AdapterSingleDate dateAdapter;
    public RecyclerView rvDates;
    public ArrayList<String> myGroups = new ArrayList<>();
    public FirebaseDatabase database;
    public DatabaseReference reference;
    public String userId= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        userId = saved.getString("userId","");
        main = (FloatingActionButton) findViewById(R.id.button1);
        myGroups = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        myDates = new ArrayList<>();

        rvDates = (RecyclerView)findViewById(R.id.rvDatesMain);


        ConstraintLayout contLay = findViewById(R.id.bg_main);
        AnimationDrawable animDraw = (AnimationDrawable)contLay.getBackground();
        animDraw.setEnterFadeDuration(2000);
        animDraw.setExitFadeDuration(4000);
        animDraw.start();


        //GROUPS SERVICE
//        Intent ii = new Intent(getApplicationContext(), GroupServices.class);
//        PendingIntent pii = PendingIntent.getService(getApplicationContext(), 2222, ii,
//        PendingIntent.FLAG_CANCEL_CURRENT);
//        //getting current time and add 5 seconds to it
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.SECOND, 5);
//        //registering our pending intent with alarmmanager
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), pii);

        startService(new Intent(this, GroupServices.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
        myDates.clear();
        loadMyDates();
    }

    public void goMaps(View v){
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void myProfile(View v){
        startActivity(new Intent(this, MyProfile.class));
    }

    public void goMyGroups(View v){startActivity(new Intent(this, MyGroups.class));}

    public void goDates(View v){startActivity(new Intent(this, Dates.class));}

    public void logOut(View v){

        SharedPreferences.Editor editor = saved.edit();
        editor.putString("userEmail","");
        editor.putString("userId","");
        editor.putString("userPass","");
        editor.commit();

        stopService(new Intent(this, GroupServices.class));
        startActivity(new Intent(this, Start.class));
    }

    public void loadMyDates(){

        ////// CHECK MY GROUPS
        reference.child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroup:dataSnapshot.getChildren()){

                    for (DataSnapshot pojoUser:pojoGroup.getChildren()){
                        MealGroup userGroup = pojoUser.getValue(MealGroup.class);
                        if (pojoUser.getKey().equals(userId) && userGroup.isActive()){
                            myGroups.add(pojoGroup.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ////// SEARCH DATE CREATION ON MY GROUPS
        reference.child("dates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoUser :dataSnapshot.getChildren()){
                    for (DataSnapshot pojoGroup:pojoUser.getChildren()){
                        if (myGroups.contains(pojoGroup.getKey())){
                            for (DataSnapshot pojoDate:pojoGroup.getChildren()){

                                GroupDate singleDate = pojoDate.getValue(GroupDate.class);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date currentTime = Calendar.getInstance().getTime();

                                try {
                                    Date date = sdf.parse(singleDate.getDayDate());
                                    if (date.compareTo(currentTime) >= 0 || singleDate.getDayDate().equals(sdf.format(currentTime))){
                                        myDates.add(singleDate);
                                        dateAdapter.notifyItemInserted(myDates.size()-1);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        dateAdapter = new AdapterSingleDate(myDates, saved.getString("userId",""));
        rvDates.setAdapter(dateAdapter);
        rvDates.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}
