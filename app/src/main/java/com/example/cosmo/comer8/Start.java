package com.example.cosmo.comer8;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Start extends AppCompatActivity {

    LinearLayout loginBox;
    Button go, start;
    EditText email, password;
    LinearLayout progressBar;


    protected FirebaseDatabase database;
    protected DatabaseReference firebaseReference;

    protected String[] userData;
    protected Intent goMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        loginBox = (LinearLayout)findViewById(R.id.loginBox);
        loginBox.setVisibility(View.INVISIBLE);

        start = (Button)findViewById(R.id.start);
        go = (Button)findViewById(R.id.go);
        go.setVisibility(View.INVISIBLE);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        userData = new String[]{"","",""};

        progressBar = (LinearLayout)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        goMain = new Intent(this, MainActivity.class);
        onLogin(go.getRootView());


        //// Permisos de usuario

        if (ContextCompat.checkSelfPermission(Start.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Start.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Start.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
    }


    public void onLogin(View v){
        //// SHARED PREFERENCES ////
        SharedPreferences saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        if(saved != null){
            start.setVisibility(View.INVISIBLE);
            email.setText(saved.getString("email",""));
            password.setText(saved.getString("pass",""));
            try {
                onGo(v);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            v.setVisibility(v.INVISIBLE);
            loginBox.setVisibility(v.VISIBLE);
            go.setVisibility(View.VISIBLE);
        }
    }

    public void onGo(View v) throws InterruptedException {
        loginBox.setVisibility(View.INVISIBLE);
        go.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        database = FirebaseDatabase.getInstance();
        firebaseReference = database.getReference();
        
        firebaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User pojo = new User("","","","","",0,"","",0f,0f);

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    pojo = child.getValue(User.class);
                    if (email.getText().toString().equals(pojo.getEmail()) && password.getText().toString().equals(pojo.getPass())) {
                        userData[0] = child.getKey();
                        userData[1] = pojo.getEmail();
                        userData[2] = pojo.getPass();
                    }
                }

                if(!userData[0].equals("")){
                    goMain.putExtra("key", userData[0]);
                    saveShared("email", email.getText().toString());
                    saveShared("pass", password.getText().toString());
                    saveShared("id", pojo.getId());
                    startActivity(goMain);
                    go.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Start.this, getString(R.string.welcome), Toast.LENGTH_SHORT).show();

                }else{
                    loginBox.setVisibility(View.VISIBLE);
                    go.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Start.this, getString(R.string.invalid_parameters), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void onSignin(View v){
        startActivity(new Intent(this, Register.class));
    }

    public void saveShared(String key, String value){
        SharedPreferences idUsuario = getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor obj_editor = idUsuario.edit();
        obj_editor.putString(key,value);
        obj_editor.commit();
    }
}
