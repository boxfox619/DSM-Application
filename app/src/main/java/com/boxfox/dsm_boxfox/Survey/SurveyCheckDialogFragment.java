package com.boxfox.dsm_boxfox.Survey;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsm_boxfox.R;

import java.util.ArrayList;


public class SurveyCheckDialogFragment extends DialogFragment {
    private LinearLayout layout;
    private TextView tv;
    private String title;
    private int num;
    public static SurveyCheckDialogFragment init(String title,int num) {
        SurveyCheckDialogFragment dialog = new SurveyCheckDialogFragment();
        dialog.title = title;
        dialog.num = num;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_survey_check_dialog, null);
        layout = (LinearLayout)view.findViewById(R.id.layouts);
        tv = (TextView)view.findViewById(R.id.title);
        tv.setText(title);
        setting();
        builder.setView(view)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FeedListActivity.feedItemList.remove(num);
                        FeedListActivity.adapter = new MyRecyclerAdapter(FeedListActivity.co, FeedListActivity.feedItemList);
                        FeedListActivity.mRecyclerView.setAdapter(FeedListActivity.adapter);
                    }
                });
        return builder.create();
    }
    public void setting(){
        if(FeedListActivity.feedItemList.get(num).getType().equals("단답형")){
            EditText et = new EditText(FeedListActivity.co);
            et.setHint("답변란");
            layout.addView(et);
            return;
        }
        ArrayList<String> list = FeedListActivity.feedItemList.get(num).getItems();
        for(int i=0;i<list.size();i++){
            CheckBox t = new CheckBox(FeedListActivity.co);
            t.setText(list.get(i));
            layout.addView(t);
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
            Toast.makeText(FeedListActivity.co, FeedListActivity.feedItemList.get(num).getCheckable() + "개를 초과해 선택할 수 없습니다!", Toast.LENGTH_LONG).show();
            SurveyCheckDialogFragment.init(tv.getText().toString(),num).show(getFragmentManager(), "tag");
            return false;
        }
        return true;
    }
}
