package org.iii.testjjsdk;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.jorjin.jjsdk.display.DisplayManager;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This Activity is for AudioManager and DisplayManager Example.
 **/

public class DeviceControlActivity extends AppCompatActivity {

	private Context context = this;

	private DisplayManager displayManager;

	private int brightness, volume;
	private int displayMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devicecontrol);

		initUIComponent();
		displayManager = new DisplayManager(context);
		displayManager.open();

	}

	@Override
	protected void onStop() {
		super.onStop();
		displayManager.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initUIComponent() {
		RadioButton radioDisplay2D = findViewById(R.id.btn_display_2D);
		RadioButton radioDisplay3D = findViewById(R.id.btn_display_3D);

		Button testMute = (Button)findViewById(R.id.testMute);

		testMute.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				displayManager.setMute();
			}

		});


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

		TextView textBrightnessValue = findViewById(R.id.text_brightness_value);
		SeekBar seekBrightness = findViewById(R.id.seekbar_brightness);

		seekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
}
