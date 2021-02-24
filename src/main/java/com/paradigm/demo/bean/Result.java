package com.paradigm.demo.bean;

import lombok.Data;
import org.springframework.stereotype.Component;


@Component
@Data
public class Result {
	private String predicate;
	private String subject;
	private String object;
}
