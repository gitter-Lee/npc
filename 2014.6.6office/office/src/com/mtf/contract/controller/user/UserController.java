/*
 * Copyright (c) 2013 LIAONING SHIDAI_WANHENG CO.,LTD. All Rights Reserved.
 * This work contains SHIDAI_WANHENG CO.,LTD.'s unpublished
 * proprietary information which may constitute a trade secret
 * and/or be confidential. This work may be used only for the
 * purposes for which it was provided, and may not be copied
 * or disclosed to others. Copyright notice is precautionary
 * only, and does not imply publication.
 *
 */
package com.mtf.contract.controller.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import com.mtf.contract.editor.DateEditor;
import com.mtf.contract.editor.DoubleEditor;
import com.mtf.contract.editor.IntegerEditor;
import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.User;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.model.impl.ApplicationImpl;
import com.mtf.contract.model.impl.ContractImpl;
import com.mtf.contract.model.impl.PaymentImpl;
import com.mtf.contract.service.DashboardService;
import com.mtf.contract.service.IUserService;
import com.mtf.contract.util.CommonUtil;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.LogUtils;
import com.mtf.contract.util.TextUtils;
import com.mtf.contract.util.Utils;

/**
 * 用户相关操作入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("userController")
@RequestMapping("/user")
public class UserController {

	private static final Logger		logger	= Logger.getLogger(UserController.class);

	private IUserService			userService;
	private MessageSource			messages;
	private CookieLocaleResolver	localeResolver;
	private DashboardService		dashboardService;

	@Autowired
	public void setDashboardService(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	@Autowired
	public void setCookieLocaleResolver(CookieLocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor(true));
		binder.registerCustomEditor(Integer.class, new IntegerEditor(true));
		binder.registerCustomEditor(Double.class, new DoubleEditor(true));
	}

	/**
	 * 
	 * @param uid 登录名
	 * @param pwd 密码
	 * @param request
	 * @param response
	 * @param session
	 * @return 
	 * @throws PmException
	 */
	@RequestMapping(value = "/doLogin", method = RequestMethod.POST)
	@ResponseBody
	public Json doLogin(@RequestParam("uid") String uid,
	                    @RequestParam("pwd") String pwd,
	                    @RequestParam("cookieRemember") String cookieRemember,
	                    HttpServletRequest request,
	                    HttpServletResponse response,
	                    HttpSession session) throws PmException {
		Json j = new Json();
		Map<String, String> mapStr = new HashMap<String, String>();
		// 国际化显示
		String userEmpty = this.messages.getMessage("c_user.uidpwdempty", null, null);
		String invalidName = this.messages.getMessage("c_user.invalid.loginname", null, null);
		String invalidPass = this.messages.getMessage("c_user.invalid.password", null, null);
		String uidWrong = this.messages.getMessage("c_user.uidpwdwrong", null, null);
		String disableed = this.messages.getMessage("c_user.accountdisabled", null, null);
		mapStr.put("userEmpty", userEmpty);
		mapStr.put("uidWrong", uidWrong);
		mapStr.put("disableed", disableed);
		// validate
		if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(pwd) || TextUtils.getTrimmedLength(uid) == 0 || TextUtils.getTrimmedLength(pwd) == 0) {
			j.setSuccess(false);
			j.setMsg(userEmpty);
			return j;
		} else if (!TextUtils.isPrintableAsciiOnly(uid.trim())) {
			j.setSuccess(false);
			j.setMsg(invalidName);
			return j;
		} else if (!TextUtils.isPrintableAsciiOnly(pwd.trim())) {
			j.setSuccess(false);
			j.setMsg(invalidPass);
			return j;
		}
		
		// invoke service
		try {
			User user = new User();
			user.setLoginName(uid);
			user.setPassword(pwd);
			user.setMapStr(mapStr);
			SessionInfo sessionInfo = this.userService.getAsLogin(user);
			boolean loginResult = sessionInfo.isLoginResult();
			if (true == loginResult) {
				// check ip
				sessionInfo.setIp(Utils.getIP(request));
				
				// write log
				LogUtils.log(sessionInfo);
				
				// update language setting
				if (!TextUtils.isEmpty(sessionInfo.getLanguage())) {
					this.localeResolver.setLocale(request, response, new Locale(sessionInfo.getLanguage()));
				} else {
					this.localeResolver.setLocale(request, response, Locale.ENGLISH);
				}
				j.setSuccess(true);
				j.setObj(sessionInfo);
				if ("checked".equals(cookieRemember)) {
					// 添加用户保存密码功能
					Cookie cookie = new Cookie("userInfo89554128", uid + "-" + pwd);
					// 设定日期为30天;
					Integer maxDate = Integer.valueOf(CommonUtil.getConfig("cookie.login.max.date"));
					cookie.setMaxAge(60 * 60 * 24 * maxDate);
					cookie.setPath("/");
					response.addCookie(cookie);
				}
			} else {
				j.setMsg(sessionInfo.getLoginMessage());
				j.setSuccess(false);
			}
			// add session
			session.setAttribute(Constants.SESSION_INFO, sessionInfo);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
	


	@RequestMapping("/doLogout")
	@ResponseBody
	public Json doLogout(HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		Json j = new Json();
		j.setSuccess(true);
		return j;
	}
	
	@RequestMapping("/toDashboard")
	public ModelAndView toDashboard(HttpSession session) throws PmException {
		
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("user/dashboard");
		
		return mv;
	}
	
	@RequestMapping("/toEditProfile")
	public ModelAndView toProfile(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView();
		User user = this.userService.getWithProfile(sessionInfo.getUserId(), sessionInfo.getUserId());
		mv.addObject("user", user);
		mv.setViewName("user/profile");
		return mv;
	}
	
	@RequestMapping("/toPassword")
	public ModelAndView toPassword(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("user/password");
		return mv;
	}
	
	@RequestMapping(value = "/doPassword", method = RequestMethod.POST)
	@ResponseBody
	public Json doPassword(@RequestParam("pwdOld") String pwdOld,
	                    @RequestParam("pwdNew") String pwdNew,
	                    @RequestParam("pwdNew1") String pwdNew1,
	                    HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(pwdOld)) {
			j.setSuccess(false);
			j.setMsg("旧密码不能为空");
			return j;
		} else if (TextUtils.isEmpty(pwdNew) || TextUtils.isEmpty(pwdNew1) || TextUtils.getTrimmedLength(pwdNew) == 0 || TextUtils.getTrimmedLength(pwdNew1) == 0) {
			j.setSuccess(false);
			j.setMsg("新密码不能为空");
			return j;
		} else if (!pwdNew.equals(pwdNew1)) {
			j.setSuccess(false);
			j.setMsg("新密码不匹配");
			return j;
		} else if (!TextUtils.isPrintableAsciiOnly(pwdNew.trim())) {
			j.setSuccess(false);
			j.setMsg("密码格式错误");
			return j;
		}
		
		// invoke service
		try {
			User user = new User();
			user.setId(sessionInfo.getUserId());
			user.setPassword(pwdNew);
			user.setUserId(sessionInfo.getUserId());
			this.userService.editPassword(sessionInfo.getUserId(), pwdOld, user);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
}
