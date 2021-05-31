package org.iii.testjjsdk;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import iottalk.DAI;

public class SensorIottalkActivity extends AppCompatActivity {
    private Context context = this;

    private SA sa;
    private DAI dai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_iottalk);
        sa = new SA(context);
        dai = new DAI(sa);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dai.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        dai.terminate();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}