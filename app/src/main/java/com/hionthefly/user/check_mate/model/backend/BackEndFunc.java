package com.hionthefly.user.check_mate.model.backend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;

import com.hionthefly.user.check_mate.model.entities.Gender;
import com.hionthefly.user.check_mate.model.entities.Message;
import com.hionthefly.user.check_mate.model.entities.MyLocation;
import com.hionthefly.user.check_mate.model.entities.Events;
import com.hionthefly.user.check_mate.model.entities.Person;

import java.util.ArrayList;

/**
 * Created by User on 03/12/2017.
 */

public interface BackEndFunc {

    public boolean addPerson(Person person, Bitmap mBitmap, android.content.Context context) throws Exception;
    public boolean addPerson(Person person) throws Exception;
    public boolean updatePerson(Person person, android.content.Context context, boolean finishActivity);
    public boolean deletePerson(String userName, android.content.Context context);
    public Person getPerson(final String id);
    public Person getPerson(int id);

    public boolean addEvent(Events events) throws Exception;
    public boolean updateEvent(Events events);
    public boolean deleteEvent(String eventName);
    public Events getEvent(String eventName);
    public boolean removePersonFromEvent(Person person, android.content.Context context,boolean finishActivity);
    public boolean addPersonToEvent(String eventID, String personID);
    public boolean addPersonToEvent(Events event, Person person);
    public boolean addPersonToEvent(String eventID, Person person);
    public boolean removeAndAddPersonToEvent(String eventID, Person person, android.content.Context context);
    public boolean removeAndAddPersonToEventAndEnterActivity(Person person, Events events, android.content.Context context, Intent intent, ProgressDialog progressDialog);
    public boolean addPersonToEventAndEnterActivity(Events events, Person person, android.content.Context context,Intent intent,ProgressDialog progressDialog);
    public boolean sendMessage(Message message, String title);

    public ArrayList<Person> getAllPeople();
    public ArrayList<Person> getAllPeopleByGender(Gender gender);

    public ArrayList<Events> getAllEvents();
    public ArrayList<Events> getAllEventsByLocationDistance(MyLocation myLocation, double distance, ArrayList<Events> mEvents);
    public Events getEventByLocation(MyLocation myLocation,double distance, Events mEvent);
    public double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b);
}
