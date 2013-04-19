package com.proinlab.animaker;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.proinlab.functions.B;
import com.proinlab.functions.DataBaseHelper;
import com.proinlab.functions.FileManager;

public class M00_List extends Activity {

	private FileManager FILEMANAGER = new FileManager();
	private DBEditFn DBEDIT = new DBEditFn();
	private DataBaseHelper mHelper;

	private ListView LISTVIEW_ANI;
	private ImageButton BUTTON_ADDPROJECT, BUTTON_SETTING;

	private ArrayList<String> ARRAYLIST_LIST;
	private ArrayAdapter<String> ARRAYADAPTER_LIST;
	private String STRING_SELECTED_FILENAME = null;

	private int displayWidth, displayHeight;

	@Override
	public void onResume() {
		super.onResume();
		RefreshList();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m00_list);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		if (displayWidth > displayHeight) {
			displayHeight = display.getWidth();
			displayWidth = display.getHeight();
		}

		mHelper = new DataBaseHelper(this);

		LISTVIEW_ANI = (ListView) findViewById(R.id.m00_main_ListView_anilist);
		BUTTON_ADDPROJECT = (ImageButton) findViewById(R.id.m00_list_addprojectbtn);
		BUTTON_SETTING = (ImageButton) findViewById(R.id.m00_list_settingbtn);

		BUTTON_ADDPROJECT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String dir = DBEDIT.CREATE_FILENAME(mHelper);

				long nowmills = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String now = sdf.format(new Date(nowmills));

				STRING_SELECTED_FILENAME = now;
				DBEDIT.INSERT(mHelper, now, dir, "Detail");

				FILEMANAGER.CREATE_FILE_FODLER(dir);

				Intent intent = new Intent(M00_List.this, M01_Preview.class);
				intent.putExtra("PROJECTNAME", STRING_SELECTED_FILENAME);
				startActivity(intent);
			}
		});

		BUTTON_SETTING.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(M00_List.this, M02_Setting.class);
				startActivity(intent);
			}
		});

		RefreshList();

	}

	private boolean RefreshList() {
		String[] tmpstrl = DBEDIT.FIND_ALL_FILENAME(mHelper);

		if (tmpstrl == null) {
			ARRAYLIST_LIST = new ArrayList<String>();
			ARRAYLIST_LIST.add("No File");
			ARRAYADAPTER_LIST = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, ARRAYLIST_LIST);
			LISTVIEW_ANI.setAdapter(ARRAYADAPTER_LIST);
			LISTVIEW_ANI.setOnItemClickListener(null);
			LISTVIEW_ANI.setOnItemLongClickListener(null);
			return false;
		} else {
			ARRAYLIST_LIST = new ArrayList<String>();
			for (int i = 0; i < tmpstrl.length; i++)
				this.ARRAYLIST_LIST.add(tmpstrl[i]);
			ARRAYADAPTER_LIST = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, ARRAYLIST_LIST);
			LISTVIEW_ANI.setAdapter(ARRAYADAPTER_LIST);
			LISTVIEW_ANI.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent intent = new Intent(M00_List.this, M01_Preview.class);
					intent.putExtra("PROJECTNAME", ARRAYLIST_LIST.get(arg2));
					startActivity(intent);
				}
			});
			LISTVIEW_ANI
					.setOnItemLongClickListener(new OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							STRING_SELECTED_FILENAME = ARRAYLIST_LIST.get(arg2);
							Log.i("TAG", STRING_SELECTED_FILENAME);
							showDialog(B.DIALOG_SELECT_FILE);
							return false;
						}
					});
			return true;
		}
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case B.DIALOG_SELECT_FILE:
			String[] arr = { "Rename", "Delete" };
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this).setItems(arr,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								ChangeInfoDialog();
							} else if (which == 1) {
								String[] tmp = DBEDIT.FIND_BY_FILENAME(mHelper,
										STRING_SELECTED_FILENAME);
								FILEMANAGER
										.DELETE_FILE_FODLER(tmp[DataBaseHelper.FILEDIR]);
								DBEDIT.DELETE(mHelper,
										tmp[DataBaseHelper.FILEDIR]);
								STRING_SELECTED_FILENAME = null;
								RefreshList();
							}
						}
					});
			AlertDialog alert = alt_bld.create();
			alert.setTitle("Project");
			alert.setCanceledOnTouchOutside(true);
			return alert;
		case B.DIALOG_DELETE:
			alt_bld = new AlertDialog.Builder(this);
			alt_bld.setMessage("Delete?")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									String[] tmp = DBEDIT.FIND_BY_FILENAME(
											mHelper, STRING_SELECTED_FILENAME);
									FILEMANAGER
											.DELETE_FILE_FODLER(tmp[DataBaseHelper.FILEDIR]);
									DBEDIT.DELETE(mHelper,
											tmp[DataBaseHelper.FILEDIR]);
									STRING_SELECTED_FILENAME = null;
									RefreshList();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			alert = alt_bld.create();
			alert.setTitle("Delete");
			alert.setCanceledOnTouchOutside(true);
			return alert;
		}

		return null;
	}

	private void ChangeInfoDialog() {
		final LinearLayout l2 = (LinearLayout) View.inflate(this,
				R.layout.m00_00_addlistdialog, null);
		EditText Title2 = (EditText) l2
				.findViewById(R.id.m00_00_EditText_title);
		EditText Detail2 = (EditText) l2
				.findViewById(R.id.m00_00_EditText_detail);
		final String[] tmp2 = DBEDIT.FIND_BY_FILENAME(mHelper,
				STRING_SELECTED_FILENAME);
		Title2.setText(tmp2[DataBaseHelper.FILENAME]);
		Detail2.setText(tmp2[DataBaseHelper.FILEDETAIL]);
		final EditText tmp = Title2;
		final EditText tmpD = Detail2;
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(l2);
		alt_bld.setMessage("Input title & detail")
				.setCancelable(false)
				.setPositiveButton("Change",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String[] tmp2 = DBEDIT.FIND_BY_FILENAME(
										mHelper, STRING_SELECTED_FILENAME);
								DBEDIT.CHANGEDATA(mHelper, tmp.getText()
										.toString(), tmpD.getText().toString(),
										tmp2[DataBaseHelper.FILEDIR]);
								RefreshList();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = alt_bld.create();
		alert.setTitle("Change Info");
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}
}
