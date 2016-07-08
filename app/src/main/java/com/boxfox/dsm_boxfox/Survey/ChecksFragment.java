package com.boxfox.dsm_boxfox.Survey;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dsm_boxfox.R;


public class ChecksFragment extends DialogFragment {
    private String title;
    public static ChecksFragment init(String text){
        ChecksFragment checks = new ChecksFragment();
        checks.title=text;
        return checks;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_checks, null);
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.layouts);
        ((TextView)view.findViewById(R.id.textView22)).setText(title);
        builder.setView(view)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

}
