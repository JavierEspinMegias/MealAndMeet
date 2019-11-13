package com.example.cosmo.comer8;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateGroup extends AppCompatActivity {


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private DatabaseReference newRef;

    private EditText meat, price, hour, info;
    private boolean haveLocation;

    private Spinner spinner;
    private String[] weekDays;
    public ArrayAdapter<String>spinnerAdapter;

    public Button sendGroup;

    //ELECCION FOTOGRAFIA
    private ImageView finalImage;
    public Uri outputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        reference = database.getReference();

        meat = (EditText)findViewById(R.id.getMeal);
        price = (EditText)findViewById(R.id.getPrice);
        hour = (EditText)findViewById(R.id.getHour);
        info = (EditText)findViewById(R.id.getInfo);
        haveLocation = false;
        sendGroup = (Button)findViewById(R.id.sendGroup);

        finalImage = (ImageView)findViewById(R.id.imageView4);

        spinner = (Spinner)findViewById(R.id.getDay);
        weekDays = new String[]{WeekDayNames.MONDAY.toString(), WeekDayNames.TUESDAY.toString(), WeekDayNames.WEDNESDAY.toString(), WeekDayNames.THURSDAY.toString(), WeekDayNames.FRIDAY.toString(), WeekDayNames.SATURDAY.toString(),  WeekDayNames.SUNDAY.toString()};

        spinner.setPrompt("Days");
        spinnerAdapter = new ArrayAdapter<String>(CreateGroup.this,android.R.layout.simple_spinner_item,weekDays);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        //PERMISOS
        if(ContextCompat.checkSelfPermission(CreateGroup.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CreateGroup.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CreateGroup.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},1000);
        }

        finalImage.setImageResource(R.mipmap.ic_take_photo);

        sendGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGroup();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences saved = getSharedPreferences("data", Context.MODE_PRIVATE);
        if(saved.getString("newGroupLatitude","") != null && !saved.getString("newGroupLatitude","").equals("")){
            haveLocation = true;
        }else{
            Toast.makeText(this, R.string.need_location, Toast.LENGTH_SHORT).show();
        }
    }


    public void sendGroup(){

        //IMAGE LOAD
        if (finalImage.getDrawable() != null){

            if (haveLocation
                    && !price.getText().toString().isEmpty()
                    && !meat.getText().toString().isEmpty()
                    && !spinner.getSelectedItem().toString().isEmpty()
                    && !hour.getText().toString().isEmpty()
                    && !info.getText().toString().isEmpty()
                    && finalImage.getDrawable() != null){


                SharedPreferences saved = getSharedPreferences("data", Context.MODE_PRIVATE);
                String latitude = saved.getString("newGroupLatitude", "");
                String longitude = saved.getString("newGroupLongitude", "");
                String area = saved.getString("newGroupArea","");

                ///    YEAH we do the magic here ---  new GROUP ID   ///
                newRef = database.getReference();
                newRef = reference.child("groups").child(saved.getString("userId","")).push();
                String idGroup = newRef.getKey();

                ///    YEAH we do the magic here ---  new CALENDAR ID   ///
                newRef = database.getReference();
                newRef = reference.child("calendars").child(saved.getString("userId","")).child(idGroup).push();
                String idCalendar = newRef.getKey();


                //    Initialize GROUP
                MealGroup groupCreated = new MealGroup(idGroup,saved.getString("userId",""),meat.getText().toString(),latitude,longitude,area,price.getText().toString(),idCalendar, true, info.getText().toString(),"");

                //    Initialize CALENDAR
                CalendarGroup newCalendar = new CalendarGroup(idCalendar,saved.getString("userId",""),idGroup,info.getText().toString(), spinner.getSelectedItem().toString(),meat.getText().toString(),latitude,longitude,hour.getText().toString(),price.getText().toString());

                //    SAVE THE NEW GROUP AND CALENDAR
                reference.child("groups").child(idGroup).child(saved.getString("userId","")).setValue(groupCreated);
                reference.child("calendars").child(saved.getString("userId","")).child(idGroup).child(newCalendar.getCalendarKey()).setValue(newCalendar);

                // SAVE A NEW DATE
                newRef = newRef.child("dates").child(idGroup).child(saved.getString("userId","")).push();
                final String idDate = newRef.getKey();

                String format = "";
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                int calendarDate = checkDayWeek(newCalendar.getCalendarDay());

                //CHECK WE HAVE DATE REGISTERED
                Calendar now = Calendar.getInstance();
                int weekday = now.get(Calendar.DAY_OF_WEEK);
                if (weekday != calendarDate) {
                    // calculate how much to add
                    // the int number is the difference between Saturday and our day
                    int days = (Calendar.SATURDAY - weekday + calendarDate) % 7;
                    now.add(Calendar.DAY_OF_YEAR, days);
                }
                // now is the date you want
                Date nextDate = now.getTime();
                format = sdf.format(nextDate).split(" ")[0];

                GroupDate newDate = new GroupDate(idDate,saved.getString("userId",""),idGroup,newCalendar.getCalendarInfo(),newCalendar.getCalendarDay(),newCalendar.getCalendarMenu(),newCalendar.getCalendarLatitude(),newCalendar.getCalendarLongitude(),newCalendar.getCalendarHour(),newCalendar.getCalendarPrice(),format, false);

                // SAVE DATE
                reference.child("dates").child(saved.getString("userId","")).child(idGroup).child(idDate).setValue(newDate);


                // SAVE IMAGE
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference newFinalImage = storageRef.child(idGroup);

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
                        Toast.makeText(CreateGroup.this, getResources().getString(R.string.no_money), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CreateGroup.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
                //    Restore shared to next group creation
                SharedPreferences.Editor editor = saved.edit();
                editor.putString("newGroupLocation","");
                editor.commit();

//            sendGroup(groupCreated.getIdGroup());

                Toast.makeText(this, R.string.group_created, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MyGroups.class));
            }else{
                Toast.makeText(this, R.string.need_all_fields, Toast.LENGTH_SHORT).show();
            }


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

    public void goSelectLocation(View v){startActivity(new Intent(this, SelectLocation.class));}

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
