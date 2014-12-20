package com.mtf.demo.controller;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	private static final Logger	logger	= Logger.getLogger(IndexController.class);

	@RequestMapping("/index")
	public String index(HttpSession session) {
		if (session != null && session.getAttribute("user") != null) {
			return "main";
		}
		return "user/login";
	}
}
