package com.boxfox.dsm_boxfox.Sub;

import org.json.JSONArray;

import io.realm.RealmObject;

public class FeedItem2 extends RealmObject {
    private int num;
    private String title,date;
    private int type;

    public void setType(int a){type=a;}
    public String getTitle() {
        return title;
    }
    public void setNum(int n){
        num=n;
    }
    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setDate(String title) {
        this.date = title;
    }

    public int getNum() {
        return num;
    }

    public int getType() {
        return type;
    }
}