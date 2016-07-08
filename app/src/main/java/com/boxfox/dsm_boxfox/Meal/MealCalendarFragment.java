package com.boxfox.dsm_boxfox.Meal;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.boxfox.dsm_boxfox.MainActivity;
import com.example.dsm_boxfox.R;
import com.boxfox.dsm_boxfox.Server.start;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class MealCalendarFragment extends DialogFragment {
    public static int Month=1;
    private int years,month,day;
    private DatePicker dp;
    private TextView tv,tv2;
    private Context contexnt;

    public void test(FragmentManager fm, String tag, Context con){
        contexnt =con;
        super.show(fm,tag);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_meal_calendar, null);
        tv = (TextView)view.findViewById(R.id.textView999);
        tv2 = (TextView)view.findViewById(R.id.textView1000);
        final DatePicker dp = (DatePicker)view.findViewById(R.id.datepicker999);

        if(!start.Network){
            tv2.setText("Network Error");
            tv.setText("서버와 연결되지 않았습니다.\r\n");
            tv.append("네트워크 상태를 확인해 주세요");
        }

        new Network().start();

        builder.setView(view)
                .setPositiveButton("급식 확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (start.Network) {
                            Intent intent = new Intent(getActivity(), MealActivity.class);
                            intent.putExtra("Year",dp.getYear());
                            intent.putExtra("Month",dp.getMonth() + 1);
                            intent.putExtra("Day", dp.getDayOfMonth());
                            startActivity(intent);
                        } else {
                            ((MainActivity)contexnt).Message(getResources().getString(R.string.NetworkCheck));
                        }
                    }
                })
                .setNeutralButton("오늘의 급식", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Calendar c = Calendar.getInstance();
                        Intent intent = new Intent(getActivity(), MealActivity.class);
                        intent.putExtra("Year",c.get(Calendar.YEAR));
                        intent.putExtra("Month",c.get(Calendar.MONTH)+1);
                        intent.putExtra("day",c.get(Calendar.DAY_OF_MONTH));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MealCalendarFragment.this.getDialog().cancel();
                            }
                        }

                );
        return builder.create();
    }
    private class Network extends Thread {

        @Override
        public void run() {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH)+1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            final int time = c.get(Calendar.HOUR_OF_DAY);
            try {
                JSONObject obj = null;
                if(time<19) {
                    JSONObject commend = new JSONObject();
                    commend.put("Year", year);
                    commend.put("Month", month);
                    commend.put("Day", day);
                    commend.put("Command", 319);
                    obj = start.soc.readResult(getActivity(),commend).getObj();
                }else{
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, 1);

                    int DayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if(day+1>DayOfMonth){
                        if(month==12){
                            JSONObject commend = new JSONObject();
                            commend.put("Year", year+1);
                            commend.put("Month", 1);
                            commend.put("Day", 1);
                            commend.put("Command", 319);
                            obj = start.soc.readResult(getActivity(),commend).getObj();
                        }else {
                            month += 1;
                            day = 1;
                            JSONObject commend = new JSONObject();
                            commend.put("Year", year);
                            commend.put("Month", month+1);
                            commend.put("Day", 1);
                            commend.put("Command", 319);
                            obj = start.soc.readResult(getActivity(),commend).getObj();
                        }
                    }else{
                        JSONObject commend = new JSONObject();
                        commend.put("Year", year);
                        commend.put("Month", month);
                        commend.put("Day", day+1);
                        commend.put("Command", 319);
                        obj = start.soc.readResult(getActivity(),commend).getObj();
                    }
                }
                if (obj.getInt("Command")==719) {
                    final JSONArray Breakfirst = obj.getJSONArray("First");
                    final JSONArray Lunch = obj.getJSONArray("Lunch");
                    final JSONArray Dinner = obj.getJSONArray("Dinner");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (time > 0 && time < 9) {
                                    tv2.setText("오늘의 아침\r\n");
                                    for (int i = 0; i < Breakfirst.length(); i++)
                                        tv.append(Breakfirst.getString(i) + "\r\n");
                                } else if (8 < time && 13 > time) {
                                    tv2.setText("오늘의 점심\r\n");
                                    for (int i = 0; i < Lunch.length(); i++)
                                        tv.append(Lunch.getString(i) + "\r\n");
                                } else if (13 < time && 19 > time) {
                                    tv2.setText("오늘의 저녁\r\n");
                                    for (int i = 0; i < Dinner.length(); i++)
                                        tv.append(Dinner.getString(i) + "\r\n");
                                } else {
                                    tv2.setText("내일의 아침\r\n");
                                    for (int i = 0; i < Breakfirst.length(); i++)
                                        tv.append(Breakfirst.getString(i) + "\r\n");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ((MainActivity)contexnt).Message("오늘의 급식 불러오기 실패");
                            }
                        }
                    });
                } else {
                    ((MainActivity)contexnt).Message("오늘의 급식 불러오기 실패");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((MainActivity)contexnt).Message("오늘의 급식 불러오기 실패");
            }
        }
    }

}
