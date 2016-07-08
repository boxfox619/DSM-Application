package com.boxfox.dsm_boxfox.Meal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfox.dsm_boxfox.MainActivity;
import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MealActivity extends AppCompatActivity {
    private int year, month, day;
    private TextView tv1,tv2,tv3;
    private ProgressDialog dialog;
    private MainActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        year = getIntent().getExtras().getInt("Year");
        month = getIntent().getExtras().getInt("Month");
        day = getIntent().getExtras().getInt("Day");
        context = ((MainActivity)getParent());
        tv1 = (TextView)findViewById(R.id.first);
        tv2 = (TextView)findViewById(R.id.second);
        tv3 = (TextView)findViewById(R.id.third);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day-1==0){
                    if(month==1){
                        year-=1;
                        month=12;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month-1, 1);

                        day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    }else{
                        month-=1;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month-1, 1);

                        day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    }
                }else{
                    day-=1;
                }
                new getMeal().start();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month-1, 1);

                int DayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                if(day+1>DayOfMonth){
                    if(month==12){
                        month=1;
                        year+=1;
                        day=1;
                    }else {
                        month += 1;
                        day = 1;
                    }
                }else{
                    day+=1;
                }
                new getMeal().start();
                return false;
            }
        });
        new getMeal().start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meal, menu);
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

    private class getMeal extends Thread {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = ProgressDialog.show(MealActivity.this, "",
                            "잠시만 기다려 주세요..", true);
                }
            });
            try {
                JSONObject command = new JSONObject();
                command.put("Year",year);
                command.put("Month",month);
                command.put("Day",day);
                command.put("Command",319);
                System.out.println(command);
                JSONObject obj = start.soc.readResult(MealActivity.this,command).getObj();
                if (obj.getInt("Command")==719) {
                    final JSONArray Breakfirst = obj.getJSONArray("First");
                    final JSONArray Lunch = obj.getJSONArray("Lunch");
                    final JSONArray Dinner = obj.getJSONArray("Dinner");
                    final JSONArray AllBreakfirst = obj.getJSONArray("FirstAllergy");
                    final JSONArray AllLunch = obj.getJSONArray("LunchAllergy");
                    final JSONArray AllDinner = obj.getJSONArray("DinnerAllergy");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tv1.setText("");
                                tv2.setText("");
                                tv3.setText("");
                                ((TextView)findViewById(R.id.allfirst)).setText("");
                                ((TextView)findViewById(R.id.alllunch)).setText("");
                                ((TextView)findViewById(R.id.alldinner)).setText("");
                                ((TextView) findViewById(R.id.Day)).setText(year + "." + month + "." + day);
                                for (int i = 0; i < Breakfirst.length(); i++)
                                    tv1.append(Breakfirst.getString(i) + "\r\n");
                                for (int i = 0; i < Lunch.length(); i++)
                                    tv3.append(Lunch.getString(i) + "\r\n");
                                for (int i = 0; i < Dinner.length(); i++)
                                    tv2.append(Dinner.getString(i) + "\r\n");
                                for (int i = 0; i < AllBreakfirst.length(); i++)
                                    ((TextView) findViewById(R.id.allfirst)).append(AllBreakfirst.getString(i) + "/");
                                for (int i = 0; i < AllLunch.length(); i++)
                                    ((TextView) findViewById(R.id.alllunch)).append(AllLunch.getString(i) + "/");
                                for (int i = 0; i < AllDinner.length(); i++)
                                    ((TextView) findViewById(R.id.alldinner)).append(AllDinner.getString(i) + "/");
                                dialog.cancel();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                finish();
                                dialog.cancel();
                                Toast.makeText(MealActivity.this,"급식정보를 가져오지 못했습니다.3",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            dialog.cancel();
                            Toast.makeText(MealActivity.this,"급식정보를 가져오지 못했습니다.1",Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        dialog.cancel();
                        Toast.makeText(MealActivity.this,"급식정보를 가져오지 못했습니다.2",Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        }
    }
}
