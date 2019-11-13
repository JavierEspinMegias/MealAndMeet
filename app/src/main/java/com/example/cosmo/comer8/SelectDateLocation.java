package com.example.cosmo.comer8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectDateLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double lat = 0.0;
    private double lng = 0.0;
    public CircleOptions circleOptions;
    public SharedPreferences saved;
    public String latiCal, longiCal, newLat, newLong;
    int areaDate;
    public LatLng groupLocation;
    public LocationManager locationManager;
    public Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        circleOptions = new CircleOptions();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            latiCal = bundle.getString("latitudeDate");
            longiCal = bundle.getString("longitudeDate");
            areaDate = Integer.parseInt(bundle.getString("areaDate"));
            lat = Double.parseDouble(latiCal);
            lng = Double.parseDouble(longiCal);
            groupLocation = new LatLng(lat, lng);

        }else{
            Toast.makeText(this, R.string.data_error, Toast.LENGTH_SHORT).show();
            finish();
        }
        // Load zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);


        LatLng groupPos = new LatLng(lat, lng);
//        mMap.addMarker(new MarkerOptions().position(groupPos).title(getResources().getString(R.string.group_position)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(groupPos));

        mMap.addMarker(new MarkerOptions().position(groupPos));
        // Adding the circle to the GoogleMap
        circleOptions
                .center(groupPos)
                .radius(areaDate)
                .strokeColor(Color.BLACK)
                .fillColor(getColor(R.color.bgLists))
                .strokeWidth(5);

        mMap.addCircle(circleOptions);


        mMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onMapClick(LatLng point) {

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
                newLat = point.latitude+"";
                newLong = point.longitude+"";


                mMap.addMarker(new MarkerOptions().position(groupLocation));
                // Adding the circle to the GoogleMap
                circleOptions
                        .center(groupLocation)
                        .radius(areaDate)
                        .strokeColor(Color.BLACK)
                        .fillColor(getColor(R.color.bgLists))
                        .strokeWidth(5);

                mMap.addCircle(circleOptions);
            }
        });


    }


    public void selectArea(View v){
        SharedPreferences.Editor editor = saved.edit();
        editor.putString("newDateLatitude",newLat);
        editor.putString("newDateLongitude",newLong);
        editor.commit();
        finish();
    }
}
