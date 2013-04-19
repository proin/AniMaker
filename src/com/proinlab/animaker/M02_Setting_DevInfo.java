package com.proinlab.animaker;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class M02_Setting_DevInfo extends Activity {

	private ImageButton BUTTON_BACK;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m02_setting_devinfo);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				
		BUTTON_BACK = (ImageButton) findViewById(R.id.m02_setting_dev_navibtn);

		BUTTON_BACK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});		
	}
}
