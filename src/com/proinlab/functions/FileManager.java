package com.proinlab.functions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * SDCARD/.Impression/ Category Name / File Type / FileDir / 1~n.png
 * 
 * @author PROIN LAB
 */

public class FileManager {

	/**
	 * 폴더를 생성한다
	 * 
	 * @param FileID
	 * @return
	 */
	public boolean CREATE_FILE_FODLER(String FileID) {
		File FILE = new File(B.SDCARD_DIRECTORY + "FILE/" + FileID);
		if (!FILE.exists())
			while (FILE.mkdirs())
				;
		return true;
	}

	/**
	 * 파일 폴더를 삭제한다
	 * 
	 * @param FileDir
	 * @return
	 */
	public boolean DELETE_FILE_FODLER(String FileDir) {

		String mPath = B.SDCARD_DIRECTORY + "FILE/" + FileDir;
		DeleteDir(mPath);

		return true;
	}

	public static void DeleteDir(String path) {
		File file = new File(path);
		if (!file.exists())
			return;

		File[] childFileList = file.listFiles();
		for (File childFile : childFileList) {
			if (childFile.isDirectory()) {
				DeleteDir(childFile.getAbsolutePath());
			} else {
				childFile.delete();
			}
		}
		file.delete();
	}

	/**
	 * 개별 화면을 저장한다
	 * 
	 * @param foreground
	 * @param saveDir
	 *            : 파일명까지 전부 경로
	 * @return
	 */
	public boolean SAVE_SINGLE_PAGE(Bitmap foreground, String saveDir) {
		File file = new File(saveDir);
		if (!file.exists())
			while (file.mkdir())
				;
		String savePath = saveDir;
		if (foreground != null)
			saveBitmapPNG(savePath, foreground);
		return true;
	}

	/**
	 * 경로에 있는 이미지의 알파값을 변경시켜 저장한다
	 * 
	 * @param imgDir
	 *            이미지의 전체 경로
	 * @param parentPath
	 *            이미지의 부모 경로
	 * @param alpha
	 * @return
	 */
	public boolean PNG_ALPHACHANGE_BY_DIR(String imgDir, String parentPath,
			int alpha) {
		
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imgDir, opts);
		
		if (bitmap == null)
			return false;

		bitmap = SetBitmapAlpha(bitmap, alpha, bitmap.getWidth(),
				bitmap.getHeight());
		saveBitmapPNG(parentPath + "background.png", bitmap);

		return true;
	}

	/**
	 * 전체 페이지수를 센다
	 * 
	 * @param Dir
	 *            전체 경로
	 * @return
	 */
	public int GET_LENGTH_SAVE_PAGES(String Dir) {
		File file = new File(Dir);
		String[] lists = file.list();
		int returnnum = 0;
		if (lists != null)
			returnnum = lists.length;
		return returnnum;
	}

	/**
	 * 해당 페이지를 삭제한다
	 * 
	 * @param Dir
	 *            ex) ~~/~~/dir
	 * @param page
	 */
	public void DELETE_PAGE(String Dir, int page) {
		File file = new File(Dir);
		String[] lists = file.list();

		File delete = new File(Dir + "/" + Integer.toString(page) + ".png");
		delete.delete();

		for (int i = page + 1; i < lists.length + 1; i++) {
			File rename = new File(Dir + "/" + Integer.toString(i) + ".png");
			File target = new File(Dir + "/" + Integer.toString(i - 1) + ".png");
			rename.renameTo(target);
		}
	}
	
	private boolean saveBitmapPNG(String FileDir, Bitmap bitmap) {
		if (FileDir == null || bitmap == null)
			return false;

		File saveFile = new File(FileDir);

		if (saveFile.exists()) {
			while (!saveFile.delete())
				;
		}

		try {
			saveFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		OutputStream out = null;
		try {
			out = new FileOutputStream(saveFile);
			bitmap.compress(CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}
	
	private Bitmap SetBitmapAlpha(Bitmap org_bitmap, int alpha, int width,
			int height) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Paint p = new Paint();
		p.setDither(true);
		p.setFlags(Paint.ANTI_ALIAS_FLAG);
		p.setAlpha(alpha);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(org_bitmap, 0, 0, p);

		return bitmap;
	}
}
