package com.boxfox.dsm_boxfox.Sub;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.example.dsm_boxfox.R;

import java.util.Calendar;

public class MonthPickDialogFragment extends DialogFragment {
    private int year ,month;
    public static MonthPickDialogFragment init(int year, int month){
        MonthPickDialogFragment dialog = new MonthPickDialogFragment();
        dialog.year = year;
        dialog.month = month;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.month_picker, null);

        final NumberPicker monthPicker = (NumberPicker)view.findViewById(R.id.monthsPicker);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        monthPicker.setWrapSelectorWheel(false);
        final NumberPicker yearPicker = (NumberPicker)view.findViewById(R.id.yearPicker);
        yearPicker.setMaxValue(calendar.get(Calendar.YEAR) + 2);
        yearPicker.setMinValue(2014);
        yearPicker.setValue(calendar.get(Calendar.YEAR));

        builder.setView(view)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        month = monthPicker.getValue();
                        year = yearPicker.getValue();
                        ((PlansActivity)getActivity()).new ListInput().start();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}