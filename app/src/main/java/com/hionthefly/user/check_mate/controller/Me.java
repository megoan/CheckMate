package com.hionthefly.user.check_mate.controller;

import com.hionthefly.user.check_mate.model.entities.Person;

import java.util.ArrayList;

/**
 * Created by User on 09/12/2017.
 */

public class Me {
    public static Person ME=new Person();
    public static ArrayList<String> favoriteID=new ArrayList<>();
    public static ArrayList<Person> favorites=new ArrayList<>();
    public static ArrayList<String> events=new ArrayList<>();

    public static boolean checkifPersonIsInFavorites(String id){
        for(Person person:favorites){
            if(id.equals(person.get_id()))return true;
        }
        return false;
    }

    public static void removeFromFavorites(String id, String eventName) {
        events.remove(eventName);
        for(Person person: favorites)
        {
            if(id.equals(person.get_id()))
            {
                favorites.remove(person);
                return;
            }
        }
    }
}
