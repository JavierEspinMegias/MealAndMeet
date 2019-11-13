package com.example.cosmo.comer8;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.storage.StorageReference;

import java.util.List;

//Los adaptadores heredan de RecyclerView.Adapter
public class AdapterMyGroups extends RecyclerView.Adapter<AdapterMyGroups.ViewHolder> {

    //Resto de variables de la clase
    private List<MealGroup> myGroups;
    public String idUser;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    public FirebaseStorage storage;
    public StorageReference storageRef;
    public CalendarGroup data;

    public SharedPreferences saved;
    public SharedPreferences.Editor editor;


    //En un adaptador es obligatorio definir una clase que herede de RecyclerView.ViewHolder
    //La clase ViewHolder hará referencia a los elementos de la vista creada para el recycler view
    public class ViewHolder extends RecyclerView.ViewHolder {
        //Su constructor debera enlazar las variables del controlador con la vista
        public TextView menuGroup, price, infoGroup;
        public Button calendar, goGroup, mapGroup, whatsup;
        public ImageView imageGroup;
        public GroupDate gDate;



        public ViewHolder(final View itemView) {
            super(itemView);

            saved = itemView.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
            editor = saved.edit();
            this.calendar = itemView.findViewById(R.id.goCalendar);
            this.menuGroup = itemView.findViewById(R.id.menuGroup);
            this.price = itemView.findViewById(R.id.price);
            this.infoGroup = itemView.findViewById(R.id.infoGroup);
            this.mapGroup = itemView.findViewById(R.id.buttonMap);
            this.goGroup = itemView.findViewById(R.id.goGroup);
            this.imageGroup = itemView.findViewById(R.id.imageGroup);
            this.gDate = new GroupDate();
            this.whatsup = itemView.findViewById(R.id.whatsup);
        }
    }



    //El constructor deberá enlazar los datos del modelos con los del controlador
    public AdapterMyGroups(List<MealGroup> groups, String idUser) {
        this.myGroups = groups;
        this.idUser = idUser;
        this.data = new CalendarGroup();
    }

    //Debemos crear el método onCreateViewHolder que enlace la clase ViewHolder creada con la vista
    //Debe devolver un manejador de vistas enlazado a la vista que vayamos utilizar para cada item del adaptador
    @Override
    public AdapterMyGroups.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Especificamos el fichero XML que se utilizará como vista
        View contactView = inflater.inflate(R.layout.view_group, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    //Debemos sobrecargar onBindViewHolder que enlaza los datos del modelo con la vista
    @Override
    public void onBindViewHolder(final AdapterMyGroups.ViewHolder viewHolder, final int position) {
        final MealGroup group = this.myGroups.get(position);
        viewHolder.price.setText(group.getPrice());
        viewHolder.infoGroup.setText(group.getTitle());
        viewHolder.menuGroup.setText(group.getInfoGroup());



        database = FirebaseDatabase.getInstance();
        reference = database.getReference();



        reference.child("calendars").child(idUser).child(myGroups.get(position).getIdGroup()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleDate:dataSnapshot.getChildren()){
                    CalendarGroup calGroup = singleDate.getValue(CalendarGroup.class);
                    if (calGroup.getCalendarMenu()!=""){
                        viewHolder.menuGroup.setText(calGroup.getCalendarMenu());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        //// GO MAP LOCATION GROUP
        viewHolder.mapGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                reference.child("groups").child(myGroups.get(position).getIdGroup()).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MealGroup singleMeal = dataSnapshot.getValue(MealGroup.class);
                        String lati = singleMeal.getLatitude();
                        String longi = singleMeal.getLongitude();

                        Intent goGroup =  new Intent(v.getContext(), ViewLocation.class);
                        goGroup.putExtra("latitude",lati);
                        goGroup.putExtra("longitude",longi);
                        goGroup.putExtra("area","50");
                        v.getContext().startActivity(goGroup);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



        //// GO WHATSUP GROUP
        viewHolder.whatsup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                reference.child("groups").child(myGroups.get(position).getIdGroup()).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MealGroup singleMeal = dataSnapshot.getValue(MealGroup.class);
                        if (singleMeal.getSnippet().equals("") && singleMeal.getidUserMasterGroup().equals(idUser)){
                            Toast.makeText(viewHolder.itemView.getContext(), R.string.create_whatsapp, Toast.LENGTH_LONG).show();


                            Intent goWhats =  new Intent(viewHolder.itemView.getContext(), CreateWhats.class);
                            editor.putString("groupId",myGroups.get(position).getIdGroup());
                            editor.commit();
                            viewHolder.itemView.getContext().startActivity(goWhats);

                        }else if(!singleMeal.getSnippet().equals("")){
                            Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                            String url = "https://chat.whatsapp.com/"+singleMeal.getSnippet();
                            intentWhatsapp.setData(Uri.parse(url));
                            intentWhatsapp.setPackage("com.whatsapp");
                            viewHolder.itemView.getContext().startActivity(intentWhatsapp);
                        }else{
                            Toast.makeText(viewHolder.itemView.getContext(), R.string.this_group_no_whats, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        /// VIEW SINGLE GROUP
        viewHolder.goGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("calendars").child(idUser).child(myGroups.get(position).getIdGroup()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()){

                            Intent goCal =  new Intent(viewHolder.itemView.getContext(), CreateMealCal.class);
                            editor.putString("groupId",myGroups.get(position).getIdGroup());
                            editor.putString("userMasterIdCal",myGroups.get(position).getidUserMasterGroup());
                            editor.putString("areaCal",myGroups.get(position).getRadiusMap());
                            editor.commit();

                            viewHolder.itemView.getContext().startActivity(goCal);
                        }else{

                            Intent goGroup =  new Intent(viewHolder.itemView.getContext(), ViewMyGroup.class);
                            goGroup.putExtra("groupKey",myGroups.get(position).getIdGroup());
                            viewHolder.itemView.getContext().startActivity(goGroup);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });

        /// SET CALENDAR LINK

        viewHolder.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.itemView.getContext().startActivity(new Intent(viewHolder.itemView.getContext(), CalendarActivity.class));
            }
        });


        /// SET IMAGE GROUP

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        storageRef.child(group.getIdGroup()).getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                viewHolder.imageGroup.setImageResource(R.mipmap.ic_no_photo);
            }
        });
        storageRef.child(group.getIdGroup()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(viewHolder.itemView.getContext()).load(uri).into(viewHolder.imageGroup);
            }
        });






    }


    //Debemos sobrecargar getItemCount que devuelve el número de elementos que habrá en la vista
    //Si estamos utilizando una clase contenedor de Java nos bastará, la mayoría de la veces, con devolver el valor de su método size
    @Override
    public int getItemCount() { return this.myGroups.size(); }

}
