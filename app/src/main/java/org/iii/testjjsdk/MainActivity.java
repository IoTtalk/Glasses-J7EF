package org.iii.testjjsdk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	private ImageButton imgBtnSensor;
	private ImageButton imgBtnCamera;
	private ImageButton imgBtnDc;
	private ImageButton imgBtnAf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUIComponent();
	}

	private void initUIComponent() {
		imgBtnSensor = findViewById(R.id.btn_sensor);
		imgBtnCamera = findViewById(R.id.btn_camera);
		imgBtnDc = findViewById(R.id.btn_device_control);
		imgBtnAf = findViewById(R.id.btn_af_activity);

		imgBtnSensor.setOnClickListener(v -> startActivity(new Intent(this,
				SensorActivity.class)));
		imgBtnCamera.setOnClickListener(v -> startActivity(new Intent(this,
				CameraActivity.class)));
		imgBtnDc.setOnClickListener(
				v -> startActivity(new Intent(this, DeviceControlActivity.class)));
		imgBtnAf.setOnClickListener(v -> startActivity(new Intent(this,
				AllFeatureActivity.class)));
	}
}
