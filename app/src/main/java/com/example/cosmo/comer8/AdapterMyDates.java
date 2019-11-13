package com.example.cosmo.comer8;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

//Los adaptadores heredan de RecyclerView.Adapter
public class AdapterMyDates extends RecyclerView.Adapter<AdapterMyDates.ViewHolder> {

    //Resto de variables de la clase
    private List<CalendarGroup> myDates;
    public String idUser;


    //En un adaptador es obligatorio definir una clase que herede de RecyclerView.ViewHolder
    //La clase ViewHolder hará referencia a los elementos de la vista creada para el recycler view
    public class ViewHolder extends RecyclerView.ViewHolder {
        //Su constructor debera enlazar las variables del controlador con la vista
        public TextView dateDay, menuGroup, hour, price, infoGroup;
        public Button calendar, goGroup;

        public DatabaseReference firebaseRef;

        public ViewHolder(final View itemView) {
            super(itemView);

            firebaseRef = FirebaseDatabase.getInstance().getReference();

            this.goGroup = itemView.findViewById(R.id.groupDate);
            this.menuGroup = itemView.findViewById(R.id.dateMenu);
            this.dateDay = itemView.findViewById(R.id.dateDay);
            this.hour = itemView.findViewById(R.id.dateHour);
            this.price = itemView.findViewById(R.id.priceDate);
            this.infoGroup = itemView.findViewById(R.id.infoDate);

        }
    }



    //El constructor deberá enlazar los datos del modelos con los del controlador
    public AdapterMyDates(List<CalendarGroup> dates, String idUser) {
        this.myDates = dates;
        this.idUser = idUser;
    }

    //Debemos crear el método onCreateViewHolder que enlace la clase ViewHolder creada con la vista
    //Debe devolver un manejador de vistas enlazado a la vista que vayamos utilizar para cada item del adaptador
    @Override
    public AdapterMyDates.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Especificamos el fichero XML que se utilizará como vista
        View contactView = inflater.inflate(R.layout.view_date, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    //Debemos sobrecargar onBindViewHolder que enlaza los datos del modelo con la vista
    @Override
    public void onBindViewHolder(final AdapterMyDates.ViewHolder viewHolder, final int position) {
        final CalendarGroup date = this.myDates.get(position);


        viewHolder.price.setText(" "+date.getCalendarPrice());
        viewHolder.infoGroup.setText(" "+date.getCalendarInfo());
        viewHolder.dateDay.setText(" "+date.getCalendarDay());
        viewHolder.menuGroup.setText(" "+date.getCalendarMenu());
        viewHolder.hour.setText(" "+date.getCalendarHour());

        viewHolder.goGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goGroup =  new Intent(v.getContext(), ViewMyGroup.class);
                goGroup.putExtra("groupKey",date.getCalendarGroup());
                v.getContext().startActivity(goGroup);
            }
        });
    }


    //Debemos sobrecargar getItemCount que devuelve el número de elementos que habrá en la vista
    //Si estamos utilizando una clase contenedor de Java nos bastará, la mayoría de la veces, con devolver el valor de su método size
    @Override
    public int getItemCount() { return this.myDates.size(); }

}
