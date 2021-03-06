package com.hionthefly.user.check_mate.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hionthefly.user.check_mate.R;
import com.hionthefly.user.check_mate.model.datasource.ListDataSource;
import com.hionthefly.user.check_mate.model.entities.Gender;
import com.hionthefly.user.check_mate.model.entities.Person;
import com.firebase.client.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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
            loadFavorites();
            Me.ME.set_id(sharedPreferences.getString("ID",null));
            Me.ME.setName(sharedPreferences.getString("NAME",null));
            Me.ME.setAge(sharedPreferences.getInt("AGE",0));
            Me.ME.setGender(Gender.valueOf(sharedPreferences.getString("GENDER",null)));
            Me.ME.setImageUrl(sharedPreferences.getString("IMAGEURL",null));
            Me.ME.setAtEvent(sharedPreferences.getBoolean("ATEVENT",false));
            Me.ME.setEventId(sharedPreferences.getString("EVENTID",null));
            Me.ME.setKashur(sharedPreferences.getString("KASHUR",null));
            Me.ME.setEventCity(sharedPreferences.getString("EVENTCITY",null));
            Me.ME.setEventCountry(sharedPreferences.getString("EVENTCOUNTRY",null));

            Intent intent =new Intent(SplashActivity.this,MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id",sharedPreferences.getString("ID",null));
            startActivity(intent);
            finish();
            //loadPerson(sharedPreferences.getString("ID",null));


        }
        else {
            Intent intent=new Intent(SplashActivity.this,GetStartedActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();
        }
    }

    private void loadFavorites() {
        FileInputStream fis = null;
        try {
            fis = this.openFileInput("favorite.txt");

        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            convertStringToArrayList(sb.toString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void convertStringToArrayList(String string) {

        String[] people=string.split("~\\^~\\^~\\^~");
        for(int i=0;i<people.length;i++)
        {
            Person person=new Person();
            String[] p=people[i].split("~\\^~\\^~");
            person.setName(p[1]);
            person.setAge(Integer.parseInt(p[2]));
            person.setKashur(p[3]);
            person.setGender(Gender.valueOf(p[4]));
            person.set_id(p[5]);
            person.setImageUrl(p[6]);
            Me.favorites.add(person);
            Me.events.add(p[7]);
           /* String favorite="favorite_"+favoriteNumber+1+"\n";
            favorite+=me.getName()+"\n";
            favorite+=me.getAge()+"\n";
            favorite+=me.getKashur()+"\n";
            favorite+=me.getGender()+"\n";
            favorite+=me.get_id()+"\n";
            favorite+=eventName+"~~\n";*/
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
