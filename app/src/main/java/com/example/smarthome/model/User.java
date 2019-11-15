package com.example.smarthome.model;

import androidx.annotation.Nullable;

/*
* This class declares the Implementation of an User of the App which is gonna be uploaded to Firebase.
* The User includes an Username, Email and Password (is going to be changed later)
* */
public class User {
    private String Email;
    private String Username;
    private String Password;

    /**
     * User Constructor takes Email, Username and Password which all are not allowed to be empty,
     * else it is gonna throw an @IllegalArguementException
     * @param email that the user has
     * @param username that the user declares
     * @param password
     */
    public User(String email, String username, String password) {
        this.Email = email;
        this.Username = username;
        this.Password = password;
    }

    //Default constructor
    public User(){}

    /**
     * Getter and Setters, so the an User can be created with the data of Firebase
     */
    public String getEmail() {
        return Email;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    /**
     * comparing an Object to check later in Firebase if there is already a similar User
     * @param obj Object to compare with
     * @return True if email, username is equal
     * else return false
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if(this.Email.equals(((User) obj).getEmail()) && this.Username.equals(((User)obj).getUsername()) && this.getPassword().equals(((User)obj).getPassword())){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "Email='" + Email + '\'' +
                ", Username='" + Username + '\'' +
                ", Password='" + Password + '\'' +
                '}';
    }
}
