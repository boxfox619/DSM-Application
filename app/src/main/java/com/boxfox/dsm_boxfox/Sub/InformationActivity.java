package com.boxfox.dsm_boxfox.Sub;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfox.dsm_boxfox.CheckInActivity;
import com.boxfox.dsm_boxfox.MainActivity;
import com.boxfox.dsm_boxfox.Server.Cache;
import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;

public class InformationActivity extends AppCompatActivity {
    LinearLayout layout,layout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        layout = (LinearLayout)findViewById(R.id.layouts);
        layout2 = (LinearLayout)findViewById(R.id.layouts2);
        setting();
        try {
            Scrab();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    protected void setting(){
        TextView btn = ((TextView)findViewById(R.id.IDTEXT));
        btn.setText("ID : " + ((MainActivity) getParent()).getLogin().getID());
        ((Button)findViewById(R.id.Chagne)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDialogFragment.init("비밀번호 변경", "Password").show(getFragmentManager(), "tag");
            }
        });
        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            start.soc.onlyWrite(new JSONObject().put("Command", 215));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                ((MainActivity) getParent()).getLogin().setLogged(false);
                ((TextView)((Activity)getParent()).findViewById(R.id.testtextsss)).setText("");
                ((MainActivity) getParent()).reset();
                finish();
            }
        });
        ((TextView)((Activity)getParent()).findViewById(R.id.testtextsss)).setText(((MainActivity) getParent()).getLogin().getID());
        ((Button)findViewById(R.id.checkinbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this, CheckInActivity.class));
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Scrab() throws IOException, JSONException {
        layout2.removeAllViews();
        final Cache cache = new Cache(this);
        final JSONObject obj = cache.Read("Scrabs");
        final JSONArray arr = obj.getJSONArray("Scrabs");
        for(int i=0;i<arr.length();i++){
            final JSONObject objs = arr.getJSONObject(i);
            final View view = LayoutInflater.from(this).inflate(R.layout.cardlist_row, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView date = (TextView) view.findViewById(R.id.contexts);
            ImageButton downbtn = ((ImageButton)view.findViewById(R.id.download_btn));
            title.setText(Html.fromHtml(objs.getString("RealTitle")));
            date.setText(Html.fromHtml(objs.getString("Date")));
            downbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                    String text[] = new String[0], text2[] = new String[0];
                    JSONArray arr = new JSONArray(objs.getString("FileName"));
                    if (arr.length() > 0) {
                        JSONArray arr2 = new JSONArray(objs.getString("FileUrl"));
                        text = new String[arr.length()];
                        text2 = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            text[i] = arr.getString(i);
                            text2[i] = arr2.getString(i);
                        }
                    }
                        UserDialogFragment.init2(text,text2).show(getFragmentManager(), "tag");
                    } catch (JSONException e) {
                    }
                }
            });
            final int finalI2 = i;
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    InformationActivity2.positionc= finalI2;
                        InformationActivity2.arrs =arr;
                    startActivity(new Intent(InformationActivity.this,InformationActivity2.class));
                }
            });
            layout2.addView(view);
        }
    }
    private JSONArray remove(JSONArray arr,int point){
        JSONArray arrs = new JSONArray();
        for(int i=0;i<arr.length();i++){
            if(i!=point) {
                try {
                    arrs.put(arr.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return arrs;
    }
}

