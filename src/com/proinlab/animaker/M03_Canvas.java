package com.proinlab.animaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.proinlab.canvasview.AboutPen;
import com.proinlab.canvasview.CanvasView;
import com.proinlab.canvasview.ColorSet;
import com.proinlab.functions.B;
import com.proinlab.functions.DataBaseHelper;
import com.proinlab.functions.FileManager;

public class M03_Canvas extends Activity {

	private FileManager FILEMANAGER = new FileManager();
	private DBEditFn DBEDIT = new DBEditFn();
	private DataBaseHelper mHelper;

	private RelativeLayout.LayoutParams rlp;
	private LinearLayout.LayoutParams llp;

	private String ProjectName, ProjectDir;
	private int displayWidth, displayHeight;
	private int fps = 2, alpha = 40, WorkspacePage = 1, PageListPage = 1,
			allFilePages = 0, DeletePageNum;
	// 파일 경로 : ProjectDir/WorkspacePage.png

	private int FocusedMenu = 0;
	private static final int MENUID_PENSTYLE = 1;
	private static final int MENUID_PAGELIST = 2;
	private static final int MENUID_SETBG = 3;
	private static final int MENUID_PLAYVEDIO = 4;

	private CanvasView mCanvasView;

	private ImageView IMAGEVIEW_CANVASBG;
	private ImageButton IMAGEBUTTON_BACK, IMAGEBUTTON_PENSETTING,
			IMAGEBUTTON_LIST, IMAGEBUTTON_PLAYVIDEO, IMAGEBUTTON_SETBG;
	private TextView TEXTVIEW_PAGENAVI;

	private ImageButton IMAGEBUTTON_PENSETTING_EXIT;

	private RelativeLayout LAYOUT_CANVAS;

	private LinearLayout LAYOUT_PENSETTING;
	private ImageButton IMAGEBUTTON_PENSTYLE_PEN, IMAGEBUTTON_PENSTYLE_MARKER,
			IMAGEBUTTON_PENSTYLE_ERASER;
	private SeekBar SEEKBAR_PENWIDTH;
	private ImageView IMAGEVIEW_PENWIDTH;
	private TextView TEXTVIEW_PENWIDTH;
	private LinearLayout LAYOUT_COLORLIST;

	private int CurrentPenStyle = AboutPen.Pen;
	private int PenColor = Color.BLACK, MarkerColor = Color.rgb(206, 230, 185);
	private int PenWidth = 4, EraserWidth = 10;

	private ImageView[] IMAGEVIEW_COLORLIST = new ImageView[30];
	private int MarkerWidth = 20;
	private int[] colorint;

	private LinearLayout LAYOUT_PAGELIST, LAYOUT_PAGELIST_1, LAYOUT_PAGELIST_2;
	private ImageButton IMAGEBUTTON_PAGELIST_EXIT,
			IMAGEBUTTON_PAGELIST_PREPAGE, IMAGEBUTTON_PAGELIST_NEXTPAGE,
			IMAGEBUTTON_PAGELIST_PAGEADD, IMAGEBUTTON_PAGELIST_PAGEADD_IMG;
	private TextView TEXTVIEW_PAGELIST_NAVI;

	@Override
	public void onPause() {
		super.onPause();
		FILEMANAGER.SAVE_SINGLE_PAGE(mCanvasView.getWorkspaceBitmap(),
				ProjectDir + "/" + Integer.toString(WorkspacePage) + ".png");
		mCanvasView.distroyView();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m03_canvas);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		fps = pref.getInt(M02_Setting.SETTING_FPS_TAG, 2);
		alpha = pref.getInt(M02_Setting.SETTING_BGF_TAG, 40);
		alpha = 255 * alpha / 100;

		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		if (displayWidth < displayHeight) {
			displayHeight = display.getWidth();
			displayWidth = display.getHeight();
		}

		mHelper = new DataBaseHelper(this);

		ProjectName = getIntent().getExtras().getString(
				B.INTENT_M00_M01_PROJECT_NAME);
		ProjectDir = DBEDIT.FIND_BY_FILENAME(mHelper, ProjectName)[DataBaseHelper.FILEDIR];
		ProjectDir = B.SDCARD_DIRECTORY + "FILE/" + ProjectDir;

		findViewId();

		mCanvasView = new CanvasView(this);
		mCanvasView.initializeCanvas(displayWidth * 9 / 10, displayHeight);
		LAYOUT_CANVAS.addView(mCanvasView);
		if (FILEMANAGER.GET_LENGTH_SAVE_PAGES(ProjectDir) != 0) {
			mCanvasView.setWorkspaceBitmap(BitmapFactory.decodeFile(ProjectDir
					+ "/1.png"));
		}

		MainMenuInit();
		AllMenuHide();

		PenColorInit();
		PenSelectInit();
		penWidthInit();

		PageListinit();
		TEXTVIEW_PAGENAVI.setText(Integer.toString(WorkspacePage) + " / "
				+ Integer.toString(allFilePages));

	}

	/*
	 * 펜 스타일 변경 관련 함수
	 */

	private void PenSetting(int PenColor, int PenWidth) {
		IMAGEBUTTON_PENSTYLE_PEN
				.setBackgroundResource(R.drawable.pensetting_icon_pen_unselect);
		IMAGEBUTTON_PENSTYLE_MARKER
				.setBackgroundResource(R.drawable.pensetting_icon_neon_unselect);
		IMAGEBUTTON_PENSTYLE_ERASER
				.setBackgroundResource(R.drawable.pensetting_icon_eraser_unselect);
		switch (CurrentPenStyle) {
		case AboutPen.Pen:
			IMAGEBUTTON_PENSTYLE_PEN
					.setBackgroundResource(R.drawable.pensetting_icon_pen);
			if (PenColor != 0) {
				this.PenColor = PenColor;
				mCanvasView.setPenColor(this.PenColor);
			}
			if (PenWidth > 0) {
				this.PenWidth = PenWidth;
				mCanvasView.setPenSize(this.PenWidth);

				TEXTVIEW_PENWIDTH.setText(Integer.toString(PenWidth));
				rlp = new RelativeLayout.LayoutParams(PenWidth, PenWidth);
				rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
				IMAGEVIEW_PENWIDTH.setLayoutParams(rlp);
				SEEKBAR_PENWIDTH.setProgress(PenWidth * 4);
			}
			break;
		case AboutPen.MARKER:
			IMAGEBUTTON_PENSTYLE_MARKER
					.setBackgroundResource(R.drawable.pensetting_icon_neon);
			if (PenColor != 0) {
				MarkerColor = PenColor;
				mCanvasView.setPenColor(MarkerColor);
				mCanvasView.setPenAlpha(100);
			}
			if (PenWidth > 0) {
				MarkerWidth = PenWidth;
				mCanvasView.setPenSize(MarkerWidth);

				TEXTVIEW_PENWIDTH.setText(Integer.toString(PenWidth));
				rlp = new RelativeLayout.LayoutParams(PenWidth, PenWidth);
				rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
				IMAGEVIEW_PENWIDTH.setLayoutParams(rlp);
				SEEKBAR_PENWIDTH.setProgress(PenWidth * 4);
			}
			break;
		case AboutPen.ERASER:
			IMAGEBUTTON_PENSTYLE_ERASER
					.setBackgroundResource(R.drawable.pensetting_icon_eraser);
			if (PenWidth > 0) {
				EraserWidth = PenWidth;
				mCanvasView.setPenSize(EraserWidth);

				TEXTVIEW_PENWIDTH.setText(Integer.toString(PenWidth));
				rlp = new RelativeLayout.LayoutParams(PenWidth, PenWidth);
				rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
				IMAGEVIEW_PENWIDTH.setLayoutParams(rlp);
				SEEKBAR_PENWIDTH.setProgress(PenWidth * 4);
			}
			break;
		}
	}

	private void PenSelectInit() {
		IMAGEBUTTON_PENSTYLE_PEN
				.setBackgroundResource(R.drawable.pensetting_icon_pen);
		mCanvasView.setPenColor(PenColor);
		mCanvasView.setPenSize(PenWidth);

		IMAGEBUTTON_PENSTYLE_PEN.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IMAGEBUTTON_PENSTYLE_PEN
						.setBackgroundResource(R.drawable.pensetting_icon_pen_unselect);
				IMAGEBUTTON_PENSTYLE_MARKER
						.setBackgroundResource(R.drawable.pensetting_icon_neon_unselect);
				IMAGEBUTTON_PENSTYLE_ERASER
						.setBackgroundResource(R.drawable.pensetting_icon_eraser_unselect);
				IMAGEBUTTON_PENSTYLE_PEN
						.setBackgroundResource(R.drawable.pensetting_icon_pen);
				CurrentPenStyle = AboutPen.Pen;
				mCanvasView.setPenStyle(CurrentPenStyle);
				PenSetting(PenColor, PenWidth);

				for (int i = 0; i < 30; i++) {
					IMAGEVIEW_COLORLIST[i].setImageBitmap(null);
					if (PenColor == colorint[i]) {
						IMAGEVIEW_COLORLIST[i]
								.setImageResource(R.drawable.m01_canvas_colorselect);
					}
				}
			}
		});

		IMAGEBUTTON_PENSTYLE_MARKER
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						IMAGEBUTTON_PENSTYLE_PEN
								.setBackgroundResource(R.drawable.pensetting_icon_pen_unselect);
						IMAGEBUTTON_PENSTYLE_MARKER
								.setBackgroundResource(R.drawable.pensetting_icon_neon_unselect);
						IMAGEBUTTON_PENSTYLE_ERASER
								.setBackgroundResource(R.drawable.pensetting_icon_eraser_unselect);
						IMAGEBUTTON_PENSTYLE_MARKER
								.setBackgroundResource(R.drawable.pensetting_icon_neon);
						CurrentPenStyle = AboutPen.MARKER;
						mCanvasView.setPenStyle(CurrentPenStyle);
						PenSetting(MarkerColor, MarkerWidth);

						for (int i = 0; i < 30; i++) {
							IMAGEVIEW_COLORLIST[i].setImageBitmap(null);
							if (MarkerColor == colorint[i]) {
								IMAGEVIEW_COLORLIST[i]
										.setImageResource(R.drawable.m01_canvas_colorselect);
							}
						}
					}
				});

		IMAGEBUTTON_PENSTYLE_ERASER
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						IMAGEBUTTON_PENSTYLE_PEN
								.setBackgroundResource(R.drawable.pensetting_icon_pen_unselect);
						IMAGEBUTTON_PENSTYLE_MARKER
								.setBackgroundResource(R.drawable.pensetting_icon_neon_unselect);
						IMAGEBUTTON_PENSTYLE_ERASER
								.setBackgroundResource(R.drawable.pensetting_icon_eraser_unselect);
						IMAGEBUTTON_PENSTYLE_ERASER
								.setBackgroundResource(R.drawable.pensetting_icon_eraser);
						CurrentPenStyle = AboutPen.ERASER;
						mCanvasView.setPenStyle(CurrentPenStyle);
						PenSetting(0, EraserWidth);

						for (int i = 0; i < 30; i++) {
							IMAGEVIEW_COLORLIST[i].setImageBitmap(null);
						}
					}
				});
	}

	private void penWidthInit() {
		TEXTVIEW_PENWIDTH.setText(Integer.toString(PenWidth));
		rlp = new RelativeLayout.LayoutParams(PenWidth, PenWidth);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		IMAGEVIEW_PENWIDTH.setLayoutParams(rlp);

		SEEKBAR_PENWIDTH.setMax(320);
		SEEKBAR_PENWIDTH.setProgress(PenWidth * 4);
		SEEKBAR_PENWIDTH
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						TEXTVIEW_PENWIDTH.setText(Integer
								.toString(progress / 4));
						rlp = new RelativeLayout.LayoutParams(progress / 4,
								progress / 4);
						rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
						IMAGEVIEW_PENWIDTH.setLayoutParams(rlp);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						if (seekBar.getProgress() == 0)
							PenSetting(0, 1);
						else
							PenSetting(0, seekBar.getProgress() / 4);
					}
				});

	}

	private void PenColorInit() {
		LAYOUT_COLORLIST.removeAllViewsInLayout();
		llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		llp.setMargins(20, 20, 20, 20);
		LAYOUT_COLORLIST.setLayoutParams(llp);

		ColorSet cs = new ColorSet();
		colorint = cs.BasicColorSet();
		for (int i = 0; i < 3; i++) {
			LinearLayout tmpl = new LinearLayout(this);
			tmpl.setGravity(Gravity.CENTER);
			for (int j = 0; j < 10; j++) {
				IMAGEVIEW_COLORLIST[i * 10 + j] = new ImageView(this);
				IMAGEVIEW_COLORLIST[i * 10 + j].setBackgroundColor(colorint[i
						* 10 + j]);
				llp = new LinearLayout.LayoutParams(displayWidth / 17,
						displayHeight / 16);
				llp.setMargins(4, displayHeight / 96, 4, displayHeight / 96);
				IMAGEVIEW_COLORLIST[i * 10 + j].setLayoutParams(llp);
				IMAGEVIEW_COLORLIST[i * 10 + j]
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								for (int i = 0; i < 30; i++) {
									IMAGEVIEW_COLORLIST[i].setImageBitmap(null);
								}
								for (int i = 0; i < 30; i++) {
									if (v == IMAGEVIEW_COLORLIST[i]) {
										IMAGEVIEW_COLORLIST[i]
												.setImageResource(R.drawable.m01_canvas_colorselect);
										PenSetting(colorint[i], 0);
									}
								}
							}
						});
				tmpl.addView(IMAGEVIEW_COLORLIST[i * 10 + j]);
			}
			LAYOUT_COLORLIST.addView(tmpl);
		}
		IMAGEVIEW_COLORLIST[4]
				.setImageResource(R.drawable.m01_canvas_colorselect);
	}

	/*
	 * 페이지 리스트 관련 함수
	 */

	private BitmapFactory.Options opts = new BitmapFactory.Options();

	private void pageListAdd() {
		FILEMANAGER.SAVE_SINGLE_PAGE(mCanvasView.getWorkspaceBitmap(),
				ProjectDir + "/" + Integer.toString(WorkspacePage) + ".png");
		allFilePages = allFilePages + 1;
		WorkspacePage = allFilePages;
		PageListPage = (WorkspacePage - 1) / 8 + 1;
		mCanvasView.initializeCanvas(displayWidth * 9 / 10, displayHeight);
		FILEMANAGER.SAVE_SINGLE_PAGE(mCanvasView.getWorkspaceBitmap(),
				ProjectDir + "/" + Integer.toString(WorkspacePage) + ".png");
		PageListRefresh((WorkspacePage - 1) / 8 + 1);
		TEXTVIEW_PAGENAVI.setText(Integer.toString(WorkspacePage) + " / "
				+ Integer.toString(allFilePages));
		TEXTVIEW_PAGELIST_NAVI.setText(Integer.toString(PageListPage) + " / "
				+ Integer.toString((allFilePages - 1) / 8 + 1));
	}

	private void PageListinit() {
		// TODO 버튼 동작 설정
		allFilePages = FILEMANAGER.GET_LENGTH_SAVE_PAGES(ProjectDir);
		PageListPage = (WorkspacePage - 1) / 8 + 1;
		PageListRefresh(PageListPage);

		TEXTVIEW_PAGELIST_NAVI.setText(Integer.toString(PageListPage) + " / "
				+ Integer.toString((allFilePages - 1) / 8 + 1));

		if (FocusedMenu == MENUID_PAGELIST) {
			IMAGEBUTTON_PAGELIST_PAGEADD_IMG
					.setBackgroundResource(R.drawable.canvas_pages);
			IMAGEBUTTON_PAGELIST_PAGEADD
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							pageListAdd();
						}
					});
		} else if (FocusedMenu == MENUID_SETBG) {
			IMAGEBUTTON_PAGELIST_PAGEADD_IMG
					.setBackgroundResource(R.drawable.clearbtn);
			IMAGEBUTTON_PAGELIST_PAGEADD
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							IMAGEVIEW_CANVASBG.setImageBitmap(null);
						}
					});
		}

		IMAGEBUTTON_PAGELIST_PREPAGE
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (PageListPage == 1) {
							PageListPage = (allFilePages - 1) / 8 + 1;
							PageListRefresh(PageListPage);
							TEXTVIEW_PAGELIST_NAVI.setText(Integer
									.toString(PageListPage)
									+ " / "
									+ Integer
											.toString((allFilePages - 1) / 8 + 1));
						} else {
							PageListPage--;
							PageListRefresh(PageListPage);
							TEXTVIEW_PAGELIST_NAVI.setText(Integer
									.toString(PageListPage)
									+ " / "
									+ Integer
											.toString((allFilePages - 1) / 8 + 1));
						}
					}
				});

		IMAGEBUTTON_PAGELIST_NEXTPAGE
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (PageListPage == (allFilePages - 1) / 8 + 1) {
							PageListPage = 1;
							PageListRefresh(PageListPage);
							TEXTVIEW_PAGELIST_NAVI.setText(Integer
									.toString(PageListPage)
									+ " / "
									+ Integer
											.toString((allFilePages - 1) / 8 + 1));
						} else {
							PageListPage++;
							PageListRefresh(PageListPage);
							TEXTVIEW_PAGELIST_NAVI.setText(Integer
									.toString(PageListPage)
									+ " / "
									+ Integer
											.toString((allFilePages - 1) / 8 + 1));
						}
					}
				});

	}

	private void PageListRefresh(int page) {
		allFilePages = FILEMANAGER.GET_LENGTH_SAVE_PAGES(ProjectDir);

		opts.inSampleSize = 8;
		opts.inJustDecodeBounds = false;
		llp = new LinearLayout.LayoutParams(displayWidth / 8, displayHeight / 8);
		llp.setMargins(10, 10, 10, 10);

		LAYOUT_PAGELIST_1.removeAllViewsInLayout();
		LAYOUT_PAGELIST_2.removeAllViewsInLayout();

		int cur_page = (page - 1) * 8;

		for (int i = 0; i < 4; i++) {
			cur_page = cur_page + 1;
			if (cur_page <= allFilePages) {
				ImageView imgv = new ImageView(this);
				imgv.setImageBitmap(BitmapFactory.decodeFile(ProjectDir + "/"
						+ Integer.toString(cur_page) + ".png", opts));
				imgv.setBackgroundResource(R.drawable.canvas_pagelist_box);
				imgv.setLayoutParams(llp);
				final int k = cur_page;

				if (FocusedMenu == MENUID_PAGELIST)
					imgv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							FILEMANAGER.SAVE_SINGLE_PAGE(
									mCanvasView.getWorkspaceBitmap(),
									ProjectDir + "/"
											+ Integer.toString(WorkspacePage)
											+ ".png");
							WorkspacePage = k;
							mCanvasView.setWorkspaceBitmap(BitmapFactory
									.decodeFile(ProjectDir + "/"
											+ Integer.toString(WorkspacePage)
											+ ".png"));
							TEXTVIEW_PAGENAVI.setText(Integer
									.toString(WorkspacePage)
									+ " / "
									+ Integer.toString(allFilePages));
							PageListRefresh(PageListPage);
						}
					});
				else if (FocusedMenu == MENUID_SETBG)
					imgv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							IMAGEVIEW_CANVASBG.setImageBitmap(BitmapFactory
									.decodeFile(ProjectDir + "/"
											+ Integer.toString(k) + ".png"));
							IMAGEVIEW_CANVASBG.setAlpha(alpha);
							Toast.makeText(
									M03_Canvas.this,
									Integer.toString(k)
											+ " page has been set as a background.",
									Toast.LENGTH_SHORT).show();
						}
					});

				imgv.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						DeletePageNum = k;
						showDialog(0);
						return false;
					}
				});
				LAYOUT_PAGELIST_1.addView(imgv);
			} else {
				ImageView imgv = new ImageView(this);
				imgv.setLayoutParams(llp);
				LAYOUT_PAGELIST_1.addView(imgv);
			}
		}

		for (int i = 0; i < 4; i++) {
			cur_page = cur_page + 1;
			if (cur_page <= allFilePages) {
				ImageView imgv = new ImageView(this);
				imgv.setImageBitmap(BitmapFactory.decodeFile(ProjectDir + "/"
						+ Integer.toString(cur_page) + ".png", opts));
				imgv.setBackgroundResource(R.drawable.canvas_pagelist_box);
				imgv.setLayoutParams(llp);
				final int k = cur_page;

				if (FocusedMenu == MENUID_PAGELIST)
					imgv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							FILEMANAGER.SAVE_SINGLE_PAGE(
									mCanvasView.getWorkspaceBitmap(),
									ProjectDir + "/"
											+ Integer.toString(WorkspacePage)
											+ ".png");
							WorkspacePage = k;
							mCanvasView.setWorkspaceBitmap(BitmapFactory
									.decodeFile(ProjectDir + "/"
											+ Integer.toString(WorkspacePage)
											+ ".png"));
							TEXTVIEW_PAGENAVI.setText(Integer
									.toString(WorkspacePage)
									+ " / "
									+ Integer.toString(allFilePages));
							PageListRefresh(PageListPage);
						}
					});
				else if (FocusedMenu == MENUID_SETBG)
					imgv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							IMAGEVIEW_CANVASBG.setImageBitmap(BitmapFactory
									.decodeFile(ProjectDir + "/"
											+ Integer.toString(k) + ".png"));
							IMAGEVIEW_CANVASBG.setAlpha(alpha);
							Toast.makeText(
									M03_Canvas.this,
									Integer.toString(k)
											+ " page has been set as a background.",
									Toast.LENGTH_SHORT).show();
						}
					});

				imgv.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						DeletePageNum = k;
						showDialog(0);
						return false;
					}
				});
				LAYOUT_PAGELIST_2.addView(imgv);
			} else {
				ImageView imgv = new ImageView(this);
				imgv.setLayoutParams(llp);
				LAYOUT_PAGELIST_2.addView(imgv);
			}
		}

		LAYOUT_PAGELIST_1.setGravity(Gravity.CENTER);
		LAYOUT_PAGELIST_2.setGravity(Gravity.CENTER);
	}

	/*
	 * 메인 메뉴 관련 함수
	 */

	private void MainMenuInit() {
		IMAGEBUTTON_BACK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		IMAGEBUTTON_PENSETTING.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FocusedMenu == MENUID_PENSTYLE) {
					AllMenuHide();
					FocusedMenu = 0;
				} else {
					AllMenuHide();
					IMAGEBUTTON_PENSETTING
							.setBackgroundResource(R.drawable.naviclick);
					LAYOUT_PENSETTING.setVisibility(View.VISIBLE);
					LAYOUT_PENSETTING
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
								}
							});
					FocusedMenu = MENUID_PENSTYLE;
				}
			}
		});

		IMAGEBUTTON_PENSETTING_EXIT
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (FocusedMenu == MENUID_PENSTYLE) {
							AllMenuHide();
							FocusedMenu = 0;
						} else {
							AllMenuHide();
							IMAGEBUTTON_PENSETTING
									.setBackgroundResource(R.drawable.naviclick);
							LAYOUT_PENSETTING.setVisibility(View.VISIBLE);
							FocusedMenu = MENUID_PENSTYLE;
						}
					}
				});

		IMAGEBUTTON_LIST.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FocusedMenu == MENUID_PAGELIST) {
					AllMenuHide();
					FocusedMenu = 0;
				} else {
					AllMenuHide();
					IMAGEBUTTON_LIST
							.setBackgroundResource(R.drawable.naviclick);
					LAYOUT_PAGELIST.setVisibility(View.VISIBLE);
					LAYOUT_PAGELIST
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
								}
							});
					FocusedMenu = MENUID_PAGELIST;
					PageListinit();
				}
			}
		});

		IMAGEBUTTON_SETBG.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FocusedMenu == MENUID_SETBG) {
					AllMenuHide();
					FocusedMenu = 0;
				} else {
					AllMenuHide();
					IMAGEBUTTON_SETBG
							.setBackgroundResource(R.drawable.naviclick);
					LAYOUT_PAGELIST.setVisibility(View.VISIBLE);
					LAYOUT_PAGELIST
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
								}
							});
					FocusedMenu = MENUID_SETBG;
					PageListinit();
				}
			}
		});

		IMAGEBUTTON_PAGELIST_EXIT
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (FocusedMenu == MENUID_PAGELIST) {
							AllMenuHide();
							FocusedMenu = 0;
						} else if (FocusedMenu == MENUID_SETBG) {
							AllMenuHide();
							FocusedMenu = 0;
						}
					}
				});

		IMAGEBUTTON_PLAYVIDEO.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FocusedMenu == MENUID_PLAYVEDIO) {
					AllMenuHide();
					FocusedMenu = 0;
				} else {
					AllMenuHide();
					IMAGEBUTTON_PLAYVIDEO
							.setBackgroundResource(R.drawable.naviclick);
					FocusedMenu = MENUID_PLAYVEDIO;
				}
			}
		});
	}

	private void AllMenuHide() {
		LAYOUT_PENSETTING.setVisibility(View.GONE);
		LAYOUT_PAGELIST.setVisibility(View.GONE);

		IMAGEBUTTON_PENSETTING.setBackgroundResource(R.drawable.navi);
		IMAGEBUTTON_LIST.setBackgroundResource(R.drawable.navi);
		IMAGEBUTTON_PLAYVIDEO.setBackgroundResource(R.drawable.navi);
		IMAGEBUTTON_SETBG.setBackgroundResource(R.drawable.navi);
	}

	private void findViewId() {

		IMAGEVIEW_CANVASBG = (ImageView) findViewById(R.id.m03_canvas_ImageView_CanvasBG);

		// 우측 메뉴 목록
		IMAGEBUTTON_BACK = (ImageButton) findViewById(R.id.m03_canvas_ImageButton_exit);
		IMAGEBUTTON_PENSETTING = (ImageButton) findViewById(R.id.m03_canvas_ImageButton_penstyle);
		IMAGEBUTTON_LIST = (ImageButton) findViewById(R.id.m03_canvas_ImageButton_pagelist);
		IMAGEBUTTON_PLAYVIDEO = (ImageButton) findViewById(R.id.m03_canvas_ImageButton_play);
		IMAGEBUTTON_SETBG = (ImageButton) findViewById(R.id.m03_canvas_ImageButton_SetBG);
		TEXTVIEW_PAGENAVI = (TextView) findViewById(R.id.m03_canvas_textview_pagenavi);

		LAYOUT_CANVAS = (RelativeLayout) findViewById(R.id.m03_canvas_Layout_workspace);

		// 펜 설정 관련
		LAYOUT_PENSETTING = (LinearLayout) findViewById(R.id.m03_canvas_Layout_PenSetting);
		IMAGEBUTTON_PENSETTING_EXIT = (ImageButton) findViewById(R.id.m03_canvas_Pensetting_exit);
		IMAGEBUTTON_PENSTYLE_PEN = (ImageButton) findViewById(R.id.m03_canvas_ImageButton_penstyle_pen);
		IMAGEBUTTON_PENSTYLE_MARKER = (ImageButton) findViewById(R.id.m03_canvas_ImageButton_penstyle_marker);
		IMAGEBUTTON_PENSTYLE_ERASER = (ImageButton) findViewById(R.id.m03_canvas_ImageButton_penstyle_eraser);
		SEEKBAR_PENWIDTH = (SeekBar) findViewById(R.id.m03_canvas_SeekBar_width);
		IMAGEVIEW_PENWIDTH = (ImageView) findViewById(R.id.m03_canvas_ImageView_Circle);
		TEXTVIEW_PENWIDTH = (TextView) findViewById(R.id.m03_canvas_TextView_widthsize);
		LAYOUT_COLORLIST = (LinearLayout) findViewById(R.id.m03_canvas_Layout_colorlist);

		// 페이지 리스트 관련
		LAYOUT_PAGELIST = (LinearLayout) findViewById(R.id.m03_canvas_Layout_PageList);
		LAYOUT_PAGELIST_1 = (LinearLayout) findViewById(R.id.m03_canvas_PageList_Layout_1);
		LAYOUT_PAGELIST_2 = (LinearLayout) findViewById(R.id.m03_canvas_PageList_Layout_2);
		IMAGEBUTTON_PAGELIST_EXIT = (ImageButton) findViewById(R.id.m03_canvas_PageList_exit);
		IMAGEBUTTON_PAGELIST_PREPAGE = (ImageButton) findViewById(R.id.m03_canvas_PageList_navi_pre);
		IMAGEBUTTON_PAGELIST_NEXTPAGE = (ImageButton) findViewById(R.id.m03_canvas_PageList_navinext);
		IMAGEBUTTON_PAGELIST_PAGEADD = (ImageButton) findViewById(R.id.m03_canvas_PageList_addpage);
		TEXTVIEW_PAGELIST_NAVI = (TextView) findViewById(R.id.m03_canvas_PageList_navitxt);
		IMAGEBUTTON_PAGELIST_PAGEADD_IMG = (ImageButton) findViewById(R.id.m03_canvas_PageList_addpage_img);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			String[] arr = { "Delete" };
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setItems(arr, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								if (DeletePageNum == 1) {
									if (DeletePageNum == FILEMANAGER
											.GET_LENGTH_SAVE_PAGES(ProjectDir)) {
										mCanvasView.initializeCanvas(
												displayWidth * 9 / 10,
												displayHeight);
										PageListRefresh(PageListPage);
										return;
									} else {
										FILEMANAGER.DELETE_PAGE(ProjectDir,
												DeletePageNum);
									}
								} else {
									FILEMANAGER.DELETE_PAGE(ProjectDir,
											DeletePageNum);
								}
								break;
							}
							if (WorkspacePage > FILEMANAGER
									.GET_LENGTH_SAVE_PAGES(ProjectDir))
								WorkspacePage = FILEMANAGER
										.GET_LENGTH_SAVE_PAGES(ProjectDir);

							mCanvasView.setWorkspaceBitmap(BitmapFactory
									.decodeFile(ProjectDir + "/"
											+ Integer.toString(WorkspacePage)
											+ ".png"));
							allFilePages = FILEMANAGER
									.GET_LENGTH_SAVE_PAGES(ProjectDir);
							PageListRefresh(PageListPage);
							TEXTVIEW_PAGENAVI.setText(Integer
									.toString(WorkspacePage)
									+ " / "
									+ Integer.toString(allFilePages));
						}
					});
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		}
		return null;
	}

}
