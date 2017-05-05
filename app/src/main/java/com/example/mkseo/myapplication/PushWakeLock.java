package com.example.mkseo.myapplication;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by mkseo on 2017. 5. 4..
 */

public class PushWakeLock {

    private static PowerManager.WakeLock sCpuWakeLock;
    private static KeyguardManager.KeyguardLock mKeyguardLock;
    private static boolean isScreenLock;

    static public void acquireCpuWakeLock(Context context) {
        Log.d("PushWakeLock", "Acquiring cpu wake lock");
        Log.d("PushWakeLock", "wake sCpuWakeLock = " + sCpuWakeLock);

        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");

        sCpuWakeLock.acquire(3000);

    }

    static public void releaseCpuLock() {
        Log.d("PushWakeLock", "Releasing cpu wake lock");
        Log.d("PushWakeLock", "release sCpuWakeLock = " + sCpuWakeLock);

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }

}
