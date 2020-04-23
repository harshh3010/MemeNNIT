package com.codebee.v2.memennit.Model;

import java.io.Serializable;

public class Post implements Serializable {
    private String PostUrl;
    private String Caption;
    private String Upvotes;
    private String Time;
    private String Username;
    private String DpUrl;
    private String Email;

    public Post(){
    }

    public Post(String postUrl, String caption, String upvotes, String time, String username, String dpUrl) {
        PostUrl = postUrl;
        Caption = caption;
        Upvotes = upvotes;
        Time = time;
        Username = username;
        DpUrl = dpUrl;
    }

    public String getDpUrl() {
        return DpUrl;
    }

    public void setDpUrl(String dpUrl) {
        DpUrl = dpUrl;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPostUrl() {
        return PostUrl;
    }

    public void setPostUrl(String postUrl) {
        PostUrl = postUrl;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getUpvotes() {
        return Upvotes;
    }

    public void setUpvotes(String upvotes) {
        Upvotes = upvotes;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public long getTimeInLong(){
        return  Long.parseLong(getTime());
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
