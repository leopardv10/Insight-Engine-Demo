package com.paradigm.demo.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ZipTest {

	public static void main(String[] args) throws Exception {
		System.out.println(zipUncompress("../test.zip", "../store"));
	}

	/**
	 * zip文件解压
	 * @param inputFile  待解压文件夹/文件
	 * @param destDirPath  解压路径
	 */
	public static List<String> zipUncompress(String inputFile, String destDirPath) throws Exception {
		File srcFile = new File(inputFile);
		// 判断源文件是否存在
		if (!srcFile.exists()) {
			throw new Exception(srcFile.getPath() + "所指文件不存在");
		}
		ZipFile zipFile = new ZipFile(srcFile);  // 创建压缩文件对象
		// 开始解压
		Enumeration<?> entries = zipFile.entries();
		List<String> files = new ArrayList<>();

		System.out.println(entries);

		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			System.out.println(entry.getName());
			// 如果是文件夹，就创建个文件夹
			if (entry.isDirectory()) {
				String dirPath = destDirPath + "/" + entry.getName();
				srcFile.mkdirs();
			} else {
				// 如果是文件，就先创建一个文件，然后用io流把内容copy过去
				if(entry.getName().length() > 8 && entry.getName().substring(0, 8).equalsIgnoreCase("__MACOSX")) {
					continue;
				}
				files.add(entry.getName());
				File targetFile = new File(destDirPath + "/" + entry.getName());
				// 保证这个文件的父文件夹必须要存在
				if (!targetFile.getParentFile().exists()) {
					targetFile.getParentFile().mkdirs();
				}
				targetFile.createNewFile();
				// 将压缩文件内容写入到这个文件中
				InputStream is = zipFile.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(targetFile);
				int len;
				byte[] buf = new byte[1024];
				while ((len = is.read(buf)) != -1) {
					fos.write(buf, 0, len);
				}
				// 关流顺序，先打开的后关闭
				fos.close();
				is.close();
			}
		}
		return files;
	}

	private static String getTemplateContent() throws Exception {
		File file = new File("../test.txt");
		FileInputStream inputStream = new FileInputStream(file);
		int length = inputStream.available();
		byte bytes[] = new byte[length];
		inputStream.read(bytes);
		inputStream.close();
		String str =new String(bytes, StandardCharsets.UTF_8);
		return str;
	}
}
