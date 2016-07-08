package com.boxfox.dsm_boxfox.Server;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;


public class DSMVertxStream {
    private Socket server;
    public ConnectivityManager manager;
    int count;
    public DSMVertxStream(ConnectivityManager co){
        this.manager=co;
    }

    public boolean accept() {
        try {
            if(isNetWork()) {
                server = new Socket(start.server, start.port);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        count++;
        return false;
    }

    public void close(){
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean isNetWork(){
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)){
            return true;
        }else{
            return false;
        }
    }

    public void checking(){
        if(server.isClosed()){accept();}
    }

    public void onlyWrite(JSONObject obj){
        try {
            server.getOutputStream().write(obj.toString().getBytes());
        } catch (IOException e) {
            close();
            if(isNetWork()) {
                accept();
                onlyWrite(obj);
            }
            count=0;
        }
    }

    public DSMPacket readResult(final Context context,JSONObject obj) {
        final DSMPacket packet = new DSMPacket();
        try {
            int count=0;
            server.getOutputStream().write(obj.toString().getBytes());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int len;
            while(count<100) {
                if(server.getInputStream().available() > 0){
                    len = server.getInputStream().read();
                    buffer.write(len);
                }else {
                    if(buffer.size() > 1) {
                        break;
                    }else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count++;
                    }
                }
            }
            if(count<20) {
                byte[] byteArray = buffer.toByteArray();
                String commend = new String(byteArray, Charset.forName("UTF-8"));
                packet.setObj(commend);
            }else
                server.close();
        } catch (IOException e) {
            close();
            if(count<=10&&isNetWork()) {
                accept();
                readResult(context,obj);
            }
            count=0;
        }
        try {
            if(packet.getObj().getString("Command").equals("184")) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "보안 문제 발생", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return packet;
    }

    public String readResultString(JSONObject obj) {
        try {
            int count=0;
            server.getOutputStream().write(obj.toString().getBytes());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int len;
            while (count<50) {
                if (server.getInputStream().available() > 0) {
                    len = server.getInputStream().read();
                    buffer.write(len);
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                    if (buffer.size() > 1)
                        break;
                }
            }
            if(count<20) {
                byte[] byteArray = buffer.toByteArray();
                String commend = new String(byteArray, Charset.forName("UTF-8"));
                return commend;
            }else
                server.close();
        } catch (IOException e) {
            close();
            if (count<=10&&isNetWork()) {
                accept();
                readResultString(obj);
            }
            count=0;
        }
        return null;
    }
}