package com.example.cosmo.comer8;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyProfile extends AppCompatActivity {

    public DatabaseReference firebaseReference;
    public FirebaseStorage storage;
    public StorageReference storageRef;
    private String userId;

    public EditText name, email, pass, pass2, surname, address, age, phoneNumber, radius;

    public SharedPreferences saved;

    public User myUserFinal;

    private ImageView finalImage;
    public Uri outputFileUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ConstraintLayout contLay = findViewById(R.id.bg_profile);
        AnimationDrawable animDraw = (AnimationDrawable)contLay.getBackground();
        animDraw.setEnterFadeDuration(2000);
        animDraw.setExitFadeDuration(4000);
        animDraw.start();


        firebaseReference = FirebaseDatabase.getInstance().getReference();

        saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        userId = saved.getString("userId","");
        finalImage = (ImageView)findViewById(R.id.userImage);

        name = (EditText)findViewById(R.id.editName);
        email = (EditText)findViewById(R.id.editEmail);
        pass = (EditText)findViewById(R.id.editPass);
        pass2 = (EditText)findViewById(R.id.editPass2);
        surname = (EditText)findViewById(R.id.editSurname);
        address = (EditText)findViewById(R.id.editAddress);
        age = (EditText)findViewById(R.id.editAge);
        phoneNumber = (EditText)findViewById(R.id.editPhone);
        radius = (EditText)findViewById(R.id.radius);

        getImage(userId);
        loadUser(userId);

        Toast.makeText(this, getResources().getString(R.string.click_to_edit), Toast.LENGTH_LONG).show();
        finalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openImageIntent(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }



    public void getImage(final String userId){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        storageRef.child(userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(finalImage);
            }

        });
    }


    public void sendPhoto(String userId){

        //IMAGE LOAD
        if (finalImage.getDrawable() != null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference newFinalImage = storageRef.child(userId);

            finalImage.setDrawingCacheEnabled(true);
            finalImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) finalImage.getDrawable()).getBitmap();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = newFinalImage.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MyProfile.this, getResources().getString(R.string.no_money), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MyProfile.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(this, getResources().getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
        }
    }

    ////////////////////////////////////////////
    //CHOOSE IMAGE METHODS
    String mCurrentPhotoPath;
    static final int REQUEST_CODE = 1;

    private File createImageFile() throws IOException {


        String imageFileName = "mm_"+ System.currentTimeMillis();

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }



    public void openImageIntent(View v) throws IOException {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "image" + File.separator);
        root.mkdirs();
        final String fname = (createImageFile()).toString();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getResources().getString(R.string.choose_source));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }
                if (isCamera) {
                    try {
                        Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputFileUri);
                        finalImage.setImageBitmap(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    outputFileUri = data == null ? null : data.getData();
                    Bitmap image = null;
                    try {
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputFileUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finalImage.setImageBitmap(image);
                }
            }
        }
    }


    public void sendData(View v){
        sendPhoto(userId);
        sendDataChange();
    }


    public void loadUser(String userId){
        firebaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myUserFinal = dataSnapshot.getValue(User.class);
                name.setText(myUserFinal.getName());
                email.setText(myUserFinal.getEmail());
                pass.setText(myUserFinal.getPass());
                pass2.setText(myUserFinal.getPass());
                surname.setText(myUserFinal.getSurname());
                age.setText(myUserFinal.getAge()+"");
                address.setText(myUserFinal.getAddress());
                radius.setText(myUserFinal.getArea()+"");
                phoneNumber.setText(myUserFinal.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void sendDataChange(){

        String pName = name.getText().toString();
        String pEmail = email.getText().toString();
        String pPass = pass.getText().toString();
        String pSurname = surname.getText().toString();
        String pAddress = address.getText().toString();
        String pAge = age.getText().toString();


        if (isValidEmail(pEmail)){
            if (isValidPass(pPass) && pPass.equals(pass2.getText().toString())){
                if (isValidName(pName)){
                    if (isValidPhoneNumber(phoneNumber.getText().toString())){
                        if (pSurname !="" && pAddress != "" && pAge != ""){
                            myUserFinal.setAge(Integer.parseInt(pAge));
                            myUserFinal.setAddress(pAddress);
                            myUserFinal.setSurname(pSurname);
                            myUserFinal.setArea(Double.parseDouble(radius.getText().toString()));
                            myUserFinal.setEmail(pEmail);
                            myUserFinal.setPass(pPass);
                            myUserFinal.setName(pName);
                            myUserFinal.setPhone(phoneNumber.getText().toString());
                            myUserFinal.setId(userId);
;
                            firebaseReference.child("users").child(userId).setValue(myUserFinal);

                            // Save user sharedPreferences
                            SharedPreferences saveUser = getSharedPreferences("data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor obj_editor = saveUser.edit();
                            obj_editor.putString("userId",myUserFinal.getId());
                            obj_editor.commit();
                            obj_editor.putString("userEmail",myUserFinal.getEmail());
                            obj_editor.commit();
                            obj_editor.putString("userPass",myUserFinal.getPass());
                            obj_editor.commit();
                            obj_editor.putString("userArea",myUserFinal.getArea()+"");
                            obj_editor.commit();

                            Toast.makeText(this, R.string.user_modified, Toast.LENGTH_SHORT).show();

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
