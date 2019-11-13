package com.example.cosmo.comer8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersRequests extends AppCompatActivity {
    public DatabaseReference firebaseReference;
    public DatabaseReference newRef;


    public AdapterUsers userAdapter;
    public RecyclerView rvUsers;
    public ArrayList<User> allUsers = new ArrayList<User>();
    public ArrayList<String> idUsers = new ArrayList<>();

    public ArrayList<String> idGroups = new ArrayList<>();

    public SharedPreferences saved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_requests);


        firebaseReference = FirebaseDatabase.getInstance().getReference();
        newRef = FirebaseDatabase.getInstance().getReference();
        rvUsers = (RecyclerView)findViewById(R.id.rvUsers);
        saved = getSharedPreferences("data", Context.MODE_PRIVATE);

        firebaseReference.child("notifications").child("groups").child(saved.getString("userId","")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroups:dataSnapshot.getChildren()){
                    for (DataSnapshot pojoNoti:pojoGroups.getChildren()){
                        NotificationGroup not = (NotificationGroup)pojoNoti.getValue(NotificationGroup.class);

                        if (not.getType().equals("request") && not.isActive()){

                            // REQUESTS TO MY GROUPS
                            idUsers.add(not.getUserId());
                            idGroups.add(not.getGroupId());
                            userAdapter.notifyItemInserted(idGroups.indexOf(not));
                        }else{

                            // REQUEST TO OTHER's GROUP
                        }
                    }
                }
                for (String id: idUsers){
                    firebaseReference.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User userNew = (User) dataSnapshot.getValue(User.class);
                            allUsers.add(userNew);
                            userAdapter.notifyItemInserted(allUsers.indexOf(userNew));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        userAdapter = new AdapterUsers(allUsers, saved.getString("userId",""), true, idGroups);
        rvUsers.setAdapter(userAdapter);
//        rvUsers.setLayoutManager(new LinearLayoutManager(this));
//        rvUsers.setLayoutManager(new StaggeredGridLayoutManager(3, 1));
        rvUsers.setLayoutManager(new GridLayoutManager(this, 2));
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
