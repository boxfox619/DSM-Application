package com.boxfox.dsm_boxfox.Survey;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class FeedItem {
    private String title;
    private JSONArray items;
    private ArrayList<String> itemss;
    private int checkable;
    private String type="선택형";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setItems(ArrayList<String> text){
        itemss = text;
    }
    public ArrayList<String> getItems(){
        return itemss;
    }

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }

    public void setItems(JSONArray text){
        itemss = new ArrayList<String>();
        for(int i=0;i<text.length();i++){
            try {
                itemss.add(text.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        items = text;
    }
    public void setCheckable(int i){this.checkable=i;}
    public int getCheckable(){return checkable;}

}
