package com.paradigm.demo.service;


import com.paradigm.demo.dao.ContentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class ContentService {
	@Autowired
	private ContentDao contentDao;

	public void insert(int task_id, String file_name, String status) {
		contentDao.init(task_id, file_name, status);
	}

	public void ocrUpdate(String text) {
		contentDao.ocrUpdate(text);
	}

	public void finish(int task_id, String file_name) { contentDao.finish(task_id, file_name); }

	public String select(int task_id, String file_name) { return contentDao.getText(task_id, file_name); }

	public int selectId() { return contentDao.getCurId(); }

	public void delete(int task_id) { contentDao.delete(task_id); }
}
