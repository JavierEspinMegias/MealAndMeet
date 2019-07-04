package com.example.cosmo.comer8;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

//Los adaptadores heredan de RecyclerView.Adapter
public class AdapterSingleDate extends RecyclerView.Adapter<AdapterSingleDate.ViewHolder> {

    //Resto de variables de la clase
    private List<GroupDate> myDates;
    public String idUser;

    //En un adaptador es obligatorio definir una clase que herede de RecyclerView.ViewHolder
    //La clase ViewHolder hará referencia a los elementos de la vista creada para el recycler view
    public class ViewHolder extends RecyclerView.ViewHolder {
        //Su constructor debera enlazar las variables del controlador con la vista
        public TextView dateDay;
        public DatabaseReference firebaseRef;
        private Button goDate;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.firebaseRef = FirebaseDatabase.getInstance().getReference();
            this.goDate = itemView.findViewById(R.id.go_date);
            this.dateDay = itemView.findViewById(R.id.dateDay);
        }
    }



    //El constructor deberá enlazar los datos del modelos con los del controlador
    public AdapterSingleDate(List<GroupDate> dates, String idUser) {
        this.myDates = dates;
        this.idUser = idUser;
    }

    //Debemos crear el método onCreateViewHolder que enlace la clase ViewHolder creada con la vista
    //Debe devolver un manejador de vistas enlazado a la vista que vayamos utilizar para cada item del adaptador
    @Override
    public AdapterSingleDate.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Especificamos el fichero XML que se utilizará como vista
        View contactView = inflater.inflate(R.layout.view_single_date, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    //Debemos sobrecargar onBindViewHolder que enlaza los datos del modelo con la vista
    @Override
    public void onBindViewHolder(final AdapterSingleDate.ViewHolder viewHolder, final int position) {
        final GroupDate date = this.myDates.get(position);
        viewHolder.dateDay.setText(" "+date.getDayDate());

        viewHolder.goDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goGroup =  new Intent(v.getContext(), ViewSingleDateActivity.class);
                goGroup.putExtra("dateKey",date.getDateId());
                v.getContext().startActivity(goGroup);
            }
        });

    }


    //Debemos sobrecargar getItemCount que devuelve el número de elementos que habrá en la vista
    //Si estamos utilizando una clase contenedor de Java nos bastará, la mayoría de la veces, con devolver el valor de su método size
    @Override
    public int getItemCount() { return this.myDates.size(); }

}
