    package com.example.cosmo.comer8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

    public class CreateMealCal extends AppCompatActivity {


        public FirebaseDatabase database;
        public DatabaseReference reference;
        private DatabaseReference newRef;
        private SharedPreferences saved;
        public SharedPreferences.Editor editor;
        private String userId, userMasterId, calendarId, groupId;
        private EditText info, menu, dayDate, hour, price, weekDay;
        private MealGroup singleGroup;
        public Button setCalendar;
        private Spinner spinner;
        private ArrayList<String> weekDays;
        public ArrayAdapter<String>spinnerAdapter;
        public String[] availableDays;
        public String[] days;
        public boolean haveLocation;
        public CalendarGroup newGroupCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal_cal);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        saved = getSharedPreferences("data", Context.MODE_PRIVATE);


        userId = saved.getString("userId","");
        groupId = saved.getString("groupId","");
        userMasterId =  saved.getString("userMasterIdCal","");



        haveLocation = false;
        editor = saved.edit();

        spinner = (Spinner)findViewById(R.id.getDay);

        info = (EditText)findViewById(R.id.getInfo);
        price = (EditText)findViewById(R.id.getPrice);
        menu = (EditText)findViewById(R.id.getMeal);
        hour = (EditText)findViewById(R.id.getHour);
        weekDays = new ArrayList<String>();

        availableDays = new String[]{WeekDayNames.MONDAY.toString(),
                WeekDayNames.TUESDAY.toString(), WeekDayNames.WEDNESDAY.toString(),
                WeekDayNames.THURSDAY.toString(), WeekDayNames.FRIDAY.toString(),
                WeekDayNames.SATURDAY.toString(),  WeekDayNames.SUNDAY.toString()};



        reference.child("groups").child(groupId).child(userMasterId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                singleGroup = dataSnapshot.getValue(MealGroup.class);

                editor.putString("latitudeCal",singleGroup.getLatitude());
                editor.putString("longitudeCal",singleGroup.getLongitude());
                editor.putString("areaCal",singleGroup.getRadiusMap());
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        reference.child("calendars").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoUser:dataSnapshot.getChildren()){
                    for (DataSnapshot pojoGroup:pojoUser.getChildren()){
                        if (pojoGroup.getKey().equals(groupId)){
                            for (DataSnapshot pojoCal:pojoGroup.getChildren()){
                                CalendarGroup calDays = pojoCal.getValue(CalendarGroup.class);
                                weekDays.add(calDays.getCalendarDay());
                                for(int i = 0; i < availableDays.length; i++){
                                    String day = availableDays[i];
                                    if (weekDays.contains(day)){
                                        availableDays[i]="---";
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




        reference.child("calendars").child(userMasterId).child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoCal:dataSnapshot.getChildren()){
                    CalendarGroup newCalendar = pojoCal.getValue(CalendarGroup.class);
                    newGroupCalendar = newCalendar;
                    price.setText(newCalendar.getCalendarPrice());
                    info.setText(newCalendar.getCalendarInfo());
                    hour.setText(newCalendar.getCalendarHour());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        spinner.setPrompt("Days");
        spinnerAdapter = new ArrayAdapter<String>(CreateMealCal.this,android.R.layout.simple_spinner_item,availableDays);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


    }


        public void goSelectLocation(View v){
            Intent i = new Intent(this, SelectCalendarLocation.class);

            i.putExtra("latitudeCal", singleGroup.getLatitude());
            i.putExtra("longitudeCal", singleGroup.getLongitude());
            i.putExtra("areaCal", singleGroup.getRadiusMap());


        startActivity(i);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!saved.getString("newCalLatitude","").equals("")){
            haveLocation = true;
        }
    }

    public void sendCalendar(View v){

        String spinnerPosition = (String) spinner.getItemAtPosition(spinner.getSelectedItemPosition());

        if (haveLocation
        && !menu.getText().toString().equals("")
        && !hour.getText().toString().equals("")
        && !spinner.getSelectedItem().toString().equals("---")
        && !spinnerPosition.isEmpty()){

            newRef = database.getReference();
            newRef = newRef.child("calendars").child(userId).child(groupId).child("").push();
            final String idCalendar = newRef.getKey();

            newGroupCalendar.setCalendarDay(spinner.getSelectedItem().toString());
            newGroupCalendar.setCalendarHour(hour.getText().toString());
            newGroupCalendar.setCalendarLatitude(saved.getString("newCalLatitude",""));
            newGroupCalendar.setCalendarLongitude(saved.getString("newCalLongitude",""));
            newGroupCalendar.setCalendarUser(userId);
            newGroupCalendar.setCalendarMenu(menu.getText().toString());
            newGroupCalendar.setCalendarKey(idCalendar);

            reference.child("calendars").child(userId).child(groupId).child(idCalendar).setValue(newGroupCalendar);



            String format = "";
            Calendar now = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            int calendarDate = checkDayWeek(newGroupCalendar.getCalendarDay());
            int weekday = now.get(Calendar.DAY_OF_WEEK);
            if (weekday != calendarDate) {
                // calculate how much to add
                // the int number is the difference between Saturday and our day
                int days = (Calendar.SATURDAY - weekday + calendarDate) % 7;
                now.add(Calendar.DAY_OF_YEAR, days);
            }
            // now is the date you want
            Date nextDate = now.getTime();
            format = sdf.format(nextDate).split(" ")[0];

            final String idDate = newRef.getKey();
            newRef = newRef.child("dates").child(userId).child(groupId).child(idDate).push();

            GroupDate newDate = new GroupDate(idDate,userId,newGroupCalendar.getCalendarGroup(),newGroupCalendar.getCalendarInfo(),newGroupCalendar.getCalendarDay(),newGroupCalendar.getCalendarMenu(),newGroupCalendar.getCalendarLatitude(),newGroupCalendar.getCalendarLongitude(),newGroupCalendar.getCalendarHour(),newGroupCalendar.getCalendarPrice(), format, true);
///////////////  SAVE DATE
            reference.child("dates").child(userId).child(groupId).child(idDate).setValue(newDate);


            startActivity(new Intent(this, CalendarActivity.class));

        }else{
            Toast.makeText(this, R.string.need_all_fields, Toast.LENGTH_SHORT).show();
        }
    }

    public int checkDayWeek(String day){

            switch (day){
                case "MONDAY":
                    return Calendar.MONDAY;
                case "TUESDAY":
                    return Calendar.TUESDAY;
                case "SATURDAY":
                    return Calendar.SATURDAY;
                case "FRIDAY":
                    return Calendar.FRIDAY;
                case "THURSDAY":
                    return Calendar.THURSDAY;
                case "WEDNESDAY":
                    return Calendar.WEDNESDAY;
                case "SUNDAY":
                    return Calendar.SUNDAY;
            }
            return -1;
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
