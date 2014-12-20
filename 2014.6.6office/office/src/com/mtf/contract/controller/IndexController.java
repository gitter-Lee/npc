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
package com.mtf.contract.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Resource;
import com.mtf.contract.model.impl.QuestionnaireImpl;
import com.mtf.contract.model.impl.UserQuestionnaireImpl;
import com.mtf.contract.service.ResourceService;
import com.mtf.contract.util.Constants;
import com.mtf.questionnaire.controller.ControllerUserQuestionnaire;

/**
 * 系统入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("indexController")
public class IndexController {

	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	public ResourceService getResourceService() {
		return resourceService;
	}
	
	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
/**
 * 加入问卷ID变量
 * @param quest
 * @param session
 * @return
 * @throws PmException
 */
	@RequestMapping("/index")
	public ModelAndView index(HttpSession session) throws PmException{
		if (session != null && session.getAttribute(Constants.SESSION_INFO) != null) {
			ModelAndView mv = new ModelAndView("main");
//			SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
			// 取得用户所有菜单
			Resource resource = new Resource();
			// 访问类型 菜单(Menu):m
			resource.setAccess("m");
			// 查询主菜单
			resource.setLevel(0);
			List<Resource> listResources0 = resourceService.selectMenu(resource);
			System.err.println(listResources0);	
			// 查询一级子菜单
			resource.setLevel(1);
			List<Resource> listResources1 = resourceService.selectMenu(resource);
			System.err.println(listResources1);
			mv.addObject("listResources0", listResources0);
			mv.addObject("listResources1", listResources1);
			return mv;
		} else {
			 return new ModelAndView("user/login");
		}
	}
	
	@RequestMapping("/")
	public String root(HttpSession session) {
		if (session != null && session.getAttribute(Constants.SESSION_INFO) != null) {
			return "main";
		}
		return "user/login";
	}
	
	/**
	 * 问卷跳转
	 * @return
	 */
	@RequestMapping("/indexQuest")
	public ModelAndView indexQuest(@RequestParam("quesId") String quesId, HttpSession session)throws PmException{
			ModelAndView mv = new ModelAndView("user/login");
			mv.addObject("questId", quesId);
			return mv;
		

		
	}
}
