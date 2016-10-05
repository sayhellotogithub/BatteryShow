package com.iblogstreet.batteryshow;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import com.iblogstreet.batteryshow.utils.Loger;
import com.iblogstreet.batteryshow.view.BatteryViewSelf;


/**
 * Created by Administrator on 2016/10/5.
 */

public class BatteryActivity
        extends Activity
{
    private static final String TAG ="BatteryActivity" ;
    private BatteryViewSelf mBatteryView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        mBatteryView = (BatteryViewSelf) findViewById(R.id.battery_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregister();
    }

    private void register() {
        registerReceiver(batteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void unregister() {
        unregisterReceiver(batteryChangedReceiver);
    }

    // 接受广播
    private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                // BatteryManager.BATTERY_STATUS_CHARGING 表示是充电状态
                // BatteryManager.BATTERY_STATUS_DISCHARGING 放电中
                // BatteryManager.BATTERY_STATUS_NOT_CHARGING 未充电
                // BatteryManager.BATTERY_STATUS_FULL 电池满
                int status = intent.getIntExtra("status", 0); // 电池状态
                Loger.e(TAG, "status" + status);
                //BatteryManager.BATTERY_STATUS_CHARGING;
                int power = level * 100 / scale;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    //充电中
                    Loger.e(TAG, "充电中");
                    mBatteryView.setCharging(true);
                    mBatteryView.setPower(power);
                } else {
                    Loger.e(TAG, "充电完成");

                    mBatteryView.setCharging(false);
                    mBatteryView.setPower(power);
                }
            }
        }
    };
}
