package com.example.cosmo.comer8;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText name, email, pass, pass2, surname, address, age;
    TextView key;
    Button sign;
    String id;

    public User usu = new User();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referencia = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sign = (Button)findViewById(R.id.sign);
        name = (EditText)findViewById(R.id.editName);
        email = (EditText)findViewById(R.id.editEmail);
        pass = (EditText)findViewById(R.id.editPass);
        pass2 = (EditText)findViewById(R.id.editPass2);
        surname = (EditText)findViewById(R.id.editSurname);
        address = (EditText)findViewById(R.id.editAddress);
        age = (EditText)findViewById(R.id.editAge);
        key = (TextView)findViewById(R.id.key);



//        if (checkEdit()){
//            sign.setText("Edit");
//        }

    }

    public boolean checkEdit(){
        referencia.child("users").child((getIntent().getStringExtra("key"))).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id = (getIntent().getStringExtra("key"));
                Iterator<DataSnapshot> ite = dataSnapshot.getChildren().iterator();
                Toast.makeText(Register.this, id, Toast.LENGTH_SHORT).show();
                key.setText(id);

                usu.setAddress(ite.next().getValue().toString());
                usu.setAge(Integer.parseInt(ite.next().getValue().toString()));
                usu.setEmail(ite.next().getValue().toString());
                usu.setName(ite.next().getValue().toString());
                usu.setPass(ite.next().getValue().toString());
                usu.setSurname(ite.next().getValue().toString());

                name.setText(usu.getName());
                email.setText(usu.getEmail());
                pass.setText(usu.getPass());
                surname.setText(usu.getSurname());
                address.setText(usu.getAddress());
                age.setText(""+usu.getAge());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return false;
    }

    public void newUser(View v){

        String pName = name.getText().toString();
        String pEmail = email.getText().toString();
        String pPass = pass.getText().toString();
        String pSurname = surname.getText().toString();
        String pAddress = address.getText().toString();
        String pAge = age.getText().toString();


        if (!sign.getText().equals("Edit")){
            if (pName.equals("") || pEmail.equals("") || pPass.equals("") || pSurname.equals("") || pAddress.equals("") || pAge.equals("")){
                Toast.makeText(this, "All fields please", Toast.LENGTH_SHORT).show();
            } else {

////////////////////////No funciona la seleccion de email, nunca encuentra el email igual//////////////////////////////
                if (referencia.child("users").child("email").equals(pEmail)) {
                    Toast.makeText(this, "Email already registered \ngo login or try another one", Toast.LENGTH_SHORT).show();
                }else {

                    User userAux = new User(pName,pEmail,pPass,pSurname,pAddress,Integer.parseInt(pAge),"","",0,0);

                    referencia.child("users").push().setValue(userAux);
                    Toast.makeText(this, "Welcome "+pName, Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            User userAux = new User(pName,pEmail,pPass,pSurname,pAddress,Integer.parseInt(pAge), "","",0,0);
            Map<String, Object> userEdit = new HashMap<>();
            //userEdit.put("users", userAux);
            //referencia.updateChildren(userEdit);
            referencia.child("users").child(getIntent().getStringExtra("key")).child("email").setValue(pEmail);
            referencia.child("users").child(getIntent().getStringExtra("key")).child("pass").setValue(pPass);
            referencia.child("users").child(getIntent().getStringExtra("key")).child("surname").setValue(pSurname);
            referencia.child("users").child(getIntent().getStringExtra("key")).child("address").setValue(pAddress);
            referencia.child("users").child(getIntent().getStringExtra("key")).child("age").setValue(Integer.parseInt(pAge));
            referencia.child("users").child(getIntent().getStringExtra("key")).child("id").setValue(Integer.parseInt(pAge));
            referencia.child("users").child(getIntent().getStringExtra("key")).child("nick").setValue(Integer.parseInt(pAge));
            referencia.child("users").child(getIntent().getStringExtra("key")).child("idPhoto").setValue(Integer.parseInt(pAge));
        }
    }
}
