package com.example.user.check_mate.model.backend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.user.check_mate.controller.Me;
import com.example.user.check_mate.model.datasource.ListDataSource;
import com.example.user.check_mate.model.entities.Events;
import com.example.user.check_mate.model.entities.Gender;
import com.example.user.check_mate.model.entities.Message;
import com.example.user.check_mate.model.entities.MyLocation;
import com.example.user.check_mate.model.entities.Person;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 12/01/2018.
 */

public class BackendForFirebase implements BackEndFunc {
    DatabaseReference ref;
    FirebaseDatabase database;
    DatabaseReference userRef;
    FirebaseStorage storage;
    StorageReference storageReference;
    String postId;
    Person person;
    @Override
    public boolean addPerson(Person person,Bitmap mBitmap) throws Exception {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        //get reference to storage
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        userRef=ref.child("people");
        postId= userRef.push().getKey();
        Me.ME.set_id(postId);
        StorageReference ref=storageReference.child("images/"+postId);
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
                Person person =new Person(Me.ME.getName(),Me.ME.getAge(),Me.ME.getGender(),Me.ME.getImageUrl(),Me.ME.getAboutMe(),Me.ME.getKashur(),Me.ME.getEventId(),Me.ME.isAtEvent(),postId);
                userRef.child(postId).setValue(person);

            }
        });
        return false;
    }

    @Override
    public boolean addPerson(Person person) throws Exception {
        return false;
    }

    @Override
    public boolean updatePerson(Person person) {
        ref = database.getReference().child("people").child(person.get_id());
        Map newUserData = new HashMap();
        newUserData.put("age", person.getAge());
        newUserData.put("atEvent", person.isAtEvent());
        newUserData.put("gender", person.getGender());
        newUserData.put("imageUrl", person.getImageUrl());
        newUserData.put("name", person.getName());
        newUserData.put("kashur", person.getKashur());
        newUserData.put("eventId", person.getEventId());
        ref.updateChildren(newUserData);
        return false;
    }

    @Override
    public boolean deletePerson(String userName) {

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        userRef=ref.child("people").child(userName);
        userRef.removeValue();
        StorageReference ref=storageReference.child("images/"+userName);
        ref.delete();
        return false;
    }

    @Override
    public Person getPerson(final String id)
    {
        return null;
    }

    @Override
    public Person getPerson(int id) {
        return null;
    }

    @Override
    public boolean addEvent(Events events) throws Exception {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        userRef=ref.child("events").child(events.getmMyLocation().getCountry()).child(events.getmMyLocation().getCity());
        postId= userRef.push().getKey();
        events.setFirebaseID(postId);
        userRef.child(postId).setValue(events);
        return false;
    }

    @Override
    public boolean updateEvent(Events events) {
        return false;
    }

    @Override
    public boolean deleteEvent(String eventName) {
        return false;
    }

    @Override
    public Events getEvent(String eventName) {
        return null;
    }

    @Override
    public boolean removePersonFromEvent(String eventID, String personID) {
        database = FirebaseDatabase.getInstance();
        ref=database.getReference().child("peopleAtEvent").child(eventID).child(personID);
        ref.removeValue();
        return false;
    }

    @Override
    public boolean addPersonToEvent(String eventID, String personID) {

        return false;
    }

    @Override
    public boolean addPersonToEvent(Events event, Person person) {
        database = FirebaseDatabase.getInstance();
        ref=database.getReference().child("peopleAtEvent").child(event.getFirebaseID()).child(person.get_id());
        ref.setValue(person);
        return false;
    }

    @Override
    public boolean addPersonToEvent(String eventID, Person person) {
        database = FirebaseDatabase.getInstance();
        ref=database.getReference().child("peopleAtEvent").child(eventID).child(person.get_id());
        ref.setValue(person);
        return false;
    }

    @Override
    public boolean sendMessage(Message message, String title) {
        database = FirebaseDatabase.getInstance();
        ref=database.getReference().child("messages").child(title);
        postId= ref.push().getKey();
        ref.child(postId).setValue(message);
        return false;
    }


    @Override
    public ArrayList<Person> getAllPeople() {
        return null;
    }

    @Override
    public ArrayList<Person> getAllPeopleByGender(Gender gender) {
        return null;
    }

    @Override
    public ArrayList<Events> getAllEvents() {
        return null;
    }

    @Override
    public ArrayList<Events> getAllEventsByLocationDistance(MyLocation myLocation, double distance, ArrayList<Events> mEvents) {

        ArrayList<Events> events = new ArrayList<>();
        for (Events event : mEvents) {
            double locationDistance = meterDistanceBetweenPoints(myLocation.getLatitude(), myLocation.getLongitude(), event.getmMyLocation().getLatitude(), event.getmMyLocation().getLongitude());
            if (locationDistance < distance) {
                events.add(event);
            }
        }
        return events;
    }

    @Override
    public Events getEventByLocation(MyLocation myLocation, double distance, Events mEvent) {
        double locationDistance = meterDistanceBetweenPoints(myLocation.getLatitude(), myLocation.getLongitude(), mEvent.getmMyLocation().getLatitude(), mEvent.getmMyLocation().getLongitude());
        if (locationDistance < distance) {
            return mEvent;
        }
        return null;
    }

    @Override
    public double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (double) (180.f / Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;

    }


}
