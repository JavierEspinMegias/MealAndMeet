package com.example.cosmo.comer8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateWhats extends AppCompatActivity {
    public SharedPreferences saved;
    public Button setLink;
    public String userId, groupId;
    public FirebaseDatabase database;
    public DatabaseReference reference;
    public EditText linkData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_whats);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        linkData = (EditText)findViewById(R.id.linkData);
        saved = getSharedPreferences("data", Context.MODE_PRIVATE);

        userId = saved.getString("userId","");
        groupId = saved.getString("groupId","");

        setLink = (Button)findViewById(R.id.sendLink);


        setLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!linkData.getText().toString().equals("")){
                    reference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot pojoUser:dataSnapshot.getChildren()){
                                reference.child("groups").child(groupId).child(pojoUser.getKey()).child("snippet").setValue(linkData.getText().toString());
                            }
                            Toast.makeText(CreateWhats.this, R.string.group_linked, Toast.LENGTH_SHORT).show();
                            Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                            String url = "https://chat.whatsapp.com/"+linkData.getText().toString();
                            intentWhatsapp.setData(Uri.parse(url));
                            intentWhatsapp.setPackage("com.whatsapp");
                            startActivity(intentWhatsapp);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }
}
