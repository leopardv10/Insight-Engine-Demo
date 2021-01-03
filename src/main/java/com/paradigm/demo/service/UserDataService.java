package com.paradigm.demo.service;

import com.paradigm.demo.dao.UserDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {
	@Autowired
	private UserDataDao userDataDao;

	public String selectById(int id) { return userDataDao.selectById(id); }

	public void insert(String file_name, String content, String spo) {
		userDataDao.insertData(file_name, content, spo);
	}

	public void delete(int id) {
		userDataDao.delete(id);
	}
}
