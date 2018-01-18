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
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.user.check_mate.R;
import com.example.user.check_mate.model.backend.BackEndFunc;
import com.example.user.check_mate.model.backend.FactoryMethod;
import com.example.user.check_mate.model.backend.SelectedDataSource;
import com.example.user.check_mate.model.entities.Gender;
import com.example.user.check_mate.model.entities.Person;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private String userChosenTask;
    File file;
    Uri uri;
    CircleImageView imageView;
    EditText nameText;
    Spinner ageSpinner;
    RadioButton femaleRadio;
    RadioButton maleRadio;
    Intent CropIntent;
    Button getStartedButton;
    BackEndFunc backEndFunc= FactoryMethod.getBackEndFunc(SelectedDataSource.dataSourceType);
    boolean imageSelected=false;
    Bitmap mBitmap;
    StorageReference storageReference;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prifile);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        nameText=findViewById(R.id.nameText);
        ageSpinner=findViewById(R.id.ageSpinner);
        femaleRadio=findViewById(R.id.femaleRadio);
        maleRadio=findViewById(R.id.maleRadio);
        getStartedButton=findViewById(R.id.getStartedButton);
        imageView=findViewById(R.id.imageView);
        getSupportActionBar().setTitle(R.string.update_profile);



        Integer[] items = new Integer[81];
        for(int i=0;i<items.length;i++)
        {
            items[i]=i+18;
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,R.layout.age_spinner, items);
        ageSpinner.setAdapter(adapter);
        ageSpinner.setSelection(3);

        nameText.setText(Me.ME.getName());
        Gender gender=Me.ME.getGender();
        if(gender==Gender.FEMALE){
            femaleRadio.setChecked(true);
        }
        else {
            maleRadio.setChecked(true);
        }
        if(gender==Gender.FEMALE){
            Picasso.with(this)
                    .load(Me.ME.getImageUrl())
                    .fit()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.woman)
                    .into(imageView);
        }
        else {
            Picasso.with(this)
                    .load(Me.ME.getImageUrl())
                    .fit()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.boy)
                    .into(imageView);
        }

        //imageView.setImageBitmap(Me.ME.getImg().getBitmap());

        ageSpinner.setSelection(Me.ME.getAge()-18);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=nameText.getText().toString();
                if (name!=null && name.length()>0) {
                    Me.ME.setName(name);
                }
                Me.ME.setAge((Integer) ageSpinner.getSelectedItem());
                Gender gender=null;
                if(femaleRadio.isChecked())gender=Gender.FEMALE;
                else if(maleRadio.isChecked())gender=Gender.MALE;
                Me.ME.setGender(gender);
                if(name==null || name.length()==0)inputWarningDialog(getString(R.string.must_enter_name));
                else if(Me.ME.getAge()==0)inputWarningDialog(getString(R.string.must_enter_age));
                else if(Me.ME.getGender()==null)inputWarningDialog(getString(R.string.must_enter_gender));
                else if(Me.ME.getImageUrl()==null)inputWarningDialog(getString(R.string.must_enter_image));

                else
                {
                    if (mBitmap!=null) {
                        //Me.ME.setImg(new MyBitmap(mBitmap));
                    }
                    try {

                        updateFireBase();
                        //backEndFunc.updatePerson(Me.ME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    public void updateFireBase()
    {
        if(imageSelected)
        {
            storage=FirebaseStorage.getInstance();
            storageReference=storage.getReference();
            StorageReference ref=storageReference.child("images/"+Me.ME.get_id());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    String url=downloadUrl.toString();
                    Me.ME.setImageUrl(url);
                    Person person =new Person(Me.ME.getName(),Me.ME.getAge(),Me.ME.getGender(),Me.ME.getImageUrl(),Me.ME.getAboutMe(),Me.ME.getKashur(),Me.ME.getEventId(),Me.ME.isAtEvent(),Me.ME.get_id());
                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("people").child(Me.ME.get_id());
                    databaseReference.setValue(person);
                    SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this);
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
                }
            });
        }
        else {
            Person person =new Person(Me.ME.getName(),Me.ME.getAge(),Me.ME.getGender(),Me.ME.getImageUrl(),Me.ME.getAboutMe(),Me.ME.getKashur(),Me.ME.getEventId(),Me.ME.isAtEvent(),Me.ME.get_id());
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("people").child(Me.ME.get_id());
            databaseReference.setValue(person);
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this);
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
        }
    }


    public void inputWarningDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Invalid input!");
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
                    if(userChosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { getString(R.string.take_photo), getString(R.string.choose_from_library),
                getString(R.string.cancel) };

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle(R.string.add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(EditProfileActivity.this);

                if (items[item].equals(getString(R.string.take_photo))) {
                    userChosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals(getString(R.string.choose_from_library))) {
                    userChosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals( getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
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
}
