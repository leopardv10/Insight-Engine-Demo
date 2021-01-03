package com.paradigm.demo.bean;


import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserData {
	private int id;
	private String file_name;
	private String content;
	private String spo;
}
