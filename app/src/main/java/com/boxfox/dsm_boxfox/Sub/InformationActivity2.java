package com.boxfox.dsm_boxfox.Sub;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016-02-15.
 */

public class InformationActivity2 extends AppCompatActivity {
    public static int positionc;
    public TextView tv1,tv2,tv3;
    Toolbar toolbar;
    ImageView imgs;
    int Filenum;
    String filelist[],filelink[];
    public Context co;
    public static JSONArray arrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        co = InformationActivity2.this;
        getItems();
        setting();
    }
    private void setting(){
        String text[] = new String[0], text2[] = new String[0];
        try {
            JSONObject objs = arrs.getJSONObject(positionc);
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
            filelist = text;
            filelink = text2;
            Filenum = text.length;
            tv1.setText(objs.getString("RealTitle"));
            tv3.setText(objs.getString("Context").replaceAll("/t/t/", "\r\n"));
            tv2.setText(objs.getString("Date") + "-" + objs.getString("Writer"));
        }catch(JSONException e) {
            e.printStackTrace();
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
        return super.onOptionsItemSelected(item);
    }

    public static class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage("첨부파일이 없습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            return;
                        }
                    });
            return builder.create();
        }
    }

}