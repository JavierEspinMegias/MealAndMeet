package com.example.cosmo.comer8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Dates extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button goCalendar;


    public DatabaseReference firebaseReference;

    /// GET INFO TO ADAPTER
    public AdapterMyDates dateAdapter;
    public RecyclerView rvDates;
    public ArrayList<CalendarGroup> myDates = new ArrayList<>();
    public SharedPreferences saved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates);

        goCalendar = (Button) findViewById(R.id.goCalendar);


        firebaseReference = FirebaseDatabase.getInstance().getReference();
        rvDates = (RecyclerView)findViewById(R.id.rvMyDates);
        saved = getSharedPreferences("data", Context.MODE_PRIVATE);

        goCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dates.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        myDates.clear();
        loadMyDates();
    }

    public void loadMyDates(){
        firebaseReference.child("calendars").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoUser:dataSnapshot.getChildren()){
                    for(DataSnapshot pojoGroup: pojoUser.getChildren()){
                        for(DataSnapshot pojoDate: pojoGroup.getChildren()) {
                            if (pojoUser.getKey().equals(saved.getString("userId", ""))) {
                                CalendarGroup nDate = (CalendarGroup) pojoDate.getValue(CalendarGroup.class);
                                myDates.add(nDate);
                                dateAdapter.notifyItemInserted(myDates.indexOf(nDate));

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dateAdapter = new AdapterMyDates(myDates, saved.getString("userId",""));
        rvDates.setAdapter(dateAdapter);
        rvDates.setLayoutManager(new LinearLayoutManager(this));

    }


    // MENUS
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.go_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return true;
    }
}
