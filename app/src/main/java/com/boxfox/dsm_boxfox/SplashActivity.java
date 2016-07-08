package com.boxfox.dsm_boxfox;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.boxfox.dsm_boxfox.Server.start;
import com.example.dsm_boxfox.R;

import java.util.UUID;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView img=(ImageView)findViewById(R.id.img);
        img.setImageResource(R.drawable.run);
        AnimationDrawable ani=(AnimationDrawable)img.getDrawable();
        ani.start();
        ImageView imgs = (ImageView)findViewById(R.id.imageView4);
        RotateAnimation rotate = new RotateAnimation(0, 36000, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000000);
        rotate.setInterpolator(new LinearInterpolator());
        imgs.startAnimation(rotate);
        getPermission();
        new start(this).start();
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog dialog;
            dialog = new AlertDialog.Builder(this).setTitle("종료확인")
                    // .setIcon(R.drawable.warning)
                    .setMessage("종료하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            finish();
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
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPermission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                AlertDialog.Builder ab = new AlertDialog.Builder(SplashActivity.this);
                ab.setMessage("푸쉬 알림 기능을 사용하기 위해 스마트폰의 정보가 필요합니다(기기의 아이디). 다음을 눌러 퍼미션 권한을 허용해 주세요");
                ab.setPositiveButton("다음", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int REQUEST_CODE = 2;
                        ActivityCompat.requestPermissions(
                                SplashActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                REQUEST_CODE);
                    }
                });
                ab.show();
                return;
            }
        }
        start.UUID = GetDevicesUUID(SplashActivity.this);
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
                start.UUID = GetDevicesUUID(SplashActivity.this);
                AlertDialog.Builder ab = new AlertDialog.Builder(SplashActivity.this);
                ab.setMessage("이제 푸시 알림 기능을 사용할 수 있습니다. 설정을 통해 원하는 푸시 알림을 ON/OFF할 수 있습니다.");
                ab.setPositiveButton("확인", null);
                ab.show();
            } else {
                AlertDialog.Builder ab = new AlertDialog.Builder(SplashActivity.this);
                ab.setMessage("푸시 알림 기능을 사용할 수 없습니다. 푸쉬기능을 사용하기 위해선 설정의 애플리케이션 관리를 통해 권한을 허용해 주세요");
                ab.setPositiveButton("확인", null);
                ab.show();
            }
        }
    }
}
