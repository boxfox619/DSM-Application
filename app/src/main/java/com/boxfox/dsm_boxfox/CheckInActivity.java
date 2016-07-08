package com.boxfox.dsm_boxfox;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CheckInActivity extends AppCompatActivity {
    private LinearLayout layout;
    private ArrayList<CheckBox> checkList = new ArrayList<CheckBox>();
    private JSONArray arrs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        init();
    }

    public void init(){
        layout = (LinearLayout)findViewById(R.id.check_in_layout);
        new Init().start();
        ((FloatingActionButton)findViewById(R.id.fabBtnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Save().start();
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
        getMenuInflater().inflate(R.menu.menu_information, menu);
        return true;
    }

    private class Init extends Thread{

        @Override
        public void run(){
            try {
                final JSONObject obj = start.soc.readResult(CheckInActivity.this,new JSONObject().put("Command",314)).getObj();
                if(obj.getString("Command").equals("719")) {
                    final String selected = obj.getString("Select");
                    final JSONArray arr = obj.getJSONArray("Data");
                    arrs = arr;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.removeAllViews();
                            checkList.clear();
                            int counts = 0;
                            for (int i = 0; i < arr.length(); i++) {
                                try {
                                    JSONObject data = arr.getJSONObject(i);
                                    View view = LayoutInflater.from(CheckInActivity.this).inflate(R.layout.cardlist_row2, null);
                                    checkList.add((CheckBox) view.findViewById(R.id.checks));
                                    TextView title = (TextView) view.findViewById(R.id.titles);
                                    TextView count = (TextView) view.findViewById(R.id.contexts);
                                    title.setText(data.getString("Title"));
                                    count.setText("인원 : " + data.getInt("Count") + "/20");
                                    switch (data.getInt("Type")) {
                                        case 1:
                                            ((ImageView) view.findViewById(R.id.IvIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_create));
                                            break;
                                        case 2:
                                            ((ImageView) view.findViewById(R.id.IvIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_group));
                                            break;
                                    }
                                    final int finalCount = counts;
                                    ((CheckBox) view.findViewById(R.id.checks)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkBoxClick(finalCount,false);
                                        }
                                    });
                                    ((CardView)view.findViewById(R.id.card)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkBoxClick(finalCount,true);
                                        }
                                    });
                                    counts++;
                                    layout.addView(view);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!selected.equals("")) {
                                for (int i = 0; i < arr.length(); i++) {
                                    try {
                                        if (arr.getJSONObject(i).getString("Title").equals(selected))
                                            checkList.get(i).setChecked(true);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    });
                }else errPrint();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void checkBoxClick(int finalCount,boolean check){
            for (int i = 0; i < checkList.size(); i++)
                if (finalCount != i)
                    checkList.get(i).setChecked(false);
            if(check)
            checkList.get(finalCount).setChecked(!checkList.get(finalCount).isChecked());
        }

        private void errPrint() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        Snackbar.make((RelativeLayout)findViewById(R.id.rootLayouts), "연장학습 불러오기에 실패했습니다.", Snackbar.LENGTH_SHORT)
                                .setAction("확인", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                })
                                .show();
                }
            });
        }
    }

    private class Save extends Thread{

        @Override
        public void run(){
            String title="";
            for (int i = 0; i < checkList.size(); i++)
                if(checkList.get(i).isChecked()){
                    try {
                        title = arrs.getJSONObject(i).getString("Title");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;}
            JSONObject command = new JSONObject();
            try {
                command.put("Command",315);
                command.put("Select",title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if(start.soc.readResult(CheckInActivity.this, command).getObj().getString("Command").equals("719"))
                    Snackbar.make((RelativeLayout)findViewById(R.id.rootLayouts), "연장학습 신청을 성공적으로 마쳤습니다.", Snackbar.LENGTH_SHORT)
                        .setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show();
                else
                    Snackbar.make((RelativeLayout)findViewById(R.id.rootLayouts), "연장학습 신청중 오류가 발생했습니다.", Snackbar.LENGTH_SHORT)
                            .setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            })
                            .show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new Init().start();
        }
    }
}
