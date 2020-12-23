package com.example.relogiodecabeceira;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Runnable mRunnable;
    private Handler mHandler = new Handler();
    private boolean mTicker = false;


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

        this.mViewHolder.textHourMinute = findViewById(R.id.text_hour_minute);
        this.mViewHolder.textSeconds = findViewById(R.id.text_seconds);
        this.mViewHolder.textBattery = findViewById(R.id.text_battery);

        this.registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        this.mTicker = true;
        this.startClock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mTicker = false;
        this.unregisterReceiver(this.mReceiver);
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

                long now = SystemClock.elapsedRealtime();
                long next = now + (1000 - (now % 1000));
                mHandler.postAtTime(mRunnable, next);
            }
        };
        this.mRunnable.run();
    }
}