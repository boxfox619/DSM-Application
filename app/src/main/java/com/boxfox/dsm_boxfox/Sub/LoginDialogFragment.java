package com.boxfox.dsm_boxfox.Sub;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.boxfox.dsm_boxfox.MainActivity;
import com.example.dsm_boxfox.R;
import com.boxfox.dsm_boxfox.Server.Logined;
import com.boxfox.dsm_boxfox.Server.start;

import org.json.JSONObject;

import java.util.regex.Pattern;



public class LoginDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_login_dialog, null);
        final EditText Id = (EditText)view.findViewById(R.id.username);
        final EditText Password = (EditText)view.findViewById(R.id.password);
        Id.setFilters(new InputFilter[]{filterAlpha2});
        Password.setFilters(new InputFilter[]{filterAlpha2});


        builder.setView(view)
                .setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (start.Network) {
                            Logging l = new Logging(Id.getText().toString(), Password.getText().toString());
                            l.start();
                            while (l.end) {
                            }
                            if (((MainActivity) getActivity()).getLogin().islogin()) {
                                ((MainActivity) getActivity()).Message("로그인 성공!");
                                ((MainActivity) getActivity()).reset();
                            }
                        } else {
                            ((MainActivity) getActivity()).Message("네트워크 오류!");
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    protected InputFilter filterAlpha2 = new InputFilter(){

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9@.]+$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

    private class Logging extends Thread {
        boolean end=true;
        String id,password;
        Logging(String id, String password){
            this.id = id;
            this.password = password;
        }
        @Override
        public void run(){
            try{
                JSONObject commend = new JSONObject();
                commend.put("Command", 750);
                commend.put("ID", id);
                commend.put("Password",password);
                commend.put("UUID", start.UUID);
                JSONObject after = start.soc.readResult(getActivity(),commend).getObj();
                String check = after.getString("Command");
                if(check.equals("120")){
                    ((MainActivity) getActivity()).Message(getResources().getString(R.string.worng_password));
                }else if(check.equals("100")){
                    ((MainActivity) getActivity()).Message(getResources().getString(R.string.havent_id));
                }else if(check.equals("e877")){
                    ((MainActivity) getActivity()).Message(getResources().getString(R.string.login_err));
                }else{
                    String permission;
                    permission = after.getString("Permission");
                    ((MainActivity) getActivity()).setLogin(new Logined());
                    ((MainActivity) getActivity()).getLogin().setID(id);
                    ((MainActivity) getActivity()).getLogin().setLogged(true);
                    ((MainActivity) getActivity()).getLogin().setPermission(permission);
                }
            }catch(Exception e){
                ((MainActivity) getActivity()).Message(getResources().getString(R.string.network_err));
                e.printStackTrace();
            }
            end=false;
        }
    }

}
