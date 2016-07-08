package com.boxfox.dsm_boxfox.Applications;

import io.realm.RealmObject;

/**
 * Created by boxfox on 2016-06-01.
 */
public class DSMNotification extends RealmObject{
    public static final int TYPE_BROAD = 1;
    public static final int TYPE_FAMILER = 2;
    public static final int TYPE_MISSION = 3;
    public static final int TYPE_MESSAGE = 4;

    private String title;
    private String context;
    private int type;
    private float milliSecond;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getTime() {
        return milliSecond;
    }

    public void setMilliSecond(float milliSecond) {
        this.milliSecond = milliSecond;
    }
}
