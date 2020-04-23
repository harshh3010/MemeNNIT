package com.codebee.v2.memennit.Util;

import com.codebee.v2.memennit.Model.Achievement;

import java.util.ArrayList;

public class AchievementApi {
    private ArrayList<Achievement> myArr;

    public void setAchievements(){
        myArr.add(new Achievement("1","M for Memer","Upload one meme","Locked","200"));
        myArr.add(new Achievement("2","Shower some love","Like a meme","Locked","200"));
        myArr.add(new Achievement("3","Self-loving","Like your own post","Locked","200"));
    }
}
