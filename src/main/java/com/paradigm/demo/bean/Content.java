package com.paradigm.demo.bean;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;


@Component
@Data
public class Content {
	private int task_id;
	private String file_name;
	private String status;
	private String content;
	private Timestamp create_time;
	private Timestamp last_update;
}
