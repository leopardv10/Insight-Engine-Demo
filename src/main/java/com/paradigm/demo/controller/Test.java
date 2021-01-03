package com.paradigm.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class Test {
	@RequestMapping(value="/hello/{username}")
	@ResponseBody
	public String sayHello(@PathVariable("username") String username) {
		return "hello lwei";
	}
}
