package com.example.cosmo.comer8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class Admin extends AppCompatActivity {
    public Button main;
    public SharedPreferences saved;

    public ArrayList<User> users = new ArrayList<>();

    public ArrayList<MealGroup> groups = new ArrayList<>();

    public ArrayList<CalendarGroup> calendars = new ArrayList<>();

    public ArrayList<GroupDate> dates = new ArrayList<>();


    ArrayList <String> usersId = new ArrayList<>();

    public AdapterAdmin adminAdapter;
    public RecyclerView rvAdmin;

    public FirebaseDatabase database;
    public DatabaseReference reference;
    public String userId= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        userId = saved.getString("userId","");
        main = (Button)findViewById(R.id.button1);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        users = new ArrayList<>();
        groups = new ArrayList<>();
        calendars = new ArrayList<>();
        dates = new ArrayList<>();
        rvAdmin = (RecyclerView)findViewById(R.id.rvAdmin);

        ConstraintLayout contLay = findViewById(R.id.bg_admin);
        AnimationDrawable animDraw = (AnimationDrawable)contLay.getBackground();
        animDraw.setEnterFadeDuration(2000);
        animDraw.setExitFadeDuration(4000);
        animDraw.start();


        Toast.makeText(this, "Click GROUPS or USERS to START", Toast.LENGTH_LONG).show();

    }

    public void logOut(View v){

        SharedPreferences.Editor editor = saved.edit();
        editor.putString("userEmail","");
        editor.putString("userId","");
        editor.putString("userPass","");
        editor.commit();

        stopService(new Intent(this, GroupServices.class));
        startActivity(new Intent(this, Start.class));
    }

    public void loadUsers(View v){
        users.clear();
        groups.clear();
        calendars.clear();
        dates.clear();
        usersId.clear();

        adminAdapter = new AdapterAdmin(users, groups,calendars, dates, usersId,"users");
        reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user:dataSnapshot.getChildren()){
                    String idUser = user.getKey();
                    usersId.add(user.getKey());
                    adminAdapter.notifyItemInserted(usersId.indexOf(idUser));
                    User newUser = user.getValue(User.class);
                    users.add(newUser);
                    adminAdapter.notifyItemInserted(users.indexOf(newUser));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adminAdapter = new AdapterAdmin(users, groups,calendars,dates, usersId,"users");
        rvAdmin.setAdapter(adminAdapter);
        rvAdmin.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void loadGroups(View v){
        users.clear();
        groups.clear();
        calendars.clear();
        dates.clear();
        usersId.clear();

        adminAdapter = new AdapterAdmin(users, groups,calendars,dates, usersId,"groups");
        reference.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroup:dataSnapshot.getChildren()){
                    for (DataSnapshot pojoUser:pojoGroup.getChildren()){
                        MealGroup userGroup = pojoUser.getValue(MealGroup.class);
                        String idUser = pojoUser.getKey();
                        usersId.add(idUser);
                        adminAdapter.notifyItemInserted(usersId.indexOf(idUser));
                        groups.add(userGroup);
                        adminAdapter.notifyItemInserted(groups.indexOf(userGroup));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adminAdapter = new AdapterAdmin(users, groups,calendars,dates,usersId,"groups");
        rvAdmin.setAdapter(adminAdapter);
        rvAdmin.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }

    public void loadCalendars(View v){
        users.clear();
        groups.clear();
        calendars.clear();
        dates.clear();
        usersId.clear();

        adminAdapter = new AdapterAdmin(users, groups,calendars,dates, usersId,"calendars");
        reference.child("calendars").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroup:dataSnapshot.getChildren()){
                    for (DataSnapshot pojoUser:pojoGroup.getChildren()){
                        for (DataSnapshot pojoCal:pojoUser.getChildren()){
                            String idUser = pojoUser.getKey();
                            usersId.add(pojoUser.getKey());
                            adminAdapter.notifyItemInserted(usersId.indexOf(idUser));
                            CalendarGroup userCal = pojoCal.getValue(CalendarGroup.class);
                            calendars.add(userCal);
                            adminAdapter.notifyItemInserted(calendars.indexOf(userCal));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adminAdapter = new AdapterAdmin(users, groups,calendars,dates, usersId,"calendars");
        rvAdmin.setAdapter(adminAdapter);
        rvAdmin.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void loadDates(View v){
        users.clear();
        groups.clear();
        calendars.clear();
        dates.clear();
        usersId.clear();

        adminAdapter = new AdapterAdmin(users, groups,calendars,dates, usersId,"dates");
        reference.child("dates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroup:dataSnapshot.getChildren()){
                    for (DataSnapshot pojoUser:pojoGroup.getChildren()){
                        for (DataSnapshot pojoDate:pojoUser.getChildren()){
                            String idUser = pojoUser.getKey();
                            usersId.add(pojoUser.getKey());
                            adminAdapter.notifyItemInserted(usersId.indexOf(idUser));
                            GroupDate groupDate = pojoDate.getValue(GroupDate.class);
                            dates.add(groupDate);
                            adminAdapter.notifyItemInserted(dates.indexOf(groupDate));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        adminAdapter = new AdapterAdmin(users, groups,calendars,dates, usersId,"dates");
        rvAdmin.setAdapter(adminAdapter);
        rvAdmin.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void loadGroupPhotos(View v){
        users.clear();
        groups.clear();
        calendars.clear();
        dates.clear();
        usersId.clear();

        adminAdapter = new AdapterAdmin(users, groups,calendars,dates, usersId,"groupPhotos");

        reference.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroup:dataSnapshot.getChildren()){
                    for (DataSnapshot pojoUser:pojoGroup.getChildren()){
                        MealGroup groupDate = pojoUser.getValue(MealGroup.class);
                        String idUser = pojoUser.getKey();

                        usersId.add(idUser);
                        adminAdapter.notifyItemInserted(usersId.indexOf(idUser));
                        groups.add(groupDate);
                        adminAdapter.notifyItemInserted(groups.indexOf(groupDate));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adminAdapter = new AdapterAdmin(users, groups, calendars, dates, usersId,"groupPhotos");
        rvAdmin.setAdapter(adminAdapter);
        rvAdmin.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void loadUserPhotos(View v){
        users.clear();
        groups.clear();
        calendars.clear();
        dates.clear();
        usersId.clear();

        adminAdapter = new AdapterAdmin(users, groups,calendars, dates, usersId,"userPhotos");
        reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user:dataSnapshot.getChildren()){
                    User newUser = user.getValue(User.class);
                    users.add(newUser);
                    adminAdapter.notifyItemInserted(users.indexOf(newUser));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adminAdapter = new AdapterAdmin(users, groups,calendars,dates, usersId,"userPhotos");
        rvAdmin.setAdapter(adminAdapter);
        rvAdmin.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

}
