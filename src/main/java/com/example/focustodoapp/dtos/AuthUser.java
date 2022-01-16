package com.example.focustodoapp.dtos;

public class AuthUser {
    public Integer id;
    public String username, password;

    public AuthUser(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
