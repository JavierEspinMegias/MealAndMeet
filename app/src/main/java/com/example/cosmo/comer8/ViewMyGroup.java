package com.example.cosmo.comer8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewMyGroup extends AppCompatActivity {
    public DatabaseReference firebaseReference;
    public DatabaseReference newRef;

    public FirebaseStorage storage;
    public StorageReference storageRef;


    public AdapterUsers userAdapter;
    public RecyclerView rvUsers;
    public ArrayList<User> usersGroup = new ArrayList<User>();
    public ArrayList<String> idUsers = new ArrayList<>();

    public SharedPreferences saved;
    public TextView price, menu, info;
    public ImageView photoGroup;
    private String groupId, userId, userAdminGroup;
    private Button exitOrJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_group);

        price = (TextView)findViewById(R.id.viewPrice);
        menu = (TextView)findViewById(R.id.viewMenu);
        info = (TextView)findViewById(R.id.infoGroup);
        exitOrJoin = (Button)findViewById(R.id.exitOrJoin);
        photoGroup = (ImageView)findViewById(R.id.imageView2);

        firebaseReference = FirebaseDatabase.getInstance().getReference();
        newRef = FirebaseDatabase.getInstance().getReference();
        rvUsers = (RecyclerView)findViewById(R.id.rvUsers);
        saved = getSharedPreferences("data", Context.MODE_PRIVATE);

        groupId = getIntent().getStringExtra("groupKey");

        userId = saved.getString("userId","");


        firebaseReference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users:dataSnapshot.getChildren()){
                    MealGroup userAdmin = users.getValue(MealGroup.class);
                    userAdminGroup = userAdmin.getidUserMasterGroup();
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setImageGroup();

        firebaseReference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot pojoUser:dataSnapshot.getChildren()){
                    MealGroup nGroup = (MealGroup) pojoUser.getValue(MealGroup.class);
                    if (nGroup.isActive()){
                        idUsers.add(pojoUser.getKey());
                        price.setText(nGroup.getPrice());
                        menu.setText(nGroup.getTitle());
                        info.setText(nGroup.getInfoGroup());
                    }
                    

                    firebaseReference.child("users").child(pojoUser.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            User nUser = (User)dataSnapshot.getValue(User.class);
                            if(nUser.isActive()){
                                usersGroup.add(nUser);
                                userAdapter.notifyItemInserted(usersGroup.indexOf(nUser));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


                // CHECK IF IT IS NOT MY GROUP
                if (!idUsers.contains(userId) && idUsers.size() < 7){

                    exitOrJoin.setText(R.string.join);
                    exitOrJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //CHECK USER HAS NOT ALREADY APPLIED TO THIS GROUP
                            firebaseReference.child("notifications").child("groups").child(userId).child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean hasApplied = false;
                                    for (DataSnapshot pojoNoti:dataSnapshot.getChildren()){
                                        NotificationGroup checkNoti = (NotificationGroup)pojoNoti.getValue(NotificationGroup.class);
                                        if (checkNoti.isActive() && checkNoti.getType().equals("apply")){
                                            hasApplied = true;
                                            break;
                                        }
                                    }
                                    if (!hasApplied){
                                        // SET ADMIN NOTIFICATION
                                        NotificationGroup notifyAdmin = new NotificationGroup("",userId,groupId,getResources().getString(R.string.notifyNewRequest),"request",Calendar.getInstance().getTime().toString(),false,true);
                                        newRef = firebaseReference.push();
                                        notifyAdmin.setNotificationId(newRef.getKey());
                                        firebaseReference.child("notifications").child("groups").child(userAdminGroup).child(groupId).child(notifyAdmin.getNotificationId()).setValue(notifyAdmin);

                                        // SET USER NOTIFICATION
                                        NotificationGroup notifyUser = new NotificationGroup("",userId,groupId,getResources().getString(R.string.waiting_response),"apply",Calendar.getInstance().getTime().toString(),false,true);
                                        firebaseReference.child("notifications").child("groups").child(userId).child(groupId).child(notifyAdmin.getNotificationId()).setValue(notifyUser);
                                        Toast.makeText(ViewMyGroup.this, R.string.ullBeNotified, Toast.LENGTH_LONG).show();

                                    }else{
                                        Toast.makeText(ViewMyGroup.this, R.string.youHaveApplied, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }else if(!idUsers.contains(userId) && idUsers.size() > 6){
                    Toast.makeText(ViewMyGroup.this, R.string.group_full, Toast.LENGTH_SHORT).show();
                    exitOrJoin.setText(R.string.group_full);
                }else{
                    exitOrJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // CHECK IF IM THE ADMIN
                            if (userAdminGroup.equals(userId)){
                                firebaseReference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String newAdmin = "";
                                        for (DataSnapshot user:dataSnapshot.getChildren()){
                                            if (!user.getKey().equals(userId)){
                                                newAdmin = user.getKey();
                                            }
                                        }
                                        for (DataSnapshot editUser:dataSnapshot.getChildren()){
                                            MealGroup editNewUser = editUser.getValue(MealGroup.class);
                                            editNewUser.setidUserMasterGroup(newAdmin);
                                            firebaseReference.child("groups").child(groupId).child(userId).setValue(editNewUser);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                firebaseReference.child("groups").child(groupId).child(userId).child("active").setValue(false);

                                /////////////DELETE GROUP
//                                firebaseReference.child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        int activeUsers = 0;
//                                        for (DataSnapshot activerUsers:dataSnapshot.getChildren()){
//                                            MealGroup newUser = activerUsers.getValue(MealGroup.class);
//                                            if (newUser.isActive()){
//                                                activerUsers=1;
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
                            }else{
                                firebaseReference.child("groups").child(groupId).child(userId).child("active").setValue(false);
                            }
                            Toast.makeText(ViewMyGroup.this, R.string.youHaveLeftGroup, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userAdapter = new AdapterUsers(usersGroup, saved.getString("userId",""), false);
        rvUsers.setAdapter(userAdapter);
        rvUsers.setLayoutManager(new StaggeredGridLayoutManager(3, 1));
    }

    public void setImageGroup(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        storageRef.child(groupId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(photoGroup);
            }
        });
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
