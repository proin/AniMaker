/*****************************************************************************
 * @author PROIN LAB [ DB ±¸Á¶ ]
 *         -------------------------------------------------------------------
 *         TABLE : FILE_LIST, CATEGORY_LIST
 *         -------------------------------------------------------------------
 *         FILE_LIST ROW : FILENAME LATESTTIME FIRSTTIME CATEGORY FILEDIR
 *         FILETYPE
 *         -------------------------------------------------------------------
 *         CATEGORY_LIST ROW : CATEGORY
 *****************************************************************************/

package com.proinlab.functions;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String DATABASENAME = B.SDCARD_DIRECTORY
			+ "FILE_INFO.db";

	public static final String DB_TABLE_NAME = "FILE_LIST";

	public static final String DB_ROW_FILENAME = "FILENAME";
	public static final String DB_ROW_FILEDETAIL = "FILEDETAIL";
	public static final String DB_ROW_FILEDIR = "FILEDIR";

	public static final String[] DB_COLUMNS = { "FILENAME", "FILEDETAIL",
			"FILEDIR" };

	public static final int FILENAME = 0;
	public static final int FILEDETAIL = 1;
	public static final int FILEDIR = 2;

	public DataBaseHelper(Context context) {
		super(context, DATABASENAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DB_TABLE_NAME
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + DB_ROW_FILEDIR
				+ " TEXT, " + DB_ROW_FILENAME + " TEXT, " + DB_ROW_FILEDETAIL
				+ " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
		onCreate(db);
	}

}
