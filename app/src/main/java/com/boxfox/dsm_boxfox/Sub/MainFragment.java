package com.boxfox.dsm_boxfox.Sub;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boxfox.dsm_boxfox.Applications.DSMNotification;
import com.boxfox.dsm_boxfox.CheckInActivity;
import com.boxfox.dsm_boxfox.MainActivity;
import com.boxfox.dsm_boxfox.Meal.MealCalendarFragment;
import com.boxfox.dsm_boxfox.Server.Logined;
import com.example.dsm_boxfox.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class MainFragment extends android.support.v4.app.Fragment {
    public static View layoutView;
    View tv;
    int fragVal;

    public static MainFragment init(int val) {
        MainFragment truitonFrag = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_main, container,
                false);
        CardView homePage = (CardView)layoutView.findViewById(R.id.homepage);
        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WebActivity.class));
            }
        });

        CardView plan = (CardView)layoutView.findViewById(R.id.plan);
        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PlansActivity.class));
            }
        });

        CardView meal = (CardView)layoutView.findViewById(R.id.meal);
        meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MealCalendarFragment().test(((Activity) getContext()).getFragmentManager(), "tag", getContext());
            }
        });

        CardView mission = (CardView)layoutView.findViewById(R.id.mission);
        mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getContext(), MissionActivity.class);
                startActivity(in);
            }
        });

        CardView faceBoox = (CardView)layoutView.findViewById(R.id.facebook);
        faceBoox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.facebook.com/대덕소프트웨어마이스터고등학교-463920667081098/?fref=ts");
                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);
            }
        });

        CardView checkIn = (CardView)layoutView.findViewById(R.id.checkin);
        CardView cafe = (CardView)layoutView.findViewById(R.id.cafe);
        if(!(((MainActivity)getContext()).getLogin().islogin()&&((MainActivity)getContext()).getLogin().getPermission()== Logined.PERMISSION_STUDNET)){
            ((LinearLayout)layoutView.findViewById(R.id.MainMenu)).removeView(checkIn);
            ((LinearLayout)layoutView.findViewById(R.id.SubMenu)).removeView(cafe);
        }else{
            checkIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), CheckInActivity.class));
                }
            });
            cafe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("http://cafe.naver.com/onlyonedsm");
                    Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(it);
                }
            });
        }

        final LinearLayout notifications = (LinearLayout) layoutView.findViewById(R.id.notifications);
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
        RealmResults<DSMNotification> list = realm.where(DSMNotification.class).findAll();
        notifications.removeAllViews();
        if(list.size()==0){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.cardlist_notification, null);
            ((TextView)view.findViewById(R.id.title)).setText("Push Notification");
            ((TextView)view.findViewById(R.id.contexts)).setText("이곳에 푸시 알림이 나타납니다.");
            notifications.addView(view);
        }
        for(final DSMNotification notification : list){
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.cardlist_notification, null);
            ((TextView)view.findViewById(R.id.title)).setText(notification.getTitle());
            ((TextView)view.findViewById(R.id.contexts)).setText(notification.getContext());
            ((View)view.findViewById(R.id.card)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (notification.getType()) {
                        case DSMNotification.TYPE_BROAD:
                            ((MainActivity)getContext()).setCurrentItem(2);
                            break;
                        case DSMNotification.TYPE_FAMILER:
                            ((MainActivity)getContext()).setCurrentItem(1);
                            break;
                        case DSMNotification.TYPE_MISSION:
                            Intent in = new Intent(getContext(), MissionActivity.class);
                            startActivity(in);
                            break;
                    }
                    RealmConfiguration realmConfig = new RealmConfiguration.Builder(getContext()).build();
                    Realm.setDefaultConfiguration(realmConfig);
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.where(DSMNotification.class).equalTo("title", notification.getTitle()).equalTo("context", notification.getContext()).equalTo("type", notification.getType()).equalTo("milliSecond", notification.getTime()).findFirst().deleteFromRealm();
                    realm.commitTransaction();
                    notifications.removeView(view);
                    if(notifications.getChildCount()==0){
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.cardlist_notification, null);
                        ((TextView)view.findViewById(R.id.title)).setText("Push Notification");
                        ((TextView)view.findViewById(R.id.contexts)).setText("이곳에 푸시 알림이 나타납니다.");
                        notifications.addView(view);
                    }
                }
            });
            notifications.addView(view);
        }


/*
        if (((MainActivity) getContext()).getLogin().islogin()) {
                ((TextView)((Activity) getContext()).findViewById(R.id.testtextsss)).setText(((MainActivity) getContext()).getLogin().getName());
            btn4.setText("내정보");
            btn4.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_DOWN == event.getAction()) {
                        btn4.setBackgroundColor(Color.parseColor("#00BFFF"));
                    } else if (MotionEvent.ACTION_UP == event.getAction()) {
                        btn4.setBackgroundColor(Color.parseColor("#2196F3"));
                        startActivity(new Intent(getContext(), InformationActivity.class));
                    } else if (MotionEvent.ACTION_CANCEL == event.getAction()) {
                        btn4.setBackgroundColor(Color.parseColor("#2196F3"));
                    }
                    return false;
                }
            });
        }*/
        layoutView.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
        return layoutView;
    }
}