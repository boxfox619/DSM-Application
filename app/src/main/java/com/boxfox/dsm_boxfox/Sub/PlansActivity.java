package com.boxfox.dsm_boxfox.Sub;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class PlansActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    private int year ,month,byear,bmonth;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        dialog = ProgressDialog.show(PlansActivity.this, "",
                "잠시만 기다려 주세요..", true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH)+1;
        new ListInput().start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.monthPick) {
            MonthPickDialogFragment.init(year,month).show(getFragmentManager(),"tag");
        }

        return super.onOptionsItemSelected(item);
    }

    public class ListInput extends Thread {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
            JSONObject command = new JSONObject();
            try {
                command.put("Command", 119);
                command.put("Year",year);
                command.put("Month",month);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                final JSONObject obj = start.soc.readResult(PlansActivity.this,command).getObj();
                if (obj.getString("Command").equals("719")) {
                    runOnUiThread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            try {
                                Calendar cal= Calendar.getInstance ();
                                LinearLayout layout = (LinearLayout) findViewById(R.id.mainlayout);
                                layout.removeAllViews();
                                JSONArray arr = null;
                                arr = obj.getJSONArray("Data");
                                toolbar.setTitle(month+"월 학사일정");
                                for (int i = 0; i < arr.length(); i++){
                                    if(arr.getString(i).length()==3)continue;
                                    View view = LayoutInflater.from(PlansActivity.this).inflate(R.layout.plan_group, null);
                                    ((TextView)view.findViewById(R.id.plan_day)).setText(arr.getString(i).substring(0, 3));
                                    String arrs[] = arr.getString(i).substring(3,arr.getString(i).length()).split("@");
                                    cal.set(Calendar.YEAR, year);
                                    cal.set(Calendar.MONTH, month - 1);
                                    cal.set(Calendar.DATE, i + 1);
                                    switch (cal.get(Calendar.DAY_OF_WEEK)){
                                        case 1:
                                            ((TextView)view.findViewById(R.id.day_of_week)).setText("일요일");
                                            break;
                                        case 2:
                                            ((TextView)view.findViewById(R.id.day_of_week)).setText("월요일");
                                            break;
                                        case 3:
                                            ((TextView)view.findViewById(R.id.day_of_week)).setText("화요일");
                                            break;
                                        case 4:
                                            ((TextView)view.findViewById(R.id.day_of_week)).setText("수요일");
                                            break;
                                        case 5:
                                            ((TextView)view.findViewById(R.id.day_of_week)).setText("목요일");
                                            break;
                                        case 6:
                                            ((TextView)view.findViewById(R.id.day_of_week)).setText("금요일");
                                            break;
                                        case 7:
                                            ((TextView)view.findViewById(R.id.day_of_week)).setText("토요일");
                                            break;
                                    }
                                    for(int k =0;k<arrs.length;k++) {
                                        String str = arrs[k];
                                        View card = LayoutInflater.from(PlansActivity.this).inflate(R.layout.planlist_row, null);
                                        if(str.contains("중간고사")||str.contains("기말고사")||str.contains("듣기평가")||str.contains("모의평가")){
                                            ((LinearLayout)card.findViewById(R.id.posts)).setBackgroundResource(R.color.cardBlue);
                                            ((TextView)card.findViewById(R.id.title)).setTextColor(Color.parseColor("#ffffff"));
                                        }else if(str.contains("의무귀가")){
                                            ((LinearLayout)card.findViewById(R.id.posts)).setBackgroundResource(R.color.cardGreen);
                                            ((TextView)card.findViewById(R.id.title)).setTextColor(Color.parseColor("#ffffff"));
                                        }
                                        if(k>0)
                                            ((TextView) card.findViewById(R.id.title)).setText(" "+str.replace("\r","").replace("\n","").replace("\t","").replace("  ",""));
                                        else
                                            ((TextView) card.findViewById(R.id.title)).setText(str.replace("\r", "").replace("\n", "").replace("\t", "").replace("  ", ""));
                                        ((LinearLayout)view.findViewById(R.id.PlanList)).addView(card);
                                    }
                                    layout.addView(view);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    byear = year;
                    bmonth = month;
                }else if(obj.getString("Command").equals("100")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PlansActivity.this, "학사일정이 없습니다.", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            year = byear;
                            month = bmonth;
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PlansActivity.this, "네트워크 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            year = byear;
                            month = bmonth;
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlansActivity.this, "로딩 실패", Toast.LENGTH_SHORT).show();
                        year = byear;
                        month = bmonth;
                    }
                });
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();
                }
            });
        }
    }

}
