package com.paradigm.demo.bean;


import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Attribute {
	private String attr;
	private String concept;
	private String value;
}
