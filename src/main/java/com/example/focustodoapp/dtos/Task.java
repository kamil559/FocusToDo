package com.example.focustodoapp.dtos;

public class Task {
    private int id;
    private String name;
    private Boolean done;
    private String dueDate;
    private String note;
    private String createdAt;
    private Integer project;

    public Task(int id, String name, Boolean done, String dueDate, Integer project, String note, String createdAt) {
        this.id = id;
        this.name = name;
        this.done = done;
        this.dueDate = dueDate;
        this.note = note;
        this.createdAt = createdAt;
        this.project = project;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getDone() {
        return done;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getNote() {
        return note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getProject() {
        return project;
    }
}
