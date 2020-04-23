package com.codebee.v2.memennit.Model;

public class User {
    private String FName;
    private String LName;
    private String Username;
    private String UserId;
    private String DPurl;
    private String Bio;
    private String Email;
    private String Streak;
    private String MaxStreak;
    private String Level;
    private String XP;
    private String Title;
    private String Rank;
    private String LastPost;

    public User(String FName, String LName, String username, String userId, String DPurl, String bio, String email) {
        this.FName = FName;
        this.LName = LName;
        Username = username;
        UserId = userId;
        this.DPurl = DPurl;
        Bio = bio;
        Email = email;
    }

    public User() {
    }

    public String getFName() {
        return FName;
    }

    public String getLastPost() {
        return LastPost;
    }

    public void setLastPost(String lastPost) {
        LastPost = lastPost;
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

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
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
