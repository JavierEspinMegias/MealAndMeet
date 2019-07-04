package com.example.cosmo.comer8;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double lat = 0.0;
    private double lng = 0.0;
    public EditText getArea;
    public TextView actualAddress;
    public SeekBar radiousBar;

    public LocationManager locationManager;
    public Location location;

    protected FirebaseDatabase database;
    protected DatabaseReference refe;
    protected FirebaseStorage storage;
    protected StorageReference storageRef;

    public ImageView img;


    public CircleOptions circleOptions;
    public User myUser;
    public Location userPreferredLocation;
    public LatLng clickedLocation;
    public String groupLat;
    public String groupLong;

    public MealGroup[] groups;

    public SharedPreferences saved;

    /// GET INFO TO ADAPTER
    public AdapterMyGroups groupAdapter;
    public RecyclerView rvGroups;
    public ArrayList<MealGroup> myGroups = new ArrayList<>();
    private static final String TAG = MapsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // -  DATABASE - USER - LOCATION  -
        database = FirebaseDatabase.getInstance();
        refe = database.getReference();
        saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        userPreferredLocation = new Location("");
        myUser = new User();
        groups = new MealGroup[]{};

        circleOptions = new CircleOptions();
        getArea = (EditText) findViewById(R.id.getArea);
        actualAddress = (TextView) findViewById(R.id.actualAddress);
        radiousBar = (SeekBar) findViewById(R.id.radiousBar);

        radiousBar.setMax(5000);
        getArea.setText("300");
        radiousBar.setProgress(300);


        img = new ImageView(getApplicationContext());
        img.setImageResource(R.drawable.common_google_signin_btn_icon_dark_normal);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        // SET UP GOOGLE MAP BUTTONS
        mMap = googleMap;



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Load controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

//        Intent intentData = new Intent();
//        groupLat = getIntent().getStringExtra("groupLat");
//        groupLong = getIntent().getStringExtra("groupLong");


        //LOAD USER
        refe.child("users").child(saved.getString("userId", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myUser = (User) dataSnapshot.getValue(User.class);
                getArea.setText(myUser.getArea()+"");
                radiousBar.setProgress(500);
                clickedLocation = new LatLng(myUser.getLatitude(),myUser.getLongitude());

                // Load my starting position
                goMyStartingPos(groupLat,groupLong);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // AUTO GETAREA INCREASE ON RADIOUS BAR MOVEs
        radiousBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getArea.setText(progress + "");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMap.clear();


                circleOptions.radius(seekBar.getProgress());
                mMap.addCircle(circleOptions);
                circleOptions.radius(Double.parseDouble(getArea.getText().toString())).strokeColor(Color.BLACK).fillColor(getColor(R.color.bgLists)).strokeWidth(2);

                mMap.addCircle(circleOptions);

                // Generate MARKER
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_antena)).position(clickedLocation)).setTag("clicked");

                loadGroupsArea(clickedLocation.latitude+"",clickedLocation.longitude+"");
            }
        });


        // SET THE CLICK LISTENERS
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onMapClick(LatLng point) {
                // Write your address marks
                List<Address> data = null;
                try {
                    data = new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(point.latitude, point.longitude, 1);
                    actualAddress.setText(data.get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // REMOVE MAP MARKERS
                mMap.clear();

                // SET CLICKED LOCATION
                clickedLocation = point;

                // Generate MARKER
                mMap.addMarker(new MarkerOptions() .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_antena)).position(point)).setTag("clicked");

                // Adding the circle to the GoogleMap
                circleOptions.center(point).radius(Double.parseDouble(getArea.getText().toString())).strokeColor(Color.BLACK).fillColor(getColor(R.color.bgLists)).strokeWidth(2);
                mMap.addCircle(circleOptions);


                // LOAD GROUPS INTO AREA
                loadGroupsArea(point.latitude+"", point.longitude+"");

            }
        });


        try {
            // Draw groups
            loadGroupsArea(myUser.getLatitude()+"", myUser.getLatitude()+"");
            
        }catch (Exception e){
            Toast.makeText(this, "Yeah! LOCATION ERROR!", Toast.LENGTH_SHORT).show();
        }

    }




    /////////       LOAD GROUP AREA       ///////////
    public void loadGroupsArea(final String clickLatitude, final String clickLongitude) {
        refe.child("groups").addValueEventListener(new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot pojoGroup : dataSnapshot.getChildren()) {
                for (DataSnapshot pojoUser : pojoGroup.getChildren()) {


                    final MealGroup newGroup = pojoUser.getValue(MealGroup.class);
                    if (pojoUser.getKey().equals(newGroup.getidUserMasterGroup())){

                        if (newGroup.isActive()){
                            // CLICK PARAMETERS
                            LatLng clickUbi = new LatLng(Double.parseDouble(clickLatitude),Double.parseDouble(clickLongitude));
//                    Toast.makeText(MapsActivity.this, clickUbi.latitude+"latlong   -   CLICK"+clickUbi.longitude, Toast.LENGTH_SHORT).show();

                            Location thisLoc = new Location("");
                            thisLoc.setLatitude(clickUbi.latitude);
                            thisLoc.setLongitude(clickUbi.longitude);
//                    Toast.makeText(MapsActivity.this, thisLoc.getLatitude()+"location   -   CLICK"+thisLoc.getLongitude(), Toast.LENGTH_SHORT).show();


                            // GROUPS PARAMETERS
                            LatLng newUbi = new LatLng(Double.parseDouble(newGroup.getLatitude()), Double.parseDouble(newGroup.getLongitude()));
//                    Toast.makeText(MapsActivity.this, newGroup.getLatitude()+"latlong   -   GROUP"+newGroup.getLongitude(), Toast.LENGTH_SHORT).show();

                            Location locGroup = new Location("");
                            locGroup.setLatitude(newUbi.latitude);
                            locGroup.setLongitude(newUbi.longitude);
//                    Toast.makeText(MapsActivity.this, locGroup.getLatitude()+"location   -   GROUP"+locGroup.getLongitude(), Toast.LENGTH_SHORT).show();




                            // COMPARE DISTANCES AND DRAW GROUPS
                            float distPointToGroup = thisLoc.distanceTo(locGroup);

                            if (Double.parseDouble(getArea.getText().toString()) + Double.parseDouble(newGroup.getRadiusMap()) > distPointToGroup || distPointToGroup < 10) {
                                // ADD TARGET MARKER
                                MarkerOptions mark = new MarkerOptions();

                                if (newGroup.getInfoGroup().split(" ").length>3){
                                    final String[] newInfo = newGroup.getInfoGroup().split(" ");

                                    mark
                                            .position(new LatLng(Double.parseDouble(newGroup.getLatitude()), Double.parseDouble(newGroup.getLongitude())))
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_group_map))
                                            .snippet("Info: "+newInfo[0]+" "+newInfo[1]+" "+newInfo[2]+"\n"+"Price: "+newGroup.getPrice()+" euros"+"\n"+"People: "+pojoGroup.getChildrenCount()+" persons");
                                }else{
                                    mark
                                            .position(new LatLng(Double.parseDouble(newGroup.getLatitude()), Double.parseDouble(newGroup.getLongitude())))
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_group_map))
                                            .snippet("Info: "+newGroup.getInfoGroup()+"\n"+"Price: "+newGroup.getPrice()+" euros"+"\n"+"People: "+pojoGroup.getChildrenCount()+" persons");

                                }


                                final Marker mark1 = mMap.addMarker(mark);

                                mark1.setTag(newGroup.getIdGroup());
                                mark1.setTitle(newGroup.getTitle());
                                mark1.showInfoWindow();


                                // WINDOW CLICK LISTENER
                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        Intent goGroup =  new Intent(getApplicationContext(), ViewMyGroup.class);
                                        goGroup.putExtra("groupKey",marker.getTag().toString());
                                        startActivity(goGroup);
                                    }
                                });



                                // WINDOW ADAPTER
                                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                    @Override
                                    public View getInfoWindow(Marker arg0) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(Marker marker) {

                                        final LinearLayout info = new LinearLayout(getApplicationContext());
                                        info.setOrientation(LinearLayout.VERTICAL);

                                        TextView title = new TextView(getApplicationContext());
                                        title.setTextColor(Color.BLACK);
                                        title.setGravity(Gravity.CENTER);
                                        title.setTextSize(16);
                                        title.setTypeface(null, Typeface.BOLD);
                                        title.setText(marker.getTitle());

                                        TextView snippet = new TextView(getApplicationContext());
                                        snippet.setTextColor(Color.GRAY);
                                        snippet.setText(marker.getSnippet());

                                        info.addView(title);
                                        info.addView(snippet);
                                        return info;
                                    }
                                });

                                CircleOptions circleOptions = new CircleOptions();
                                circleOptions.center(newUbi);
                                circleOptions.radius(Double.parseDouble(newGroup.getRadiusMap()));
                                circleOptions.strokeColor(Color.RED);
                                circleOptions.fillColor(Color.TRANSPARENT);
                                circleOptions.strokeWidth(3);
                                mMap.addCircle(circleOptions);
//                        setUpClusterer(clickLatitude, clickLongitude);

                            }

                        }

                    }
                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }});

    }


    /////////    TOOLS      ///////////
    public void goMyStartingPos(String lati, String longi) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantdistPointToGroup)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        if (lati == null){

//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lat = location.getLatitude();
            lng = location.getLongitude();


            LatLng myLocation = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

//            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person)).position(myLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
            mMap.setMyLocationEnabled(true);


            // Adding the circle to the GoogleMap
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                circleOptions.center(myLocation).radius(Double.parseDouble(myUser.getArea() + "")).strokeColor(Color.BLACK).fillColor(getColor(R.color.bgLists)).strokeWidth(5);
            }

            // Write your address marks
            List<Address> data = null;
            try {
                data = new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(myLocation.latitude, myLocation.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            actualAddress.setText(data.get(0).getAddressLine(0));

            mMap.addCircle(circleOptions);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));

        }else{

            LatLng myLocation = new LatLng(Double.parseDouble(lati), Double.parseDouble(longi));
//            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person)).position(myLocation));


            // Adding the circle to the GoogleMap
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                circleOptions.center(myLocation).radius(Double.parseDouble(myUser.getArea() + "")).strokeColor(Color.BLACK).fillColor(getColor(R.color.bgLists)).strokeWidth(5);
            }

            // Write address marks
            List<Address> data = null;
            try {
                data = new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(myLocation.latitude, myLocation.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            actualAddress.setText(data.get(0).getAddressLine(0));


            mMap.setMyLocationEnabled(true);
            mMap.addCircle(circleOptions);
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));


            //    Restore shared to next group creation
            SharedPreferences.Editor editor = saved.edit();
            editor.putString("groupLat","");
            editor.putString("groupLong","");
            editor.commit();

        }
    }

    public void loadGroups() {
        refe.child("groups").child(saved.getString("userId", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojo : dataSnapshot.getChildren()) {
                    MealGroup nGroup = (MealGroup) pojo.getValue(MealGroup.class);
                    myGroups.add(nGroup);
                    groupAdapter.notifyItemInserted(myGroups.indexOf(nGroup));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        groupAdapter = new AdapterMyGroups(myGroups, saved.getString("userId", ""));
        rvGroups.setAdapter(groupAdapter);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));

    }

    public void getImageGroup(final ImageView img, String grouId){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        storageRef.child(grouId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(img);
            }
        });

    }

    public void loadUsers() {
        refe.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojo : dataSnapshot.getChildren()) {
                    User newUser = pojo.getValue(User.class);
                    if (!pojo.getKey().equals(saved.getString("userId", ""))) {
                        LatLng newUbi = new LatLng(newUser.getLatitude(), newUser.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(newUbi).title(newUser.getNick()).icon(BitmapDescriptorFactory.fromResource(R.drawable.googleg_disabled_color_18))).setTag(newUser);
                        Location locUser = new Location("");
                        locUser.setLatitude(newUser.getLatitude());
                        locUser.setLongitude(newUser.getLongitude());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}