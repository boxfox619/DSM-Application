package com.boxfox.dsm_boxfox.Sub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * Created by Administrator on 2016-02-12.
 */

public class UserDialogFragment extends DialogFragment {
    private String[] arr,filelink;
    private ArrayList<String> mSelectedItems;

    public static UserDialogFragment init(RealmResults<File> all){
        UserDialogFragment dialog = new UserDialogFragment();
        dialog.arr = new String[all.size()];
        dialog.filelink = new String[all.size()];
        if(all.size()>0){
            for(int i=0;i<all.size();i++){
                dialog.arr[i] = all.get(i).getFileName();
                dialog.filelink[i]=all.get(i).getFileLink();
            }
        }
        return dialog;
    }

    public static UserDialogFragment init2(String[] filelist, String[] filelink) {
        UserDialogFragment dialog = new UserDialogFragment();
        dialog.arr = filelist;
        dialog.filelink = filelink;
        return dialog;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList<String>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("다운받으실 첨부파일을 선택해 주세요")
                .setMultiChoiceItems(arr, null, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked)
                            mSelectedItems.add(arr[which]);
                        else if(mSelectedItems.contains(arr[which]))
                            mSelectedItems.remove(mSelectedItems.indexOf(arr[which]));
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < mSelectedItems.size(); i++) {
                            for(int j=0;j < arr.length;j++) {
                                if(arr[j].equalsIgnoreCase(mSelectedItems.get(i))){
                                    String Text = filelink[j];
                                    if(!Text.contains("http://dsm.hs.kr"))
                                        Text = "http://dsm.hs.kr"+Text;
                                    Uri uri = Uri.parse(Text);
                                    Intent it = new Intent(Intent.ACTION_VIEW,uri);
                                    startActivity(it);

                                    //new FileUrlDownlad("http://dsm.hs.kr"+filelink[j],filelist[j]).start();
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return builder.create();
    }
}