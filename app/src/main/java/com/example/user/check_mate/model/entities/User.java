package com.example.user.check_mate.model.entities;

/**
 * Created by User on 03/12/2017.
 */

public abstract class User {
    private String userName;
    private String password;
    private String emailAddress;


    public User() {
    }

    public User(String userName, String password, String emailAddress) {
        this.userName = userName;
        this.password = password;
        this.emailAddress = emailAddress;
    }

    public User(User other) {
        this.userName = other.userName;
        this.password = other.password;
        this.emailAddress = other.emailAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
