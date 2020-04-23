package com.codebee.v2.memennit.Util;

import android.app.Activity;
import android.app.Application;

public class UserApi extends Application {
    private  String UserID;
    private String Email;
    private String Name;
    private String FName;
    private String LName;
    private String DPurl;
    private String Bio;
    private String Username;
    private String Streak;
    private String MaxStreak;
    private String Level;
    private String XP;
    private String Title;
    private String Rank;
    private String LastPost;


    private static  UserApi instance;

    public static UserApi getInstance(){
        if(instance == null)
            instance = new UserApi();
        return  instance;
    }

    public UserApi() {
    }

    public String getUserID() {
        return UserID;
    }

    public String getLastPost() {
        return LastPost;
    }

    public void setLastPost(String lastPost) {
        LastPost = lastPost;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getDPurl() {
        return DPurl;
    }

    public void setDPurl(String DPurl) {
        this.DPurl = DPurl;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getStreak() {
        return Streak;
    }

    public void setStreak(String streak) {
        Streak = streak;
    }

    public String getMaxStreak() {
        return MaxStreak;
    }

    public void setMaxStreak(String maxStreak) {
        MaxStreak = maxStreak;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getXP() {
        return XP;
    }

    public void setXP(String XP) {
        this.XP = XP;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getRank() {
        return Rank;
    }

    public void setRank(String rank) {
        Rank = rank;
    }
}
