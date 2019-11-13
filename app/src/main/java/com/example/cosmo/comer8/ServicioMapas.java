package com.example.cosmo.comer8;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ServicioMapas extends Service {
    private DatabaseReference firebaseReference;
    private SharedPreferences data;
    private NotificationManager mNotificationManager;
    private Notification.Builder mBuilder;

    private long latitudMiPos;
    private long longitudMiPos;

    public String userId;

    public ServicioMapas(long pLat, long pLong) {
        super();
        this.longitudMiPos = pLong;
        this.latitudMiPos = pLat;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseReference = FirebaseDatabase.getInstance().getReference();
        data = getSharedPreferences("data", Context.MODE_PRIVATE);
        userId = data.getString("userId","");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void geoMomento(long pLat, long pLong){
        firebaseReference.child("geo").child(userId).child("miPosicionActual").child("latitud").setValue(pLat);
        firebaseReference.child("geo").child(userId).child("miPosicionActual").child("longitud").setValue(pLong);
    }
}
