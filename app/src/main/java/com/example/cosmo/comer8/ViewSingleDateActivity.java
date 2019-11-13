package com.example.cosmo.comer8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PublicKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewSingleDateActivity extends AppCompatActivity {

    public FirebaseDatabase database;
    public DatabaseReference reference;
    private SharedPreferences saved;
    public SharedPreferences.Editor editor;
    private String userId, dateId;
    private EditText info, menu, dayDate, hour, price, weekDay;
    private GroupDate singleDate;
    public Button editDate, mapButton;
    public boolean haveLocation;
    public boolean myDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_date);

        saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        haveLocation = false;
        editor = saved.edit();

        saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        userId = saved.getString("userId","");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        dateId = getIntent().getStringExtra("dateKey");

        info = (EditText)findViewById(R.id.dateInfo);
        menu = (EditText)findViewById(R.id.menuData);
        dayDate = (EditText)findViewById(R.id.dateData);
        hour = (EditText)findViewById(R.id.dateHour);
        price = (EditText)findViewById(R.id.priceData);
        dayDate.setEnabled(false);


        editDate = (Button)findViewById(R.id.editDate);
        mapButton = (Button)findViewById(R.id.button_map);


        editor.putString("newDateLatitude","");
        editor.putString("newDateLongitude","");
        editor.commit();

        // SET DATA
        reference.child("dates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoUser:dataSnapshot.getChildren()){
                    for (DataSnapshot dataGroup:pojoUser.getChildren()){
                        for (final DataSnapshot dataDate:dataGroup.getChildren()){
                            if (dataDate.getKey().equals(dateId)){
                                singleDate = dataDate.getValue(GroupDate.class);
                                info.setText(singleDate.getInfo());
                                menu.setText(singleDate.getMenu());
                                dayDate.setText(singleDate.getDayDate());
                                hour.setText(singleDate.getHour());
                                price.setText(singleDate.getPrice());

                                if (!userId.equals(singleDate.getUserId())){
                                    editDate.setVisibility(View.GONE);
                                    myDate = false;
                                    info.setEnabled(false);
                                    menu.setEnabled(false);
                                    hour.setEnabled(false);
                                    price.setEnabled(false);
                                    System.out.println("not my date");
                                }else{
                                    myDate = true;


                                    /// USERS CANT EDIT ALREADY PAST DATES
                                    Date date = Calendar.getInstance().getTime();

                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    String today = formatter.format(date);

                                    if (today.compareTo(singleDate.getDayDate()) <= 0) {
                                        System.out.println("earlier");

                                    }else{
                                        editDate.setVisibility(View.GONE);
                                        info.setEnabled(false);
                                        menu.setEnabled(false);
                                        hour.setEnabled(false);
                                        price.setEnabled(false);
                                        Toast.makeText(ViewSingleDateActivity.this, R.string.you_edit_date, Toast.LENGTH_SHORT).show();

                                    }
                                }

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Click listener
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /// NOT IMPLEMENTED _ CHECK IF YOU WANT CHANGE THIS DATE FOR A PAST DATE
                    if (compareDates(singleDate.getDayDate(),dayDate.getText().toString())){


                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date dateSetted = sdf.parse(singleDate.getDayDate());
                        c.setTime(dateSetted);
                        final String dayOfWeek = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                        boolean edited = false;

                        if (!singleDate.getPrice().equals(price.getText().toString())){
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(singleDate.getDateId()).child("price").setValue(price.getText().toString());
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(singleDate.getDateId()).child("edited").setValue(true);
                            edited = true;
                        }
                        if (!singleDate.getInfo().equals(info.getText().toString())){
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(singleDate.getDateId()).child("info").setValue(info.getText().toString());
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(singleDate.getDateId()).child("edited").setValue(true);
                            edited = true;
                        }
                        if (!singleDate.getHour().equals(hour.getText().toString())){
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(singleDate.getDateId()).child("hour").setValue(hour.getText().toString());
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(singleDate.getDateId()).child("edited").setValue(true);
                            edited = true;
                        }
                        if (!singleDate.getMenu().equals(menu.getText().toString())){
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(singleDate.getDateId()).child("menu").setValue(menu.getText().toString());
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(singleDate.getDateId()).child("edited").setValue(true);
                            edited = true;
                        }
                        if(haveLocation){
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(dateId).child("latitude").setValue(saved.getString("newDateLatitude",singleDate.getLatitude()));
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(dateId).child("longitude").setValue(saved.getString("newDateLongitude",singleDate.getLongitude()));
                            reference.child("dates").child(userId).child(singleDate.getGroupId()).child(singleDate.getDateId()).child("edited").setValue(true);
                            edited = true;
                            saved = getSharedPreferences("data", Context.MODE_PRIVATE);
                            editor.putString("newDateLatitude","");
                            editor.putString("newDateLongitude","");
                            editor.commit();

                        }
                        if (edited){
                            Toast.makeText(ViewSingleDateActivity.this, R.string.date_edited, Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }else{
                        Toast.makeText(ViewSingleDateActivity.this, R.string.data_error, Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        // MAP EDIT
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myDate){
                    Intent i = new Intent(v.getContext(), SelectDateLocation.class);
                    i.putExtra("latitudeDate", singleDate.getLatitude());
                    i.putExtra("longitudeDate", singleDate.getLongitude());
                    i.putExtra("areaDate", "300");
                    startActivity(i);
                }else{
                    Intent i = new Intent(v.getContext(), ViewLocation.class);
                    i.putExtra("latitude", singleDate.getLatitude());
                    i.putExtra("longitude", singleDate.getLongitude());
                    i.putExtra("area", "300");
                    startActivity(i);
                }
            }
        });
    }


    public boolean compareDates(String dSetted, String dEdit) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dateSetted = sdf.parse(dSetted);
        Date dateEdit = sdf.parse(dEdit);
        if (dateEdit.compareTo(dateSetted) < 0 || dateSetted == null || dateEdit == null){
            Toast.makeText(this, R.string.incorrect_date, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public final static boolean isValidName(CharSequence name){
        String regx = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        if(saved.getString("newDateLatitude","") != null && !saved.getString("newDateLongitude","").equals("")){
            haveLocation = true;
        }else{
            Toast.makeText(this, R.string.go_select_position, Toast.LENGTH_SHORT).show();
        }
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
