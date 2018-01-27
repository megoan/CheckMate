package com.hionthefly.user.check_mate.model.backend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.hionthefly.user.check_mate.R;
import com.hionthefly.user.check_mate.controller.ExitActivity;
import com.hionthefly.user.check_mate.controller.MainActivity;
import com.hionthefly.user.check_mate.controller.Me;
import com.hionthefly.user.check_mate.model.entities.Events;
import com.hionthefly.user.check_mate.model.entities.Gender;
import com.hionthefly.user.check_mate.model.entities.Message;
import com.hionthefly.user.check_mate.model.entities.MyLocation;
import com.hionthefly.user.check_mate.model.entities.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    public boolean addPerson(Person person, Bitmap mBitmap, final android.content.Context context) throws Exception {
        ProgressDialog progDailog = new ProgressDialog(context);
        progDailog.setMessage(context.getString(R.string.gettingStarted));
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);
        progDailog.show();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        //get reference to storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userRef = ref.child("people");
        postId = userRef.push().getKey();
        Me.ME.set_id(postId);
        mBitmap = getResizedBitmap(mBitmap, 200, 200);
        StorageReference ref = storageReference.child("images/" + postId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String url = downloadUrl.toString();
                Me.ME.setImageUrl(url);
                Person person = new Person(Me.ME.getName(), Me.ME.getAge(), Me.ME.getGender(), Me.ME.getImageUrl(), Me.ME.getAboutMe(), Me.ME.getKashur(), Me.ME.getEventCountry(), Me.ME.getEventCity(), Me.ME.getEventId(), Me.ME.isAtEvent(), postId);
                userRef.child(postId).setValue(person);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ID", Me.ME.get_id());
                editor.putString("NAME", Me.ME.getName());
                editor.putString("GENDER", String.valueOf(Me.ME.getGender()));
                editor.putInt("AGE", Me.ME.getAge());
                editor.putString("IMAGEURL", Me.ME.getImageUrl());
                editor.putBoolean("ATEVENT", Me.ME.isAtEvent());
                editor.putString("EVENTID", Me.ME.getEventId());
                editor.putString("KASHUR", Me.ME.getKashur());
                editor.commit();
                Intent intent = new Intent(context, MainActivity.class);

                intent.putExtra("ID", Me.ME.get_id());
                ((Activity) context).finish();
                ((Activity) context).startActivity(intent);
            }
        });
        return false;
    }

    @Override
    public boolean addPerson(Person person) throws Exception {
        return false;
    }

    @Override
    public boolean updatePerson(Person person, final android.content.Context context, final boolean finishActivity) {
        final ProgressDialog progDailog = new ProgressDialog(context);
        progDailog.setMessage(context.getString(R.string.updating));
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);
        progDailog.show();
        ref = database.getReference().child("people").child(person.get_id());
        Map newUserData = new HashMap();
        newUserData.put("age", person.getAge());
        newUserData.put("atEvent", person.isAtEvent());
        newUserData.put("gender", person.getGender());
        newUserData.put("imageUrl", person.getImageUrl());
        newUserData.put("name", person.getName());
        newUserData.put("kashur", person.getKashur());
        newUserData.put("eventId", person.getEventId());
        newUserData.put("eventCountry", person.getEventCountry());
        newUserData.put("eventCity", person.getEventCity());
        ref.updateChildren(newUserData).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Me.ME.setAtEvent(false);
                Me.ME.setEventId("");
                Me.ME.setEventCountry("");
                Me.ME.setEventCity("");

                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("ID",Me.ME.get_id());
                editor.putString("NAME",Me.ME.getName());
                editor.putString("GENDER", String.valueOf(Me.ME.getGender()));
                editor.putInt("AGE",Me.ME.getAge());
                editor.putString("IMAGEURL",Me.ME.getImageUrl());
                editor.putBoolean("ATEVENT",Me.ME.isAtEvent());
                editor.putString("EVENTID",Me.ME.getEventId());
                editor.putString("KASHUR",Me.ME.getKashur());
                editor.putString("EVENTCOUNTRY",Me.ME.getEventCountry());
                editor.putString("EVENTCITY",Me.ME.getEventCity());
                editor.commit();
                if (finishActivity) {
                    progDailog.dismiss();
                    ((Activity) context).finish();
                } else {
                    progDailog.dismiss();
                }
            }
        });
        return false;
    }

    @Override
    public boolean deletePerson(final String userName, final android.content.Context context) {
        ProgressDialog progDailog = new ProgressDialog(context);
        progDailog.setMessage(context.getString(R.string.deleting));
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);
        progDailog.show();

        database = FirebaseDatabase.getInstance();
        if (Me.ME.isAtEvent()) {
           userRef= database.getReference().child("peopleAtEvent").child(Me.ME.getEventCountry()).child(Me.ME.getEventCity()).child(Me.ME.getEventId()).child(Me.ME.get_id());
            userRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();
                    ref = database.getReference();
                    userRef = ref.child("people").child(userName);
                    userRef.removeValue();
                    StorageReference ref = storageReference.child("images/" + userName);
                    ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ExitActivity.exitApplicationAndRemoveFromRecent(context);
                            ((Activity) context).finish();
                            System.exit(0);
                        }
                    });
                }
            });
        }


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        ref = database.getReference();
        userRef = ref.child("people").child(userName);
        userRef.removeValue();
        StorageReference ref = storageReference.child("images/" + userName);
        ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ExitActivity.exitApplicationAndRemoveFromRecent(context);
                ((Activity) context).finish();
                System.exit(0);
            }
        });


        return false;
    }

    @Override
    public Person getPerson(final String id) {
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
        userRef = ref.child("events").child(events.getmMyLocation().getCountry()).child(events.getmMyLocation().getCity());
        postId = userRef.push().getKey();
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
    public boolean removePersonFromEvent(final Person person, final android.content.Context context, final boolean finishActivity) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("peopleAtEvent").child(person.getEventCountry()).child(person.getEventCity()).child(person.getEventId()).child(person.get_id());
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updatePerson(person, context, finishActivity);
            }
        });
        return false;
    }

    @Override
    public boolean addPersonToEvent(String eventID, String personID) {

        return false;
    }

    @Override
    public boolean addPersonToEvent(Events event, Person person) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("peopleAtEvent").child(event.getmMyLocation().getCountry()).child(event.getmMyLocation().getCity()).child(event.getFirebaseID()).child(person.get_id());
        ref.setValue(person);
        return false;
    }

    @Override
    public boolean addPersonToEvent(String eventID, Person person) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("peopleAtEvent").child(person.getEventCountry()).child(person.getEventCity()).child(eventID).child(person.get_id());
        ref.setValue(person);
        return false;
    }

    @Override
    public boolean removeAndAddPersonToEvent(String eventID, final Person person, final android.content.Context context) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("peopleAtEvent").child(person.getEventCountry()).child(person.getEventCity()).child(eventID).child(person.get_id());
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ref.setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ((Activity) context).finish();
                    }
                });
            }
        });

        return false;
    }

    @Override
    public boolean removeAndAddPersonToEventAndEnterActivity(final Person person, final Events events, final android.content.Context context, final Intent intent, final ProgressDialog progressDialog) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("peopleAtEvent").child(person.getEventCountry()).child(person.getEventCity()).child(person.getEventId()).child(person.get_id());
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ref = database.getReference().child("peopleAtEvent").child(events.getmMyLocation().getCountry()).child(events.getmMyLocation().getCity()).child(events.getFirebaseID()).child(person.get_id());
                ref.setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ref = database.getReference().child("people").child(person.get_id());
                        Map newUserData = new HashMap();
                        newUserData.put("age", person.getAge());
                        newUserData.put("atEvent", person.isAtEvent());
                        newUserData.put("gender", person.getGender());
                        newUserData.put("imageUrl", person.getImageUrl());
                        newUserData.put("name", person.getName());
                        newUserData.put("kashur", person.getKashur());
                        newUserData.put("eventId", person.getEventId());
                        newUserData.put("eventCountry", person.getEventCountry());
                        newUserData.put("eventCity", person.getEventCity());
                        ref.updateChildren(newUserData).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                progressDialog.dismiss();
                                Me.ME.setAtEvent(true);
                                Me.ME.setEventCountry(events.getmMyLocation().getCountry());
                                Me.ME.setEventCity(events.getmMyLocation().getCity());
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("ID", Me.ME.get_id());
                                editor.putString("NAME", Me.ME.getName());
                                editor.putString("GENDER", String.valueOf(Me.ME.getGender()));
                                editor.putInt("AGE", Me.ME.getAge());
                                editor.putString("IMAGEURL", Me.ME.getImageUrl());
                                editor.putBoolean("ATEVENT", Me.ME.isAtEvent());
                                editor.putString("EVENTID", Me.ME.getEventId());
                                editor.putString("KASHUR", Me.ME.getKashur());
                                editor.putString("EVENTCOUNTRY",Me.ME.getEventCountry());
                                editor.putString("EVENTCITY",Me.ME.getEventCity());
                                editor.commit();
                                ((Activity) context).startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
        return true;
    }

    @Override
    public boolean addPersonToEventAndEnterActivity(final Events events, final Person person, final android.content.Context context, final Intent intent, final ProgressDialog progressDialog) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("peopleAtEvent").child(events.getmMyLocation().getCountry()).child(events.getmMyLocation().getCity()).child(events.getFirebaseID()).child(person.get_id());
        ref.setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ref = database.getReference().child("people").child(person.get_id());
                Map newUserData = new HashMap();
                newUserData.put("age", person.getAge());
                newUserData.put("atEvent", person.isAtEvent());
                newUserData.put("gender", person.getGender());
                newUserData.put("imageUrl", person.getImageUrl());
                newUserData.put("name", person.getName());
                newUserData.put("kashur", person.getKashur());
                newUserData.put("eventId", person.getEventId());
                newUserData.put("eventCountry", person.getEventCountry());
                newUserData.put("eventCity", person.getEventCity());
                ref.updateChildren(newUserData).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        progressDialog.dismiss();
                        Me.ME.setAtEvent(true);
                        Me.ME.setEventCountry(events.getmMyLocation().getCountry());
                        Me.ME.setEventCity(events.getmMyLocation().getCity());
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ID", Me.ME.get_id());
                        editor.putString("NAME", Me.ME.getName());
                        editor.putString("GENDER", String.valueOf(Me.ME.getGender()));
                        editor.putInt("AGE", Me.ME.getAge());
                        editor.putString("IMAGEURL", Me.ME.getImageUrl());
                        editor.putBoolean("ATEVENT", Me.ME.isAtEvent());
                        editor.putString("EVENTID", Me.ME.getEventId());
                        editor.putString("KASHUR", Me.ME.getKashur());
                        editor.putString("EVENTCOUNTRY",Me.ME.getEventCountry());
                        editor.putString("EVENTCITY",Me.ME.getEventCity());
                        editor.commit();
                        ((Activity) context).startActivity(intent);
                    }
                });
            }
        });

        return false;
    }

    @Override
    public boolean sendMessage(Message message, String title) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("messages").child(title);
        postId = ref.push().getKey();
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

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}
