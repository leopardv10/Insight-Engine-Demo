package com.paradigm.demo.controller;


import com.paradigm.demo.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userdata")
public class UserDataController {
	@Autowired
	private UserDataService userDataService;

	@RequestMapping("/insert")
	public String insert() {
		userDataService.insert("123.pdf", "test", "test");
		return "done";
	}
}
