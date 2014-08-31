package jp.codezine.sample.heroku.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class FileUtils {
	
	public static final byte[] readStream(InputStream is) throws IOException {
		int len = 8192;
		byte[] buf = new byte[len];
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int n = is.read(buf);
			while (n != -1) {
				os.write(buf, 0, n);
				n = is.read(buf);
			}
			return os.toByteArray();
		} finally {
			is.close();
		}
	}
	
	public static final byte[] readFile(File f) throws IOException {
		int len = f.length() > Integer.MAX_VALUE ? 8192 : (int)f.length();
		byte[] buf = new byte[len];
		FileInputStream is = new FileInputStream(f);
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream((int)f.length());
			int n = is.read(buf);
			while (n != -1) {
				os.write(buf, 0, n);
				n = is.read(buf);
			}
			return os.toByteArray();
		} finally {
			is.close();
		}
	}
	
	public static String readFileAsString(File f, String enc) throws IOException {
		return new String(readFile(f), enc);
	}
	
	public static String readFileAsString(File f) throws IOException {
		return readFileAsString(f, "utf-8");
	}
	
	public static void writeFile(File file, byte[] data) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		try {
			os.write(data);
		} finally {
			os.close();
		}
	}
	
	public static void writeFile(File file, String text, String enc) throws IOException {
		writeFile(file, text.getBytes(enc));
	}
	
	public static void writeFile(File file, String text) throws IOException {
		writeFile(file, text, "utf-8");
	}
	
	public static void writeFile(File file, InputStream is) throws IOException {
		try {
			FileOutputStream os = new FileOutputStream(file);
			try {
				byte[] buf = new byte[8192];
				int n = is.read(buf);
				while (n != -1) {
					os.write(buf, 0, n);
					n = is.read(buf);
				}
			} finally {
				os.close();
			}
		} finally {
			is.close();
		}
	}
	
	public static boolean contentEquals(File f1, File f2) throws IOException {
		if (f1.length() != f2.length()) {
			return false;
		}
		
		FileInputStream is1 = new FileInputStream(f1);
		try {
			FileInputStream is2 = new FileInputStream(f2);
			try {
				byte[] buf1 = new byte[8192];
				byte[] buf2 = new byte[8192];
				while (true) {
					int n1 = is1.read(buf1);
					int n2 = is2.read(buf2);
					if (n1 != n2) {
						return false;
					}
					if (n1 == -1) {
						return true;
					}
					if (!Arrays.equals(buf1, buf2)) {
						return false;
					}
				}
			} finally {
				is2.close();
			}
		} finally {
			is1.close();
		}
	}
	
	public static void copy(File src, File dest) throws IOException {
		FileInputStream is = new FileInputStream(src);
		try {
			FileOutputStream os = new FileOutputStream(dest);
			try {
				byte[] buf = new byte[8192];
				int n = is.read(buf);
				while (n != -1) {
					os.write(buf, 0, n);
					n = is.read(buf);
				}
			} finally {
				os.close();
			}
		} finally {
			is.close();
		}
	}
	
	public static String getExt(File file) {
		String name = file.getName();
		int idx = name.lastIndexOf('.');
		if (idx != -1) {
			return name.substring(idx+1);
		}
		return null;
	}
	
	public static void deleteRecursive(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null && files.length > 0) {
				for (int i=0; i<files.length; i++) {
					deleteRecursive(files[i]);
				}
			}
		}
		f.delete();
	}
}
