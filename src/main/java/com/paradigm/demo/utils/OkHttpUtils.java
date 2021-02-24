package com.paradigm.demo.utils;

import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class OkHttpUtils {

	public Response postOCR(String path, String fileName, String wordURL, String pdfURL) throws IOException {
		String redundant = fileName.substring(0, fileName.indexOf('/') + 1);
		path = path + redundant;
		fileName = fileName.substring(fileName.indexOf('/') + 1);

		OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES)
				.writeTimeout(5, TimeUnit.MINUTES)
				.readTimeout(5, TimeUnit.MINUTES).build();
		MediaType mediaType = MediaType.parse("application/json");

		RequestBody body =
				new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart
				("file", fileName, RequestBody.create(MediaType.parse("application/octet-stream"),
				new File(path + fileName))).build();

		String suffix = fileName.substring(fileName.lastIndexOf('.'));
		//----------处理pdf----------
		if(suffix.equalsIgnoreCase(".pdf")) {
			Request request = new Request.Builder().url(pdfURL).method("POST", body).build();
			return client.newCall(request).execute();
		//----------处理word，md----------
		} else{
			Request request = new Request.Builder().url(wordURL).method("POST", body).build();
			return client.newCall(request).execute();
		}
	}

	public Response postSPO(String s, String is_aURL) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, s);
		Request request = new Request.Builder()
				.url(is_aURL)
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		return client.newCall(request).execute();
	}

	public Response postAttribute(String s, String attriURL) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, s);
		Request request = new Request.Builder()
				.url(attriURL)
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		return client.newCall(request).execute();
	}
}