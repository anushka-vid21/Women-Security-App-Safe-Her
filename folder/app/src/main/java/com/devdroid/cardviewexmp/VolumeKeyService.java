package com.devdroid.cardviewexmp;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

public class VolumeKeyService extends AccessibilityService {

    private static final String TAG = "VolumeKeyService";
    private static final int LONG_PRESS_THRESHOLD = 2000; // milliseconds
    private long volumeDownPressTime;
    private LocationManager locationManager;
    private MyDBHelper dbHelper;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not used, but required override
    }

    @Override
    public void onInterrupt() {
        // Required override, no specific action needed here
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d(TAG, "KeyEvent detected: " + event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                volumeDownPressTime = System.currentTimeMillis();
                Log.d(TAG, "Volume Down pressed at: " + volumeDownPressTime);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                long pressDuration = System.currentTimeMillis() - volumeDownPressTime;
                Log.d(TAG, "Volume Down released, duration: " + pressDuration + " ms");
                if (pressDuration >= LONG_PRESS_THRESHOLD) {
                    Log.d(TAG, "Long press detected, performing emergency action");
                    performEmergencyActionIfLocked();
                }
            }
        }
        return super.onKeyEvent(event);
    }

    private void performEmergencyActionIfLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
            Log.d(TAG, "Screen is locked, performing emergency action");
            confirmActionWithVibration();
            List<String> contacts = dbHelper.getAllNonEmptyContacts();
            Log.d(TAG, "Fetched contacts: " + contacts.size());
            if (contacts.isEmpty()) {
                Log.d(TAG, "No contacts available, aborting SMS sending");
                return;
            }
            sendEmergencySmsWithLocation(contacts);
        } else {
            Log.d(TAG, "Screen is not locked; emergency action not triggered.");
        }
    }

    private void confirmActionWithVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                Log.d(TAG, "Vibration triggered with Android O+ API");
            } else {
                vibrator.vibrate(500);
                Log.d(TAG, "Vibration triggered with older Android version");
            }
        } else {
            Log.d(TAG, "Vibrator service not available");
        }
    }

    private void sendEmergencySmsWithLocation(List<String> contacts) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {
            try {
                Log.d(TAG, "Requesting single location update");
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        String locationMessage = "Emergency! My location: https://maps.google.com/?q=" +
                                location.getLatitude() + "," + location.getLongitude();
                        Log.d(TAG, "Location fetched: " + location.getLatitude() + ", " + location.getLongitude());

                        for (String contact : contacts) {
                            Log.d(TAG, "Sending SMS to: " + contact);
                            sendSms(contact, locationMessage);
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.d(TAG, "Location provider status changed: " + provider + " " + status);
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        Log.d(TAG, "Location provider enabled: " + provider);
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        Toast.makeText(VolumeKeyService.this, "Location service is disabled.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Location provider disabled: " + provider);
                    }
                }, Looper.getMainLooper());
            } catch (SecurityException e) {
                Toast.makeText(this, "Location permission required.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Location permission not granted.", e);
            }
        } else {
            Log.e(TAG, "Location manager not available");
        }
    }

    private void sendSms(String phoneNumber, String message) {
        Log.d(TAG, "Sending SMS to " + phoneNumber + ": " + message);
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Emergency message sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SMS sent to: " + phoneNumber);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send message to " + phoneNumber, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "SMS send failed for " + phoneNumber, e);
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Service is connected.");
        // Initialize database helper and accessibility info
        dbHelper = new MyDBHelper(this);

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
        Log.d(TAG, "VolumeKeyService connected and initialized");
        Toast.makeText(this, "Volume Key Service Started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "VolumeKeyService Started ");
    }
}
