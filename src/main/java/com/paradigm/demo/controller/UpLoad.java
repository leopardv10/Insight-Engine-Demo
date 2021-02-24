package com.paradigm.demo.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paradigm.demo.service.AttriService;
import com.paradigm.demo.service.ContentService;
import com.paradigm.demo.service.SpoService;
import com.paradigm.demo.utils.CreateTaskId;
import com.paradigm.demo.utils.OkHttpUtils;
import com.paradigm.demo.utils.Txt2String;
import com.paradigm.demo.utils.ZipUncompress;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


@Controller
public class UpLoad{
	@Value("${ocr.pdfURL}")
	private String pdfURL;
	@Value("${ocr.wordURL}")
	private String wordURL;
	@Value("${spoURL}")
	private String spoURL;
	@Value("${attributeURL}")
	private String attributeURL;

	private final ContentService contentService;
	private final SpoService spoService;
	private final AttriService attriService;

	private static final Logger LOGGER = LoggerFactory.getLogger(UpLoad.class);

	public UpLoad(ContentService contentService, SpoService spoService, AttriService attriService) {
		this.contentService = contentService;
		this.spoService = spoService;
		this.attriService = attriService;
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
		String filePath = System.getProperty("user.dir") + "/userFile/";  // 保存用户上传文件的路径
		// ----------如果服务器没有userFile文件夹则新建一个----------
		File userFile = new File(filePath);
		if(!userFile.exists() && !userFile.isDirectory()) {
			userFile.mkdirs();
		}
		File dest = new File(filePath + fileName);  // tomcat服务器先创建空zip文件

		file.transferTo(dest);  // 将用户上传的zip文件内容写入到tomcat服务器的空文件
		List<String> file_names = ZipUncompress.zipUncompress(dest, filePath);  // 解压zip，并获取zip里所有的文件名
		LOGGER.info("上传成功");

		OkHttpUtils okhttp = new OkHttpUtils();
		// ----------获取当前时间----------
		Calendar cal = Calendar.getInstance();
		java.util.Date date = cal.getTime();
		String time=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
		// ----------通过hash的方式来获取当前taskId----------
		int taskId = CreateTaskId.toHash(fileName + time);

		for(String name: file_names) {
			contentService.insert(taskId, name, "processing");
			String tmp = name.substring(name.lastIndexOf('.'));

			// ----------txt文件----------
			if(tmp.equalsIgnoreCase(".txt")) {
				String paragraph = Txt2String.getContent(filePath, name);  // 将txt里内容读取为string
				// ----------将文本按标点拆分成一句话----------
				String[] seperated = paragraph.split("[？！。]");
				// ----------调用isA服务----------
				spo(seperated, okhttp, taskId, name);
				// ----------调用attribute模型----------
				attribute(seperated, okhttp, taskId, name);

			// ----------pdf, doc, docx文件----------
			} else if(tmp.equalsIgnoreCase(".doc") || tmp.equalsIgnoreCase(".docx") ||
					tmp.equalsIgnoreCase(".pdf")) {
				// ----------调用OCR服务----------
				Response response1 = okhttp.postOCR(filePath, name, wordURL, pdfURL);
//					okhttp3.ResponseBody body2 = okhttp3.ResponseBody.create(mediaType, response.body().string());
				String paragraph = response1.body().string();
				contentService.ocrUpdate(paragraph);
				String[] seperated = paragraph.split("[？！。]");
				// ----------调用isA服务----------
				spo(seperated, okhttp, taskId, name);
				// ----------调用attribute模型----------
				attribute(seperated, okhttp, taskId, name);
			}
		}
		return "{\"msg\": \"任务完成\", \"task_id\": \"" + taskId + "\"}";
	}


	public void spo(String[] seperated, OkHttpUtils okhttp, int taskId, String name) throws IOException {
		for(String content: seperated) {
			// ----------调用is_a模型----------
			String formedContent = "{\"data\": [\"" + content.replace("\"", "").
					replace("\n", "") + "\"]}";
			Response response = okhttp.postSPO(formedContent, spoURL);
			String body = response.body().string();
			// ----------提取is_a模型的返回结果，并转换成json格式----------
			String res = "{" + body.substring(body.indexOf("\"spo_list\""), body.length() - 1);
			Gson gson = new Gson();
			Map<String, List<Map<String, Object>>> spoList =
					gson.fromJson(res, new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType());

			for (Map spo : spoList.get("spo_list")) {
				Map<String, Object> subject_index = (Map<String, Object>) spo.get("subject_index");
				int sub_begin = (int) Double.parseDouble(subject_index.get("begin").toString());
				int sub_end = (int) Double.parseDouble(subject_index.get("end").toString());

				Map<String, Object> object_index = (Map<String, Object>) spo.get("object_index");
				int obj_begin = (int) Double.parseDouble(object_index.get("begin").toString());
				int obj_end = (int) Double.parseDouble(object_index.get("end").toString());

				String subject = (String) spo.get("subject");
				String object =  (String) spo.get("object");
				if(subject.length() <= 1 || object.length() <= 1)
					continue;
				spoService.insert(taskId, name, (String) spo.get("predicate"),
						subject, object, sub_begin, sub_end, obj_begin, obj_end);
			}
		}
	}


	public void attribute(String[] seperated, OkHttpUtils okhttp, int taskId, String name) throws IOException{
		for(String content: seperated) {
			// ----------调用is_a模型----------
			String formedContent = "{\"data\": [\"" + content.replace("\"", "").replace("\n", "") + "\"]}";
			Response response = okhttp.postAttribute(formedContent, attributeURL);
			String body = response.body().string();

			// ----------提取is_a模型的返回结果，并转换成json格式----------
			String res = "{" + body.substring(body.indexOf("\"event_content_list"), body.length() - 1);
			Gson gson = new Gson();
			Map<String, List<Map<String, Object>>> eventList =
					gson.fromJson(res, new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType());

			for (Map eventContent : eventList.get("event_content_list")) {
				// ----------621表示概念----------
				List<String> list621 = new ArrayList<>();
				if(eventContent.get("key") == "621") {
					List<Map<String, Object>> contentList= (List<Map<String, Object>>) eventContent.get("content");
					for(Map values: contentList) {
						String value = (String) values.get("value");
						list621.add(value);
					}
				}

				// ----------622表示属性----------
				List<String> list622 = new ArrayList<>();
				if(eventContent.get("key") == "622") {
					List<Map<String, Object>> contentList= (List<Map<String, Object>>) eventContent.get("content");
					for(Map values: contentList) {
						String value = (String) values.get("value");
						list622.add(value);
					}
				}

				// ----------623表示值----------
				List<String> list623 = new ArrayList<>();
				if(eventContent.get("key") == "623") {
					List<Map<String, Object>> contentList= (List<Map<String, Object>>) eventContent.get("content");
					for(Map values: contentList) {
						String value = (String) values.get("value");
						list623.add(value);
					}
				}

				for(String value621: list621) {
					for(String value622: list622) {
						for(String value623: list623) {
							attriService.insert(taskId, name, value622, value621, value623);
						}
					}
				}
			}
		}
		// ----------处理完当前file更新状态----------
		contentService.finish(taskId, name, "finished");
	}
}
