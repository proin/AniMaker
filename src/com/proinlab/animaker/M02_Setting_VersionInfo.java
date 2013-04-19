package com.proinlab.animaker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class M02_Setting_VersionInfo extends Activity {

	private ImageButton BUTTON_BACK;

	private TextView LatestVer, CurrentVer;
	private Button Update;

	private String latestver, version;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m02_setting_versioninfo);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		PackageManager pm = getPackageManager();

		try {
			version = pm.getPackageInfo(getPackageName(),
					PackageManager.GET_SIGNATURES).versionName;
		} catch (NameNotFoundException e) {
			version = "";
		}

		BUTTON_BACK = (ImageButton) findViewById(R.id.m02_setting_navibtn);
		LatestVer = (TextView) findViewById(R.id.m02_setting_versioninfo_latestver);
		CurrentVer = (TextView) findViewById(R.id.m02_setting_versioninfo_currentver);
		Update = (Button) findViewById(R.id.m02_setting_versioninfo_updatebtn);

		BUTTON_BACK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		CurrentVer.setText(version);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);

		latestver = pref.getString("latestversion", "1.0.0");

		LatestVer.setText(latestver);

		if (latestver.equals(version))
			Update.setVisibility(View.GONE);
		else
			Update.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Uri uri = Uri
							.parse("market://details?id=com.proinlab.animaker");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
			});
	}

}
