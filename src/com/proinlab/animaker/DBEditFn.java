/************************************************************************
 * @author PROIN LAB [ DB 관리 함수 ]
 *         --------------------------------------------------------------
 *         DB_FN_INSERT / DB_FN_FIND / DB_FN_DELETE / DB_FN_CHANGEDATA /
 *         DB_FN_FIND_FILENAME_BY_CATEGORY
 *         --------------------------------------------------------------
 *         DB_FN_FIND_CATEGORY_ALL / DB_FN_FIND_CATEGORY_EXIST /
 *         DB_FN_INSERT_CATEGORY
 ************************************************************************/

package com.proinlab.animaker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.proinlab.functions.DataBaseHelper;

public class DBEditFn {

	/**
	 * 
	 * @param mHelper
	 * @param FileName
	 * @param FileDir
	 * @param detail
	 * @return
	 */
	public boolean INSERT(SQLiteOpenHelper mHelper, String FileName,
			String FileDir, String detail) {

		if (EXIST_BY_FILENAME(mHelper, FileName))
			return false;

		SQLiteDatabase db;
		ContentValues row;

		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_FILEDIR, FileDir);
		row.put(DataBaseHelper.DB_ROW_FILENAME, FileName);
		row.put(DataBaseHelper.DB_ROW_FILEDETAIL, detail);
		db.insert(DataBaseHelper.DB_TABLE_NAME, null, row);
		mHelper.close();
		return true;
	}

	/**
	 * 해당 카테고리의 파일 이름 목록을 구함
	 * 
	 * @param mHelper
	 * @param Category
	 * @return Category의 파일 이름 목록
	 */

	public String[] FIND_ALL_FILENAME(SQLiteOpenHelper mHelper) {
		SQLiteDatabase db;

		ArrayList<String> returnStr;
		String[] result = null;
		String[] columns = DataBaseHelper.DB_COLUMNS;

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor == null)
			return null;
		else if (cursor.getCount() == 0)
			result = null;
		else {
			returnStr = new ArrayList<String>();
			while (cursor.moveToNext()) {
				returnStr.add(cursor.getString(DataBaseHelper.FILENAME));
			}
			result = new String[returnStr.size()];
			for (int i = 0; i < returnStr.size(); i++)
				result[i] = returnStr.get(i);
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * 파일 이름으로부터 파일 정보 불러오기
	 * 
	 * @param mHelper
	 * @param 파일이름
	 * @return String[] : Category, Dir, filename, type, firstSaveTime,
	 *         LastSaveTime
	 */
	public String[] FIND_BY_FILENAME(SQLiteOpenHelper mHelper, String FileName) {
		SQLiteDatabase db;

		String[] result = null;
		String[] columns = DataBaseHelper.DB_COLUMNS;

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor == null)
			return null;
		if (cursor.getCount() == 0)
			result = null;
		else {
			result = new String[6];
			while (cursor.moveToNext()) {
				if (FileName.equals(cursor.getString(DataBaseHelper.FILENAME))) {
					for (int i = 0; i < 3; i++)
						result[i] = cursor.getString(i);
				}
			}
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * 파일 이름 생성 함수. 0부터 생성해서 1씩 증가시킨다.
	 * 
	 * @param mHelper
	 * @return 생성된 파일 이름 String
	 */
	public String CREATE_FILENAME(SQLiteOpenHelper mHelper) {
		SQLiteDatabase db;

		long num = -1;
		String[] columns = DataBaseHelper.DB_COLUMNS;

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor == null)
			return null;
		if (cursor.getCount() == 0)
			num = 0;
		else {
			boolean isExist = true;
			while (isExist) {
				isExist = false;
				cursor.moveToFirst();
				num++;
				if (num == Long.parseLong(cursor
						.getString(DataBaseHelper.FILEDIR))) {
					isExist = true;
				}
				while (cursor.moveToNext()) {
					if (num == Long.parseLong(cursor
							.getString(DataBaseHelper.FILEDIR)))
						isExist = true;
				}

			}
		}

		cursor.close();
		mHelper.close();

		return Long.toString(num);
	}

	/**
	 * 해당 파일이 DB에 존재하는지 확인. 존재하면 true 값을 반환한다.
	 * 
	 * @param mHelper
	 * @param FileDir
	 * @return 존재여부
	 */
	public boolean EXIST(SQLiteOpenHelper mHelper, String FileDir) {
		SQLiteDatabase db;

		String[] columns = DataBaseHelper.DB_COLUMNS;

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			mHelper.close();
			return false;
		} else {
			while (cursor.moveToNext()) {
				if (FileDir.equals(cursor.getString(DataBaseHelper.FILEDIR))) {
					cursor.close();
					mHelper.close();
					return true;
				}
			}
		}

		cursor.close();
		mHelper.close();
		return false;
	}

	/**
	 * 해당 파일의 이름이 DB에 존재하는지 확인. 존재하면 true 값을 반환한다.
	 * 
	 * @param mHelper
	 * @param FileName
	 * @return
	 */
	public boolean EXIST_BY_FILENAME(SQLiteOpenHelper mHelper, String FileName) {
		SQLiteDatabase db;

		String[] columns = DataBaseHelper.DB_COLUMNS;

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_NAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			mHelper.close();
			return false;
		} else {
			while (cursor.moveToNext()) {
				if (FileName.equals(cursor.getString(DataBaseHelper.FILENAME))) {
					cursor.close();
					mHelper.close();
					return true;
				}
			}
		}

		cursor.close();
		mHelper.close();
		return false;
	}

	/**
	 * DB에서 파일 경로에 해당하는 정보를 삭제한다. 존재하지 않는 파일이면 false
	 * 
	 * @param mHelper
	 * @param FileDir
	 * @return
	 */
	public boolean DELETE(SQLiteOpenHelper mHelper, String FileDir) {
		if (!EXIST(mHelper, FileDir))
			return false;

		SQLiteDatabase db;
		db = mHelper.getWritableDatabase();
		db.delete(DataBaseHelper.DB_TABLE_NAME, DataBaseHelper.DB_ROW_FILEDIR
				+ " = '" + FileDir + "'", null);
		mHelper.close();
		return true;
	}

	/**
	 * DB의 파일 정보를 갱신한다. 최초 저장 시간은 변경 못함
	 * 
	 * @param mHelper
	 * @param FileName
	 * @param FileDetail
	 * @param FileDir
	 * @return
	 */
	public boolean CHANGEDATA(SQLiteOpenHelper mHelper, String FileName,
			String FileDetail, String FileDir) {
		if (!EXIST(mHelper, FileDir))
			return false;

		SQLiteDatabase db;
		ContentValues row;
		db = mHelper.getWritableDatabase();

		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_FILENAME, FileName);
		row.put(DataBaseHelper.DB_ROW_FILEDETAIL, FileDetail);

		db.update(DataBaseHelper.DB_TABLE_NAME, row,
				DataBaseHelper.DB_ROW_FILEDIR + " = '" + FileDir + "'", null);

		mHelper.close();
		return true;
	}
}
