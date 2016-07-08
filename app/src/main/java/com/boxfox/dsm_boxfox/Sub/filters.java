package com.boxfox.dsm_boxfox.Sub;


import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

public class filters {
    public static InputFilter phone = new InputFilter(){

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[0-9-]+$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

    public static InputFilter number = new InputFilter(){

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[0-9]+$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

    public static InputFilter filterEN = new InputFilter(){

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9@.]+$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

    public static InputFilter filterKE = new InputFilter(){

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅎ가-흐]+$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

    public static InputFilter filterKEN = new InputFilter(){

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅎ가-흐0-9]+$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

    public static InputFilter filterK = new InputFilter(){

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅎ가-흐]+$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };



}