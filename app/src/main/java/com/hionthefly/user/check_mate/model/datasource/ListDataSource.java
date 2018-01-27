package com.hionthefly.user.check_mate.model.datasource;

import com.hionthefly.user.check_mate.model.entities.Events;
import com.hionthefly.user.check_mate.model.entities.Person;

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

}
