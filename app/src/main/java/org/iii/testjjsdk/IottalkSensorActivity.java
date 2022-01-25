package org.iii.testjjsdk;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.jorjin.jjsdk.sensor.SensorDataListener;
import com.jorjin.jjsdk.sensor.SensorManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InterruptedIOException;

import tw.iottalk.iot.Dan;
import tw.iottalk.iot.Csmapi;

/**
 * This Activity is for SensorManager Example.
 **/

public class IottalkSensorActivity extends AppCompatActivity {

	private static final int COUNT_OF_SENSORS = 7;
	private final int Acc = 0;
	private final int Gyro = 1;
	private final int Rv = 2;
	private final int Compass = 3;
	private final int Light = 4;
	private final int LinearAcc = 5;
	private final int Gravity = 6;
	private Context context = this;
	private TextView[] sensorValue = new TextView[COUNT_OF_SENSORS];
	private TextView[] updateFrequency = new TextView[COUNT_OF_SENSORS];
	private int[] count = new int[COUNT_OF_SENSORS];

	private Handler handler = new Handler();
	private Runnable rateRunnable;

	private SensorManager sensorManager;
	private SensorDataListener sensorDataListener = new SensorDataListener() {
		@Override
		public void onSensorDataChanged(int type, float[] values, long l) {
			updateDataText(type, values);
		}
	};

	private boolean[] state = new boolean[COUNT_OF_SENSORS];

	private float[] accData = new float[3];
	private float[] gyrData= new float[3];
	private float[] magData= new float[3];
	private float[] oriData= new float[3];
	private float[] lightData = new float[1];

	private String iottalk_server = "https://5.iottalk.tw";
	private String mac_addr = "J7EF";
	private Dan dan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		initUIComponent();

		sensorManager = new SensorManager(context);
		sensorManager.addSensorDataListener(sensorDataListener);

		dan = set_device();
		register_device();
		initTimer();
		pushTimer();
	}

	@Override
	protected void onStop() {
		super.onStop();
		sensorManager.removeSensorDataListener(sensorDataListener);
		sensorManager.release();

		handler.removeCallbacks(rateRunnable);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initUIComponent() {
		sensorValue[Acc] = findViewById(R.id.text_acc_v);
		sensorValue[Gravity] = findViewById(R.id.text_gravity_v);
		sensorValue[Gyro] = findViewById(R.id.text_gyro_v);
		sensorValue[Light] = findViewById(R.id.text_light_v);
		sensorValue[LinearAcc] = findViewById(R.id.text_linear_acc_v);
		sensorValue[Compass] = findViewById(R.id.text_mag_v);
		sensorValue[Rv] = findViewById(R.id.text_rv_v);

		updateFrequency[Acc] = findViewById(R.id.text_acc_r);
		updateFrequency[Gravity] = findViewById(R.id.text_gravity_r);
		updateFrequency[Gyro] = findViewById(R.id.text_gyro_r);
		updateFrequency[Light] = findViewById(R.id.text_light_r);
		updateFrequency[LinearAcc] = findViewById(R.id.text_linear_acc_r);
		updateFrequency[Compass] = findViewById(R.id.text_mag_r);
		updateFrequency[Rv] = findViewById(R.id.text_rv_r);

		initSwitch();
	}

	private void initSwitch() {
		SwitchCompat switchAcc = findViewById(R.id.switch_acc);
		SwitchCompat switchGravity = findViewById(R.id.switch_gravity);
		SwitchCompat switchGyro = findViewById(R.id.switch_gyro);
		SwitchCompat switchLight = findViewById(R.id.switch_light);
		SwitchCompat switchLinearAcc = findViewById(R.id.switch_linear_acc);
		SwitchCompat switchMag = findViewById(R.id.switch_mag);
		SwitchCompat switchRv = findViewById(R.id.switch_rv);
		switchAcc.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_ACCELEROMETER_3D);
				state[Acc] = true;
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_ACCELEROMETER_3D);
				state[Acc] = false;
			}
		});

		switchGravity.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_GRAVITY_VECTOR);
				state[Gravity] = true;
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_GRAVITY_VECTOR);
				state[Gravity] = false;
			}
		});
		switchGyro.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_GYROMETER_3D);
				state[Gyro] = true;
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_GYROMETER_3D);
				state[Gyro] = false;
			}
		});
		switchLight.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_AMBIENTLIGHT);
				state[Light] = true;
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_AMBIENTLIGHT);
				state[Light] = false;
			}
		});
		switchLinearAcc.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_LINEAR_ACCELEROMETER);
				state[LinearAcc] = true;
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_LINEAR_ACCELEROMETER);
				state[LinearAcc] = false;
			}
		});
		switchMag.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_COMPASS_3D);
				state[Compass] = true;
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_COMPASS_3D);
				state[Compass] = false;
			}
		});
		switchRv.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_DEVICE_ORIENTATION);
				state[Rv] = true;
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_DEVICE_ORIENTATION);
				state[Rv] = false;
			}
		});
	}

	private void initTimer() {
		rateRunnable = () -> {
			refreshDataText();
			handler.postDelayed(rateRunnable, 1000);
		};
		handler.postDelayed(rateRunnable, 1000);
	}

	private void updateDataText(int type, float[] data) {
		runOnUiThread(() -> {
			switch (type) {
				case SensorManager.SENSOR_TYPE_ACCELEROMETER_3D:
					sensorValue[Acc].setText(
							getString(R.string.sensor_value3, data[0], data[1], data[2]));
					count[Acc]++;
					for (int i=0; i<3; i++){ accData[i] = data[i]; }
					break;
				case SensorManager.SENSOR_TYPE_GRAVITY_VECTOR:
					sensorValue[Gravity].setText(
							getString(R.string.sensor_value3, data[0], data[1], data[2]));
					count[Gravity]++;
					break;
				case SensorManager.SENSOR_TYPE_GYROMETER_3D:
					sensorValue[Gyro].setText(
							getString(R.string.sensor_value3, data[0], data[1], data[2]));
					count[Gyro]++;
					for (int i=0; i<3; i++){ gyrData[i] = data[i]; }
					break;
				case SensorManager.SENSOR_TYPE_AMBIENTLIGHT:
					sensorValue[Light].setText(getString(R.string.sensor_value1f, data[0]));
					count[Light]++;
					for (int i=0; i<1; i++){ lightData[i] = data[i]; }
					break;
				case SensorManager.SENSOR_TYPE_LINEAR_ACCELEROMETER:
					sensorValue[LinearAcc].setText(
							getString(R.string.sensor_value3, data[0], data[1], data[2]));
					count[LinearAcc]++;
					break;
				case SensorManager.SENSOR_TYPE_COMPASS_3D:
					sensorValue[Compass].setText(
							getString(R.string.sensor_value3, data[0], data[1], data[2]));
					count[Compass]++;
					for (int i=0; i<3; i++){ magData[i] = data[i]; }
					break;
				case SensorManager.SENSOR_TYPE_DEVICE_ORIENTATION:
					sensorValue[Rv].setText(
							getString(R.string.sensor_value4, data[0], data[1], data[2], data[3]));
					count[Rv]++;
					float[] ori = Quaternion2EulerAngles(data);
					for (int i=0;i<3;i++){
						ori[i] = (float) Math.toDegrees(ori[i]);
						oriData[i] = ori[i];
					}
					break;
			}
		});
	}


	private void refreshDataText() {
		runOnUiThread(() -> {
			updateFrequency[Acc].setText(getString(R.string.sensor_value1d, count[Acc]));
			updateFrequency[Gravity].setText(getString(R.string.sensor_value1d, count[Gravity]));
			updateFrequency[Gyro].setText(getString(R.string.sensor_value1d, count[Gyro]));
			updateFrequency[Light].setText(getString(R.string.sensor_value1d, count[Light]));
			updateFrequency[LinearAcc].setText(
					getString(R.string.sensor_value1d, count[LinearAcc]));
			updateFrequency[Compass].setText(getString(R.string.sensor_value1d, count[Compass]));
			updateFrequency[Rv].setText(getString(R.string.sensor_value1d, count[Rv]));

			for (int i = 0; i < COUNT_OF_SENSORS; i++) {
				count[i] = 0;
			}
		});
	}

	private void pushTimer() {
		rateRunnable = () -> {
			Thread pushthread = new Thread(new Runnable() {
				@Override
				public void run() {
					pushDataToIottalk();
				}
			});
			pushthread.start();

			handler.postDelayed(rateRunnable, 1000);
		};
		handler.postDelayed(rateRunnable, 1000);
	}

	private void pushDataToIottalk() {
		if(state[Acc]){
			JSONArray data;
			try {
				data = new JSONArray(accData);
				dan.push("Acceleration-I",data);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(state[Gyro]){
			JSONArray data;
			try {
				data = new JSONArray(gyrData);
				dan.push("Gyroscope-I",data);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(state[Compass]){
			JSONArray data;
			try {
				data = new JSONArray(magData);
				dan.push("Magnetometer-I",data);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(state[Rv]){
			JSONArray data;
			try {
				data = new JSONArray(oriData);
				dan.push("Orientation-I",data);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(state[Light]){
			JSONArray data;
			try {
				data = new JSONArray(lightData);
				dan.push("Luminance-I",data);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private static float[] Quaternion2EulerAngles(float[] quaternion){
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

	Dan set_device() {
		Dan dan = null;
		final JSONArray dflist;
		dflist = new JSONArray();
		dflist.put("Acceleration-I");
		dflist.put("Gyroscope-I");
		dflist.put("Magnetometer-I");
		dflist.put("Orientation-I");
		dflist.put("Luminance-I");
		JSONObject profile;
		try {
			profile = new JSONObject() {
				{
					put("d_name", "01.Glasses");
					put("dm_name", "Glasses");
					put("u_name", "yb");
					put("is_sim", false);
					put("df_list", dflist);
				}
			};

			dan = new Dan(iottalk_server,mac_addr, profile);
			return dan;


		} catch (JSONException e) {
			e.printStackTrace();
		}

		return dan;
	}
	void register_device() {
		Thread regthread = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.v("Thread", "Dan.register start");
				dan.register_device();
				Log.v("Thread", "Dan.register finish");
			}
		});
		regthread.start();
	}

	void register_device_c() {
		final JSONArray dflist;
		dflist = new JSONArray();
		dflist.put("Acceleration-I");
		dflist.put("Gyroscope-I");
		dflist.put("Magnetometer-I");
		dflist.put("Orientation-I");
		dflist.put("Luminance-I");
		JSONObject profile;
		try {
			profile = new JSONObject() {
				{
					put("d_name", "01.Glasses");
					put("dm_name", "Glasses");
					put("u_name", "yb");
					put("is_sim", false);
					put("df_list", dflist);
				}
			};

			final JSONObject finalProfile = profile;
			Thread regthread = new Thread(new Runnable() {
				@Override
				public void run() {
					//
					try {
						Csmapi.register(mac_addr, finalProfile);
					} catch (JSONException | InterruptedIOException e) {
						e.printStackTrace();
					}
					Log.v("Thread", "Csmapi.register finish");
				}
			});
			regthread.start();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
