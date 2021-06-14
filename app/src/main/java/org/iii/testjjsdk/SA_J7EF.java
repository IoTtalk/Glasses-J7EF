package org.iii.testjjsdk;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.widget.TextView;

import com.jorjin.jjsdk.sensor.SensorDataListener;
import com.jorjin.jjsdk.sensor.SensorManager;

import org.json.JSONArray;
import org.json.JSONException;

import iottalk.DAN;
import iottalk.DeviceFeature;

import static com.serenegiant.utils.UIThreadHelper.runOnUiThread;

public class SA {
    private static final int COUNT_OF_SENSORS = 3;
    private static final int COUNT_OF_PH_SENSORS = 3;
    private static final int COUNT_OF_ORI = 2;

    private final int Acc = 0;
    private final int Gyro = 1;
    private final int Light = 2;
    private final int Ori = 3;
    private final int AccPh = 4;
    private final int GyroPh = 5;
    private final int LightPh = 6;
    private final int OriPh = 7;

    public Activity activity;
    private Context context;
    private TextView[] sensorValue = new TextView[COUNT_OF_SENSORS+COUNT_OF_PH_SENSORS+COUNT_OF_ORI];

    private float[] AccData = new float[3];
    private float[] GyroData = new float[3];
    private float[] LightData = new float[1];

    private float[] AccPhData = new float[3];
    private float[] GyroPhData = new float[3];
    private float[] LightPhData = new float[1];

    private float[] OriData = new float[3];
    private float[] OriPhData = new float[3];

    private Handler handler = new Handler();
    private Runnable rateRunnable;

    private SensorManager sensorManager;
    private android.hardware.SensorManager andSensorManager;

    // IoTtalk setting
    public String api_url = "http://panettone.iottalk.tw:11822/csm";
    public String device_model = "J7EF";
    public String device_name = "J7EF_test";
    //Set IDFs in this format
    public DeviceFeature AccMeterDF = new DeviceFeature("Acceleration-I1", "idf"){
        @Override
        public JSONArray getPushData() throws JSONException {
            JSONArray r = new JSONArray(AccData);
            return r;
        }
    };
    public DeviceFeature GyroscopeDF = new DeviceFeature("Gyroscope-I1", "idf"){
        @Override
        public JSONArray getPushData() throws JSONException {
            JSONArray r = new JSONArray(GyroData);
            return r;
        }
    };
    public DeviceFeature LightDF = new DeviceFeature("Light-I1", "idf"){
        @Override
        public JSONArray getPushData() throws JSONException {
            JSONArray r = new JSONArray(LightData);
            return r;
        }
    };
    public DeviceFeature OrientationDF = new DeviceFeature("Orientation-I1", "idf"){
        @Override
        public JSONArray getPushData() throws JSONException {
            JSONArray r = new JSONArray(OriData);
            return r;
        }
    };
    public DeviceFeature AccPhDF = new DeviceFeature("Acceleration-I2", "idf"){
        @Override
        public JSONArray getPushData() throws JSONException {
            JSONArray r = new JSONArray(AccPhData);
            return r;
        }
    };
    public DeviceFeature GyroPhDF = new DeviceFeature("Gyroscope-I2", "idf"){
        @Override
        public JSONArray getPushData() throws JSONException {
            JSONArray r = new JSONArray(GyroPhData);
            return r;
        }
    };
    public DeviceFeature LightPhDF = new DeviceFeature("Light-I2", "idf"){
        @Override
        public JSONArray getPushData() throws JSONException {
            JSONArray r = new JSONArray(LightPhData);
            return r;
        }
    };
    public DeviceFeature OrientationPhDF = new DeviceFeature("Orientation-I2", "idf"){
        @Override
        public JSONArray getPushData() throws JSONException {
            JSONArray r = new JSONArray(OriPhData);
            return r;
        }
    };

    //Set the push interval
    public double push_interval = 0.2;
    public String device_addr = "aa20210531";
    public boolean persistent_binding = true;

    //invoke after DAN finish register
    public void on_register(DAN dan){
        initUIComponent();
        registerSensor();
        System.out.println("register successfully");
    }

    //invoke after DAN finish deregister
    public void on_deregister(){
        System.out.println("deregister successfully");
    }

    //invoke after DAN finish connect to server
    public void on_connect(){
        System.out.println("connect successfully");
    }

    //invoke after DAN finish disconnect to server, but NOT INCLUDED unexpected disconnection
    public void on_disconnect(){
        unregisterSensor();
        System.out.println("disconnect successfully");
    }

    private void initUIComponent() {
        sensorValue[Acc] = activity.findViewById(R.id.text_acc_v);
        sensorValue[Gyro] = activity.findViewById(R.id.text_gyro_v);
        sensorValue[Light] = activity.findViewById(R.id.text_light_v);
        sensorValue[Ori] = activity.findViewById(R.id.text_ori_v);
        sensorValue[AccPh] = activity.findViewById(R.id.text_acc_ph_v);
        sensorValue[GyroPh] = activity.findViewById(R.id.text_gyro_ph_v);
        sensorValue[LightPh] = activity.findViewById(R.id.text_light_ph_v);
        sensorValue[OriPh] = activity.findViewById(R.id.text_ori_ph_v);
        TextView connectTextView = activity.findViewById(R.id.text_connect);
        connectTextView.setText("Server : "+api_url+"\nDevice model : "+device_model+"\nDevice name : "+device_name);
    }

    private SensorDataListener sensorDataListener = new SensorDataListener() {
        @Override
        public void onSensorDataChanged(int type, float[] values, long l) {
            updateDataText(type, values);
        }
    };

    private void updateDataText(int type, float[] data) {
        runOnUiThread(() -> {
            switch (type) {
                case SensorManager.SENSOR_TYPE_ACCELEROMETER_3D:
                    sensorValue[Acc].setText(
                            context.getString(R.string.sensor_value3, data[0], data[1], data[2]));
                    for (int i=0; i<3; i++){ AccData[i] = data[i]; }
                    break;
                case SensorManager.SENSOR_TYPE_GYROMETER_3D:
                    sensorValue[Gyro].setText(
                            context.getString(R.string.sensor_value3, data[0], data[1], data[2]));
                    for (int i=0; i<3; i++){ GyroData[i] = data[i]; }
                    break;
                case SensorManager.SENSOR_TYPE_AMBIENTLIGHT:
                    sensorValue[Light].setText(context.getString(R.string.sensor_value1f, data[0]));
                    for (int i=0; i<1; i++){ LightData[i] = data[i]; }
                    break;
                case SensorManager.SENSOR_TYPE_DEVICE_ORIENTATION:
                    float ori[] = Quaternion2EulerAngles(data);
                    for (int i=0;i<3;i++){
                        ori[i] = (float) Math.toDegrees(ori[i]);
                        OriData[i] = ori[i];
                    }
                    sensorValue[Ori].setText(
                            context.getString(R.string.sensor_value3, ori[0], ori[1], ori[2]));
                    break;
            }
        });
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            runOnUiThread(() -> {
                switch (event.sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER:
                        for (int i=0; i<3; i++){AccPhData[i] = event.values[i];}
                        sensorValue[AccPh].setText(
                                context.getString(R.string.sensor_value3, event.values[0], event.values[1], event.values[2]));
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        for (int i=0; i<3; i++){GyroData[i] = event.values[i];}
                        sensorValue[GyroPh].setText(
                                context.getString(R.string.sensor_value3, event.values[0], event.values[1], event.values[2]));
                        break;
                    case Sensor.TYPE_LIGHT:
                        LightPhData[0] = event.values[0];
                        sensorValue[LightPh].setText(
                                context.getString(R.string.sensor_value1f, event.values[0]));
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        float ori[] = Quaternion2EulerAngles(event.values);
                        for (int i=0;i<3;i++){
                            ori[i] = (float) Math.toDegrees(ori[i]);
                            OriPhData[i] = ori[i];
                        }
                        sensorValue[OriPh].setText(
                                context.getString(R.string.sensor_value3, ori[0], ori[1], ori[2]));
                }
            });
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    void registerSensor(){
        // init glasses sensors
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sensorManager = new SensorManager(context);
                sensorManager.addSensorDataListener(sensorDataListener);
                enableAllGlassesSensor();
            }
        });
        // init phone sensors
        andSensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accSensor = andSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroSensor = andSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor rvSensor = andSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        Sensor lightSensor = andSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        andSensorManager.registerListener(sensorEventListener, accSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        andSensorManager.registerListener(sensorEventListener, gyroSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        andSensorManager.registerListener(sensorEventListener, rvSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        andSensorManager.registerListener(sensorEventListener, lightSensor, android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
    }

    void unregisterSensor(){
        disableAllGlassesSensor();
        sensorManager.removeSensorDataListener(sensorDataListener);
        sensorManager.release();
        andSensorManager.unregisterListener(sensorEventListener);
    }

    void enableAllGlassesSensor(){
        for (int i=0; i<7; i++){
            sensorManager.open(i);
        }
    }

    void disableAllGlassesSensor(){
        for (int i=0; i<7; i++){
            sensorManager.close(i);
        }
    }

    public static float[] Quaternion2EulerAngles(float[] quaternion){
        float[] angles = new float[3];

        float qw = quaternion[0];
        float qx = quaternion[1];
        float qy = quaternion[2];
        float qz = quaternion[3];

        //roll (x-axis rotation)
        double sinr_cosp = +2.0 * (qw * qx + qy * qz);
        double cosr_cosp = +1.0 - 2.0 * (qx * qx + qy * qy);
        angles[0] = (float) Math.atan2(sinr_cosp, cosr_cosp);

        // pitch (y-axis rotation)
        double sinp = +2.0 * (qw * qy - qz * qx);
        angles[1] = (float) Math.asin(sinp);

        // yaw (z-axis rotation)
        double siny_cosp = +2.0 * (qw * qz + qx * qy);
        double cosy_cosp = +1.0 - 2.0 * (qy * qy + qz * qz);
        angles[2] = (float) Math.atan2(siny_cosp, cosy_cosp);

        //0:yaw 1:roll 2:pitch
        return angles;
    }

    public SA(Context context){
        this.context = context;
        this.activity = (Activity) context;
    }
}
