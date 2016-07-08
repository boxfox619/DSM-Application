package com.boxfox.dsm_boxfox.Survey;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfox.dsm_boxfox.MainActivity;
import com.boxfox.dsm_boxfox.Server.SurveyOutputStream;
import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class FeedListActivity extends AppCompatActivity {
    static String surveyName;
    private static final String TAG = "RecyclerViewExample";

    public static ArrayList<FeedItem> feedItemList = new ArrayList<FeedItem>();

    public static RecyclerView mRecyclerView;

    public static MyRecyclerAdapter adapter;
    public static Context co;
    static FragmentManager fm;
    private static TextView tv;
    FloatingActionButton fabBtn;

    public static int positionc;
    public static SurveyCreateDialogFragment dialogs;
    private ProgressDialog dialog;
    public static JSONObject surveyData;
    public static Toolbar toolbar;
    public static int total=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
        if(MyRecyclerAdapter.check==2) {
            dialog = ProgressDialog.show(FeedListActivity.this, "",
                    "잠시만 기다려 주세요..", true);
            new SurveyInputStream(positionc).start();
        }
        total=0;
        dialogs = new SurveyCreateDialogFragment();
        fm = getFragmentManager();
        co = this;
        setContentView(R.layout.activity_feed_list);
        tv = (TextView)findViewById(R.id.textView6);
        setting();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setting(){
        fabBtn = (FloatingActionButton) findViewById(R.id.fabBtn);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyRecyclerAdapter.check==1) {
                    tv.setVisibility(View.INVISIBLE);
                    new SurveyDialogFragment().show(getFragmentManager(), "tag");
                }else{

                }
            }
        });
        fabBtn.animate().translationY(fabBtn.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public class SurveyInputStream extends Thread {
        int position;
        public SurveyInputStream(int position){
            this.position = position;
        }
        @Override
        public void run(){
            try{
                JSONObject command = new JSONObject();
                command.put("Command", 1127);
                command.put("Data", position);
                JSONObject obj = start.soc.readResult(FeedListActivity.this,command).getObj();
                if(!obj.getString("Command").equals("719")){
                    Toast.makeText(FeedListActivity.co, "네트워크 오류!", Toast.LENGTH_LONG).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.cancel();
                        }
                    });
                    return;
                }
                    final String check = obj.getString("Answer");
                    String text = obj.getString("Data");
                    JSONObject data = new JSONObject(text);
                    final String permission = data.getString("Permission");
                    final String date = data.getString("Date");
                    surveyName = data.getString("Name");
                    ArrayList<FeedItem> item = new ArrayList<FeedItem>();
                    JSONArray arr = data.getJSONArray("Data");
                    for(int i=0;i<arr.length();i++){
                        FeedItem tem = new FeedItem();
                        tem.setTitle(arr.getJSONObject(i).getString("Title"));
                        tem.setCheckable(arr.getJSONObject(i).getInt("Check"));
                        tem.setType(arr.getJSONObject(i).getString("Type"));
                        tem.setItems(arr.getJSONObject(i).getJSONArray("Data"));
                        item.add(tem);
                    }
                final ArrayList<FeedItem> list = item;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(permission.contains(start.logined.getPermissionString())) {
                            if(!check.equals("No")) {
                                tv.setVisibility(View.INVISIBLE);
                                feedItemList = list;
                                adapter = new MyRecyclerAdapter(FeedListActivity.this, feedItemList);
                                mRecyclerView.setAdapter(adapter);
                                dialog.cancel();
                                surveyData = new JSONObject();
                                try {
                                    surveyData.put("SurveyName",surveyName);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                toolbar.setTitle(FeedListActivity.total+"/"+FeedListActivity.feedItemList.size()+"  "+FeedListActivity.surveyName);
                                fabBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white_36dp));
                                fabBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(total==FeedListActivity.feedItemList.size()){
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    JSONObject obj = new JSONObject();
                                                    try {
                                                        obj.put("Command",1117);
                                                        obj.put("Data",surveyData);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    final String str = start.soc.readResultString(obj);
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if(str.equals("329")){
                                                                Toast.makeText(FeedListActivity.this,"설문조사 응답에 실패했습니다(보안문제)",Toast.LENGTH_SHORT).show();
                                                            }else if(str.equals("e329")){
                                                                Toast.makeText(FeedListActivity.this,"설문조사 응답에 실패했습니다(오류)",Toast.LENGTH_SHORT).show();
                                                            }else if(str.equals("719")) {
                                                                Toast.makeText(FeedListActivity.this, "설문조사 응답에 성공했습니다!", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
                                                        }
                                                    });
                                                }
                                            }).start();
                                        }else{
                                            Toast.makeText(FeedListActivity.this,"설문조사를 모두 작성해 주세요", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                dialog.cancel();
                                finish();
                                Toast.makeText(FeedListActivity.this,"이미 종료된 설문조사 입니다! "+date,Toast.LENGTH_LONG).show();
                            }
                        }else{
                            dialog.cancel();
                            finish();
                            Toast.makeText(FeedListActivity.this,"해당 설문조사에 참여할 수 없습니다!"+permission,Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static class SurveyCreateDialogFragment extends DialogFragment {
        private DatePicker dp;
        private RadioButton pb1,pb2;
        private EditText ebs;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_survey_create_dialog, null);
            getItems(view);
            //ebs.setFilters(new InputFilter[]{filterAlpha});
            builder.setView(view)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            if (checkItems()) {
                                ProgressDialog dialogs = ProgressDialog.show(getContext(), "",
                                        "잠시만 기다려 주세요..", true);
                                String permission = "";
                                if (((RadioButton) view.findViewById(R.id.radioButton4)).isChecked())
                                    permission += "S";
                                if (((RadioButton) view.findViewById(R.id.radioButton6)).isChecked())
                                    permission += "P";
                                String date = dp.getYear()+"."+(dp.getMonth()+1)+"."+dp.getDayOfMonth();
                                new SurveyOutputStream(FeedListActivity.feedItemList, ebs.getText().toString(), permission,date).start();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                dialogs.cancel();
                                new start(getContext()).start();
                                ((Activity)getContext()).finish();

                            }
                        }
                    })
                    .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }


        private boolean checkItems(){
            if (!(ebs.getText().length() > 0)) {
                Toast.makeText(FeedListActivity.co, "설문조사의 이름을 입력해 주세요!", Toast.LENGTH_LONG).show();
                return false;
            }

            for(int i=0;i< start.survey.length;i++){
                if(start.survey[i].equals(ebs.getText())){
                    Toast.makeText(FeedListActivity.co, "이미 같은 이름의 설문조사가 존재 합니다.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            System.out.println(ebs.getText()+" "+dp.getYear()+" "+dp.getMonth()+" "+dp.getDayOfMonth()+" "+year+" "+month+" "+day);
            if ((dp.getYear() < year)||(dp.getYear() == year && dp.getMonth()+1 == month && dp.getDayOfMonth() <= day)||(dp.getYear() == year && dp.getMonth()+1 < month)) {
                Toast.makeText(FeedListActivity.co, "정확한 기간을 설정해 주세요!", Toast.LENGTH_LONG).show();
                return false;
            }
            if(pb1.isChecked()==false&&pb2.isChecked()==false){
                Toast.makeText(FeedListActivity.co, "조사 대상을 설정해 주세요!", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        private void getItems(View v){
            pb1=(RadioButton)v.findViewById(R.id.radioButton4);
            pb2=(RadioButton)v.findViewById(R.id.radioButton6);
            dp = (DatePicker)v.findViewById(R.id.Period);
            ebs = (EditText)v.findViewById(R.id.textsss);
        }

        protected InputFilter filterAlpha = new InputFilter(){

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅎ가-흐0-9@.]+$");
                if(!ps.matcher(source).matches()){
                    return "";
                }
                return null;
            }
        };
    }

    public static class SurveyDialogFragment  extends DialogFragment {
        private RadioButton c2,c3;
        private EditText text;
        private Button t1,t2,t3;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_survey_dialog, null);
            getItems(view);
            setting();
            builder.setView(view)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            FeedItem item = new FeedItem();
                            item.setTitle(text.getText().toString());
                            if(c3.isChecked())item.setType(c3.getText()+"");
                            else if(c2.isChecked()) item.setType(c2.getText()+"");

                            ArrayList<String> items = new ArrayList<String>();
                            item.setItems(items);
                            FeedListActivity.feedItemList.add(item);
                            FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                            FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                        }
                    })
                    .setNeutralButton("생성", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (checks()) {
                                new SurveyCreateDialogFragment().show(FeedListActivity.fm, "tag");
                            }
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SurveyDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
        private void getItems(View v){
            c2=(RadioButton)v.findViewById(R.id.checkBoxs2);
            c3=(RadioButton)v.findViewById(R.id.checkBoxs3);
            t1 = (Button)v.findViewById(R.id.templet1);
            t2= (Button)v.findViewById(R.id.templet2);
            t3 = (Button)v.findViewById(R.id.templet3);
            text = (EditText)v.findViewById(R.id.editText);
        }

        private void setting(){
            c2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c3.setChecked(false);
                }
            });
            c3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c2.setChecked(false);
                }
            });
            t1.setOnClickListener(templet);
            t2.setOnClickListener(templet);
            t3.setOnClickListener(templet);
            //text.setFilters(new InputFilter[]{filterAlpha});
        }

        View.OnClickListener templet = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeedItem item = new FeedItem();
                ArrayList<String> items = new ArrayList<String>();
                ArrayList<String> itemk = new ArrayList<String>();
                switch(v.getId()){
                    case R.id.templet1 :
                        item.setTitle(text.getText().toString());
                        item.setItems(items);
                        itemk.add("동의");
                        itemk.add("비동의");
                        item.setItems(itemk);
                        item.setCheckable(1);
                        FeedListActivity.feedItemList.add(item);
                        FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                        FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                        SurveyDialogFragment.this.getDialog().cancel();
                        break;
                    case R.id.templet2 :
                        item.setTitle(text.getText().toString());
                        item.setItems(items);
                        itemk.add("매우 불만족");
                        itemk.add("불만족");
                        itemk.add("조금불만족");
                        itemk.add("조금만족");
                        itemk.add("만족");
                        itemk.add("매우 만족");
                        item.setItems(itemk);
                        item.setCheckable(1);
                        FeedListActivity.feedItemList.add(item);
                        FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                        FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                        SurveyDialogFragment.this.getDialog().cancel();
                        break;
                    case R.id.templet3 :
                        item.setTitle("급식신청");
                        item.setItems(items);
                        itemk.add("조식");
                        itemk.add("중식");
                        itemk.add("석식");
                        itemk.add("주말 급식");
                        item.setItems(itemk);
                        item.setCheckable(itemk.size());
                        FeedListActivity.feedItemList.add(item);
                        FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                        FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                        SurveyDialogFragment.this.getDialog().cancel();
                        break;
                }
            }
        };

        protected InputFilter filterAlpha = new InputFilter(){

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅎ가-흐0-9@.]+$");
                if(!ps.matcher(source).matches()){
                    return "";
                }
                return null;
            }
        };

        private boolean checks(){
            if (FeedListActivity.feedItemList.size() == 0) {
                Toast.makeText(FeedListActivity.co, "항목을 생성해 주세요!", Toast.LENGTH_LONG).show();
                return false;
            }

            for(int i=0;i< FeedListActivity.feedItemList.size();i++){
                if(FeedListActivity.feedItemList.get(i).getTitle().equalsIgnoreCase("")){
                    Toast.makeText(FeedListActivity.co, "공백인 항목은 불가능 합니다!", Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            for (int i = 0; i < FeedListActivity.feedItemList.size() - 1; i++) {
                for (int j = i + 1; j < FeedListActivity.feedItemList.size(); j++) {
                    if (feedItemList.get(i).getTitle().equals(feedItemList.get(j).getTitle())) {
                        Toast.makeText(FeedListActivity.co, "같은 이름의 항목은 불가능 합니다!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }
            return true;
        }

    }


}
