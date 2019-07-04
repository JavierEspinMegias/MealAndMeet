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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdapterAdmin extends RecyclerView.Adapter<AdapterAdmin.ViewHolder> {

    private List<User> users;
    private List<MealGroup> groups;
    private List<CalendarGroup> calendars;
    private List<GroupDate> dates;
    private List<String> usersId;

    private String typeAdpater;

    public DatabaseReference firebaseRef;
    public FirebaseStorage storage;
    public StorageReference storageRef;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView0, textView1,textView2,textView3,textView4, textView5, textView6, textView7, textView8, textView9, textView10;
        public EditText editText0, editText1,editText2,editText3,editText4, editText5, editText6, editText7, editText8, editText9, editText10;

        private ImageView photo;

        public Button edit, remove;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.edit = itemView.findViewById(R.id.edit);
            this.remove = itemView.findViewById(R.id.remove);

            this.photo = itemView.findViewById(R.id.imageViewAdmin);
            this.photo.setVisibility(View.GONE);

            this.textView0 = itemView.findViewById(R.id.textView0);
            this.textView1 = itemView.findViewById(R.id.textView1);
            this.textView2 = itemView.findViewById(R.id.textView2);
            this.textView3 = itemView.findViewById(R.id.textView3);
            this.textView4 = itemView.findViewById(R.id.textView4);
            this.textView5 = itemView.findViewById(R.id.textView5);
            this.textView6 = itemView.findViewById(R.id.textView6);
            this.textView7 = itemView.findViewById(R.id.textView7);
            this.textView8 = itemView.findViewById(R.id.textView8);
            this.textView9 = itemView.findViewById(R.id.textView9);
            this.textView10 = itemView.findViewById(R.id.textView10);

            this.editText0 = itemView.findViewById(R.id.editText0);
            this.editText1 = itemView.findViewById(R.id.editText1);
            this.editText2 = itemView.findViewById(R.id.editText2);
            this.editText3 = itemView.findViewById(R.id.editText3);
            this.editText4 = itemView.findViewById(R.id.editText4);
            this.editText5 = itemView.findViewById(R.id.editText5);
            this.editText6 = itemView.findViewById(R.id.editText6);
            this.editText7 = itemView.findViewById(R.id.editText7);
            this.editText8 = itemView.findViewById(R.id.editText8);
            this.editText9 = itemView.findViewById(R.id.editText9);
            this.editText10 = itemView.findViewById(R.id.editText10);
        }
    }

    public AdapterAdmin(List<User> users, List<MealGroup>groups, List<CalendarGroup> calendars, List<GroupDate> dates, ArrayList<String> usersId,  String type) {

        this.typeAdpater = type;
        if (type.equals("groups")){
            this.typeAdpater = type;
            this.usersId = usersId;
            this.groups = groups;
        }else if(type.equals("users")){
            this.typeAdpater = type;
            this.usersId = usersId;
            this.users = users;
        }else if(type.equals("calendars")){
            this.typeAdpater = type;
            this.usersId = usersId;
            this.calendars = calendars;
        }else if(type.equals("dates")){
            this.typeAdpater = type;
            this.usersId = usersId;
            this.dates = dates;
        }else if(type.equals("userPhotos")){
            this.typeAdpater = type;
            this.usersId = usersId;
            this.users = users;
        }else if(type.equals("groupPhotos")){
            this.typeAdpater = type;
            this.usersId = usersId;
            this.groups = groups;
        }

        this.firebaseRef = FirebaseDatabase.getInstance().getReference();
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();
    }


    @Override
    public AdapterAdmin.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.view_single_admin, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final AdapterAdmin.ViewHolder viewHolder, final int position) {

        if (typeAdpater.equals("users")){

            final User userInspect = this.users.get(position);

            viewHolder.textView0.setText("Id");
            viewHolder.editText0.setText(userInspect.getId());

            viewHolder.textView1.setText("Email");
            viewHolder.editText1.setText(userInspect.getEmail());

            viewHolder.textView2.setText("Name");
            viewHolder.editText2.setText(userInspect.getName());

            viewHolder.textView3.setText("Age");
            viewHolder.editText3.setText(userInspect.getAge()+"");

            viewHolder.textView4.setText("Active");
            viewHolder.editText4.setText(userInspect.isActive()+"");

            viewHolder.textView5.setText("Password");
            viewHolder.editText5.setText(userInspect.getPass());

            viewHolder.textView6.setText("Location");
            viewHolder.editText6.setText(userInspect.getLatitude() + "   " + userInspect.getLongitude());

            viewHolder.textView7.setText("Address");
            viewHolder.editText7.setText(userInspect.getAddress());

            viewHolder.textView8.setText("Phone");
            viewHolder.editText8.setText(userInspect.getPhone());

            viewHolder.textView9.setText("Area");
            viewHolder.editText9.setText(userInspect.getArea()+"");

            viewHolder.textView10.setText("Nick");
            viewHolder.editText10.setText(userInspect.getNick());

            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userInspect.setId(viewHolder.editText0.getText().toString());
                    userInspect.setEmail(viewHolder.editText1.getText().toString());
                    userInspect.setName(viewHolder.editText2.getText().toString());
                    userInspect.setAge(Integer.parseInt(viewHolder.editText3.getText().toString()));
                    userInspect.setPass(viewHolder.editText5.getText().toString());
                    userInspect.setAddress(viewHolder.editText7.getText().toString());
                    userInspect.setPhone(viewHolder.editText8.getText().toString());
                    userInspect.setArea(Double.parseDouble(viewHolder.editText9.getText().toString()));
                    userInspect.setNick(viewHolder.editText10.getText().toString());

                    firebaseRef.child("users").child(userInspect.getId()).setValue(userInspect);
                }
            });

            if (userInspect.isActive()){
                viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseRef.child("users").child(userInspect.getId()).child("active").setValue(false);
                        Toast.makeText(viewHolder.itemView.getContext(), R.string.user_modified, Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                viewHolder.remove.setText("Activate user");
                viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseRef.child("users").child(userInspect.getId()).child("active").setValue(true);
                        Toast.makeText(viewHolder.itemView.getContext(), R.string.user_modified, Toast.LENGTH_LONG).show();
                    }
                });

            }


        }else if(typeAdpater.equals("groups")){

            final MealGroup groupInspect = this.groups.get(position);
            final String idUser = this.usersId.get(position);

            viewHolder.textView0.setText("Id");
            viewHolder.editText0.setText(groupInspect.getIdGroup());

            viewHolder.textView1.setText("Active");
            viewHolder.editText1.setText(groupInspect.isActive() + "");

            viewHolder.textView2.setText("Title");
            viewHolder.editText2.setText(groupInspect.getTitle());

            viewHolder.textView3.setText("Info");
            viewHolder.editText3.setText(groupInspect.getInfoGroup());

            viewHolder.textView4.setText("Price");
            viewHolder.editText4.setText(groupInspect.getPrice());

            viewHolder.textView5.setText("User Master");
            viewHolder.editText5.setText(groupInspect.getUserMaster());

            viewHolder.textView6.setText("Location");
            viewHolder.editText6.setText(groupInspect.getLatitude() + "   " + groupInspect.getLongitude());

            viewHolder.textView7.setText("Radius");
            viewHolder.editText7.setText(groupInspect.getRadiusMap()+"");

            viewHolder.textView8.setText("Calendar");
            viewHolder.editText8.setText(groupInspect.getidCalendar());

            viewHolder.textView9.setText("WhatsApp");
            viewHolder.editText9.setText(groupInspect.getSnippet());

            viewHolder.textView10.setVisibility(View.GONE);
            viewHolder.editText10.setVisibility(View.GONE);

            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupInspect.setIdGroup(viewHolder.editText0.getText().toString());
                    groupInspect.setTitle(viewHolder.editText2.getText().toString());
                    groupInspect.setInfoGroup(viewHolder.editText3.getText().toString());
                    groupInspect.setPrice(viewHolder.editText4.getText().toString());
                    groupInspect.setidUserMasterGroup(viewHolder.editText5.getText().toString());
                    groupInspect.setRadiusMap(viewHolder.editText7.getText().toString());
                    groupInspect.setidCalendar(viewHolder.editText8.getText().toString());
                    groupInspect.setSnippet(viewHolder.editText9.getText().toString());

                    firebaseRef.child("groups").child(groupInspect.getIdGroup()).child(usersId.get(position)).setValue(groupInspect);
                    Toast.makeText(viewHolder.itemView.getContext(), "Group edited", Toast.LENGTH_LONG).show();
                }
            });


            if (groupInspect.isActive()){
                viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseRef.child("groups").child(groupInspect.getIdGroup()).child(idUser).child("active").setValue(false);
                        Toast.makeText(viewHolder.itemView.getContext(), R.string.user_modified, Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                viewHolder.remove.setText("Activate group");
                viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseRef.child("groups").child(groupInspect.getIdGroup()).child(idUser).child("active").setValue(true);
                        Toast.makeText(viewHolder.itemView.getContext(), R.string.user_modified, Toast.LENGTH_LONG).show();
                    }
                });
            }

        }else if(typeAdpater.equals("calendars")){

            final CalendarGroup calendarInspect = this.calendars.get(position);

            viewHolder.textView0.setText("Id");
            viewHolder.editText0.setText(calendarInspect.getCalendarKey());

            viewHolder.textView1.setText("Group");
            viewHolder.editText1.setText(calendarInspect.getCalendarGroup());

            viewHolder.textView2.setText("Hour");
            viewHolder.editText2.setText(calendarInspect.getCalendarHour());

            viewHolder.textView3.setText("Menu");
            viewHolder.editText3.setText(calendarInspect.getCalendarMenu());

            viewHolder.textView4.setText("Price");
            viewHolder.editText4.setText(calendarInspect.getCalendarPrice());

            viewHolder.textView5.setText("User");
            viewHolder.editText5.setText(calendarInspect.getCalendarUser());

            viewHolder.textView6.setText("Location");
            viewHolder.editText6.setText(calendarInspect.getCalendarLatitude() + "   " + calendarInspect.getCalendarLongitude());

            viewHolder.textView7.setText("Week day");
            viewHolder.editText7.setText(calendarInspect.getCalendarDay());

            viewHolder.textView8.setText("Title");
            viewHolder.editText8.setText(calendarInspect.getCalendarInfo());

            viewHolder.textView9.setVisibility(View.GONE);
            viewHolder.editText9.setVisibility(View.GONE);

            viewHolder.textView10.setVisibility(View.GONE);
            viewHolder.editText10.setVisibility(View.GONE);

            viewHolder.remove.setVisibility(View.GONE);

            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendarInspect.setCalendarKey(viewHolder.editText0.getText().toString());
                    calendarInspect.setCalendarGroup(viewHolder.editText1.getText().toString());
                    calendarInspect.setCalendarHour(viewHolder.editText2.getText().toString());
                    calendarInspect.setCalendarMenu(viewHolder.editText3.getText().toString());
                    calendarInspect.setCalendarPrice(viewHolder.editText4.getText().toString());
                    calendarInspect.setCalendarUser(viewHolder.editText5.getText().toString());
                    calendarInspect.setCalendarDay(viewHolder.editText7.getText().toString());
                    calendarInspect.setCalendarInfo(viewHolder.editText8.getText().toString());

                    firebaseRef.child("calendars").child(calendarInspect.getCalendarUser()).child(calendarInspect.getCalendarGroup()).child(calendarInspect.getCalendarKey()).setValue(calendarInspect);
                    Toast.makeText(viewHolder.itemView.getContext(), "Group edited", Toast.LENGTH_LONG).show();
                }
            });


        }else if(typeAdpater.equals("dates")){

            final GroupDate dateInspect = this.dates.get(position);

            viewHolder.textView0.setText("Id");
            viewHolder.editText0.setText(dateInspect.getDateId());

            viewHolder.textView1.setText("Group");
            viewHolder.editText1.setText(dateInspect.getGroupId());

            viewHolder.textView2.setText("User");
            viewHolder.editText2.setText(dateInspect.getUserId());

            viewHolder.textView3.setText("Menu");
            viewHolder.editText3.setText(dateInspect.getMenu());

            viewHolder.textView4.setText("Price");
            viewHolder.editText4.setText(dateInspect.getPrice());

            viewHolder.textView5.setText("Week day");
            viewHolder.editText5.setText(dateInspect.getDay());

            viewHolder.textView6.setText("Location");
            viewHolder.editText6.setText(dateInspect.getLatitude() + "   " + dateInspect.getLongitude());

            viewHolder.textView7.setText("DATE");
            viewHolder.editText7.setText(dateInspect.getDayDate());

            viewHolder.textView8.setText("Info");
            viewHolder.editText8.setText(dateInspect.getInfo());

            viewHolder.textView9.setText("Hour");
            viewHolder.editText9.setText(dateInspect.getHour());

            viewHolder.textView10.setVisibility(View.GONE);
            viewHolder.editText10.setVisibility(View.GONE);

            viewHolder.remove.setVisibility(View.GONE);

            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateInspect.setDateId(viewHolder.editText0.getText().toString());
                    dateInspect.setGroupId(viewHolder.editText1.getText().toString());
                    dateInspect.setUserId(viewHolder.editText2.getText().toString());
                    dateInspect.setMenu(viewHolder.editText3.getText().toString());
                    dateInspect.setPrice(viewHolder.editText4.getText().toString());
                    dateInspect.setDay(viewHolder.editText5.getText().toString());
                    dateInspect.setDayDate(viewHolder.editText7.getText().toString());
                    dateInspect.setInfo(viewHolder.editText8.getText().toString());
                    dateInspect.setHour(viewHolder.editText9.getText().toString());

                    firebaseRef.child("calendars").child(usersId.get(position)).child(dateInspect.getGroupId()).child(dateInspect.getDateId()).setValue(dateInspect);
                    Toast.makeText(viewHolder.itemView.getContext(), "Group edited", Toast.LENGTH_LONG).show();
                }
            });

        }else if(typeAdpater.equals("groupPhotos")){
            this.firebaseRef = FirebaseDatabase.getInstance().getReference();
            this.storage = FirebaseStorage.getInstance();
            this.storageRef = storage.getReference();

            final MealGroup groupInspect = this.groups.get(position);

            viewHolder.photo.setVisibility(View.VISIBLE);

            viewHolder.remove.setVisibility(View.GONE);
            viewHolder.edit.setVisibility(View.GONE);

            viewHolder.textView0.setText("Group id");
            viewHolder.editText0.setText(groupInspect.getIdGroup());

            viewHolder.textView1.setText("User id");
            viewHolder.editText1.setText(usersId.get(position));

            viewHolder.textView2.setText("Title");
            viewHolder.editText2.setText(groupInspect.getTitle());
            viewHolder.editText2.setEnabled(false);

            viewHolder.textView3.setVisibility(View.GONE);
            viewHolder.textView4.setVisibility(View.GONE);
            viewHolder.textView5.setVisibility(View.GONE);
            viewHolder.textView6.setVisibility(View.GONE);
            viewHolder.textView7.setVisibility(View.GONE);
            viewHolder.textView8.setVisibility(View.GONE);
            viewHolder.textView9.setVisibility(View.GONE);
            viewHolder.textView10.setVisibility(View.GONE);;

            viewHolder.editText3.setVisibility(View.GONE);
            viewHolder.editText4.setVisibility(View.GONE);
            viewHolder.editText5.setVisibility(View.GONE);
            viewHolder.editText6.setVisibility(View.GONE);
            viewHolder.editText7.setVisibility(View.GONE);
            viewHolder.editText8.setVisibility(View.GONE);
            viewHolder.editText9.setVisibility(View.GONE);
            viewHolder.editText10.setVisibility(View.GONE);

            storageRef.child(groupInspect.getIdGroup()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(viewHolder.itemView.getContext()).load(uri).into(viewHolder.photo);
                }

            });

        }else if(typeAdpater.equals("userPhotos")){
            this.firebaseRef = FirebaseDatabase.getInstance().getReference();
            this.storage = FirebaseStorage.getInstance();
            this.storageRef = storage.getReference();

            final User userInspect = this.users.get(position);

            viewHolder.photo.setVisibility(View.VISIBLE);

            viewHolder.remove.setVisibility(View.GONE);
            viewHolder.edit.setVisibility(View.GONE);

            viewHolder.textView0.setText("User");
            viewHolder.editText0.setText(userInspect.getId());

            viewHolder.textView1.setText("Email");
            viewHolder.editText1.setText(userInspect.getEmail());

            viewHolder.textView2.setText("Phone");
            viewHolder.editText2.setText(userInspect.getPhone());
            viewHolder.editText2.setEnabled(false);

            viewHolder.photo.setVisibility(View.VISIBLE);
            viewHolder.textView3.setVisibility(View.GONE);
            viewHolder.textView4.setVisibility(View.GONE);
            viewHolder.textView5.setVisibility(View.GONE);
            viewHolder.textView6.setVisibility(View.GONE);
            viewHolder.textView7.setVisibility(View.GONE);
            viewHolder.textView8.setVisibility(View.GONE);
            viewHolder.textView9.setVisibility(View.GONE);
            viewHolder.textView10.setVisibility(View.GONE);;

            viewHolder.editText3.setVisibility(View.GONE);
            viewHolder.editText4.setVisibility(View.GONE);
            viewHolder.editText5.setVisibility(View.GONE);
            viewHolder.editText6.setVisibility(View.GONE);
            viewHolder.editText7.setVisibility(View.GONE);
            viewHolder.editText8.setVisibility(View.GONE);
            viewHolder.editText9.setVisibility(View.GONE);
            viewHolder.editText10.setVisibility(View.GONE);

            storageRef.child(userInspect.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(viewHolder.itemView.getContext()).load(uri).into(viewHolder.photo);
                }

            });

        }
    }


    @Override
    public int getItemCount() {
        if(typeAdpater.equals("users")){
            return this.users.size();
        }else if(typeAdpater.equals("groups")){
            return this.groups.size();
        }else if(typeAdpater.equals("calendars")){
            return this.calendars.size();
        }else if(typeAdpater.equals("dates")){
            return this.dates.size();
        }else if(typeAdpater.equals("userPhotos")){
            return this.users.size();
        }else if(typeAdpater.equals("groupPhotos")){
            return this.groups.size();
        }else{
            return -1;
        }
    }

}
