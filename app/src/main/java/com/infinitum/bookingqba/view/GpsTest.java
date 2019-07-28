package com.infinitum.bookingqba.view;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.base.LocationActivity;


import timber.log.Timber;

public class GpsTest extends AppCompatActivity {

    // UI Widgets.
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;

    private static final String TAG = GpsTest.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("======>> Activity onCreate");
//        setContentView(R.layout.activity_gps_test);
//
//        // Locate the UI widgets.
//        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
//        mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);
//        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
//        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
//        findViewById(R.id.start_act).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(GpsTest.this,SecondActivity.class));
//            }
//        });
//
//        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLocationRequestUnknowPermission();
//            }
//        });
//
//        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                locationHelpers.stopLocationUpdates();
//            }
//        });
    }

//    @Override
//    protected void updateLocation(Location location) {
//        Toast.makeText(GpsTest.this, "Location Changes",
//                Toast.LENGTH_SHORT).show();
//    }


}
