package com.boxfox.dsm_boxfox;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.boxfox.dsm_boxfox.Applications.DSMNotification;
import com.boxfox.dsm_boxfox.Server.Cache;
import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;
import com.google.android.gcm.GCMBaseIntentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class GCMIntentService extends GCMBaseIntentService {

    private static void generateNotification(Context context, String message) {

        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();


        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(icon, message, when);

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);



        //notification.setLatestEventInfo(context, title, message, intent);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);

    }

    @Override
    protected void onError(Context arg0, String arg1) {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onMessage(Context context, Intent intent) {
        String msg = intent.getStringExtra("msg");
        try {
            msg = URLDecoder.decode(msg, "EUC-KR");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        boolean check=true;
        int types = 0;
        String type = "";
        try {
            JSONObject obj = new JSONObject(msg);
            msg = obj.getString("Msg");
            type = obj.getString("Type");
        } catch (JSONException e) {
            return;
        }
        Cache c = new Cache(context);
        JSONObject obj = null;
        try {
           obj = c.Read("Setting");
            if(type.equalsIgnoreCase("broad")){
                check = obj.getBoolean("Broad");
                types = DSMNotification.TYPE_BROAD;
            }else if(type.equalsIgnoreCase("familer")){
                check = obj.getBoolean("Familer");
                types = DSMNotification.TYPE_FAMILER;
            }else if(type.equalsIgnoreCase("Mission")){
                check = obj.getBoolean("Mission");
                types = DSMNotification.TYPE_MISSION;
            }else if(type.equalsIgnoreCase("Message")){
                check = obj.getBoolean("Message");
                types = DSMNotification.TYPE_MESSAGE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(check) {
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
            realm.beginTransaction();
            DSMNotification dsmNotification = realm.createObject(DSMNotification.class);
            dsmNotification.setType(types);
            dsmNotification.setContext(msg);
            dsmNotification.setMilliSecond(Calendar.getInstance().get(Calendar.MILLISECOND));
            if(types!=DSMNotification.TYPE_MESSAGE)
            dsmNotification.setTitle(msg);
            else try {
                dsmNotification.setTitle(obj.getString("Title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            realm.commitTransaction();
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.logo_small)
                    .setContentTitle("DSM 알리미")
                    .setContentText(msg)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setTicker("DSM 알리미 - 소식 왔어요!")
                    .setAutoCancel(true);
            Notification notification = builder.build();
            nm.notify(Calendar.getInstance().get(Calendar.MILLISECOND), notification);
        }
    }



    @Override

    protected void onRegistered(Context context, final String reg_id) {
        System.out.println("키를 등록합니다.(GCM INTENTSERVICE)"+reg_id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject commend = new JSONObject();
                    commend.put("Command",1219);
                    commend.put("UUID", start.UUID);
                    commend.put("RegID",reg_id);
                    start.soc.onlyWrite(commend);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }



    @Override
    protected void onUnregistered(Context arg0, String arg1) {
        System.out.println("키를 제거합니다.(GCM INTENTSERVICE) 제거되었습니다.");
    }

}