package com.example.cosmo.comer8;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Start extends AppCompatActivity {

    LinearLayout loginBox;
    FloatingActionButton go;
    Button start;
    EditText email, password;
    LinearLayout progressBar;


    protected FirebaseDatabase database;
    protected DatabaseReference firebaseReference;

    protected String[] userData;
    protected Intent goMain;
    private boolean userExists;
    public SharedPreferences saved;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        loginBox = (LinearLayout)findViewById(R.id.loginBox);
        loginBox.setVisibility(View.INVISIBLE);

        start = (Button)findViewById(R.id.start);
        go = (FloatingActionButton)findViewById(R.id.go);
        go.setVisibility(View.INVISIBLE);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        userData = new String[]{"","",""};

        progressBar = (LinearLayout)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        goMain = new Intent(this, MainActivity.class);
        saved = getSharedPreferences("data", Context.MODE_PRIVATE);

        if(saved != null) {
            start.setVisibility(View.INVISIBLE);
            email.setText(saved.getString("userEmail", ""));
            password.setText(saved.getString("userPass", ""));
        }


        onLogin(go.getRootView());

        userExists = false;

        //// Permisos de usuario

        if (ContextCompat.checkSelfPermission(Start.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Start.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Start.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            
            Toast.makeText(this, R.string.needPermissions, Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLogin(View v){
        //// SHARED PREFERENCES ////
        if(saved != null){
            start.setVisibility(View.INVISIBLE);
            email.setText(saved.getString("userEmail",""));
            password.setText(saved.getString("userPass",""));
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

                boolean isAdmin = false;
                User pojo = new User("","","","","",0,"","",0f,0f,"",300.0, true);

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    pojo = child.getValue(User.class);
                    if (email.getText().toString().equals(pojo.getEmail()) && password.getText().toString().equals(pojo.getPass()) && pojo.isActive()) {
                        saveShared("userId",pojo.getId());
                        saveShared("userEmail",pojo.getEmail());
                        saveShared("userPass",pojo.getPass());
                        userExists = true;
                        if (email.getText().toString().equals("Admin")){
                            isAdmin=true;
                        }
                    }
                }
                if (userExists){
                    go.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Start.this, getString(R.string.welcome), Toast.LENGTH_SHORT).show();
                    if (isAdmin){
                        startActivity(new Intent(getApplicationContext(), Admin.class));
                    }else{
                        startActivity(goMain);

                    }
                }else{
                    loginBox.setVisibility(View.VISIBLE);
                    go.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Start.this, R.string.invalid_parameters, Toast.LENGTH_SHORT).show();
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
        SharedPreferences userPrefs = getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

}
