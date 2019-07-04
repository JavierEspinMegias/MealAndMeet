package com.example.cosmo.comer8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyGroups extends AppCompatActivity {
    public DatabaseReference firebaseReference;

    /// GET INFO TO ADAPTER
    public AdapterMyGroups groupAdapter;
    public RecyclerView rvGroups;
    public ArrayList<MealGroup> myGroups = new ArrayList<>();
    public SharedPreferences saved;
    public Button goMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        firebaseReference = FirebaseDatabase.getInstance().getReference();
        rvGroups = (RecyclerView)findViewById(R.id.rvMyGroups);
        saved = getSharedPreferences("data", Context.MODE_PRIVATE);

        goMap = (Button)findViewById(R.id.buttonMap);


    }

    @Override
    protected void onResume() {
        super.onResume();
        myGroups.clear();
        loadMyGroups();
    }
    
    public void loadMyGroups(){
        firebaseReference.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroup:dataSnapshot.getChildren()){
                    for(DataSnapshot pojoUser: pojoGroup.getChildren()){
                        if (pojoUser.getKey().equals(saved.getString("userId",""))){
                            MealGroup nGroup = (MealGroup) pojoUser.getValue(MealGroup.class);
                            if (nGroup.isActive()){
                                myGroups.add(nGroup);
                                groupAdapter.notifyItemInserted(myGroups.indexOf(nGroup));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        groupAdapter = new AdapterMyGroups(myGroups, saved.getString("userId",""));
        rvGroups.setAdapter(groupAdapter);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        
    }



    public void createGroup(View view){startActivity(new Intent(this, CreateGroup.class));}
    public void goRequest(View view){startActivity(new Intent(this, UsersRequests.class));}


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
