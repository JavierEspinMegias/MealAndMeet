package com.example.cosmo.comer8;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    EditText name, email, pass, pass2, surname, address, age, phoneNumber;
    TextView key;
    Button sign;
    String id;

    public User usu = new User();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    DatabaseReference newRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sign = (Button) findViewById(R.id.sign);

        name = (EditText) findViewById(R.id.editName);
        email = (EditText) findViewById(R.id.editEmail);
        pass = (EditText) findViewById(R.id.editPass);
        pass2 = (EditText) findViewById(R.id.editPass2);
        surname = (EditText) findViewById(R.id.editSurname);
        address = (EditText) findViewById(R.id.editAddress);
        age = (EditText) findViewById(R.id.editAge);
        phoneNumber = (EditText) findViewById(R.id.editPhone);

    }


    public void newUser(View v) {

        String pName = name.getText().toString();
        String pEmail = email.getText().toString();
        String pPass = pass.getText().toString();
        String pSurname = surname.getText().toString();
        String pAddress = address.getText().toString();
        String pAge = age.getText().toString();


        if (isValidEmail(pEmail)) {
            if (isValidPass(pPass)) {
                if (isValidName(pName)) {
                    if (isValidPhoneNumber(phoneNumber.getText().toString())) {
                        if (pSurname != "" && pAddress != "" && pAge != "") {

                            // We can do the magic now
                            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

                            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();

                            User newUser = new User(pName,pEmail,pPass,pSurname,pAddress,Integer.parseInt(pAge),"","",latitude,longitude,phoneNumber.getText().toString(),400.0, true);
                            newRef = reference.child("users").push();
                            String id = newRef.getKey();
                            newUser.setId(id);
                            reference.child("users").child(id).setValue(newUser);

                            Toast.makeText(this, R.string.userCreated, Toast.LENGTH_SHORT).show();

                            // Save user sharedPreferences
                            SharedPreferences saveUser = getSharedPreferences("data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor obj_editor = saveUser.edit();
                            obj_editor.putString("userId",newUser.getId());
                            obj_editor.putString("userEmail",newUser.getEmail());
                            obj_editor.putString("userPass",newUser.getPass());
                            obj_editor.putString("userArea",newUser.getArea()+"");
                            obj_editor.commit();

                            startActivity(new Intent(this, Start.class));

                        }else{
                            Toast.makeText(this, R.string.allFields, Toast.LENGTH_SHORT).show();
                        }
                    }else Toast.makeText(this, R.string.wrongPhone, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, R.string.wrongName, Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, R.string.wrongPass, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, R.string.wrongEmail, Toast.LENGTH_SHORT).show();
        }
    }



    /////// Check fields of hell

    public final static boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public final static boolean isValidPass(CharSequence pass) {
        String regx = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{7,}$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pass);
        return matcher.find();
    }

    public final static boolean isValidName(CharSequence name){
        String regx = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }

    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }
}
