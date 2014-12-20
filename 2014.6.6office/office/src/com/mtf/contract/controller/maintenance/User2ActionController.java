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
package com.mtf.contract.controller.maintenance;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.editor.DateEditor;
import com.mtf.contract.editor.DoubleEditor;
import com.mtf.contract.editor.IntegerEditor;
import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.User;
import com.mtf.contract.model.User2Action;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.PageForm;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IUser2ActionService;
import com.mtf.contract.service.IUserService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.TextUtils;

/**
 * 用户行为相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("maintenanceUser2ActionController")
@RequestMapping("/maintenance/user2action")
public class User2ActionController {

	private static final Logger		logger	= Logger.getLogger(User2ActionController.class);
	
	private IUser2ActionService		user2actionService;
	private IUserService			userService;
	
	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}
	@Autowired
	public void setUser2ActionService(IUser2ActionService user2actionService) {
		this.user2actionService = user2actionService;
	}
	
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor(true));
		binder.registerCustomEditor(Integer.class, new IntegerEditor(true));
		binder.registerCustomEditor(Double.class, new DoubleEditor(true));
	}
	
	@RequestMapping("/toAdd")
	public String toAdd(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		return "maintenance/user2action/add";
	}
	
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	@ResponseBody
	public Json doAdd(User2Action user2action, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(user2action.getUserId())) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		} else if (TextUtils.isEmpty(user2action.getActionId())) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		} else if (user2action.getAllow() == null || user2action.getAllow().intValue() < 0 || user2action.getAllow().intValue() > 1) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		}
		try {
			user2action = this.user2actionService.add(sessionInfo.getUserId(), user2action);
			j.setSuccess(true);
			j.setObj(user2action);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(@RequestParam("id") String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/user2action/edit");
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		try {
			User2Action user2action = this.user2actionService.getWithAction(sessionInfo.getUserId(), id.trim());
			if (user2action == null) {
				throw new PmException("用户行为不存在");
			}
			mv.addObject("user2action", user2action);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit(User2Action user2action, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		try {
			this.user2actionService.edit(sessionInfo.getUserId(), user2action);
			j.setSuccess(true);
			j.setObj(user2action);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	@RequestMapping("/toSearch")
	public ModelAndView toSearch(@RequestParam("userId") String userId, PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/user2action/search");
		if (TextUtils.isEmpty(userId) || TextUtils.getTrimmedLength(userId) == 0) {
			throw new PmException("参数错误");
		}
		try {
			User user = this.userService.get(sessionInfo.getUserId(), userId.trim());
			if (user == null) {
				throw new PmException("用户不存在");
			}
			mv.addObject("user", user);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public List<User2Action> doSearch(@RequestParam("userId") String userId, PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		if (TextUtils.isEmpty(userId) || TextUtils.getTrimmedLength(userId) == 0) {
			throw new PmException("参数错误");
		}
		List<User2Action> result = null;
		try {
			result = this.user2actionService.listByUserIdWithAction(sessionInfo.getUserId(), userId.trim());
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	@RequestMapping(value="/doDelete", method=RequestMethod.POST)
	@ResponseBody
	public Json doDelete(@RequestParam("id") String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		}
		try {
			this.user2actionService.delete(sessionInfo.getUserId(), id.trim());
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
}
