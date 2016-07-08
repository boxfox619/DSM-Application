package com.boxfox.dsm_boxfox.Survey;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfox.dsm_boxfox.Sub.filters;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedListRowHolder extends RecyclerView.ViewHolder {
    protected static TextView title;
    protected static int num;

    public FeedListRowHolder(final View view,int i) {
        super(view);
        this.title = (TextView) view.findViewById(R.id.title);
        if(i==1)
            view.setOnClickListener(click);
        else
            view.setOnClickListener(click2);
    }

    View.OnClickListener click = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(FeedListActivity.feedItemList.get(num).getType().equals("단답형")){
                TwoDialogFragment dia = new TwoDialogFragment();
                dia.show(FeedListActivity.fm, "tag");
                return;
            }
            OneDialogFragment dia = new OneDialogFragment();
            dia.show(FeedListActivity.fm, "tag");
        }
    };
    View.OnClickListener click2 = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            AnswerDialogFragment.init(FeedListActivity.feedItemList.get(num).getTitle(),num).show(FeedListActivity.fm, "tag");
        }
    };

    public static class OneDialogFragment extends DialogFragment {
        private LinearLayout layout,layout2;
        private Button addItem,revItem;
        private EditText tv;
        private Spinner s;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_one_dialog, null);
            layout = (LinearLayout)view.findViewById(R.id.layouts);
            layout2 = (LinearLayout)view.findViewById(R.id.layout2);
            addItem = (Button)view.findViewById(R.id.addItem);
            revItem = (Button)view.findViewById(R.id.removeItem);
            tv = (EditText)view.findViewById(R.id.title);
            tv.setText(title.getText());
            s= new Spinner(FeedListActivity.co);


            final TextView tvs = new TextView(FeedListActivity.co);
            tvs.setText("선택가능 개수:");

            setting();
            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout.addView(new EditText(FeedListActivity.co));
                    if(layout.getChildCount()>1){
                        if(layout.getChildCount()==2) {
                            layout2.addView(tvs);
                            layout2.addView(s);
                        }

                        String[] notificationStatus = new String[layout.getChildCount()];
                        for(int i=0;i<notificationStatus.length;i++){
                            notificationStatus[i]=(i+1)+"개";
                        }
                        ArrayAdapter<String> aa = new ArrayAdapter<String>(
                                FeedListActivity.co, android.R.layout.simple_spinner_item, notificationStatus );
                        s.setAdapter(aa);
                    }else{
                        layout2.removeView(s);
                    }
                }
            });
            revItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(layout.getChildCount()<2) {
                        layout2.removeView(tvs);
                        layout2.removeView(s);
                    }
                    if(layout.getChildCount()!=0)
                        layout.removeViewAt(layout.getChildCount()-1);
                    else
                        Toast.makeText(FeedListActivity.co, "더이상 삭제할 아이템이 없습니다!", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setView(view)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String title = tv.getText().toString();
                            ArrayList<String> list = new ArrayList<String>();
                            for(int i=0;i<layout.getChildCount();i++) {
                                list.add(((EditText)layout.getChildAt(i)).getText().toString());
                            }
                            FeedListActivity.feedItemList.get(num).setTitle(title);
                            FeedListActivity.feedItemList.get(num).setItems(list);
                            FeedListActivity.feedItemList.get(num).setCheckable(s.getSelectedItemPosition()+1);
                            FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                            FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                        }
                    })
                    .setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            FeedListActivity.feedItemList.remove(num);
                            FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                            FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                        }
                    })
                    .setNegativeButton("저장 후 미리보기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String title = tv.getText().toString();
                            ArrayList<String> list = new ArrayList<String>();
                            for(int i=0;i<layout.getChildCount();i++) {
                                list.add(((EditText)layout.getChildAt(i)).getText().toString());
                            }
                            FeedListActivity.feedItemList.get(num).setTitle(title);
                            FeedListActivity.feedItemList.get(num).setItems(list);
                            FeedListActivity.feedItemList.get(num).setCheckable(s.getSelectedItemPosition() + 1);
                            FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                            FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                            SurveyCheckDialogFragment.init(tv.getText().toString(),num).show(getFragmentManager(), "tag");
                        }
                    });
            return builder.create();
        }
        public void setting() {
            ArrayList<String> list = FeedListActivity.feedItemList.get(num).getItems();
            for (int i = 0; i < list.size(); i++) {
                EditText t = new EditText(FeedListActivity.co);
                t.setText(list.get(i));
                layout.addView(t);
            }

            if (layout.getChildCount() > 1) {
                TextView tv = new TextView(FeedListActivity.co);
                tv.setText("선택가능 개수:");
                layout2.addView(tv);
                layout2.addView(s);

                String[] notificationStatus = new String[layout.getChildCount()];
                for (int i = 0; i < notificationStatus.length; i++) {
                    notificationStatus[i] = (i+1) + "개";
                }
                ArrayAdapter<String> aa = new ArrayAdapter<String>(
                        FeedListActivity.co, android.R.layout.simple_spinner_item, notificationStatus);
                s.setAdapter(aa);
                s.setSelection(FeedListActivity.feedItemList.get(num).getCheckable()-1);
            }
        }
    }




    public static class TwoDialogFragment extends DialogFragment {
        private LinearLayout layout;
        private EditText tv;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_two_dialog, null);
            tv = (EditText)view.findViewById(R.id.title);
            tv.setText(title.getText());
            layout = (LinearLayout)view.findViewById(R.id.layouts);

            builder.setView(view)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String title = tv.getText().toString();
                            ArrayList<String> list = new ArrayList<String>();
                            for(int i=0;i<layout.getChildCount();i++) {
                                list.add(((EditText)layout.getChildAt(i)).getText().toString());
                            }
                            FeedListActivity.feedItemList.get(num).setTitle(title);
                            FeedListActivity.feedItemList.get(num).setItems(list);
                            FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                            FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                        }
                    })
                    .setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            FeedListActivity.feedItemList.remove(num);
                            FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                            FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                        }
                    })
                    .setNegativeButton("저장 후 미리보기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String title = tv.getText().toString();
                            ArrayList<String> list = new ArrayList<String>();
                            for(int i=0;i<layout.getChildCount();i++) {
                                list.add(((EditText)layout.getChildAt(i)).getText().toString());
                            }
                            FeedListActivity.feedItemList.get(num).setTitle(title);
                            FeedListActivity.feedItemList.get(num).setItems(list);
                            FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                            FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                            SurveyCheckDialogFragment.init(tv.getText().toString(),num).show(getFragmentManager(), "tag");
                        }
                    });
            return builder.create();
        }
    }

    public static class AnswerDialogFragment extends DialogFragment {
        private LinearLayout layout;
        private TextView tv;
        String title;
        int num,c;
        boolean check=false;

        public static AnswerDialogFragment init(String title, int num){
            AnswerDialogFragment dialog = new AnswerDialogFragment();
            dialog.title=title;
            dialog.num=num;
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_answer_dialog, null);
            layout = (LinearLayout)view.findViewById(R.id.layouts);
            tv = (TextView)view.findViewById(R.id.title);
            tv.setText(title);
            setting();
            builder.setView(view)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (FeedListActivity.feedItemList.get(num).getType().equals("단답형")) {
                                sendAnswer();
                            } else if (CountCheck()) {
                                sendAnswer();
                            }
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
        public void setting(){
            JSONArray arr = new JSONArray();
            try {
                JSONObject obj = FeedListActivity.surveyData.getJSONObject(title);
                arr = obj.getJSONArray("Data");
                check = true;
            } catch (JSONException e) {
            }
            if(FeedListActivity.feedItemList.get(num).getType().equals("단답형")){
                EditText et = new EditText(FeedListActivity.co);
                et.setHint("답변란");
                et.setFilters(new InputFilter[]{filters.filterKEN,new InputFilter.LengthFilter(45)});
                try {
                    et.setText(arr.getString(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                layout.addView(et);
                return;
            }
            ArrayList<String> list = FeedListActivity.feedItemList.get(num).getItems();
            for(int i=0;i<list.size();i++){
                CheckBox t = new CheckBox(FeedListActivity.co);
                t.setText(list.get(i));
                layout.addView(t);
                for(int j=0;j<arr.length();j++){
                    try {
                        if(list.get(i).equals(arr.get(j))){
                            t.setChecked(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private boolean CountCheck(){
            int count=0;
            for(int i=0;i<layout.getChildCount();i++){
                if(((CheckBox)layout.getChildAt(i)).isChecked()){
                    count++;
                }
            }
            if(count>FeedListActivity.feedItemList.get(num).getCheckable()){
                Toast.makeText(FeedListActivity.co, FeedListActivity.feedItemList.get(num).getCheckable() + "개 까지만 선택하셔야 합니다!", Toast.LENGTH_LONG).show();
                AnswerDialogFragment.init(tv.getText().toString(),num).show(getFragmentManager(), "tag");
                return false;
            }
            return true;
        }

        private void sendAnswer(){
            if(FeedListActivity.feedItemList.get(num).getType().equals("단답형")){
                String str =(((EditText) layout.getChildAt(0)).getText().toString());
                JSONObject obj = new JSONObject();
                try {
                    obj.put("Data", new JSONArray().put(str));
                    FeedListActivity.surveyData.put(title, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(!check){
                    FeedListActivity.total++;
                    FeedListActivity.toolbar.setTitle(FeedListActivity.total+"/"+FeedListActivity.feedItemList.size()+"  "+FeedListActivity.surveyName);
                }
                return;
            }
            JSONArray list=new JSONArray();
            for(int i=0;i<layout.getChildCount();i++) {
                if (((CheckBox) layout.getChildAt(i)).isChecked()) {
                    list.put(((CheckBox) layout.getChildAt(i)).getText().toString());
                }
            }
            JSONObject obj = new JSONObject();
            try {
                obj.put("Data", list);
                FeedListActivity.surveyData.put(title, obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(!check){
                FeedListActivity.total++;
                FeedListActivity.toolbar.setTitle(FeedListActivity.total+"/"+FeedListActivity.feedItemList.size()+"  "+FeedListActivity.surveyName);
            }
        }
    }

}