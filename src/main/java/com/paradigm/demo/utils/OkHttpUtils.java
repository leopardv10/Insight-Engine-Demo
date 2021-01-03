package com.paradigm.demo.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class OkHttpUtils {

	public String post(String path, String fileName) throws IOException {
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
		// 处理pdf
		if(suffix.equalsIgnoreCase(".pdf")) {
			Request request = new Request.Builder().url("http://172.27.128.117:5022/api/pdf2md_pro")
					.method("POST", body).build();
			Response response = client.newCall(request).execute();
			ResponseBody body2 = ResponseBody.create(mediaType, response.body().string());
			return body2.string();

		// 处理word，md
		} else{
			Request request = new Request.Builder().url("http://172.27.128.117:5022/api/word2md")
					.method("POST", body).build();
			Response response = client.newCall(request).execute();
			ResponseBody body2 = ResponseBody.create(mediaType, response.body().string());

			return body2.string();
		}
	}

	public Response postIs_a(String s) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();
		MediaType mediaType = MediaType.parse("application/json");

		String input = "{\"data\": [\"" + s.replace("\"", "").replace("\n", "") + "\"]}";

		System.out.println("is_a服务的输入：" + input);

		RequestBody body = RequestBody.create(mediaType, input);
		Request request = new Request.Builder()
				.url("http://172.27.231.11:32473")
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		Response response = client.newCall(request).execute();
		return response;
	}
}

//      String res = response.body().string();
//		Gson gson = new Gson();
//		List<Map<String,Object>> list_result = gson.fromJson(res, new TypeToken<List<Map<String,Object>>>(){}.getType());
//		return list_result.get(0).get("spo_list").toString();