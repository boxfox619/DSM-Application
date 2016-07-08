package com.boxfox.dsm_boxfox;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.boxfox.dsm_boxfox.Adapters.SelectionsAdapter;
import com.boxfox.dsm_boxfox.Applications.ApplicationActivity;
import com.boxfox.dsm_boxfox.Server.Logined;
import com.boxfox.dsm_boxfox.Server.start;
import com.boxfox.dsm_boxfox.Sub.CardListFragment;
import com.boxfox.dsm_boxfox.Sub.LoginDialogFragment;
import com.boxfox.dsm_boxfox.Sub.MainFragment;
import com.boxfox.dsm_boxfox.Sub.SettingActivity;
import com.example.dsm_boxfox.R;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private ViewPager pager;

    private NavigationView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.nav_header, null);
        try {
            registerGcm();
        }catch (RuntimeException e){
        }
        initToolbar();
        initInstances();
        setting();
        initTabHost();
    }

    public Logined getLogin(){
        return start.logined;
    }

    public void setLogin(Logined lo){
        start.logined=lo;
    }

    public void setCurrentItem(int i){
        pager.setCurrentItem(i);
    }

    public int getCurrentPage(){
        return pager.getCurrentItem();
    }

    private void initTabHost(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SelectionsAdapter mSectionsPagerAdapter = new SelectionsAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.container);
        mSectionsPagerAdapter.addFrag(MainFragment.init(0), "DSM");
        mSectionsPagerAdapter.addFrag(CardListFragment.init(1), "가정통신문");
        mSectionsPagerAdapter.addFrag(CardListFragment.init(2), "공지사항");
        mSectionsPagerAdapter.addFrag(ArrayListFragment.init(3), "설문조사");
        pager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabhost);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public void registerGcm() throws RuntimeException{
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        String regId = GCMRegistrar.getRegistrationId(this);

        if (regId.equals("")) {
            GCMRegistrar.register(this, "977100336743");
            regId = GCMRegistrar.getRegistrationId(this);

        } else {
        }
        final String finalRegId = regId;
        if(!(start.UUID.equals("null")||start.UUID==null||start.UUID.length()==0))
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject commend = new JSONObject();
                    commend.put("Command",1219);
                    commend.put("UUID", start.UUID);
                    commend.put("RegID", finalRegId);
                    start.soc.onlyWrite(commend);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }



    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            if(pager.getCurrentItem()==3&& ArrayListFragment.checks==1){
                pager.setCurrentItem(0);
            }else if(pager.getCurrentItem()==3) {
                pager.setCurrentItem(0);
            }else if(pager.getCurrentItem()==1){
                pager.setCurrentItem(0);
            }else if(pager.getCurrentItem()==1||pager.getCurrentItem()==2||pager.getCurrentItem()==3||pager.getCurrentItem()==0||pager.getCurrentItem()==0) {

                AlertDialog dialog;
                dialog = new AlertDialog.Builder(this).setTitle("종료확인")
                        // .setIcon(R.drawable.warning)
                        .setMessage("종료하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                start.soc.close();
                                moveTaskToBack(true);
                                finish();
                                //ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                //am.restartPackage(getPackageName());
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                            }
                        })
                        .show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void setting(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInstances() {
/*        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        view = (NavigationView) findViewById(R.id.navigation);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem == findViewById(R.id.ghost))
                    return true;
                if (!menuItem.isChecked()) {
                    ItemChecking(menuItem);
                    drawerLayout.closeDrawers();
                }
                return true;
            }
        });*/


       FloatingActionButton fabBtn = (FloatingActionButton) findViewById(R.id.fab);
        fabBtn.hide();
        /*fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.islogin() && login.getPermission() == "A") {
                    MyRecyclerAdapter.check = 1;
                    FeedListActivity.feedItemList = new ArrayList<FeedItem>();
                    Intent in = new Intent(MainActivity.this, FeedListActivity.class);
                    startActivity(in);
                } else {
                    Toast.makeText(MainActivity.co, "설문조사는 관리자만 작성이 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fabBtn.animate().translationY(fabBtn.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
        fabBtn.hide();
        if(login.islogin()&&login.getPermission()=="A"){
            fabBtn.show();
        }else {
            fabBtn.hide();
        }
        */


        //collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        //collapsingToolbarLayout.setTitle("DSM");

    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void ItemChecking(MenuItem menuItem){
        int a = menuItem.getItemId();
    }

    public void Message(String s){
        Snackbar.make(((CoordinatorLayout)findViewById(R.id.main_content)), s, Snackbar.LENGTH_LONG)
                .setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }else if(id == R.id.action_login){
            new LoginDialogFragment().show(getFragmentManager(), "tag");
            setCurrentItem(0);
        }else if(id == R.id.action_appinfo){
            Intent in = new Intent(MainActivity.this, ApplicationActivity.class);
            startActivity(in);
        }

        return super.onOptionsItemSelected(item);
    }

    public void reset() {
        initTabHost();
    }
}
