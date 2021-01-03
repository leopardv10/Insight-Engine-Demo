package com.paradigm.demo.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ZipUncompress {
	/**
	 * zip文件解压
	 * @param srcFile  待解压文件夹/文件
	 * @param destDirPath  解压路径
	 */
	public static List<String> zipUncompress(File srcFile,String destDirPath) throws Exception {
		// 判断源文件是否存在
		if(!srcFile.exists()) {
			throw new Exception(srcFile.getPath() + "所指文件不存在");
		}
		ZipFile zipFile = new ZipFile(srcFile);  // 创建压缩文件对象
		// 开始解压
		Enumeration<?> entries = zipFile.entries();
		List<String> file_names = new ArrayList<>();

		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			// 如果是文件夹，就跳过
			if (entry.isDirectory()) continue;
			// 跳过MACOS下解压自动生成的__MACOSX文件夹
			if(entry.getName().length() > 8 && entry.getName().substring(0, 8).equalsIgnoreCase("__MACOSX"))
				continue;
			// 文件后缀不是.doc, .docx, .pdf, .txt的话则跳过
			String suffix = entry.getName().substring(entry.getName().lastIndexOf('.'));  // 获取文件的后缀名
			if(!suffix.equalsIgnoreCase(".doc") && !suffix.equalsIgnoreCase(".docx")
					&& !suffix.equalsIgnoreCase(".pdf") && !suffix.equalsIgnoreCase(".txt"))
				continue;

			file_names.add(entry.getName());
			// 如果是文件，就先创建一个文件，然后用io流把内容copy过去
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
		return file_names;
	}
}


