package com.proinlab.animaker;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class M00_Loading extends Activity {

	private TextView textv;
	public String SETTING_VERSION_TAG = "latestversion";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m00_loading);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		httpclient = new DefaultHttpClient();
		
		textv = (TextView) findViewById(R.id.m00_loading_text);
		textv.setText("Loading...");

		GET_URL_DATA("https://play.google.com/store/apps/details?id=com.proinlab.animaker");
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String tag = "<dd itemprop=\"softwareVersion\">";
			if (htmlsource == null)
				;
			else if (htmlsource.indexOf(tag) != -1) {
				htmlsource = htmlsource.substring(htmlsource.indexOf(tag)
						+ tag.length());
				htmlsource = htmlsource.substring(0,
						htmlsource.indexOf("</dd>"));

				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString(SETTING_VERSION_TAG, htmlsource);
				editor.commit();
			}

			Intent intent = new Intent(M00_Loading.this, M00_List.class);
			startActivity(intent);
			finish();
		}
	};

	private DefaultHttpClient httpclient;
	private String htmlsource;

	private void GET_URL_DATA(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpGet request = new HttpGet();
					request.setURI(new URI(url));
					HttpResponse response = httpclient.execute(request);
					HttpEntity entity = response.getEntity();
					htmlsource = EntityUtils.toString(entity, "UTF-8");
				} catch (Exception e) {
					htmlsource = null;
				}

				mHandler.post(new Runnable() {
					public void run() {
						mHandler.sendEmptyMessage(0);
					}
				});
			}
		}).start();
	}
}
