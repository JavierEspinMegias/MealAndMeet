package com.example.cosmo.comer8;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double lat = 0.0;
    private double lng = 0.0;
    public EditText getArea;
    public TextView actualAddress;
    public SeekBar radiusBar;

    public LocationManager locationManager;
    public Location location;

    protected FirebaseDatabase database;
    protected DatabaseReference firebaseReference;
    public CircleOptions circleOptions;

    public SharedPreferences saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        circleOptions = new CircleOptions();
        database = FirebaseDatabase.getInstance();
        firebaseReference = database.getReference();

        getArea = (EditText)findViewById(R.id.getArea);
        actualAddress = (TextView) findViewById(R.id.actualAddress);

        saved = getSharedPreferences("data", Context.MODE_PRIVATE);

        radiusBar = (SeekBar)findViewById(R.id.radiusBar);
        radiusBar.setMax(5000);
        getArea.setText("300");
        radiusBar.setProgress(300);
        Toast.makeText(this, R.string.moveBarToIncreaseRadius, Toast.LENGTH_LONG).show();
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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getArea.setText(progress+"");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMap.clear();
                circleOptions.radius(seekBar.getProgress());
                mMap.addCircle(circleOptions);
                circleOptions
                        .radius(Double.parseDouble(getArea.getText().toString()))
                        .strokeColor(Color.BLACK)
                        .fillColor(getColor(R.color.bgLists))
                        .strokeWidth(5);

                mMap.addCircle(circleOptions);
            }
        });

        // Load my position
        goMyPos();

        // Load zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onMapClick(LatLng point) {

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(point));
            SharedPreferences.Editor editor = saved.edit();
            editor.putString("newGroupLatitude",point.latitude+"");
            editor.putString("newGroupLongitude",point.longitude+"");
            editor.commit();

            // Adding the circle to the GoogleMap
            circleOptions
                    .center(point)
                    .radius(Double.parseDouble(getArea.getText().toString()))
                    .strokeColor(Color.BLACK)
                    .fillColor(getColor(R.color.bgLists))
                    .strokeWidth(5);

            mMap.addCircle(circleOptions);
                try {
                    List<Address> data  = new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(point.latitude, point.longitude, 1);
                    actualAddress.setText(data.get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void goMyPos(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        lat = location.getLatitude();
        lng = location.getLongitude();


        LatLng myLocation = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(myLocation));
        // Adding the circle to the GoogleMap
        circleOptions
                .center(myLocation)
                .radius(Double.parseDouble(getArea.getText().toString()))
                .strokeColor(Color.BLACK)
                .fillColor(getColor(R.color.bgLists))
                .strokeWidth(5);


        List<Address> data  = null;
        try {
            data = new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(myLocation.latitude, myLocation.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        actualAddress.setText(data.get(0).getAddressLine(0));

        mMap.addCircle(circleOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }

    public void selectArea(View v){
        if (!getArea.getText().toString().equals("")){
            SharedPreferences.Editor editor = saved.edit();
            editor.putString("newGroupArea",getArea.getText().toString());
            editor.commit();
            Toast.makeText(this, R.string.area_selected, Toast.LENGTH_SHORT).show();

            finish();
        }else{
            Toast.makeText(this, R.string.select_area, Toast.LENGTH_SHORT).show();
        }
    }



}
