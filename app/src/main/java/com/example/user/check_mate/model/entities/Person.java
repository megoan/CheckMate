package com.example.user.check_mate.model.entities;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by User on 29/12/2017.
 */

public class Person {
    private String name;
    private int age;
    private Gender gender;
    private String imageUrl;
    private String aboutMe;
    private String kashur;
    private String eventId;
    private String eventCountry;
    private String eventCity;
    private boolean atEvent;
    private String _id;

    public Person() {
    }

    public Person(String name, int age, Gender gender, String imageUrl, String aboutMe, String kashur, String eventId, String eventCountry, String eventCity, boolean atEvent, String _id) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.aboutMe = aboutMe;
        this.kashur = kashur;
        this.eventId = eventId;
        this.eventCountry = eventCountry;
        this.eventCity = eventCity;
        this.atEvent = atEvent;
        this._id = _id;
    }

    public Person(Person other) {
        this.name = other.name;
        this.age = other.age;
        this.gender = other.gender;
        this.imageUrl = other.imageUrl;
        this.aboutMe = other.aboutMe;
        this.kashur = other.kashur;
        this.eventId = other.eventId;
        this.eventCountry = other.eventCountry;
        this.eventCity = other.eventCity;
        this.atEvent = other.atEvent;
        this._id = other._id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getKashur() {
        return kashur;
    }

    public void setKashur(String kashur) {
        this.kashur = kashur;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventCountry() {
        return eventCountry;
    }

    public void setEventCountry(String eventCountry) {
        this.eventCountry = eventCountry;
    }

    public String getEventCity() {
        return eventCity;
    }

    public void setEventCity(String eventCity) {
        this.eventCity = eventCity;
    }

    public boolean isAtEvent() {
        return atEvent;
    }

    public void setAtEvent(boolean atEvent) {
        this.atEvent = atEvent;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
