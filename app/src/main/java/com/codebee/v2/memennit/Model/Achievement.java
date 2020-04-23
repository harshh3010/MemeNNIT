package com.codebee.v2.memennit.Model;

public class Achievement {
    private String Id;
    private String Title;
    private String Description;
    private String Status;
    private String Points;

    public Achievement() {
    }

    public Achievement(String id, String title, String description, String status, String points) {
        Id = id;
        Title = title;
        Description = description;
        Status = status;
        Points = points;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPoints() {
        return Points;
    }

    public void setPoints(String points) {
        Points = points;
    }
}
