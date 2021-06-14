package org.iii.testjjsdk;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import iottalk.DAI;

public class SensorIottalkActivity extends AppCompatActivity {
    private Context context = this;

    private SA_J7EF saJ7EF;
    private SA_Phone saPhone;
    private DAI daiJ7EF;
    private DAI daiPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_iottalk);
        saJ7EF = new SA_J7EF(context);
        saPhone = new SA_Phone(context);
        daiJ7EF = new DAI(saJ7EF);
        daiPhone = new DAI(saPhone);
    }

    @Override
    protected void onResume() {
        super.onResume();
        daiJ7EF.start();
        daiPhone.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        daiJ7EF.terminate();
        daiPhone.terminate();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}