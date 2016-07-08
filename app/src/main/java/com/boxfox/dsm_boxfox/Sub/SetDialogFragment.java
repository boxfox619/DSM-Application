package com.boxfox.dsm_boxfox.Sub;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfox.dsm_boxfox.MainActivity;
import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import org.json.JSONObject;

import java.net.Socket;

public class SetDialogFragment extends DialogFragment {
    private LinearLayout layout;
    private TextView tv;
    private View view;
    private EditText et,et2;
    private String title,text;

    public static SetDialogFragment init(String title,String text){
        SetDialogFragment dialog = new SetDialogFragment();
        dialog.title = title;
        dialog.text = text;
        return dialog;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_answer_dialog, null);
        layout = (LinearLayout) view.findViewById(R.id.layouts);
        et = new EditText(getContext());
        et2 = new EditText(getContext());
        setting();
        builder.setView(view)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (et.getText().length() > 0&&et.getText().length()<15) {
                            if(et.getText().toString().equals(et2.getText().toString()))
                                new Send(et.getText().toString()).start();
                            else
                                Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "한글자 이상,15자 미만 입력해 주세요!", Toast.LENGTH_LONG).show();
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

    public void setting() {
        tv = (TextView) view.findViewById(R.id.title);
        ((TextView) view.findViewById(R.id.textView3)).setText("   수정   ");
        tv.setText(title);
        et.setHint(text);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et2.setHint(text+"확인");
        et2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(et);
        layout.addView(et2);
    }

    private class Send extends Thread {
        String data;
        Send(String data){
            this.data = data;
        }
        @Override
        public void run(){
            if(data.contains("--"))
                data = data.replaceAll("--","-");
            try{
                Socket soc = new Socket(start.server, start.port);
                JSONObject commend = new JSONObject();
                commend.put("Command", 1206);
                commend.put("Data", data);
                final String after = start.soc.readResultString(commend);
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (after.equals("719")) {
                            Snackbar.make((RelativeLayout) ((Activity)getContext()).findViewById(R.id.rootLayouts2), "데이터 수정에 성공했습니다.", Snackbar.LENGTH_SHORT)
                                    .setAction("확인", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    })
                                    .show();
                            setting();
                        } else if (after.equals("329")) {
                            Snackbar.make((RelativeLayout) ((Activity)getContext()).findViewById(R.id.rootLayouts2), "데이터 수정에 실패했습니다. (서버오류)", Snackbar.LENGTH_SHORT)
                                    .setAction("확인", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    })
                                    .show();
                        } else if (after.equals("e719")) {
                            Snackbar.make((RelativeLayout) ((Activity)getContext()).findViewById(R.id.rootLayouts2), "데이터 수정에 실패했습니다. (보안문제)", Snackbar.LENGTH_SHORT)
                                    .setAction("확인", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    })
                                    .show();
                        }
                    }
                });

            }catch(Exception e){
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make((RelativeLayout) getActivity().findViewById(R.id.rootLayouts2), "데이터 수정에 실패했습니다. (네트워크 오류)", Snackbar.LENGTH_SHORT)
                                .setAction("확인", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                })
                                .show();
                        ((MainActivity)getActivity().getParent()).getLogin().setLogged(false);
                    }
                });
                e.printStackTrace();
                return;
            }
        }
    }
}