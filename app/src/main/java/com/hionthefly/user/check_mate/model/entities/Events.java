package com.hionthefly.user.check_mate.model.entities;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by User on 03/12/2017.
 */

public class Events {
    String eventName;
    private MyDate mDate;
    private MyLocation mMyLocation;
    ArrayList<String> peopleAtevent;
    Map<String,Person> personMap;
    private String firebaseID;


    public Events() {
        peopleAtevent=new ArrayList<>();
    }

    public Events(String eventName, MyDate mDate, MyLocation mMyLocation, ArrayList<String> peopleAtevent) {
        this.mDate = mDate;
        this.mMyLocation = mMyLocation;
        this.peopleAtevent = peopleAtevent;
        this.eventName=eventName;
    }

    public Events(Events other) {
        this.mDate = other.mDate;
        this.mMyLocation = other.mMyLocation;
        this.peopleAtevent = other.peopleAtevent;
        this.eventName=other.eventName;
    }

    public Events(String eventName, MyLocation mMyLocation) {
        this.eventName = eventName;
        this.mMyLocation = mMyLocation;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public MyDate getmDate() {
        return mDate;
    }

    public void setmDate(MyDate mDate) {
        this.mDate = mDate;
    }

    public MyLocation getmMyLocation() {
        return mMyLocation;
    }

    public void setmMyLocation(MyLocation mMyLocation) {
        this.mMyLocation = mMyLocation;
    }

    public ArrayList<String> getPeopleAtevent() {
        return peopleAtevent;
    }

    public void setPeopleAtevent(ArrayList<String> peopleAtevent) {
        this.peopleAtevent = peopleAtevent;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public Map<String, Person> getPersonMap() {
        return personMap;
    }

    public void setPersonMap(Map<String, Person> personMap) {
        this.personMap = personMap;
    }

    public void addPersonToEvent(String id){
        peopleAtevent.add(id);
    }
    public void removePersonFromEvent(String id)
    {
        peopleAtevent.remove(id);
    }
}
