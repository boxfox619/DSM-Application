package com.boxfox.dsm_boxfox.Sub;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.boxfox.dsm_boxfox.Server.Cache;
import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class SettingActivity extends AppCompatActivity {
    Cache cache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        cache = new Cache(SettingActivity.this);
        JSONObject obj = null;
        try {
            obj = cache.Read("Setting");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(obj==null){
            obj = new JSONObject();
            try {
                obj.put("Broad",true);
                obj.put("Familer",true);
                obj.put("Mission",true);
                obj.put("Message",true);
                cache.Write("Setting",obj);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final JSONObject finalObj = obj;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        try {
            ((Switch)findViewById(R.id.broad_setting)).setChecked(finalObj.getBoolean("Broad"));
            ((Switch)findViewById(R.id.familer_setting)).setChecked(finalObj.getBoolean("Familer"));
            ((Switch)findViewById(R.id.mission_setting)).setChecked(finalObj.getBoolean("Mission"));
            ((Switch)findViewById(R.id.Message_setting)).setChecked(finalObj.getBoolean("Message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((Switch)findViewById(R.id.broad_setting)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    finalObj.put("Broad", isChecked);
                    cache.Write("Setting", finalObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ((Switch)findViewById(R.id.familer_setting)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    finalObj.put("Familer", isChecked);
                    cache.Write("Setting",finalObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ((Switch)findViewById(R.id.mission_setting)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    finalObj.put("Mission", isChecked);
                    cache.Write("Setting",finalObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ((Switch)findViewById(R.id.Message_setting)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    finalObj.put("Message", isChecked);
                    cache.Write("Setting",finalObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        getPermission();
    }

    private void getPermission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                AlertDialog.Builder ab = new AlertDialog.Builder(SettingActivity.this);
                ab.setMessage("푸쉬 알림 기능을 사용하기 위해 스마트폰의 정보가 필요합니다(기기의 아이디). 다음을 눌러 퍼미션 권한을 허용해 주세요");
                ab.setPositiveButton("다음", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int REQUEST_CODE = 2;
                        ActivityCompat.requestPermissions(
                                SettingActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                REQUEST_CODE);
                    }
                });
                ab.show();
                return;
            }
        }
        start.UUID = GetDevicesUUID(SettingActivity.this);
    }

    private String GetDevicesUUID(Context mContext){
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode==2){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                start.UUID = GetDevicesUUID(SettingActivity.this);
                AlertDialog.Builder ab = new AlertDialog.Builder(SettingActivity.this);
                ab.setMessage("이제 푸시 알림 기능을 사용할 수 있습니다. 설정을 통해 원하는 푸시 알림을 ON/OFF할 수 있습니다.");
                ab.setPositiveButton("확인", null);
                ab.show();
                registerGcm();
            } else {
                AlertDialog.Builder ab = new AlertDialog.Builder(SettingActivity.this);
                ab.setMessage("푸시 알림 기능을 사용할 수 없습니다. 푸쉬기능을 사용하기 위해선 설정의 애플리케이션 관리를 통해 권한을 허용해 주세요");
                ab.setPositiveButton("확인", null);
                ab.show();
                finish();
            }
        }
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

}
