package com.example.user.check_mate.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.user.check_mate.R;
import com.example.user.check_mate.model.backend.BackEndFunc;
import com.example.user.check_mate.model.backend.DataSourceType;
import com.example.user.check_mate.model.backend.FactoryMethod;
import com.example.user.check_mate.model.backend.SelectedDataSource;
import com.example.user.check_mate.model.entities.Gender;
import com.example.user.check_mate.model.entities.Person;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GetStartedActivity extends AppCompatActivity {
    String ret = "";
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChosenTask;
    File file;
    Uri uri;
    CircleImageView imageView;
    EditText nameText;
    Spinner ageSpinner;
    RadioButton femaleRadio;
    RadioButton maleRadio;
    Button getStartedButton;
    BackEndFunc backEndFunc;
    boolean imageSelected=false;
    Person person;
    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backEndFunc= FactoryMethod.getBackEndFunc(DataSourceType.DATA_INTERNET);
        //allow taking image from phone
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //set view
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_started);

        //sets firebase context
        Firebase.setAndroidContext(this);
        final Map<String,Person> people=new HashMap<>();
        nameText=findViewById(R.id.nameText);
        ageSpinner=findViewById(R.id.ageSpinner);
        femaleRadio=findViewById(R.id.femaleRadio);
        maleRadio=findViewById(R.id.maleRadio);
        getStartedButton=findViewById(R.id.getStartedButton);
        imageView=findViewById(R.id.imageView);

        person = new Person();


        Integer[] items = new Integer[81];
        for(int i=0;i<items.length;i++)
        {
            items[i]=i+18;
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,R.layout.age_spinner, items);
        ageSpinner.setAdapter(adapter);
        ageSpinner.setSelection(0);




        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                person.setName(nameText.getText().toString());
                Me.ME.setName(nameText.getText().toString());
                Me.ME.setAge((Integer) ageSpinner.getSelectedItem());
                Gender gender=null;
                if(femaleRadio.isChecked())gender=Gender.FEMALE;
                else if(maleRadio.isChecked())gender=Gender.MALE;
                Me.ME.setGender(gender);
                if(Me.ME.getName()==null || Me.ME.getName().length()==0)inputWarningDialog(getString(R.string.must_enter_name));
                else if(Me.ME.getAge()==0)inputWarningDialog(getString(R.string.must_enter_age));
                else if(Me.ME.getGender()==null)inputWarningDialog(getString(R.string.must_enter_gender));
                else if(!imageSelected)inputWarningDialog(getString(R.string.must_enter_image));
                else
                {
                    try {
                        backEndFunc.addPerson(Me.ME,mBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent =new Intent(GetStartedActivity.this,MainActivity.class);
                    SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(GetStartedActivity.this);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("ID",Me.ME.get_id());
                    editor.putString("NAME",Me.ME.getName());
                    editor.putString("GENDER", String.valueOf(Me.ME.getGender()));
                    editor.putInt("AGE",Me.ME.getAge());
                    editor.putString("IMAGEURL",Me.ME.getImageUrl());
                    editor.putBoolean("ATEVENT",Me.ME.isAtEvent());
                    editor.putString("EVENTID",Me.ME.getEventId());
                    editor.putString("KASHUR",Me.ME.getKashur());
                    editor.commit();
                    intent.putExtra("ID",Me.ME.get_id());
                    finish();
                    startActivity(intent);


                }
            }
        });



        //******************************************** image functions*********************************************************


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }
    public void inputWarningDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(GetStartedActivity.this);
        builder.setTitle(R.string.invalid_input);
        builder.setMessage(message);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChosenTask.equals(getString(R.string.take_photo)))
                        cameraIntent();
                    else if(userChosenTask.equals(getString(R.string.choose_from_library)))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_library)};

        AlertDialog.Builder builder = new AlertDialog.Builder(GetStartedActivity.this);
        builder.setTitle(R.string.add_photo);



        int checkedItem = 0; // cow
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
            }
        });
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean result= Utility.checkPermission(GetStartedActivity.this);
                ListView lw = ((AlertDialog) dialog).getListView();
                Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                if (checkedItem == getString(R.string.take_photo)) {
                    userChosenTask = getString(R.string.take_photo);
                    if(result)
                        cameraIntent();
                }
                else {
                    userChosenTask =getString(R.string.choose_from_library);
                    if(result)
                        galleryIntent();
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        /*builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(GetStartedActivity.this);

                if (items[item].equals(getString(R.string.take_photo))) {
                    userChosenTask =getString(R.string.take_photo);
                    if(result)
                        cameraIntent();

                } else if (items[item].equals(getString(R.string.choose_from_library))) {
                    userChosenTask =getString(R.string.choose_from_library);
                    if(result)
                        galleryIntent();

                } else if (items[item].equals( getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });*/
        builder.show();
    }
    private void galleryIntent()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file=new File(Environment.getExternalStorageDirectory(),"file"+String.valueOf(System.currentTimeMillis())+".jpg");
        uri= Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        intent.putExtra("return-data",true);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE){
                if(data!=null)
                {
                    uri=data.getData();
                    CropImage.activity(uri).setAspectRatio(1,1)
                            .start(this);
                }
            }
            else if (requestCode == REQUEST_CAMERA){
                CropImage.activity(uri).setAspectRatio(1,1)
                        .start(this);
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        imageView.setImageBitmap(mBitmap);
                        imageSelected=true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error = result.getError();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(GetStartedActivity.this);

        builder.setTitle(R.string.exit_check_mate);

        builder.setMessage(R.string.are_you_sure_you_want_to_leave);

        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ExitActivity.exitApplicationAndRemoveFromRecent(GetStartedActivity.this);
                finish();
                System.exit(0);

            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
