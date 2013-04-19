package com.proinlab.animaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.proinlab.functions.B;
import com.proinlab.functions.DataBaseHelper;
import com.proinlab.functions.FileManager;

public class M01_Preview extends Activity {

	private FileManager FILEMANAGER = new FileManager();
	private DBEditFn DBEDIT = new DBEditFn();
	private DataBaseHelper mHelper;

	private boolean isPlayVideo = false;
	private boolean isPauseVideo = false;

	private ImageButton BUTTON_DELETE, BUTTON_EDIT, BUTTON_SETTING,
			BUTTON_BACK;
	private TextView TEXTVIEW_TITLE, TEXTVIEW_DETAIL;

	private ImageButton IMAGEBUTTON_PLAY, IMAGEBUTTON_PAUSE;
	private ImageView IMAGEVIEW_VIDEO;
	private SeekBar SEEKBAR_VIDEO;
	private TextView TEXTVIEW_VIDEO_NAVI;

	private EditText EDITTEXT_EDITTITLE;
	private LinearLayout LAYOUT_EDITTITLE;
	private ImageButton BUTTON_EDITTITLE;

	private EditText EDITTEXT_EDITDETAIL;
	private LinearLayout LAYOUT_EDITDETAIL;
	private ImageButton BUTTON_EDITDETAIL;

	private String FILENAME = null, FILEFULLDIR = null;
	private String[] PROJECTDATA;
	private int displayWidth, displayHeight;

	private int fps;

	private BitmapFactory.Options opts = new BitmapFactory.Options();

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		fps = pref.getInt(M02_Setting.SETTING_FPS_TAG, 2);

		allpages = FILEMANAGER.GET_LENGTH_SAVE_PAGES(FILEFULLDIR);
		int min = allpages / fps / 60;
		int sec = allpages / fps % 60;
		String entiretime;
		if (sec < 10) {
			entiretime = Integer.toString(min) + ":0" + Integer.toString(sec);
		} else {
			entiretime = Integer.toString(min) + ":" + Integer.toString(sec);
		}
		TEXTVIEW_VIDEO_NAVI.setText(entiretime);
		SEEKBAR_VIDEO.setMax(allpages);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m01_preview);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		fps = pref.getInt(M02_Setting.SETTING_FPS_TAG, 2);

		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = false;

		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		if (displayWidth > displayHeight) {
			displayHeight = display.getWidth();
			displayWidth = display.getHeight();
		}

		mHelper = new DataBaseHelper(this);

		FILENAME = getIntent().getExtras().getString("PROJECTNAME");
		PROJECTDATA = DBEDIT.FIND_BY_FILENAME(mHelper, FILENAME);
		FILEFULLDIR = B.SDCARD_DIRECTORY + "FILE/"
				+ PROJECTDATA[DataBaseHelper.FILEDIR];
		allpages = FILEMANAGER.GET_LENGTH_SAVE_PAGES(FILEFULLDIR);

		BUTTON_EDIT = (ImageButton) findViewById(R.id.m01_preview_editbtn);
		BUTTON_DELETE = (ImageButton) findViewById(R.id.m01_preview_deletebtn);
		BUTTON_SETTING = (ImageButton) findViewById(R.id.m01_preview_settingbtn);
		BUTTON_BACK = (ImageButton) findViewById(R.id.m01_preview_navibtn);

		IMAGEVIEW_VIDEO = (ImageView) findViewById(R.id.m01_preview_ImageView_video);
		SEEKBAR_VIDEO = (SeekBar) findViewById(R.id.m01_preview_SeekBar_time);
		IMAGEBUTTON_PLAY = (ImageButton) findViewById(R.id.m01_preview_ImageButton_playvideo);
		IMAGEBUTTON_PAUSE = (ImageButton) findViewById(R.id.m01_preview_ImageButton_pausevideo);
		TEXTVIEW_VIDEO_NAVI = (TextView) findViewById(R.id.m01_preview_TextView_time);

		TEXTVIEW_TITLE = (TextView) findViewById(R.id.m01_preview_title);
		TEXTVIEW_DETAIL = (TextView) findViewById(R.id.m01_preview_TextView_detail);

		EDITTEXT_EDITTITLE = (EditText) findViewById(R.id.m01_preview_edittitle);
		LAYOUT_EDITTITLE = (LinearLayout) findViewById(R.id.m01_preview_edittitle_layout);
		BUTTON_EDITTITLE = (ImageButton) findViewById(R.id.m01_preview_edittitlecheckbtn);

		EDITTEXT_EDITDETAIL = (EditText) findViewById(R.id.m01_preview_editdetail);
		LAYOUT_EDITDETAIL = (LinearLayout) findViewById(R.id.m01_preview_editdetail_layout);
		BUTTON_EDITDETAIL = (ImageButton) findViewById(R.id.m01_preview_editdetailcheckbtn);

		// 재생관련
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
				displayWidth, (int) (displayWidth * displayWidth
						/ displayHeight / 0.9));
		IMAGEVIEW_VIDEO.setLayoutParams(rlp);
		IMAGEVIEW_VIDEO.setImageBitmap(BitmapFactory.decodeFile(FILEFULLDIR
				+ "/" + "1" + ".png", opts));

		IMAGEBUTTON_PLAY.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isPlayVideo = true;
				isPauseVideo = false;
				IMAGEBUTTON_PLAY.setVisibility(View.GONE);
				VideoCtrl();
				IMAGEBUTTON_PAUSE.setVisibility(View.VISIBLE);
			}
		});
		IMAGEBUTTON_PAUSE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isPlayVideo = false;
				isPauseVideo = true;
			}
		});
		SEEKBAR_VIDEO.setMax(allpages);
		SEEKBAR_VIDEO.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				count = progress;
				if (!isPlayVideo) {
					count = progress;
					IMAGEVIEW_VIDEO.setImageBitmap(BitmapFactory.decodeFile(
							FILEFULLDIR + "/" + Integer.toString(count)
									+ ".png", opts));
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		// 텍스트, 버튼 관련
		TEXTVIEW_TITLE.setText(FILENAME);
		TEXTVIEW_TITLE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LAYOUT_EDITTITLE.setVisibility(View.VISIBLE);
				EDITTEXT_EDITTITLE.setText(FILENAME);
				TEXTVIEW_TITLE.setVisibility(View.GONE);

				EDITTEXT_EDITTITLE.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(EDITTEXT_EDITTITLE,
						InputMethodManager.SHOW_FORCED);
			}
		});

		BUTTON_EDITTITLE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] tmp = DBEDIT.FIND_BY_FILENAME(mHelper, FILENAME);
				if (DBEDIT.EXIST_BY_FILENAME(mHelper, EDITTEXT_EDITTITLE
						.getText().toString())) {
					if (EDITTEXT_EDITTITLE.getText().toString()
							.equals(FILENAME)) {

					} else {
						Toast.makeText(M01_Preview.this, "Already exists",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				DBEDIT.CHANGEDATA(mHelper, EDITTEXT_EDITTITLE.getText()
						.toString(), tmp[DataBaseHelper.FILEDETAIL],
						tmp[DataBaseHelper.FILEDIR]);
				TEXTVIEW_TITLE.setText(EDITTEXT_EDITTITLE.getText().toString());
				FILENAME = EDITTEXT_EDITTITLE.getText().toString();
				PROJECTDATA = DBEDIT.FIND_BY_FILENAME(mHelper, FILENAME);
				LAYOUT_EDITTITLE.setVisibility(View.GONE);
				TEXTVIEW_TITLE.setVisibility(View.VISIBLE);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						EDITTEXT_EDITTITLE.getWindowToken(), 0);
			}
		});

		TEXTVIEW_DETAIL.setText(PROJECTDATA[DataBaseHelper.FILEDETAIL]);
		TEXTVIEW_DETAIL.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LAYOUT_EDITDETAIL.setVisibility(View.VISIBLE);
				EDITTEXT_EDITDETAIL
						.setText(PROJECTDATA[DataBaseHelper.FILEDETAIL]);
				TEXTVIEW_DETAIL.setVisibility(View.GONE);
			}
		});

		BUTTON_EDITDETAIL.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DBEDIT.CHANGEDATA(mHelper, FILENAME, EDITTEXT_EDITDETAIL
						.getText().toString(),
						PROJECTDATA[DataBaseHelper.FILEDIR]);
				TEXTVIEW_DETAIL.setText(EDITTEXT_EDITDETAIL.getText()
						.toString());
				PROJECTDATA = DBEDIT.FIND_BY_FILENAME(mHelper, FILENAME);
				LAYOUT_EDITDETAIL.setVisibility(View.GONE);
				TEXTVIEW_DETAIL.setVisibility(View.VISIBLE);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						EDITTEXT_EDITDETAIL.getWindowToken(), 0);
			}
		});

		int min = allpages / fps / 60;
		int sec = allpages / fps % 60;
		String entiretime;
		if (sec < 10) {
			entiretime = Integer.toString(min) + ":0" + Integer.toString(sec);
		} else {
			entiretime = Integer.toString(min) + ":" + Integer.toString(sec);
		}
		TEXTVIEW_VIDEO_NAVI.setText(entiretime);

		BUTTON_DELETE.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(B.DIALOG_DELETE);
			}
		});

		BUTTON_EDIT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(M01_Preview.this, M03_Canvas.class);
				intent.putExtra(B.INTENT_M00_M01_PROJECT_NAME, FILENAME);
				startActivity(intent);
			}
		});

		BUTTON_SETTING.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(M01_Preview.this, M02_Setting.class);
				startActivity(intent);
			}
		});

		BUTTON_BACK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private int count = 0, allpages = 0;
	private long curtime, pretime = 0;
	private Thread VideoThread;

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			if (isPauseVideo) {
				IMAGEBUTTON_PLAY.setVisibility(View.VISIBLE);
				IMAGEBUTTON_PAUSE.setVisibility(View.GONE);
				return;
			}
			if (msg.what == 0) {
				IMAGEVIEW_VIDEO.setImageBitmap(BitmapFactory.decodeFile(
						FILEFULLDIR + "/" + Integer.toString(count) + ".png",
						opts));
				SEEKBAR_VIDEO.setProgress(count);
			} else if (msg.what == 1) {
				IMAGEBUTTON_PLAY.setVisibility(View.VISIBLE);
				IMAGEBUTTON_PAUSE.setVisibility(View.GONE);
				isPlayVideo = false;
				count = 0;
				curtime = 0;
				pretime = 0;
				Log.i("TAG", FILEFULLDIR + "/" + "1" + ".png");
				SEEKBAR_VIDEO.setProgress(count);
				IMAGEVIEW_VIDEO.setImageBitmap(BitmapFactory.decodeFile(
						FILEFULLDIR + "/" + Integer.toString(count) + ".png",
						opts));
			}
		}
	};

	private void VideoCtrl() {
		VideoThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (count < allpages) {
					if (!isPlayVideo) {
						break;
					}
					curtime = System.currentTimeMillis();
					if (curtime - pretime >= 1000 / fps) {
						pretime = curtime;
						mhandler.post(new Runnable() {
							public void run() {
								mhandler.sendEmptyMessage(0);
								count++;
							}
						});
					}
				}
				mhandler.post(new Runnable() {
					public void run() {
						mhandler.sendEmptyMessage(1);
					}
				});

			}
		});
		VideoThread.start();
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case B.DIALOG_DELETE:
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
			alt_bld.setMessage("Do you want to Delete?")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									String[] tmp = DBEDIT.FIND_BY_FILENAME(
											mHelper, FILENAME);
									FILEMANAGER
											.DELETE_FILE_FODLER(tmp[DataBaseHelper.FILEDIR]);
									DBEDIT.DELETE(mHelper,
											tmp[DataBaseHelper.FILEDIR]);
									finish();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = alt_bld.create();
			alert.setTitle("Delete");
			alert.setCanceledOnTouchOutside(true);
			return alert;
		}

		return null;
	}

}
