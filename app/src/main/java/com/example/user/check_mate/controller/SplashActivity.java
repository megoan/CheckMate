package com.example.user.check_mate.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.user.check_mate.R;
import com.example.user.check_mate.model.datasource.ListDataSource;
import com.example.user.check_mate.model.entities.Person;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SplashActivity extends AppCompatActivity {
    String ret = "";
    boolean alreadyEntered=false;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Me.ME!=null && Me.ME.getName()!=null)alreadyEntered=true;
        ListDataSource listDataSource=new ListDataSource();
        setContentView(R.layout.activity_splash);
        Firebase.setAndroidContext(this);
        loadSharedPreference();
    }
    private void loadSharedPreference()
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.contains("ID"))
        {
            if(sharedPreferences.contains("favorite_1"))
            {
                loadFavorites();
            }
            Intent intent =new Intent(SplashActivity.this,MainActivity.class);
            intent.putExtra("id",sharedPreferences.getString("ID",null));
            //loadPerson(sharedPreferences.getString("ID",null));
            startActivity(intent);
            finish();
        }
        else {
            Intent intent=new Intent(SplashActivity.this,GetStartedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadFavorites() {
        SharedPreferences mSharedPreference1 =   PreferenceManager.getDefaultSharedPreferences(this);
        Me.favoriteID.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);
        for(int i=0;i<size;i++)
        {
            Me.favoriteID.add(mSharedPreference1.getString("favorite_" + i, null));
        }
    }

    /*private void loadPerson(final String id) {
        DatabaseReference persontAtEvent= FirebaseDatabase.getInstance().getReference("people");
        persontAtEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot item : dataSnapshot.getChildren()) {
                    if(item.getKey().equals(id)){
                    Me.ME = item.getValue(Person.class);}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }*/

}
