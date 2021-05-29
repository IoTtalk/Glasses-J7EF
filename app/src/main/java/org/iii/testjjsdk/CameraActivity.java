package org.iii.testjjsdk;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.jorjin.jjsdk.camera.CameraManager;
import com.jorjin.jjsdk.camera.CameraParameter;
import com.jorjin.jjsdk.camera.FrameListener;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This Activity is for CameraManager Example.
 **/

public class CameraActivity extends AppCompatActivity {

	private Handler handler = new Handler();
	private Runnable rateRunnable;
	private TextView textFps;
	private int fpsCount = 0;

	private CameraManager cameraManager;
	private Context context = this;

	private FrameListener frameListener = (buffer, width, height, format) -> {

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		SurfaceView cameraSurface = findViewById(R.id.surface_camera);

		cameraManager = new CameraManager(context);
		cameraManager.addSurfaceHolder(cameraSurface.getHolder());
		cameraManager.setCameraFrameListener(frameListener);
		cameraManager.setResolutionIndex(0); // Index of Resolution list.
		cameraManager.startCamera(CameraManager.COLOR_FORMAT_NV21);

		initUiComponent();
		initSpinners();
		initTimer();
	}

	@Override
	protected void onStop() {
		super.onStop();
		cameraManager.stopCamera();
	}

	private void initUiComponent() {
		textFps = findViewById(R.id.text_fps);

		Button btnCameraOpen = findViewById(R.id.btn_camera_open);
		Button btnCameraClose = findViewById(R.id.btn_camera_close);
		Button btnTakePhoto = findViewById(R.id.btn_take_photo);
		ToggleButton btnRecord = findViewById(R.id.btn_record);

		btnCameraOpen.setOnClickListener(
				v -> cameraManager.startCamera(CameraManager.COLOR_FORMAT_RGBA));
		btnCameraClose.setOnClickListener(v -> cameraManager.stopCamera());
		btnTakePhoto.setOnClickListener(v -> cameraManager.takePicture());
		btnRecord.setOnCheckedChangeListener((button, isChecked) -> {
			if (isChecked) {
				cameraManager.startRecord();
			} else {
				cameraManager.stopRecord();
			}
		});

		TextView txtBrightness = findViewById(R.id.text_brightness_value);
		SeekBar seekBarBrightness = findViewById(R.id.seekbar_brightness);
		int brightness = cameraManager.getCameraParameter().getBrightness();
		seekBarBrightness.setProgress(brightness);
		runOnUiThread(() -> txtBrightness.setText(String.valueOf(brightness)));
		seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar bar, int value, boolean b) {
				runOnUiThread(() -> txtBrightness.setText(String.valueOf(value)));
				CameraParameter cameraParameter = cameraManager.getCameraParameter();
				cameraParameter.setBrightness(value);
				cameraManager.setCameraParameter(cameraParameter);
			}

			@Override
			public void onStartTrackingTouch(SeekBar bar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar bar) {

			}
		});

		TextView txtContrast = findViewById(R.id.text_contrast_value);
		SeekBar seekBarContrast = findViewById(R.id.seekbar_contrast);
		int contrast = cameraManager.getCameraParameter().getContrast();
		seekBarContrast.setProgress(contrast);
		runOnUiThread(() -> txtContrast.setText(String.valueOf(contrast)));
		seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar bar, int value, boolean b) {
				runOnUiThread(() -> txtContrast.setText(String.valueOf(value)));
				CameraParameter cameraParameter = cameraManager.getCameraParameter();
				cameraParameter.setContrast(value);
				cameraManager.setCameraParameter(cameraParameter);
			}

			@Override
			public void onStartTrackingTouch(SeekBar bar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar bar) {

			}
		});

		TextView txtSharpness = findViewById(R.id.text_sharpness_value);
		SeekBar seekBarSharpness = findViewById(R.id.seekbar_sharpness);
		int sharpness = cameraManager.getCameraParameter().getSharpness();
		seekBarSharpness.setProgress(sharpness);
		runOnUiThread(() -> txtSharpness.setText(String.valueOf(sharpness)));
		seekBarSharpness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar bar, int value, boolean b) {
				runOnUiThread(() -> txtSharpness.setText(String.valueOf(value)));
				CameraParameter cameraParameter = cameraManager.getCameraParameter();
				cameraParameter.setSharpness(value);
				cameraManager.setCameraParameter(cameraParameter);
			}

			@Override
			public void onStartTrackingTouch(SeekBar bar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar bar) {

			}
		});

		TextView txtGamma = findViewById(R.id.text_gamma_value);
		SeekBar seekGamma = findViewById(R.id.seekbar_gamma);
		int gamma = cameraManager.getCameraParameter().getGamma();
		seekGamma.setProgress(gamma);
		runOnUiThread(() -> txtGamma.setText(String.valueOf(gamma)));
		seekGamma.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar bar, int value, boolean b) {
				runOnUiThread(() -> txtGamma.setText(String.valueOf(value)));
				CameraParameter cameraParameter = cameraManager.getCameraParameter();
				cameraParameter.setGamma(value);
				cameraManager.setCameraParameter(cameraParameter);
			}

			@Override
			public void onStartTrackingTouch(SeekBar bar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar bar) {

			}
		});

		TextView txtHue = findViewById(R.id.text_hue_value);
		SeekBar seekHue = findViewById(R.id.seekbar_hue);
		int hue = cameraManager.getCameraParameter().getHue();
		seekHue.setProgress(hue);
		runOnUiThread(() -> txtHue.setText(String.valueOf(hue)));
		seekHue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar bar, int value, boolean b) {
				runOnUiThread(() -> txtHue.setText(String.valueOf(value)));
				CameraParameter cameraParameter = cameraManager.getCameraParameter();
				cameraParameter.setHue(value);
				cameraManager.setCameraParameter(cameraParameter);
			}

			@Override
			public void onStartTrackingTouch(SeekBar bar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar bar) {

			}
		});

		TextView txtSaturation = findViewById(R.id.text_saturation_value);
		SeekBar seekSaturation = findViewById(R.id.seekbar_saturation);
		int saturation = cameraManager.getCameraParameter().getSaturation();
		seekSaturation.setProgress(saturation);
		runOnUiThread(() -> txtGamma.setText(String.valueOf(saturation)));
		seekSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar bar, int value, boolean b) {
				runOnUiThread(() -> txtSaturation.setText(String.valueOf(value)));
				CameraParameter cameraParameter = cameraManager.getCameraParameter();
				cameraParameter.setSaturation(value);
				cameraManager.setCameraParameter(cameraParameter);
			}

			@Override
			public void onStartTrackingTouch(SeekBar bar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar bar) {

			}
		});
	}

	private void initSpinners() {
		Spinner spinnerResolution = findViewById(R.id.spin_resolution);
		ArrayAdapter<String> adapterResolution = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_item,
				cameraManager.getCameraParameter().getResolutionList());

		spinnerResolution.setAdapter(adapterResolution);
		spinnerResolution.setSelection(0, false);
		spinnerResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> view, View view1, int index, long l) {
				cameraManager.setResolutionIndex(index);
			}

			@Override
			public void onNothingSelected(AdapterView<?> view) {

			}
		});

		Spinner spinnerPowerLF = findViewById(R.id.spin_power_line_f);
		ArrayAdapter<String> adapterPowerLF = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_item, new String[] {"50Hz", "60Hz"});
		spinnerPowerLF.setAdapter(adapterPowerLF);
		spinnerPowerLF.setSelection(0, false);
		spinnerPowerLF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> view, View view1, int index, long l) {
				CameraParameter cameraParameter = cameraManager.getCameraParameter();
				switch (index) {
					case 0:
						cameraParameter.setPowerLineFrequency(CameraParameter.POWER_LINE_50HZ);
						break;
					case 1:
						cameraParameter.setPowerLineFrequency(CameraParameter.POWER_LINE_60HZ);
						break;
				}
				cameraManager.setCameraParameter(cameraParameter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> view) {

			}
		});
	}

	private void initTimer() {
		rateRunnable = () -> {
			runOnUiThread(() -> textFps.setText(getString(R.string.camera_fps, fpsCount)));
			handler.postDelayed(rateRunnable, 1000);
			fpsCount = 0;
		};
		handler.postDelayed(rateRunnable, 1000);
	}
}
