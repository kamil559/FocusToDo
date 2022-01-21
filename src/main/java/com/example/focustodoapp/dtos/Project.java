package com.example.focustodoapp.dtos;


public class Project {
    private int id;
    private String name;
    private String createdAt;
    private Integer user;
    private Integer tasksCount;
    private Integer tasksDone;

    public Project(int id, String name, Integer user, String createdAt) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Project(Integer id, String name, Integer user, String createdAt, Integer tasksCount, Integer tasksDone) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.createdAt = createdAt;
        this.tasksCount = tasksCount;
        this.tasksDone = tasksDone;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getCreatedAt() { return createdAt; }
    public Integer getUser() { return user; }
    public Integer getTasksCount() { return tasksCount; }
    public Integer getTasksDone() { return tasksDone; }
}