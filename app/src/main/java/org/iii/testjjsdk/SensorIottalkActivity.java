package org.iii.testjjsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.jorjin.jjsdk.sensor.SensorManager;

import iottalk.DAI;

public class SensorIottalkActivity extends AppCompatActivity {
    private Context context = this;
    //private SensorManager sensorManager;
    SA sa;
    DAI dai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_iottalk);
        //sensorManager = new SensorManager(context);

        sa = new SA(context);
        dai = new DAI(sa);
        dai.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dai.terminate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}