package com.boxfox.dsm_boxfox.Server;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.TextView;

import com.boxfox.dsm_boxfox.MainActivity;
import com.boxfox.dsm_boxfox.Sub.CardListFragment;
import com.boxfox.dsm_boxfox.Sub.FeedItem2;
import com.boxfox.dsm_boxfox.Sub.File;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class start extends Thread {
    public static final String server="192.168.10.101";
    public static final int port=10306;
    public static String survey[]=new String[1];
    public static boolean Network;
    public static String UUID;
    public static DSMVertxStream soc;
    public static Logined logined;
    private Context context;
    public start(Context context ){
        this.context = context;
    }


    @Override
    public void run() {
        soc = new DSMVertxStream((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
        final TextView tv = (TextView) ((Activity) context).findViewById(R.id.text);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();
        Realm realm;
        try {
            realm = Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfiguration);
                realm =  Realm.getInstance(realmConfiguration);
            } catch (Exception ex){
                throw ex;
            }
        }
        try {
            try {
                if(soc.accept()) {
                    JSONObject command = new JSONObject();
                    command.put("Command", 1230);
                    command.put("UUID", UUID);
                    command.put("Broad", realm.where(FeedItem2.class).equalTo("type", CardListFragment.PAGE_BROAD).findAll().size());
                    command.put("Familer", realm.where(FeedItem2.class).equalTo("type", CardListFragment.PAGE_FAMILER).findAll().size());
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tv != null && context != null)
                                tv.setText("데이터 요청하는중...10%");
                        }
                    });
                    final DSMPacket packet = soc.readResult(context,command);
                    if (packet.getObj().getInt("Command") == 719) {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(tv!=null&&context!=null)
                                    tv.setText("가정통신 가져오는 중... 20%");
                                    try {
                                        comment(packet.getObj().getJSONArray("Familer"), "c");
                                    }catch(JSONException e){}
                                        if(tv!=null&&context!=null)
                                    tv.setText("공지사항 가져오는 중... 50%");
                                    try{
                                    comment(packet.getObj().getJSONArray("Broad"), "b");
                                    }catch(JSONException e){}
                                    if(tv!=null&&context!=null)
                                    tv.setText("설문조사 가져오는 중... 70%");
                                    getSurvey(packet.getObj().getJSONArray("Survey"), "s");
                                    if(tv!=null&&context!=null)
                                    checkUUID(packet.getObj().getJSONObject("UUID"));
                                    if(tv!=null&&context!=null)
                                    tv.setText("정보 체크중... 90%");
                                    tv.setText("100%");
                                    Network = true;
                                    Intent in = new Intent(context,MainActivity.class);
                                    context.startActivity(in);
                                    ((Activity)context).finish();
                                }catch(JSONException e){
                                    if(tv!=null&&context!=null)
                                    tv.setText("서버 연결 실패");
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        return;
                    }
                }else{
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tv != null && context != null)
                            tv.setText("서버 연결 실패");
                    }
                });
                return;
            }
            return;
        }catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


    public void getSurvey(JSONArray arr,String type) {
        try {

            if (arr.length()==0) {
                    survey[0] = "글이 존재하지 않습니다";
                return;
            }
            String coss[];
            coss = new String[arr.length()];
            for (int j = 0; j < arr.length(); j++) {
                coss[j] = arr.getString(j);
            }
                survey = coss;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    public void comment(JSONArray arr,String type) {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();
        Realm realm;
        try {
            realm = Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfiguration);
                realm =  Realm.getInstance(realmConfiguration);
            } catch (Exception ex){
                throw ex;
            }
        }
        try {
            int postType=0;
            if (type.equalsIgnoreCase("c")){
                postType= CardListFragment.PAGE_FAMILER;
            }
            else if(type.equalsIgnoreCase("b")){
                postType= CardListFragment.PAGE_BROAD;
            }
            realm.beginTransaction();
            realm.where(FeedItem2.class).equalTo("type",postType).findAll().deleteAllFromRealm();
            if(arr.length()!=0){
                realm.where(FeedItem2.class).equalTo("type",postType).findAll().deleteAllFromRealm();
                realm.where(File.class).equalTo("fileType", postType).findAll().deleteAllFromRealm();
            }else return;
            for(int i=0;i<arr.length();i++){
                FeedItem2 item = realm.createObject(FeedItem2.class);
                try {
                    JSONObject obj = arr.getJSONObject(i);
                    item.setTitle(obj.getString("Title"));
                    item.setDate(obj.getString("Date"));
                    item.setNum(i);
                    JSONArray arr1 = new JSONArray(obj.getString("FileName"));
                    JSONArray arr2 = new JSONArray(obj.getString("FileUrl"));
                    for(int k=0;k<arr1.length();k++){
                        File f = realm.createObject(File.class);
                        f.setFileName(arr1.getString(k));
                        f.setFileLink(arr2.getString(k));
                        f.setNum(i);
                        f.setFileType(postType);
                    }
                    item.setType(postType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    private void checkUUID(JSONObject objp){
        try{
            logined = new Logined();
            int check = objp.getInt("Command");
            if(check==719){
                JSONObject obj = objp.getJSONObject("Infomation");
                logined.setID(obj.getString("ID"));
                logined.setPassword(obj.getString("Password"));
                logined.setName(obj.getString("Name"));
                logined.setPhone(obj.getString("Phone"));
                if(obj.getString("Permission").equalsIgnoreCase("parent")){
                    logined.setPermission("P");
                    logined.setCnum(obj.getString("Cnum"));
                    logined.setCname(obj.getString("Cname"));
                }else if(obj.getString("Permission").equalsIgnoreCase("student")){
                    logined.setPermission("S");
                    logined.setCnum(obj.getString("Cnum"));
                }else if(obj.getString("Permission").equalsIgnoreCase("Admin")){
                    logined.setPermission("A");
                }else{
                    logined.setPermission("UnKnown");
                }
                logined.setLogged(true);
            }else{
                logined.setLogged(false);
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }


}


