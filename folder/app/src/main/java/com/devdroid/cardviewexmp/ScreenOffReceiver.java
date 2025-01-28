package com.devdroid.cardviewexmp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ScreenOffReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenOffReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the screen is turned off
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            // Log the action to confirm it's being received
            Log.d(TAG, "Screen turned off. Starting VolumeKeyService...");

            // Create an intent to start the VolumeKeyService
            Intent serviceIntent = new Intent(context, VolumeKeyService.class);

            // For Android 8.0 (API level 26) and above, background services need to be started differently
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Use startForegroundService for Android O and above
                context.startForegroundService(serviceIntent);
            } else {
                // For lower versions, you can use startService directly
                context.startService(serviceIntent);
            }
        }
    }
}
