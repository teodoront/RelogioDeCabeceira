package com.example.relogiodecabeceira;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Runnable mRunnable;
    private Handler mHandler = new Handler();
    private boolean mTicker = false;
    private boolean mLandscape = false;


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mViewHolder.textBattery.setText(String.format("%s%%", level));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Ocultando toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        this.mViewHolder.textHourMinute = findViewById(R.id.text_hour_minute);
        this.mViewHolder.textSeconds = findViewById(R.id.text_seconds);
        this.mViewHolder.textBattery = findViewById(R.id.text_battery);
        this.mViewHolder.textNight = findViewById(R.id.text_night);
        this.registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        this.mTicker = true;
        this.mLandscape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);


        this.startClock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mTicker = false;
        this.unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //Configurando p ficar full screen
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Esconde nav bar e status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    private void startClock() {
        final Calendar calendar = Calendar.getInstance();

        this.mRunnable = new Runnable() {
            @Override
            public void run() {

                if (!mTicker) {
                    return;
                }

                calendar.setTimeInMillis(System.currentTimeMillis());

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                int seconds = calendar.get(Calendar.SECOND);

                mViewHolder.textHourMinute.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minutes));
                mViewHolder.textSeconds.setText(String.format(Locale.getDefault(), "%02d", seconds));

                if (mLandscape) {
                    if (hour >= 18) {
                        mViewHolder.textNight.setVisibility(View.VISIBLE);
                    } else {
                        mViewHolder.textNight.setVisibility(View.GONE);
                    }

                    long now = SystemClock.elapsedRealtime();
                    long next = now + (1000 - (now % 1000));
                    mHandler.postAtTime(mRunnable, next);
                }
            }
        };
        this.mRunnable.run();
    }
}