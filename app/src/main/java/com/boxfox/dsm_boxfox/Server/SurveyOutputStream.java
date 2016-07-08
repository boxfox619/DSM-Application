package com.boxfox.dsm_boxfox.Server;

import com.boxfox.dsm_boxfox.Survey.FeedItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 김성래 on 2016-01-12.
 */
public class SurveyOutputStream extends Thread {
    private ArrayList<FeedItem> item;
    private String name,permission;
    private String date;

    public SurveyOutputStream(ArrayList<FeedItem> item,String name,String permission,String date){
        this.item = item;
        this.name = name;
        this.permission = permission;
        this.date = date;
    }



    @Override
    public void run() {
        JSONObject command = new JSONObject();
        try {
            command.put("Command",1126);
            command.put("Name", name);
            command.put("Permission",permission);
            command.put("Date", date);
            JSONArray arr = new JSONArray();
        for (int i = 0; i < item.size(); i++) {
            JSONObject obj = new JSONObject();
            obj.put("Title", item.get(i).getTitle());
            obj.put("Check", item.get(i).getCheckable());
            obj.put("Type", item.get(i).getType());
            JSONArray ao = new JSONArray();
            for (int j = 0; j < item.get(i).getItems().size(); j++) {
                ao.put(item.get(i).getItems().get(j));
            }
            obj.put("Data", ao);
            arr.put(obj);
        }
            command.put("Data",arr);
            System.out.println(command.toString());
            start.soc.onlyWrite(command);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}