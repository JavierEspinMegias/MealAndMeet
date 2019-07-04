package com.example.cosmo.comer8;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Los adaptadores heredan de RecyclerView.Adapter
public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.ViewHolder> {

    //Resto de variables de la clase
    private List<User> myUsers;
    private ArrayList<String> idGroups;
    public String idUser;
    private boolean isRequest;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private DatabaseReference newRef;
    public FirebaseStorage storage;
    public StorageReference storageRef;


    //En un adaptador es obligatorio definir una clase que herede de RecyclerView.ViewHolder
    //La clase ViewHolder hará referencia a los elementos de la vista creada para el recycler view
    public class ViewHolder extends RecyclerView.ViewHolder {
        //Su constructor debera enlazar las variables del controlador con la vista
        public TextView age, totalMeets, userName, titleTotalMeets;
        public DatabaseReference firebaseRef;
        private Button acceptUser, denyUser;
        public DatabaseReference firebaseReference;
        public DatabaseReference newRef;
        private ImageView userImage;

        public ViewHolder(final View itemView) {
            super(itemView);

            firebaseRef = FirebaseDatabase.getInstance().getReference();

            this.titleTotalMeets = itemView.findViewById(R.id.viewUserTotalMeets);
            this.age = itemView.findViewById(R.id.loadUserAge);
            this.totalMeets = itemView.findViewById(R.id.loadTotalMeets);
            this.userName = itemView.findViewById(R.id.loadName);

            this.acceptUser = itemView.findViewById(R.id.acceptUser);
            this.denyUser = itemView.findViewById(R.id.denyUser);
            this.firebaseReference = FirebaseDatabase.getInstance().getReference();
            this.newRef = FirebaseDatabase.getInstance().getReference();
            this.userImage = (ImageView)itemView.findViewById(R.id.userImage);

            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
        }
    }


    //El constructor deberá enlazar los datos del modelos con los del controlador
    public AdapterUsers(List<User> users, String idUser, boolean isRequest) {
        this.myUsers = users;
        this.idUser = idUser;
        this.isRequest = isRequest;
        reference = database.getReference();
    }

    //El constructor deberá enlazar los datos del modelos con los del controlador
    public AdapterUsers(List<User> users, String idUser, boolean isRequest, ArrayList<String> idGroups) {
        this.myUsers = users;
        this.idUser = idUser;
        this.isRequest = isRequest;
        this.reference = database.getReference();
        this.idGroups = idGroups;
    }


    //Debemos crear el método onCreateViewHolder que enlace la clase ViewHolder creada con la vista
    //Debe devolver un manejador de vistas enlazado a la vista que vayamos utilizar para cada item del adaptador
    @Override
    public AdapterUsers.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Especificamos el fichero XML que se utilizará como vista
        View contactView = inflater.inflate(R.layout.view_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    //Debemos sobrecargar onBindViewHolder que enlaza los datos del modelo con la vista
    @Override
    public void onBindViewHolder(final AdapterUsers.ViewHolder viewHolder, final int position) {

        final User pUser = this.myUsers.get(position);
//        final String pGroup = this.idGroups.get(position);

        viewHolder.userName.setText(pUser.getName());
        viewHolder.age.setText(pUser.getAge() + "");



        // LOAD USER IMAGE
        storageRef.child(pUser.getId()).getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                viewHolder.userImage.setImageResource(R.mipmap.ic_no_photo);
            }
        });
        storageRef.child(pUser.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(viewHolder.itemView.getContext()).load(uri).into(viewHolder.userImage);
            }});


        // CHECK ADAPTER IF WE NEED SEE ACCEPT and DENY BUTTONS
        if (!isRequest) {
            viewHolder.acceptUser.setVisibility(View.GONE);
            viewHolder.denyUser.setVisibility(View.GONE);


            // TOTAL MEETS
            viewHolder.firebaseRef.child("dates").child(pUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int cantDates = 0;
                    for (DataSnapshot pojoGroup : dataSnapshot.getChildren()) {
                        for (DataSnapshot pojoDate : pojoGroup.getChildren()){
                            cantDates += 1;
                        }
                    }
                    viewHolder.totalMeets.setText(cantDates + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else{
            // CHANGE TOTAL MEETS UTILITY -> NOW WE SHOW GROUP INFO
            viewHolder.titleTotalMeets.setText(R.string.group);
            viewHolder.firebaseRef.child("groups").child(idGroups.get(position)).child(idUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    MealGroup infoGroup = dataSnapshot.getValue(MealGroup.class);
                    viewHolder.totalMeets.setText(infoGroup.getTitle());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            viewHolder.acceptUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

///    YEAH we do the magic here ---  new CALENDAR ID   ///
                newRef = database.getReference();
                newRef = newRef.child("calendars").child(idUser).child(idGroups.get(position)).child("").push();
                final String idCalendar = newRef.getKey();

                //    Initialize CALENDAR
                reference.child("groups").child(idGroups.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//    Initialize GROUP
                        MealGroup groupCreated = new MealGroup();
                        groupCreated = dataSnapshot.getChildren().iterator().next().getValue(MealGroup.class);

                        groupCreated.setidCalendar(idCalendar);
                        groupCreated.setActive(true);

//  SAVE THE NEW USER GROUP
                        reference.child("groups").child(idGroups.get(position)).child(myUsers.get(position).getId()).setValue(groupCreated);

//  SAVE NEW CALENDAR
//                        reference.child("calendars").child(groupCreated.getidUserMasterGroup()).child(groupCreated.getIdGroup()).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for (DataSnapshot cal:dataSnapshot.getChildren()){
//                                    CalendarGroup recentCalendar = cal.getValue(CalendarGroup.class);
//                                    CalendarGroup newCalendar = new CalendarGroup(idCalendar, idUser, idGroups.get(position), recentCalendar.getCalendarInfo(), recentCalendar.getCalendarDay(), recentCalendar.getCalendarMenu(), recentCalendar.getCalendarLatitude(), recentCalendar.getCalendarLongitude(), recentCalendar.getCalendarHour(), recentCalendar.getCalendarPrice());
//                                    reference.child("calendars").child(myUsers.get(position).getId()).child(idGroups.get(position)).child(newCalendar.getCalendarKey()).setValue(newCalendar);
//
//
///// SAVE NEW DATE       //////////////////////////////////
////CHECK WE HAVE DATE REGISTERED
//                                    String format = "";
//                                    Calendar now = Calendar.getInstance();
//
//                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                                    int calendarDate = checkDayWeek(newCalendar.getCalendarDay());
//                                    int weekday = now.get(Calendar.DAY_OF_WEEK);
//                                    if (weekday != calendarDate) {
//                                        // calculate how much to add
//                                        // the int number is the difference between Saturday and our day
//                                        int days = (Calendar.SATURDAY - weekday + calendarDate) % 7;
//                                        now.add(Calendar.DAY_OF_YEAR, days);
//                                    }
//                                    // now is the date you want
//                                    Date nextDate = now.getTime();
//                                    format = sdf.format(nextDate).split(" ")[0];
//
//                                    final String idDate = newRef.getKey();
//                                    newRef = newRef.child("dates").child(myUsers.get(position).getId()).child(idGroups.get(position)).child(idDate).push();
//
//                                    GroupDate newDate = new GroupDate(idDate,myUsers.get(position).getId(),newCalendar.getCalendarGroup(),newCalendar.getCalendarInfo(),newCalendar.getCalendarDay(),newCalendar.getCalendarMenu(),newCalendar.getCalendarLatitude(),newCalendar.getCalendarLongitude(),newCalendar.getCalendarHour(),newCalendar.getCalendarPrice(), format, false);
/////////////////  SAVE DATE
//                                    reference.child("dates").child(pUser.getId()).child(newCalendar.getCalendarGroup()).child(idDate).setValue(newDate);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//  SAVE NOTIFICATION STATE TO ADMIN
                reference.child("notifications").child("groups").child(idUser).child(idGroups.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot pojoNoti:dataSnapshot.getChildren()){
                            NotificationGroup not = pojoNoti.getValue(NotificationGroup.class);
                            if (not.getUserId().equals(pUser.getId())){
                                not.setActive(false);
                                reference.child("notifications").child("groups").child(idUser).child(idGroups.get(position)).child(not.getNotificationId()).setValue(not);



//  SAVE NOTIFICATION STATE TO USER

                                not.setMessage(viewHolder.itemView.getResources().getString(R.string.userAccepted));
                                not.setUserId(pUser.getId());
                                not.setNotified(false);
                                not.setActive(true);
                                not.setType("apply");
                                reference.child("notifications").child("groups").child(pUser.getId()).child(idGroups.get(position)).child(not.getNotificationId()).setValue(not);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                Toast.makeText(viewHolder.itemView.getContext(), R.string.userAccepted, Toast.LENGTH_SHORT).show();

                Intent goMain = new Intent(v.getContext(), MyGroups.class);
                v.getContext().startActivity(goMain);
                }
            });



            viewHolder.denyUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

//                     SAVE NOTIFICATION STAGE
                    reference.child("notifications").child("groups").child(idUser).child(idGroups.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot pojoNoti:dataSnapshot.getChildren()){
                                NotificationGroup not = pojoNoti.getValue(NotificationGroup.class);
                                if (not.getUserId().equals(pUser.getId())){
                                    not.setActive(false);
                                    reference.child("notifications").child("groups").child(idUser).child(not.getGroupId()).child(not.getNotificationId()).setValue(not);

                                    Intent goMain = new Intent(v.getContext(), MyGroups.class);
                                    v.getContext().startActivity(goMain);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(viewHolder.itemView.getContext(), R.string.userDenied, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    //// TOOLS
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

    @Override
    public int getItemCount() { return this.myUsers.size(); }




}
