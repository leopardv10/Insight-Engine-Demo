package com.paradigm.demo.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paradigm.demo.service.ContentService;
import com.paradigm.demo.service.SpoService;
import com.paradigm.demo.service.UserDataService;
import com.paradigm.demo.utils.OkHttpUtils;
import com.paradigm.demo.utils.Txt2String;
import com.paradigm.demo.utils.ZipUncompress;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Controller
public class UpLoad{
	private ContentService contentService;
	private SpoService spoService;

	private static final Logger LOGGER = LoggerFactory.getLogger(UpLoad.class);

	public UpLoad(ContentService contentService, SpoService spoService) {
		this.contentService = contentService;
		this.spoService = spoService;
	}

	@GetMapping("/upload")
	public String upload() {
		return "upload";
	}

	@PostMapping("/upload")
	@ResponseBody
	public String upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			return "上传失败，请选择文件";
		}

		String fileName = file.getOriginalFilename();  // zip文件名
		String filePath = "/Users/4paradigm/downloads/";  // 服务器上文件的保存路径（需要修改）
		File dest = new File(filePath + fileName);  // tomcat服务器先创建空zip文件

		try {
			file.transferTo(dest);  // 将用户上传的zip文件内容写入到tomcat服务器的空文件
			List<String> file_names = ZipUncompress.zipUncompress(dest, filePath);  // 解压zip，并获取zip里所有的文件名

			System.out.println("file names:" + file_names.toString());

			LOGGER.info("上传成功");
			OkHttpUtils okhttp = new OkHttpUtils();
			// 调用OCR服务
			int cur = contentService.selectId();

			for(String name: file_names) {
				contentService.insert(cur+1, name, "processing");
				String tmp = name.substring(name.lastIndexOf('.'));
				// 处理txt文件
				if(tmp.equalsIgnoreCase(".txt")) {
					String content = Txt2String.getContent(filePath, name);  // 将txt里内容读取为string
					contentService.ocrUpdate(content);
//					System.out.println("txt文件里的内容：" + content);
					// 调用is_a模型
					Response response = okhttp.postIs_a(content);

					String body = response.body().string();
					String res = "{" + body.substring(body.indexOf("\"spo_list\""), body.length()-1);
					Gson gson = new Gson();
					Map<String,List<Map<String, String>>> spoList = gson.fromJson(res, new TypeToken<Map<String, List<Map<String, String>>>>(){}.getType());

					for(Map spo: spoList.get("spo_list")) {
						Map<String, Integer> subject_index = gson.fromJson((String)spo.get("subject_index"),
								new TypeToken<Map<String, Integer>>(){}.getType());
						int sub_begin = subject_index.get("begin");
						int sub_end = subject_index.get("end");

						Map<String, Integer> object_index = gson.fromJson((String)spo.get("object_index"),
								new TypeToken<Map<String, Integer>>(){}.getType());
						int obj_begin = subject_index.get("begin");
						int obj_end = subject_index.get("end");
						spoService.insert(cur+1, name, (String) spo.get("predicate"), (String) spo.get("subject"), (String) spo.get("object"),
								sub_begin, sub_end, obj_begin, obj_end);
						contentService.finish(cur + 1, name);
					}
					// System.out.println(response2.body().string());
					// 数据保存进数据库
//					spo = response2.body().string();
//					System.out.println("is_a result:" + spo);

				} else if(tmp.equalsIgnoreCase(".doc") || tmp.equalsIgnoreCase(".docx") ||
						tmp.equalsIgnoreCase(".pdf") || tmp.equalsIgnoreCase(".md")) {
					String content = okhttp.post(filePath, name);
					contentService.ocrUpdate(content);
//					String content = response1.body().string();
//					System.out.println("ocr服务返回：" + content);
					// 调用is_a模型
					Response response = okhttp.postIs_a(content);

					String body = response.body().string();
					String res = "{" + body.substring(body.indexOf("\"spo_list\""), body.length()-1);
					Gson gson = new Gson();
					Map<String,List<Map<String, String>>> spoList = gson.fromJson(res, new TypeToken<Map<String, List<Map<String, String>>>>(){}.getType());

					for(Map spo: spoList.get("spo_list")) {
						Map<String, Integer> subject_index = gson.fromJson((String)spo.get("subject_index"),
								new TypeToken<Map<String, Integer>>(){}.getType());
						int sub_begin = subject_index.get("begin");
						int sub_end = subject_index.get("end");

						Map<String, Integer> object_index = gson.fromJson((String)spo.get("object_index"),
								new TypeToken<Map<String, Integer>>(){}.getType());
						int obj_begin = subject_index.get("begin");
						int obj_end = subject_index.get("end");
						spoService.insert(cur+1, name, (String) spo.get("predicate"), (String) spo.get("subject"), (String) spo.get("object"),
								sub_begin, sub_end, obj_begin, obj_end);
						contentService.finish(cur + 1, name);
					}
				}
			}
			return "Task Finished";

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.toString(), e);
			System.out.println(e.toString());
		}
		return "上传失败！";
	}
}