package com.paradigm.demo.bean;


import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Spo {
	private int task_id;
	private String file_name;
	private String predicate;
	private String subject;
	private String object;
	private int sub_begin;
	private int sub_end;
	private int obj_begin;
	private int obj_end;
}
