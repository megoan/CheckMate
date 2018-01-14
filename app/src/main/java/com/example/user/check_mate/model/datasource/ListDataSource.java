package com.example.user.check_mate.model.datasource;

import com.example.user.check_mate.model.entities.Gender;
import com.example.user.check_mate.model.entities.MyLocation;
import com.example.user.check_mate.model.entities.Events;
import com.example.user.check_mate.model.entities.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 03/12/2017.
 */

public class ListDataSource {
    public static List<Person> personList;
    public static List<Events> eventsList;

    public ListDataSource() {
        personList=new ArrayList<>();
        eventsList =new ArrayList<>();
        //initialize();
    }

    private void initialize() {
        Person person=new Person("shmuel",26, Gender.MALE,"sdfsdf","about me","kashur","fdgsdfg",true,"fdsgdfvg");
        person.set_id(String.valueOf(1));
        personList.add(person);

        Person person2=new Person("blala",26, Gender.MALE,null,"about me","bride","fdgsdfg",true,"fdsgdfvg");
        person2.set_id(String.valueOf(2));
        personList.add(person2);

        Person person3=new Person("avi",39, Gender.FEMALE,null,"im cool","kashur","fdgsdfg",true,"fdsgdfvg");
        person3.set_id(String.valueOf(3));
        personList.add(person3);

        Person person4=new Person("yitzi",26, Gender.MALE,null,"im not","bride","fdgsdfg",true,"fdsgdfvg");
        person4.set_id(String.valueOf(4));
        personList.add(person4);

        Person person5=new Person("david",50, Gender.FEMALE,null,"about me","groom","fdgsdfg",true,"fdsgdfvg");
        person5.set_id(String.valueOf(5));
        personList.add(person5);

        Person person6=new Person("esther",58, Gender.FEMALE,null,"lalalalala","groom","fdgsdfg",true,"fdsgdfvg");
        person6.set_id(String.valueOf(6));
        personList.add(person6);

        Events events=new Events("hatziporen wedding 1",new MyLocation(31.736499,34.976910));
        ArrayList<String>eventP1=new ArrayList<>();
        eventP1.add(String.valueOf(1));
        eventP1.add(String.valueOf(2));
        eventP1.add(String.valueOf(3));
        events.setPeopleAtevent(eventP1);
        eventsList.add(events);

        Events events2=new Events("hatziporen wedding 2",new MyLocation(31.736499,34.976910));
        ArrayList<String>eventP2=new ArrayList<>();
        eventP2.add(String.valueOf(2));
        eventP2.add(String.valueOf(3));
        eventP2.add(String.valueOf(4));
        events2.setPeopleAtevent(eventP2);
        eventsList.add(events2);

        Events events3=new Events("hatziporen wedding 3",new MyLocation(31.736499,34.976910));
        ArrayList<String>eventP3=new ArrayList<>();
        eventP3.add(String.valueOf(2));
        eventP3.add(String.valueOf(2));
        eventP3.add(String.valueOf(5));
        events3.setPeopleAtevent(eventP3);
        eventsList.add(events3);

        Events events4=new Events("hatziporen wedding 4",new MyLocation(31.736499,34.976910));
        ArrayList<String>eventP4=new ArrayList<>();
        eventP4.add(String.valueOf(1));
        eventP4.add(String.valueOf(2));
        eventP4.add(String.valueOf(3));
        eventP4.add(String.valueOf(4));
        eventP4.add(String.valueOf(5));
        eventP4.add(String.valueOf(6));
        events4.setPeopleAtevent(eventP4);
        eventsList.add(events4);

        /*eventsList.add(new Events("hatziporen wedding 1",new MyLocation(31.736499,34.976910)));
        eventsList.add(new Events("hatziporen wedding 2",new MyLocation(31.736493,34.976912)));
        eventsList.add(new Events("hatziporen wedding 3",new MyLocation(31.736495,34.976913)));
        eventsList.add(new Events("hatziporen wedding 4",new MyLocation(31.736491,34.976914)));
        eventsList.add(new Events("hatziporen wedding 5",new MyLocation(31.736498,34.976916)));
        eventsList.add(new Events("bet kneset  shiur 1",new MyLocation(31.736863, 34.980668)));
        eventsList.add(new Events("bet kneset  shiur 2",new MyLocation(31.736863, 34.980668)));
        eventsList.add(new Events("bet kneset  shiur 3",new MyLocation(31.736863, 34.980668)));
        eventsList.add(new Events("bet kneset  shiur 4",new MyLocation(31.736863, 34.980668)));*/
    }
}
