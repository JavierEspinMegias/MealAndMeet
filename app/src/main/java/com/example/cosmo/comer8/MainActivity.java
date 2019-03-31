package com.example.cosmo.comer8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    private void crearGrupos(){

    }

    public void irMapas(View v){
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void myProfile(View v){
        startActivity(new Intent(this, MyProfile.class));
    }
}
