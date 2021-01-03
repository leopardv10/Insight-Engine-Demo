package com.paradigm.demo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;


public class Txt2String {
	public static String getContent(String path, String name) throws Exception{
		File file = new File(path + name);
		FileInputStream inputStream = new FileInputStream(file);
		int length = inputStream.available();
		byte bytes[] = new byte[length];
		inputStream.read(bytes);
		inputStream.close();
		String str =new String(bytes, StandardCharsets.UTF_8);
		return str;
	}
}
