package com.boxfox.dsm_boxfox.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class DSMPacket {
    private String data;
    private JSONObject obj;
    public DSMPacket(){
        data="329";
        obj = new JSONObject();
        try {
            obj.put("Command",329);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setData(String data){this.data = data;}
    public void setObj(JSONObject obj){this.obj = obj;}
    public void setObj(String data){
        try {
            obj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getData(){return data;}
    public JSONObject getObj(){return obj;}
}