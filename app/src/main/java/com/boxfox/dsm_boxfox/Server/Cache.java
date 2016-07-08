package com.boxfox.dsm_boxfox.Server;

import android.content.Context;
import android.os.Environment;

import com.boxfox.dsm_boxfox.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Administrator on 2016-02-13.
 */
public class Cache {
    private Context context;
    public Cache(Context co){
        context = co;
    }

    public File getCacheFolder(Context context) {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "cachefolder");
            if(!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }
        try {
            if (!cacheDir.isDirectory()) {
                cacheDir = context.getCacheDir();
            }
        }catch(Exception e){
            return null;
        }
        return cacheDir;
    }

    public void Write(String target,JSONObject obj) throws IOException {
        File cacheDir = getCacheFolder(context);
        if(cacheDir==null)return;
        File cacheFile = new File(cacheDir, "Cache.txt");
        if(!cacheFile.exists())cacheFile.createNewFile();
        FileInputStream inputStream = new FileInputStream(cacheFile);
        FileWriter fileWriter = new FileWriter(cacheFile);
        Scanner s = new Scanner(inputStream);
        String text="";
        while(s.hasNext()){
            text+=s.nextLine();
        }
        inputStream.close();
        JSONObject cache = null;
        try {
            cache = new JSONObject(text);
        } catch (JSONException e) {
            cache=new JSONObject();
            JSONObject obj2 = new JSONObject();

            try {
                obj2.put("Broad", true);
                obj2.put("Familer", true);
                obj2.put("Mission", true);
                obj2.put("Message", true);
                cache.put("Setting", obj2);
                cache.put("Scrabs", new JSONObject().put("Scrabs",new JSONArray()));

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        try {
            cache.put(target, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fileWriter.write(cache.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public JSONObject Read(String target) throws IOException {
        File cacheDir = getCacheFolder(context);
        File cacheFile = new File(cacheDir, "Cache.txt");
        if(!cacheFile.exists())cacheFile.createNewFile();
        FileInputStream inputStream = new FileInputStream(cacheFile);
        Scanner s = new Scanner(inputStream);
        String text="";
        while(s.hasNext()){
            text+=s.nextLine();
        }
        inputStream.close();
        JSONObject cache = null;
        try {
            cache = new JSONObject(text);
        } catch (JSONException e) {
            cache=new JSONObject();
            JSONObject obj = new JSONObject();

            try {
            obj.put("Broad", true);
            obj.put("Familer", true);
            obj.put("Mission", true);
                obj.put("Message", true);
            cache.put("Setting", obj);
            cache.put("Scrabs", new JSONObject().put("Scrabs",new JSONArray()));

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            FileWriter fileWriter = new FileWriter(cacheFile);
            fileWriter.write(cache.toString());
            fileWriter.flush();
            fileWriter.close();
        }
        try {
            return cache.getJSONObject(target);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
