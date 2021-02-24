package com.paradigm.demo.controller;

import com.paradigm.demo.bean.Result;
import com.paradigm.demo.bean.Spo;
import com.paradigm.demo.service.SpoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;


@Controller
public class Query {
	private final SpoService spoService;

	public Query(SpoService spoService) {
		this.spoService = spoService;
	}

	@PostMapping("/query")
	@ResponseBody
	public List<Result> query(@RequestParam("task_id") int task_id, @RequestParam("file_name") String file_name) {
		return spoService.select(task_id, file_name);
	}
}
