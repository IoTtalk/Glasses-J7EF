package org.iii.testjjsdk;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.jorjin.jjsdk.sensor.SensorDataListener;
import com.jorjin.jjsdk.sensor.SensorManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

/**
 * This Activity is for SensorManager Example.
 **/

public class SensorActivity extends AppCompatActivity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		initUIComponent();

		sensorManager = new SensorManager(context);
		sensorManager.addSensorDataListener(sensorDataListener);

		initTimer();
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
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_ACCELEROMETER_3D);
			}
		});

		switchGravity.setOnCheckedChangeListener((button, checked) -> {
//			state[Gravity] = checked;
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_GRAVITY_VECTOR);
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_GRAVITY_VECTOR);
			}
		});
		switchGyro.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_GYROMETER_3D);
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_GYROMETER_3D);
			}
		});
		switchLight.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_AMBIENTLIGHT);
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_AMBIENTLIGHT);
			}
		});
		switchLinearAcc.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_LINEAR_ACCELEROMETER);
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_LINEAR_ACCELEROMETER);
			}
		});
		switchMag.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_COMPASS_3D);
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_COMPASS_3D);
			}
		});
		switchRv.setOnCheckedChangeListener((button, checked) -> {
			if (checked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_DEVICE_ORIENTATION);
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_DEVICE_ORIENTATION);
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
					break;
				case SensorManager.SENSOR_TYPE_AMBIENTLIGHT:
					sensorValue[Light].setText(getString(R.string.sensor_value1f, data[0]));
					count[Light]++;
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
					break;
				case SensorManager.SENSOR_TYPE_DEVICE_ORIENTATION:
					sensorValue[Rv].setText(
							getString(R.string.sensor_value4, data[0], data[1], data[2], data[3]));
					count[Rv]++;
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
}
