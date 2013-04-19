package com.proinlab.animaker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class M02_Setting extends Activity {

	private ImageButton BUTTON_BACK, BUTTON_DEVINFO, BUTTON_HELP, BUTTON_VERSIONINFO;
	private TextView TEXTVIEW_VERSIONINFO;

	private TextView TEXTVIEW_FPS;
	private ImageButton BUTTON_FPS_INCREASE, BUTTON_FPS_REDUCE;
	private int fps_Integer = 2;
	public static final String SETTING_FPS_TAG = "SETTING_FPS";

	private TextView TEXTVIEW_BGF;
	private ImageButton BUTTON_BGF_INCREASE, BUTTON_BGF_REDUCE;
	private int bgf_Integer = 40;
	public static final String SETTING_BGF_TAG = "SETTING_BGALPHA";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m02_setting);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		PackageManager pm = getPackageManager(); 
		String version = "";
		try {
			version = pm.getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES).versionName;
		} catch (NameNotFoundException e) {
			version = "";
		}

		BUTTON_BACK = (ImageButton) findViewById(R.id.m02_setting_navibtn);
		BUTTON_DEVINFO = (ImageButton) findViewById(R.id.m02_setting_devinfo);
		BUTTON_HELP = (ImageButton) findViewById(R.id.m02_setting_help);
		BUTTON_VERSIONINFO = (ImageButton) findViewById(R.id.m02_setting_versioninfo);
		TEXTVIEW_VERSIONINFO= (TextView) findViewById(R.id.m02_setting_versioninfo_text);
		
		BUTTON_BACK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		BUTTON_DEVINFO.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(M02_Setting.this,
						M02_Setting_DevInfo.class);
				startActivity(intent);
			}
		});

		BUTTON_HELP.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(M02_Setting.this, "Preparing",
						Toast.LENGTH_SHORT).show();
			}
		});

		BUTTON_VERSIONINFO.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(M02_Setting.this,
						M02_Setting_VersionInfo.class);
				startActivity(intent);
			}
		});
		
		TEXTVIEW_VERSIONINFO.setText(version);
		
		FPSSetting();
		BGFSetting();

	}

	private void FPSSetting() {
		TEXTVIEW_FPS = (TextView) findViewById(R.id.m02_setting_fps_textview);
		BUTTON_FPS_INCREASE = (ImageButton) findViewById(R.id.m02_setting_fps_increase);
		BUTTON_FPS_REDUCE = (ImageButton) findViewById(R.id.m02_setting_fps_reduce);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);

		fps_Integer = pref.getInt(SETTING_FPS_TAG, 2);
		TEXTVIEW_FPS.setText(Integer.toString(fps_Integer));

		BUTTON_FPS_INCREASE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fps_Integer++;
				if (fps_Integer > 10) {
					fps_Integer--;
				}
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt(SETTING_FPS_TAG, fps_Integer);
				editor.commit();
				TEXTVIEW_FPS.setText(Integer.toString(fps_Integer));
			}
		});

		BUTTON_FPS_REDUCE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fps_Integer--;
				if (fps_Integer < 1) {
					fps_Integer++;
				}
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt(SETTING_FPS_TAG, fps_Integer);
				editor.commit();
				TEXTVIEW_FPS.setText(Integer.toString(fps_Integer));
			}
		});
	}

	private void BGFSetting() {
		TEXTVIEW_BGF = (TextView) findViewById(R.id.m02_setting_bgf_textview);
		BUTTON_BGF_INCREASE = (ImageButton) findViewById(R.id.m02_setting_bgf_increase);
		BUTTON_BGF_REDUCE = (ImageButton) findViewById(R.id.m02_setting_bgf_reduce);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);

		bgf_Integer = pref.getInt(SETTING_BGF_TAG, 40);
		TEXTVIEW_BGF.setText(Integer.toString(bgf_Integer));

		BUTTON_BGF_INCREASE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bgf_Integer++;
				if (bgf_Integer > 100) {
					bgf_Integer--;
				}
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt(SETTING_BGF_TAG, bgf_Integer);
				editor.commit();
				TEXTVIEW_BGF.setText(Integer.toString(bgf_Integer));
			}
		});

		BUTTON_BGF_REDUCE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bgf_Integer--;
				if (bgf_Integer < 1) {
					bgf_Integer++;
				}
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt(SETTING_BGF_TAG, bgf_Integer);
				editor.commit();
				TEXTVIEW_BGF.setText(Integer.toString(bgf_Integer));
			}
		});
	}
}
