package com.example.focustodoapp.dtos;


public class Project {
    private int id;
    private String name;
    private String createdAt;
    private Integer user;

    public Project(int id, String name, Integer user, String createdAt) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}