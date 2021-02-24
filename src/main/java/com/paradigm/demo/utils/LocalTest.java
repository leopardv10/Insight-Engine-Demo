package com.paradigm.demo.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LocalTest {
	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		java.util.Date date = cal.getTime();
		String time=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
		return time;
	}

	public static void main(String[] args) {
		String filePath = System.getProperty("user.dir") + "/userFile/";  // 保存用户上传文件的路径
		//----------如果服务器没有userFile文件夹则新建一个----------
		File userFile = new File(filePath);
		if(!userFile.exists() && !userFile.isDirectory()) {
			userFile.mkdirs();
		}
	}

}
