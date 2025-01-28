package com.devdroid.cardviewexmp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SMS_PERMISSION_CODE = 1;
    static final int LOCATION_PERMISSION_CODE = 2;

    private FusedLocationProviderClient fusedLocationClient;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD = 15.0f;// hereeee
    private boolean isSendingMessage = false;

    private MyDBHelper databaseHelper;
    AdView adview;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore state here if needed
        setContentView(R.layout.activity_main);


        // Initialize FloatingActionButtons
        FloatingActionButton button1 = findViewById(R.id.fab1);
        FloatingActionButton button2 = findViewById(R.id.fab2);
        FloatingActionButton button3 = findViewById(R.id.fab3);
        FloatingActionButton button4 = findViewById(R.id.fab4);
        FloatingActionButton button5 = findViewById(R.id.fab5);
        FloatingActionButton button6 = findViewById(R.id.fab6);
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize sensor manager and accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Initialize MediaPlayer with the alarm file
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setVolume(1.0f, 1.0f); // Set volume to maximum

        // Initialize database helper
        databaseHelper = new MyDBHelper(this);

        // Request necessary permissions
        requestPermissions();

        // Set click listeners for each button

        // FloatingActionButton 1: Handle click event for contacts
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    // Show a Toast message (for example)
                    Toast.makeText(MainActivity.this, "Contacts Button Clicked", Toast.LENGTH_SHORT).show();

                    // You can add any action you want here, for example:
                    // Intent to open the contacts screen (you can define your own screen)
                    Intent intent = new Intent(MainActivity.this, ConatctPage.class);
                    startActivity(intent);
                }
            });


        // FloatingActionButton 2: Handle click event for calling
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a Toast message (for example)
                Toast.makeText(MainActivity.this, "Calling Button Clicked", Toast.LENGTH_SHORT).show();
                // You can add any action you want here, for example:
                //Intent to make a call (if you want to initiate a call, uncomment below)
                 Intent intent = new Intent(Intent.ACTION_DIAL);
                  intent.setData(Uri.parse("tel:+7276084054"));
                  startActivity(intent);
            }
        });
//click event for google map
        // Inside MainActivity's onCreate method
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a Toast message (optional)
                Toast.makeText(MainActivity.this, "Google Map Button Clicked", Toast.LENGTH_SHORT).show();

                // Create an Intent to open GoogleMaps activity
                Intent intent = new Intent(MainActivity.this, GoogleMapsActivity .class);
                startActivity(intent);
            }
        });

        // click event for video manager
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });


// click event for alarm music
        // FloatingActionButton 5: Handle click event for playing and stopping alarm music
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    // Stop the music if it is playing
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0); // Optional: reset to the beginning of the track
                    Toast.makeText(MainActivity.this, "Music Stopped", Toast.LENGTH_SHORT).show();
                } else {
                    // Start playing music if it is not playing
                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "Playing Loud Music", Toast.LENGTH_SHORT).show();
                }
            }
        });

//click event for button 6 laws....

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Laws Page Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LawsPage.class);
                startActivity(intent);
            }
        });





        // Handle back press with a confirmation dialog
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });

        //customtoolbar


        Toolbar toolbar = findViewById(R.id.toolbar);
        //step 1;
        setSupportActionBar(toolbar);


        //step 2;
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("SAFE HER");
        }
       // toolbar.setSubtitle("MADE BY ANA");



        //ads

        adview =findViewById(R.id.adview);
        //initialize
        MobileAds.initialize(this);
        //request
        AdRequest adRequest = new AdRequest.Builder().build();
        //load
        adview.loadAd(adRequest);



    }


    // menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the toolbar
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle menu item clicks here */
        int id = item.getItemId();
        if (id == R.id.rating) {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.share){
            Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    private void showExitDialog() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setTitle("EXIT?");
        exitDialog.setMessage("Do you want to exit?");
        exitDialog.setIcon(R.drawable.exit);

        exitDialog.setPositiveButton("NO", (dialog, which) ->
                Toast.makeText(MainActivity.this, "Welcome back", Toast.LENGTH_LONG).show());

        exitDialog.setNegativeButton("YES", (dialog, which) -> finish()); // Exit the activity

        exitDialog.setNeutralButton("CANCEL", (dialog, which) ->
                Toast.makeText(MainActivity.this, "Operation Cancelled", Toast.LENGTH_LONG).show());

        exitDialog.show();
    }



    private void requestPermissions() {
        // Request SMS and location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float totalForce = (float) Math.sqrt(x * x + y * y + z * z);

        if (totalForce > SHAKE_THRESHOLD && !isSendingMessage) {
            Log.d("ShakeDetection", "Shake detected with force: " + totalForce);
            sendMessage();
        }
    }

    private void sendMessage() {
        isSendingMessage = true;

        // Retrieve contacts from the database
        Cursor cursor = databaseHelper.getAllContacts();
        if (cursor != null && cursor.moveToFirst()) {

            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY).build();
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationRequest locationRequest2 = locationRequest.setInterval(10000);
            LocationRequest locationRequest1 = locationRequest.setFastestInterval(5000);

            // Get the current location and create a Google Maps link
            //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                String locationUrl = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                                String message = "HELP ME I AM IN DANGER!\n" + "My location: " + locationUrl;
                                SmsManager smsManager = SmsManager.getDefault();

                                // Use LocationRequest for better accuracy and recent data


                                // Send the message to each contact
                                do {
                                    String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.KEY_PHONE_NO));
                                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                                        Toast.makeText(MainActivity.this, "Message sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "SMS permission denied", Toast.LENGTH_SHORT).show();
                                    }
                                } while (cursor.moveToNext());

                                cursor.close();
                            } else {
                                Toast.makeText(MainActivity.this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                            }

                            // Reset the flag after a delay
                            new Handler().postDelayed(() -> isSendingMessage = false, 2000);
                        }
                    });

            // Start location updates

        } else {
            Toast.makeText(this, "No contacts found in the database", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> isSendingMessage = false, 2000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE || requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


