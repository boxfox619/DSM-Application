package com.boxfox.dsm_boxfox.Sub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MissionActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    public static String[] arrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dialog = ProgressDialog.show(MissionActivity.this, "",
                "잠시만 기다려 주세요..", true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new ListInput().start();
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Intent in = new Intent(MissionActivity.this, InfoActivity.class);
            in.putExtra("Position",position);
            in.putExtra("TypeCheck",5);
            startActivity(in);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ListInput extends Thread{

        @Override
        public void run(){
            JSONObject command = new JSONObject();
            try {
                command.put("Command", 114);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final JSONObject obj = start.soc.readResult(MissionActivity.this,command).getObj();
            try {
                if(obj.getString("Command").equals("719")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            JSONArray arr = null;
                                arr = obj.getJSONArray("Data");
                            arrs = new String[arr.length()];
                            for(int i=0;i<arr.length();i++)
                                arrs[i] = arr.getString(i);
                            ListView lv = (ListView) findViewById(R.id.listViewsssss);
                            lv.setAdapter(new ArrayAdapter<String>(MissionActivity.this, android.R.layout.simple_list_item_1, arrs));
                            lv.setOnItemClickListener(listener);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MissionActivity.this,"로딩 실패",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.cancel();
        }

    }
}
