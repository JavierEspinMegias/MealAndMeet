package com.example.cosmo.comer8;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double lat = 0.0;
    private double lng = 0.0;
    private User myUser;

    LocationManager locationManager;
    Location location;
    float minLocation;

    DatabaseReference refe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        refe = FirebaseDatabase.getInstance().getReference();
        minLocation = 999999;
        myUser = new User("","","","","",0,"","",0f,0f);


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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Load my position
        goMyPos();

        // Draw others
        loadUsers();

        // Load zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    public void loadUsers(){
        refe.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot pojo:dataSnapshot.getChildren()){
                    User newUser = pojo.getValue(User.class);
                    LatLng newUbi = new LatLng(newUser.getLatitud(), newUser.getLongitud());
                    mMap.addMarker(new MarkerOptions().position(newUbi).title(newUser.getNick()).icon(BitmapDescriptorFactory.fromResource(R.drawable.googleg_disabled_color_18))).setTag(newUser);
                    Location locUser = new Location("");
                    locUser.setLatitude(newUser.getLatitud());
                    locUser.setLongitude(newUser.getLongitud());

                    if (locUser.distanceTo(location) < minLocation){
                        minLocation = locUser.distanceTo(location);
                        autoZoom();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // SET CLICK MARKERS
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                User user = (User)marker.getTag();
                Toast.makeText(MapsActivity.this, user.getNick(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }

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


        LatLng myUbi = new LatLng(lat, lng);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myUbi));

        SharedPreferences saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        if (saved != null){
            refe.child("user").child(saved.getString("id","")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    myUser = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        mMap.addMarker(new MarkerOptions()
                .position(myUbi)
                .title(myUser.getNick())
                .snippet(myUser.getAddress())
        ).setTag(myUser);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(myUbi));
        mMap.setMyLocationEnabled(true);
    }

    public void autoZoom(){
        if (minLocation < 1000){
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
        }else if (minLocation >= 1000 && minLocation < 10000){
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
        }else{
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
        }
    }

//    public void loadRest(){
//        mMap.clear();
//        url = getUrl(latitude, longitude, restaurant);
//        transferData[0] = mMap;
//        transferData[1] = url;
//
//        getNearbyPlaces.execute(transferData);
//        Toast.makeText(this, "Searching for Nearby Restaurants...", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Showing Nearby Restaurants...", Toast.LENGTH_SHORT).show();
//    }

//    private String getUrl(double latitide, double longitude, String nearbyPlace){
//        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        googleURL.append("location=" + latitide + "," + longitude);
//        googleURL.append("&radius=" + ProximityRadius);
//        googleURL.append("&type=" + nearbyPlace);
//        googleURL.append("&sensor=true");
//        googleURL.append("&key=" + "AIzaSyDtIWXQDUA1ufc_Vff3qbz522DnZ26Nk9w");
//
//        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());
//
//        return googleURL.toString();
//    }
}
