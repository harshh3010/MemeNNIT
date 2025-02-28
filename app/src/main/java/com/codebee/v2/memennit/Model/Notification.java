package com.codebee.v2.memennit.Model;

public class Notification {
    private String Id;
    private String Title;
    private String Content;
    private String RecieverUsername;
    private String Type;
    private String Time;
    private String PostId;
    private String PostUsername;

    public Notification() {
    }

    public Notification(String title, String content, String recieverUsername, String type, String time,String postId,String id,String postUsername) {
        Title = title;
        Content = content;
        RecieverUsername = recieverUsername;
        Type = type;
        Time = time;
        PostId = postId;
        Id = id;
        PostUsername = postUsername;
    }

    public String getPostUsername() {
        return PostUsername;
    }

    public void setPostUsername(String postUsername) {
        PostUsername = postUsername;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getRecieverUsername() {
        return RecieverUsername;
    }

    public void setRecieverUsername(String recieverUsername) {
        RecieverUsername = recieverUsername;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
