package org.iii.testjjsdk;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.jorjin.jjsdk.camera.CameraManager;
import com.jorjin.jjsdk.camera.FrameListener;
import com.jorjin.jjsdk.display.DisplayManager;
import com.jorjin.jjsdk.sensor.SensorDataListener;
import com.jorjin.jjsdk.sensor.SensorManager;
import androidx.appcompat.app.AppCompatActivity;

public class AllFeatureActivity extends AppCompatActivity {

	private static final String TAG = AllFeatureActivity.class.getSimpleName();
	private Context context = this;
	private TextView textAcc, textAccRate;

	private CameraManager cameraManager;
	private FrameListener frameListener = (buffer, i, i1, i2) -> Log.d(TAG, "onIncomingFrame");

	private SensorManager sensorManager;
	private int count = 0;
	private SensorDataListener sensorDataListener = (i, data, l) -> {
		count++;
		if (i != SensorManager.SENSOR_TYPE_ACCELEROMETER_3D) {
			return;
		}
		runOnUiThread(() -> textAcc
				.setText(getString(R.string.sensor_value3, data[0], data[1], data[2])));
	};

	private DisplayManager displayManager;

	private Handler handler = new Handler();
	private Runnable rateRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_af);

		initCamera();
		initSensor();
		initDisplay();

		initTimer();
	}

	private void initCamera() {
		SurfaceView surfaceView = findViewById(R.id.surface_camera);

		cameraManager = new CameraManager(context);
		cameraManager.setCameraFrameListener(frameListener);
		cameraManager.setResolutionIndex(0);
		cameraManager.addSurfaceHolder(surfaceView.getHolder());
		cameraManager.startCamera(CameraManager.COLOR_FORMAT_RGBA);

		Button btnCameraOpen = findViewById(R.id.btn_camera_open);
		Button btnCameraClose = findViewById(R.id.btn_camera_close);
		btnCameraOpen.setOnClickListener(
				v -> cameraManager.startCamera(CameraManager.COLOR_FORMAT_RGBA));
		btnCameraClose.setOnClickListener(v -> cameraManager.stopCamera());

		Button btnTakePhoto = findViewById(R.id.btn_take_photo);
		ToggleButton btnRecord = findViewById(R.id.btn_record);
		btnTakePhoto.setOnClickListener(v -> cameraManager.takePicture());
		btnRecord.setOnCheckedChangeListener((button, isChecked) -> {
			if (isChecked) {
				cameraManager.startRecord();
			} else {
				cameraManager.stopRecord();
			}
		});
	}

	private void initSensor() {
		sensorManager = new SensorManager(context);

		sensorManager.addSensorDataListener(sensorDataListener);

		ToggleButton btnAcc = findViewById(R.id.btn_acc);
		btnAcc.setOnCheckedChangeListener((button, isChecked) -> {
			if (isChecked) {
				sensorManager.open(SensorManager.SENSOR_TYPE_ACCELEROMETER_3D);
			} else {
				sensorManager.close(SensorManager.SENSOR_TYPE_ACCELEROMETER_3D);
			}
		});

		textAcc = findViewById(R.id.text_acc_v);
		textAccRate = findViewById(R.id.text_acc_r);
	}

	private void initDisplay() {
		displayManager = new DisplayManager(context);
		displayManager.open();
		displayManager.setDisplayMode(DisplayManager.DISPLAY_2D);

		RadioButton radioDisplay2D = findViewById(R.id.btn_display_2D);
		RadioButton radioDisplay3D = findViewById(R.id.btn_display_3D);

		radioDisplay2D.setOnCheckedChangeListener((button, isChecked) -> {
			if (isChecked) {
				displayManager.setDisplayMode(DisplayManager.DISPLAY_2D);
			}
		});
		radioDisplay3D.setOnCheckedChangeListener((button, isChecked) -> {
			if (isChecked) {
				displayManager.setDisplayMode(DisplayManager.DISPLAY_3D);
			}
		});
		SeekBar seekBar = findViewById(R.id.seekbar_brightness);
		TextView textBrightnessValue = findViewById(R.id.text_brightness_value);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar bar, int value, boolean b) {
				runOnUiThread(() -> textBrightnessValue.setText(String.valueOf(value)));
				displayManager.setBrightness(value);
			}

			@Override
			public void onStartTrackingTouch(SeekBar bar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar bar) {

			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		cameraManager.stopCamera();
		sensorManager.release();
		displayManager.close();
	}

	private void initTimer() {
		rateRunnable = () -> {
			textAccRate.setText(getString(R.string.sensor_value1d, count));
			count = 0;
			handler.postDelayed(rateRunnable, 1000);
		};
		handler.postDelayed(rateRunnable, 1000);
	}

}
