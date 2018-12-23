package com.example.music;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";

    Button buttonStart;
    Button buttonStop;
    Integer isPlay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        //Log.i("abc", "1");
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);

        // se bind cho activity khi service chay san
        // con khong thi ko bind
        bindServiceIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindServiceIfNeeded();
        mService = null;
    }
    //service bat dau chay khi nhan click
    @Override
    public void onClick(View v) {
        if (v == buttonStart&&!MyService.isServiceRunning()) {
            Intent intent = new Intent(MainActivity.this,MyService.class);
            intent.setAction(Constants.ACTION.PLAY_ACTION);
            startService(intent);
            //Toast.makeText(this,"U started new service",Toast.LENGTH_SHORT).show();
        } else if (v == buttonStop) {
           // unbind truoc
            // sau do stop
            unBindServiceIfNeeded();
            stopServiceIfNeeded();
        }
    }
    // tao 1 service connection_ket noi MyService voi Activity
    private ServiceConnection mServiceConnection;
    private MyService mService;

    private void bindServiceIfNeeded() {
        if(MyService.isServiceRunning()) {
            mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    // luu lai service
                    mService = ((MyService.MyBinder)service).getService();
                    Log.d(TAG, "onServiceConnected");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    // Do nothing
                    Log.d(TAG, "onServiceDisconnected");
                }
            };
            bindService(new Intent(this,MyService.class),mServiceConnection,BIND_AUTO_CREATE);
        }
    }

    private void unBindServiceIfNeeded() {
        if(mServiceConnection!=null&&MyService.isServiceRunning()) {
            unbindService(mServiceConnection);
            mServiceConnection = null;
        }
    }
    private void stopServiceIfNeeded() {
        if(MyService.isServiceRunning()) {
            stopService(new Intent(MainActivity.this,MyService.class));
        }
    }

}

