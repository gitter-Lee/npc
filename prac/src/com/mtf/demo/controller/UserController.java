package com.mtf.demo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mtf.demo.model.page.Json;
import com.mtf.demo.model.page.User;
import com.mtf.demo.service.IUserService;

@Controller
@RequestMapping("/userController")
public class UserController {

	private static final Logger	logger	= Logger.getLogger(UserController.class);

	private IUserService		userService;
	private MessageSource		messages;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value="/login", method=RequestMethod.POST)
	@ResponseBody
	public Json login(@RequestParam("uid") String uid, @RequestParam("pwd") String pwd, HttpServletRequest request, HttpSession session) {
		Json j = new Json();
		// validate
		if (uid == null || uid.trim().length() == 0 || pwd == null || pwd.trim().length() == 0) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.uidpwdempty", null, request.getLocale()));
			return j;
		}
		// invoke service
		User u = userService.getByNameAndPassword(uid.trim(), pwd.trim());
		// handle result
		if (u == null) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.uidpwdwrong", null, request.getLocale()));
		} else if (u.getStatus() != 0) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.accountdisabled", null, request.getLocale()));
		} else {
			j.setSuccess(true);
			j.setMsg(this.messages.getMessage("c_user.loginsuccess", null, request.getLocale()));
			session.setAttribute("user", u);
		}
		return j;
	}

	@RequestMapping("/logout")
	@ResponseBody
	public Json logout(HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		Json j = new Json();
		j.setSuccess(true);
		return j;
	}
	
	@RequestMapping("/userInfo")
	public String userinfo() {
		return "user/userInfo";
	}
	
	@RequestMapping(value="/updateMyProfile", method=RequestMethod.POST)
	@ResponseBody
	public Json updateMyProfile(@RequestParam("uid") String uid, @RequestParam("email") String email, HttpServletRequest request, HttpSession session) {
		Json j = new Json();
		// TODO
		boolean isok = System.currentTimeMillis() % 2 > 0;
		j.setSuccess(isok);
		if (!isok) {
			j.setMsg(this.messages.getMessage("c_user.updatefailed", null, request.getLocale()));
		} else {
			j.setMsg(this.messages.getMessage("c_user.updatesuccess", null, request.getLocale()));
		}
		return j;
	}
}
