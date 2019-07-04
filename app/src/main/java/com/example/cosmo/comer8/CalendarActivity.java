package com.example.cosmo.comer8;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabWidget;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.dialog.OnDaysSelectionListener;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.model.Month;
import com.applikeysolutions.cosmocalendar.selection.MultipleSelectionManager;
import com.applikeysolutions.cosmocalendar.selection.criteria.BaseCriteria;
import com.applikeysolutions.cosmocalendar.settings.appearance.AppearanceInterface;
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import static com.example.cosmo.comer8.R.color.accentTrans;
import static com.example.cosmo.comer8.R.color.colorAccent;
import static com.example.cosmo.comer8.R.color.lightGrey;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";
    private com.applikeysolutions.cosmocalendar.view.CalendarView mCalendarView;

    public FirebaseDatabase database;
    public DatabaseReference reference;
    private SharedPreferences saved;
    private String userId;
    public Set<Long> days = new TreeSet<>();
    public ArrayList<String> myGroups;

    public ArrayList<GroupDate> myDates;
    public ArrayList<GroupDate> searchDates;
    public AdapterSingleDate dateAdapter;
    public AdapterExplainedDate dateExplained;
    public RecyclerView rvDates;
    public RecyclerView rvExplained;
    public Button watch_selected;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);



        mCalendarView = (com.applikeysolutions.cosmocalendar.view.CalendarView) findViewById(R.id.calendarView);
        saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        userId = saved.getString("userId","");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        mCalendarView.setSelectionType(SelectionType.MULTIPLE);
        myGroups = new ArrayList<>();
        myDates = new ArrayList<>();
        searchDates = new ArrayList<>();
        watch_selected = (Button)findViewById(R.id.watch_selected);

        rvDates = (RecyclerView)findViewById(R.id.rvCalendarDates);
        rvExplained = (RecyclerView)findViewById(R.id.rvExplainedDates);
        days = new TreeSet<>();


        ////// CHECK MY GROUPS
        reference.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoGroup:dataSnapshot.getChildren()){
                    for (DataSnapshot pojoUser:pojoGroup.getChildren()){
                        if (pojoUser.getKey().equals(userId)){
                            myGroups.add(pojoGroup.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ////// SEARCH DATE CREATION ON MY GROUPS
        reference.child("dates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pojoUser :dataSnapshot.getChildren()){
                    for (DataSnapshot pojoGroup:pojoUser.getChildren()){
                        if (myGroups.contains(pojoGroup.getKey())){
                            for (DataSnapshot pojoDate:pojoGroup.getChildren()){
                                GroupDate singleDate = pojoDate.getValue(GroupDate.class);
                                myDates.add(singleDate);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    Date singleDayDate = sdf.parse(singleDate.getDayDate());
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(singleDayDate);
                                    days.add(calendar.getTimeInMillis());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }

                dateAdapter = new AdapterSingleDate(myDates, saved.getString("userId",""));
                rvDates.setAdapter(dateAdapter);
                rvDates.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//Define colors
                int textColor = R.color.buttonsMain;
                int selectedTextColor = R.color.colorAccent;
                int disabledTextColor = accentTrans;
                ConnectedDays connectedDays = new ConnectedDays(days, textColor, selectedTextColor, disabledTextColor);

//Connect days to calendar

                mCalendarView.setSelectedDayBackgroundColor(R.color.calendarColor);
                mCalendarView.setCurrentDayTextColor(colorAccent);
                mCalendarView.addConnectedDays(connectedDays);
                mCalendarView.setConnectedDayIconRes(R.mipmap.ic_calendar);
                mCalendarView.setWeekendDayTextColor(R.color.colorPrimaryDark);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        new CalendarDialog(this, new OnDaysSelectionListener() {
//            @Override
//            public void onDaysSelected(List<Day> selectedDays) {
//                Toast.makeText(CalendarActivity.this, "yeah", Toast.LENGTH_SHORT).show();
//            }
//        }).show();



        watch_selected.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                dateExplained.notifyItemRangeRemoved(0, searchDates.size());
                searchDates.clear();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                List<Calendar> selecDates = mCalendarView.getSelectedDates();
                ArrayList<String> selectedDaysParsed = new ArrayList<>();

                for (Calendar calaa:selecDates){
                  Date newParse = calaa.getTime();
                  String lastParse = sdf.format(newParse);
                  selectedDaysParsed.add(lastParse);
                }


                for (GroupDate singDate:myDates){
                    if (selectedDaysParsed.contains(singDate.getDayDate())){

//                        Toast.makeText(CalendarActivity.this, singDate.getDayDate(), Toast.LENGTH_SHORT).show();
                        searchDates.add(singDate);
                        dateExplained.notifyItemInserted(searchDates.size());
                    }
                }
                if (searchDates.size()==0){
                    Toast.makeText(CalendarActivity.this, R.string.nothing_to_show, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CalendarActivity.this, R.string.scroll_down_select, Toast.LENGTH_LONG).show();
                }
            }
        });

        dateExplained = new AdapterExplainedDate(searchDates, saved.getString("userId",""));
        rvExplained.setAdapter(dateExplained);
        rvExplained.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Toast.makeText(this, R.string.select_dates, Toast.LENGTH_LONG).show();

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
