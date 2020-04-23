package com.codebee.v2.memennit.Model;

public class Title {
    private String Name;
    private String Description;
    private String Status;

    public Title() {
    }

    public Title(String name, String description, String status) {
        Name = name;
        Description = description;
        Status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
}
