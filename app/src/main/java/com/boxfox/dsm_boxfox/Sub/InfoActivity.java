package com.boxfox.dsm_boxfox.Sub;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfox.dsm_boxfox.ArrayListFragment;
import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class InfoActivity extends AppCompatActivity {
    private int positionc;
    public static TextView tv1,tv2,tv3;
    private Toolbar toolbar;
    private int Filenum;
    private String filelist[],filelink[];
    private ProgressDialog dialog;
    public static Context co;
    private int typecheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(InfoActivity.this).build();
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
        positionc = getIntent().getExtras().getInt("Position");
        typecheck = getIntent().getExtras().getInt("TypeCheck");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        co = InfoActivity.this;
        getItems();
        int tmplen=0;
        if(typecheck==1){
            tmplen = realm.where(FeedItem2.class).equalTo("type",CardListFragment.PAGE_FAMILER).findAll().size();
        }else if(typecheck==2){
            tmplen = realm.where(FeedItem2.class).equalTo("type", CardListFragment.PAGE_BROAD).findAll().size();
        }else if(typecheck==5){
            tmplen = MissionActivity.arrs.length;
        }
        dialog = ProgressDialog.show(InfoActivity.co, "",
                "잠시만 기다려 주세요..", true);
        if (typecheck == 1) {
            Fllow_Info fo = new Fllow_Info(619);
            fo.start();
        } else if(typecheck == 5) {
            Fllow_Info fo = new Fllow_Info(115);
            fo.start();
        } else {
            Fllow_Info fo = new Fllow_Info(620);
            fo.start();
        }
    }
    private void getItems(){
        tv1 = (TextView) findViewById(R.id.comment_view);
        tv3 = (TextView) findViewById(R.id.textView18);
        tv2 = (TextView) findViewById(R.id.textView19);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_file) {
            if (Filenum == 0 || filelist.length == 0 || filelist[0].equalsIgnoreCase("null") || filelist[0] == null) {
                new FileMissilesDialogFragment().show(getFragmentManager(), "tag");
            } else
                UserDialogFragment.init2(filelist, filelink).show(getFragmentManager(), "tag");
        }

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }




    private class Fllow_Info extends Thread {
        int type;
        Fllow_Info(int type){
            this.type = type;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public void run(){
            try {
                JSONObject commend = new JSONObject();
                commend.put("Command", type);
                commend.put("Position", positionc);
                final JSONObject obj = start.soc.readResult(InfoActivity.this,commend).getObj();
                if(obj.getInt("Command")==329){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.cancel();
                            finish();
                            Toast.makeText(InfoActivity.this,"게시글 로딩에 실패했습니다!",Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                if(obj.getString("RealTitle").equalsIgnoreCase("NULL")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.cancel();
                            finish();
                            Toast.makeText(InfoActivity.this,"비공개 게시글 입니다!",Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                try {
                    String text[] = new String[0], text2[] = new String[0];
                    JSONArray arr = new JSONArray(obj.getString("FileName"));
                    if (arr.length() > 0) {
                        JSONArray arr2 = new JSONArray(obj.getString("FileUrl"));
                        text = new String[arr.length()];
                        text2 = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            text[i] = arr.getString(i);
                            text2[i] = arr2.getString(i);
                        }
                    }
                    filelist = text;
                    filelink = text2;
                    Filenum = filelist.length;
                }catch(JSONException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.cancel();
                            finish();
                            Toast.makeText(InfoActivity.this, "네트워크 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tv1.setText(obj.getString("RealTitle"));
                            tv3.setText(obj.getString("Context").replaceAll("/t/t/", "\r\n"));
                            tv2.setText(obj.getString("Date") + "-" + obj.getString("Writer"));
                    }catch(JSONException e){
                        e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                    finish();
                                    Toast.makeText(InfoActivity.this, "게시글 로딩에 실패했습니다!", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }
                });
                ArrayListFragment.checks =1;
                dialog.cancel();
                return;
            }catch(Exception e){
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();
                    finish();
                    Toast.makeText(InfoActivity.this, "게시글 로딩에 실패했습니다!", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
    }

}
