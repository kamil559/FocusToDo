package com.example.focustodoapp.dtos;

public class User {
    public Integer id;
    public String username, firstName, lastName;

    public User(Integer id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
