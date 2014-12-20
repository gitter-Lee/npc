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
import org.springframework.context.MessageSource;
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
import com.mtf.contract.model.Division;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IDivisionService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.TextUtils;

/**
 * 部门相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version 1.0	2013-5-30	ShouRen.Sun		modified.
 * @version <ver>
 */
@Controller("maintenanceDivisionController")
@RequestMapping("/maintenance/division")
public class DivisionController {

	private static final Logger		logger	= Logger.getLogger(DivisionController.class);
	
	private IDivisionService		divisionService;
	private MessageSource			messages;
	
	
	@Autowired
	public void setDivisionService(IDivisionService divisionService) {
		this.divisionService = divisionService;
	}

	@Autowired
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor(true));
		binder.registerCustomEditor(Integer.class, new IntegerEditor(true));
		binder.registerCustomEditor(Double.class, new DoubleEditor(true));
	}
	
	/**
	 * 跳转到部门添加页面
	 * @param session
	 * @return 部门添加页面
	 * @throws PmException
	 */
	@RequestMapping("/toAdd")
	public String toAdd(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		return "maintenance/division/add";
	}
	
	/**
	 * 执行部门添加操作
	 * @param division 部门信息
	 * @param session
	 * @return 执行结果
	 * @throws PmException
	 */
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	@ResponseBody
	public Json doAdd(Division division, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(division.getName()) || TextUtils.getTrimmedLength(division.getName()) == 0) {
			j.setSuccess(false);
			j.setMsg("部门名称不能为空");
			return j;
		}
		division.setUserId(sessionInfo.getUserId());
		try {
			division = this.divisionService.add(sessionInfo.getUserId(), division);
			j.setSuccess(true);
			j.setObj(division);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 跳转到部门检索页面
	 * @param session
	 * @return 部门检索页面
	 * @throws PmException
	 */
	@RequestMapping("/toSearch")
	public String toSearch(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		return "maintenance/division/search";
	}
	
	/**
	 * 获取所有部门信息列表
	 * @param session
	 * @return 部门信息列表
	 * @throws PmException
	 */
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public List<Division> doSearch(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		List<Division> result = null;
		try {
			result = this.divisionService.listAll(sessionInfo.getUserId());
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
	/**
	 * 跳转到部门编辑页面
	 * @param session
	 * @param divisionId 部门编号
	 * @return 部门编辑页面
	 * @throws PmException
	 */
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(@RequestParam("divisionId") String divisionId, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/division/edit");
		if (TextUtils.isEmpty(divisionId)) {
			throw new PmException("参数错误");
		}
		Division division = null;
		try {
			division = this.divisionService.get(sessionInfo.getUserId(), divisionId);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		if (division == null) {
			throw new PmException("部门不存在");
		}
		mv.addObject("division", division);
		return mv;
	}
	
	/**
	 * 执行部门编辑操作
	 * @param division 部门信息
	 * @param session
	 * @return 执行结果
	 * @throws PmException
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit(Division division, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(division.getId())) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		} else if (TextUtils.isEmpty(division.getName())) {
			j.setSuccess(false);
			j.setMsg("部门名称不能为空");
			return j;
		}
		division.setUserId(sessionInfo.getUserId());
		try {
			this.divisionService.edit(sessionInfo.getUserId(), division);
			j.setSuccess(true);
			j.setObj(division);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 执行部门删除操作
	 * @param divisionId 部门编号
	 * @param session
	 * @return 执行结果
	 * @throws PmException
	 */
	@RequestMapping(value = "/doDelete", method = RequestMethod.POST)
	@ResponseBody
	public Json doDelete(@RequestParam("divisionId") String divisionId, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		if (TextUtils.isEmpty(divisionId)) {
			throw new PmException("参数错误");
		}
		try {
			this.divisionService.delete(sessionInfo.getUserId(), divisionId);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
}
