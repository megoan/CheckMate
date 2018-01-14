package com.example.user.check_mate.model.backend;

import android.graphics.Bitmap;

import com.example.user.check_mate.model.entities.Gender;
import com.example.user.check_mate.model.entities.MyLocation;
import com.example.user.check_mate.model.entities.Events;
import com.example.user.check_mate.model.entities.Person;

import java.util.ArrayList;

/**
 * Created by User on 03/12/2017.
 */

public interface BackEndFunc {

    public boolean addPerson(Person person, Bitmap mBitmap) throws Exception;
    public boolean addPerson(Person person) throws Exception;
    public boolean updatePerson(Person person);
    public boolean deletePerson(String userName);
    public Person getPerson(final String id);
    public Person getPerson(int id);

    public boolean addEvent(Events events) throws Exception;
    public boolean updateEvent(Events events);
    public boolean deleteEvent(String eventName);
    public Events getEvent(String eventName);
    public boolean removePersonFromEvent(String eventID, String personID);
    public boolean addPersonToEvent(String eventID, String personID);
    public boolean addPersonToEvent(Events event, Person person);


    public ArrayList<Person> getAllPeople();
    public ArrayList<Person> getAllPeopleByGender(Gender gender);

    public ArrayList<Events> getAllEvents();
    public ArrayList<Events> getAllEventsByLocationDistance(MyLocation myLocation, double distance, ArrayList<Events> mEvents);
    public double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b);
}
