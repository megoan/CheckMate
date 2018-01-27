package com.hionthefly.user.check_mate.model.backend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;

import com.hionthefly.user.check_mate.model.datasource.ListDataSource;
import com.hionthefly.user.check_mate.model.entities.Gender;
import com.hionthefly.user.check_mate.model.entities.Message;
import com.hionthefly.user.check_mate.model.entities.MyLocation;
import com.hionthefly.user.check_mate.model.entities.Events;
import com.hionthefly.user.check_mate.model.entities.Person;

import java.util.ArrayList;

/**
 * Created by User on 03/12/2017.
 */

public class BackEndForList implements BackEndFunc {
    private static int eventID = 0;
    private static int personID = 0;

    public BackEndForList() {

        try {
           // initializeLists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeLists() throws Exception {
        /*Person person = new Person("shmuel", 26, Gender.MALE, null, "about me", "kashur","fdgsdfg",true,"fdsgdfvg");
        // person.set_id(1);
        addPerson(person);


        Person person2 = new Person("blala", 26, Gender.MALE, null, "about me", "bride","fdgsdfg",true,"fdsgdfvg");
        // person2.set_id(2);
        addPerson(person2);

        Person person3 = new Person("avi", 39, Gender.FEMALE, null, "im cool", "kashur","fdgsdfg",true,"fdsgdfvg");
        // person3.set_id(3);
        addPerson(person3);

        Person person4 = new Person("yitzi", 26, Gender.MALE, null, "im not", "bride","fdgsdfg",true,"fdsgdfvg");
        //person4.set_id(4);
        addPerson(person4);

        Person person5 = new Person("david", 50, Gender.FEMALE, null, "about me", "groom","fdgsdfg",true,"fdsgdfvg");
        // person5.set_id(5);
        addPerson(person5);

        Person person6 = new Person("esther", 58, Gender.FEMALE, null, "lalalalala", "groom","fdgsdfg",true,"fdsgdfvg");
        // person6.set_id(6);
        addPerson(person6);


        Events events = new Events("hatziporen wedding 1", new MyLocation(31.736499, 34.976910));
        ArrayList<String> eventP1 = new ArrayList<>();
        eventP1.add("1");
        eventP1.add("2");
        eventP1.add("3");
        events.setPeopleAtevent(eventP1);
        addEvent(events);


        Events events2 = new Events("hatziporen wedding 2", new MyLocation(31.736499, 34.976910));
        ArrayList<String> eventP2 = new ArrayList<>();
        eventP2.add("2");
        eventP2.add("3");
        eventP2.add("4");
        events2.setPeopleAtevent(eventP2);
        addEvent(events2);

        Events events3 = new Events("hatziporen wedding 3", new MyLocation(31.736499, 34.976910));
        ArrayList<String> eventP3 = new ArrayList<>();
        eventP3.add("2");
        eventP3.add("2");
        eventP3.add("5");
        events3.setPeopleAtevent(eventP3);
        addEvent(events3);

        Events events4 = new Events("hatziporen wedding 4", new MyLocation(31.736499, 34.976910));
        ArrayList<String> eventP4 = new ArrayList<>();
        eventP4.add("1");
        eventP4.add("2");
        eventP4.add("3");
        eventP4.add("4");
        eventP4.add("5");
        eventP4.add("6");
        events4.setPeopleAtevent(eventP4);
        addEvent(events4);*/
    }

    @Override
    public boolean addPerson(Person person, Bitmap mBitmap, android.content.Context context) throws Exception {
        return false;
    }

    @Override
    public boolean addPerson(Person person) throws Exception {

        personID++;
        person.set_id(String.valueOf(personID));
        ListDataSource.personList.add(new Person(person));
        return true;
    }

    @Override
    public boolean updatePerson(Person person, android.content.Context context, boolean finishActivity) {
        return false;
    }



    @Override
    public boolean deletePerson(String userName, android.content.Context context) {
        return false;
    }



    @Override
    public Person getPerson(String userName) {
        for (Person person1 : ListDataSource.personList) {
            if (userName.equals(person1.get_id())) {
                return person1;
            }
        }
        return null;
    }

    @Override
    public Person getPerson(int id) {
        for (Person person1 : ListDataSource.personList) {
            if (person1.get_id().equals("dd")) {
                return person1;
            }
        }
        return null;
    }

    @Override
    public boolean addEvent(Events events) throws Exception {
        /*for(Events events1 : ListDataSource.eventsList)
        {
            if(events.getEventName().equals(events1.getEventName()))
            {
                throw new Exception("this event name already exists!");
            }
        }*/

        ListDataSource.eventsList.add(new Events(events));
        return true;
    }

    @Override
    public boolean updateEvent(Events events) {
        for (int i = 0; i < ListDataSource.eventsList.size(); i++) {
            if (ListDataSource.eventsList.get(i).getEventName().equals(events.getEventName())) {
                ListDataSource.eventsList.set(i, events);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteEvent(String eventName) {
        Events eventsTmp = null;
        for (Events events : ListDataSource.eventsList
                ) {
            if (events.getEventName().equals(eventName)) {
                eventsTmp = events;
                break;
            }
        }
        return ListDataSource.eventsList.remove(eventsTmp);
    }

    @Override
    public Events getEvent(String eventName) {
        for (Events events1 : ListDataSource.eventsList) {
            if (eventName.equals(events1.getEventName())) {
                return events1;
            }
        }
        return null;
    }

    @Override
    public boolean removePersonFromEvent(Person person, android.content.Context context, boolean finishActivity) {
        return false;
    }


    @Override
    public boolean addPersonToEvent(String eventID, String personID) {
        for (Events events : ListDataSource.eventsList) {
            if (events.getFirebaseID().equals(eventID)) {
            events.addPersonToEvent(personID);
            updateEvent(events);
            return true;}
        }
        return false;
    }

    @Override
    public boolean addPersonToEvent(Events event, Person person) {
        return false;
    }

    @Override
    public boolean addPersonToEvent(String eventID, Person person) {
        return false;
    }

    @Override
    public boolean removeAndAddPersonToEvent(String eventID, Person person, android.content.Context context) {
        return false;
    }

    @Override
    public boolean removeAndAddPersonToEventAndEnterActivity(Person person, Events events, android.content.Context context, Intent intent, ProgressDialog progressDialog) {
        return false;
    }

    @Override
    public boolean addPersonToEventAndEnterActivity(Events events, Person person, android.content.Context context, Intent intent, ProgressDialog progressDialog) {
        return false;
    }


    @Override
    public boolean sendMessage(Message message, String title) {
        return false;
    }

    @Override
    public ArrayList<Person> getAllPeople() {
        return (ArrayList<Person>) ListDataSource.personList;
    }

    @Override
    public ArrayList<Person> getAllPeopleByGender(Gender gender) {
        ArrayList<Person> tmp = new ArrayList<>();
        for (Person person : ListDataSource.personList) {
            if (person.getGender() == gender) {
                tmp.add(person);
            }
        }
        return tmp;
    }

    @Override
    public ArrayList<Events> getAllEvents() {
        return (ArrayList<Events>) ListDataSource.eventsList;
    }

    @Override
    public ArrayList<Events> getAllEventsByLocationDistance(MyLocation myLocation, double distance, ArrayList<Events> mEvents) {
        ListDataSource.personList.add(new Person());
        ArrayList<Events> events = new ArrayList<>();
        for (Events event : ListDataSource.eventsList) {
            double locationDistance = meterDistanceBetweenPoints(myLocation.getLatitude(), myLocation.getLongitude(), event.getmMyLocation().getLatitude(), event.getmMyLocation().getLongitude());
            if (locationDistance < distance) {
                events.add(event);
            }
        }
        return events;
    }

    @Override
    public Events getEventByLocation(MyLocation myLocation, double distance, Events mEvent) {
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
