package com.paradigm.demo.service;


import com.paradigm.demo.bean.Attribute;
import com.paradigm.demo.dao.AttributeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttriService {
	@Autowired
	private AttributeDao attributeDao;

	public void insert(int task_id, String file_name, String attr, String concept, String value) {
		attributeDao.insert(task_id, file_name, attr, concept, value);
	}

	public void delete(int task_id) { attributeDao.delete(task_id);}

	public Attribute select(int task_id, String file_name) { return attributeDao.select(task_id, file_name);}
}
