package com.boxfox.dsm_boxfox.Sub;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boxfox.dsm_boxfox.ArrayListFragment;
import com.boxfox.dsm_boxfox.MainActivity;
import com.boxfox.dsm_boxfox.Server.Cache;
import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class CardListFragment extends android.support.v4.app.Fragment {
    private View layoutView;
    private int fragVal;
    private LinearLayout layout;
    private int page;
    public static final int PAGE_FAMILER =1;
    public static final int PAGE_BROAD =2;
    private static Cache cache;

    public static CardListFragment init(int val){
        CardListFragment dialog = new CardListFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        dialog.setArguments(args);
        dialog.page= val;
        dialog.cache = new Cache(dialog.getContext());
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_card_list, container,
                false);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).build();
        Realm realm;
        try {
            realm = Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfiguration);
                realm =  Realm.getInstance(realmConfiguration);
            } catch (Exception ex){
                throw ex;
            }
        }
        layout = (LinearLayout)layoutView.findViewById(R.id.recycler);
        layout.removeAllViews();
        Button btn = (Button)layoutView.findViewById(R.id.more);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting();
            }
        });
        for(int i=0;i<10;i++){
            final FeedItem2 item = realm.where(FeedItem2.class).equalTo("type",page).equalTo("num", i).findFirst();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.cardlist_row, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView date = (TextView) view.findViewById(R.id.contexts);
            ImageButton downbtn = ((ImageButton)view.findViewById(R.id.download_btn));
            title.setText(Html.fromHtml(item.getTitle()));
            date.setText(Html.fromHtml(item.getDate()));
            downbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).build();
                    Realm realm;
                    try {
                        realm = Realm.getInstance(realmConfiguration);
                    } catch (RealmMigrationNeededException e){
                        try {
                            Realm.deleteRealm(realmConfiguration);
                            realm =  Realm.getInstance(realmConfiguration);
                        } catch (Exception ex){
                            throw ex;
                        }
                    }
                    if(realm.where(File.class).equalTo("fileType",item.getType()).equalTo("num", item.getNum()).findAll().size()==0){
                        new FileMissilesDialogFragment().show(((MainActivity) getContext()).getFragmentManager(), "tag");
                    }else UserDialogFragment.init(realm.where(File.class).equalTo("fileType",item.getType()).equalTo("num", item.getNum()).findAll()).show(getActivity().getFragmentManager(), "tag");
                }
            });
            ((View)view.findViewById(R.id.postcard)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getContext(), InfoActivity.class);
                    in.putExtra("Position", item.getNum());
                    in.putExtra("TypeCheck", ((MainActivity) getContext()).getCurrentPage());
                    startActivity(in);
                }
            });
            layout.addView(view);
        }
        return layoutView;
    }
    private void setting() {
        int count = layout.getChildCount();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).build();
        Realm realm;
        try {
            realm = Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfiguration);
                realm =  Realm.getInstance(realmConfiguration);
            } catch (Exception ex){
                throw ex;
            }
        }
        int size = realm.where(FeedItem2.class).equalTo("type", ((MainActivity)getContext()).getCurrentPage()).findAll().size();
        if (count == size) return;
        int i = count;
        count += 10;
        int max = count;
        if (count > size) max = size;
        for (; i < max; i++) {
            final FeedItem2 item = realm.where(FeedItem2.class).equalTo("type",page).equalTo("num", i).findFirst();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.cardlist_row, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView date = (TextView) view.findViewById(R.id.contexts);
            ImageButton downbtn = ((ImageButton)view.findViewById(R.id.download_btn));
            title.setText(Html.fromHtml(item.getTitle()));
            date.setText(Html.fromHtml(item.getDate()));
            downbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).build();
                    Realm realm;
                    try {
                        realm = Realm.getInstance(realmConfiguration);
                    } catch (RealmMigrationNeededException e){
                        try {
                            Realm.deleteRealm(realmConfiguration);
                            realm =  Realm.getInstance(realmConfiguration);
                        } catch (Exception ex){
                            throw ex;
                        }
                    }
                    if(realm.where(File.class).equalTo("fileType",item.getType()).equalTo("num", item.getNum()).findAll().size()==0){
                        new FileMissilesDialogFragment().show(((MainActivity) getContext()).getFragmentManager(), "tag");
                    }else
                        UserDialogFragment.init(realm.where(File.class).equalTo("fileType",item.getType()).equalTo("num", item.getNum()).findAll()).show(getActivity().getFragmentManager(), "tag");
                }
            });
            ((View)view.findViewById(R.id.postcard)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getContext(), InfoActivity.class);
                    in.putExtra("Position", item.getNum());
                    in.putExtra("TypeCheck", ((MainActivity) getContext()).getCurrentPage());
                    startActivity(in);
                }
            });
            layout.addView(view);
        }
    }
}