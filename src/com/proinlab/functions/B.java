/*****************************************************************************
 * @author PROIN LAB [ BASICS ]
 *         -------------------------------------------------------------------
 *         LAYOUT : ID_main / ID_subID_name
 *         -------------------------------------------------------------------
 *         INTENT : INTENT_fromID_TO_toID_NAME
 *****************************************************************************/

package com.proinlab.functions;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class B {

	public static final String SDCARD_DIRECTORY = Environment
			.getExternalStorageDirectory().toString() + "/.AniMaker/";

	public static final int DIALOG_ADDFILE = 0;
	public static final int DIALOG_SELECT_FILE = 1;
	public static final int DIALOG_CHANGE_INFO = 2;
	public static final int DIALOG_DELETE = 3;
	
	public static final String INTENT_M00_M01_PROJECT_NAME = "PROJECT_NAME";
	
	public static void CreateToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.show();
	}

	public static String stringCheck(String str) {
		StringBuilder strbuilder = new StringBuilder();

		int size = str.length();
		for (int i = 0; i < size; i++) {
			char curChar = str.charAt(i);
			if (curChar == '\\' || curChar == '/' || curChar == ':'
					|| curChar == '*' || curChar == '?' || curChar == '"'
					|| curChar == '<' || curChar == '>' || curChar == '|') {
				strbuilder.append('_');
			} else
				strbuilder.append(curChar);
		}
		return strbuilder.toString();
	}

	public static String getUniqueFilename(File folder, String filename) {
		if (folder == null)
			return null;

		String curFileName;
		File curFile;

		if (filename.length() > 20) {
			filename = filename.substring(0, 19);
		}

		filename = stringCheck(filename);

		do {
			curFileName = filename;
			curFile = new File(folder, curFileName);
			if (curFile.exists())
				return "<FileExist>";
		} while (curFile.exists());

		return curFileName;
	}
}
