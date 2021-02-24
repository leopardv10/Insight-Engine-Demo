package com.paradigm.demo.service;

import com.paradigm.demo.bean.Result;
import com.paradigm.demo.bean.Spo;
import com.paradigm.demo.dao.SpoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SpoService {
	@Autowired
	private SpoDao spoDao;

	public void insert(int task_id, String file_name, String predicate, String subject, String object,
	                   int sub_begin, int sub_end, int obj_begin, int obj_end) {
		spoDao.insert(task_id, file_name, predicate, subject, object, sub_begin, sub_end, obj_begin, obj_end);
	}

	public void delete(int task_id) { spoDao.delete(task_id); }

	public List<Result> select(int task_id, String file_name) {
		return spoDao.select(task_id, file_name);
	}
}
