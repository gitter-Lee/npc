package com.mtf.demo.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

	private static final Logger	logger	= Logger.getLogger(MainController.class);

	@RequestMapping("/north")
	public String north() {
		return "layout/north";
	}

	@RequestMapping("/south")
	public String south() {
		return "layout/south";
	}

	@RequestMapping("/west")
	public String west() {
		return "layout/west";
	}
	
	@RequestMapping("/center")
	public String center() {
		return "layout/center";
	}
	
	@RequestMapping("/dashboard")
	public String dashboard() {
		return "layout/dashboard";
	}
}
