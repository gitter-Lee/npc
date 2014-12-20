package com.mtf.demo.controller;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring.xml", "classpath:spring-mybatis.xml", "classpath:spring-mvc.xml" })
public class UserControllerTest {

	private static final Logger	logger	= Logger.getLogger(UserControllerTest.class);

	@Autowired
	WebApplicationContext		wac;

	@Autowired
	UserController				userController;

	@Autowired
	MessageSource				messages;

	MockMvc						mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	public void testMessage() {
		String message = messages.getMessage("uidpwdempty", null, null);
		logger.debug(message);
	}

	@Test
	public void testLogin() throws Exception {
		MockHttpServletRequestBuilder req = MockMvcRequestBuilders.post("/userController/login.action");

		req.accept(MediaType.APPLICATION_JSON);
		req = req.param("uid", "Admin");
		req = req.param("pwd", "admin");

		ResultActions ra = this.mockMvc.perform(req);
		MvcResult mr = ra.andReturn();

		String result = mr.getResponse().getContentAsString();
		logger.debug(result);
	}
}
