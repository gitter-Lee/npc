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
package com.mtf.contract.controller.admin;

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
import com.mtf.contract.model.Action;
import com.mtf.contract.model.Resource;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.PageForm;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IActionService;
import com.mtf.contract.service.ResourceService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.TextUtils;

/**
 * 行为相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("adminActionController")
@RequestMapping("/admin/action")
public class ActionController {

	private static final Logger		logger	= Logger.getLogger(ActionController.class);
	
	private IActionService			actionService;
	private ResourceService		resourceService;
	
	@Autowired
	public void setActionService(IActionService actionService) {
		this.actionService = actionService;
	}
	
	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
	
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor(true));
		binder.registerCustomEditor(Integer.class, new IntegerEditor(true));
		binder.registerCustomEditor(Double.class, new DoubleEditor(true));
	}
	
	/**
	 * 跳转到添加Action页面
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toAdd")
	public ModelAndView toAdd(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("admin/action/add");
		return mv;
	}
	
	/**
	 * 执行添加Action操作，返回Json
	 * @param action
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	@ResponseBody
	public Json doAdd(Action action, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(action.getName())) {
			j.setSuccess(false);
			j.setMsg("行为名称不能为空");
			return j;
		} else if (action.getLevel() == null || action.getLevel() < 1 || action.getLevel() > 9) {
			j.setSuccess(false);
			j.setMsg("行为等级错误");
			return j;
		}
		try {
			action = this.actionService.add(sessionInfo.getUserId(), action);
			j.setSuccess(true);
			j.setObj(action);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 跳转到搜索Action页面
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toSearch")
	public String toSearch(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		return "admin/action/search";
	}
	
	/**
	 * 执行搜索Action操作，返回DataGrid
	 * @param form
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Action> doSearch(PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Action> result = new DataGrid<Action>();
		try {
			List<Action> list = this.actionService.listAll(sessionInfo.getUserId(), form, null);
			if (list != null && !list.isEmpty()) {
				result.setRows(list);
				result.setTotal(list.size());
			}
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 跳转到编辑Action Resource页面
	 * @param id
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toEditResources")
	public ModelAndView toEditResources(@RequestParam("id") String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("admin/action/editResources");
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		id = id.trim();
		try {
			Action action = this.actionService.get(sessionInfo.getUserId(), id);
			if (action == null) {
				throw new PmException("行为不存在");
			}
			mv.addObject("action", action);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	/**
	 * 执行搜索Action Resource操作，返回DataGrid
	 * @param id
	 * @param page
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/doSearchResources")
	@ResponseBody
	public DataGrid<Resource> doSearchResources(@RequestParam("id") String id, PageForm page, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		id = id.trim();
		DataGrid<Resource> result = new DataGrid<Resource>();
		try {
			List<Resource> resources = this.resourceService.listByActionId(sessionInfo.getUserId(), id);
			if (resources != null && !resources.isEmpty()) {
				result.setRows(resources);
				result.setTotal(resources.size());
			}
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 执行编辑Action Resource操作，返回Json
	 * @param id
	 * @param resourceIds
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doEditResources", method = RequestMethod.POST)
	@ResponseBody
	public Json doEditResources(@RequestParam("id") String id,
	                            @RequestParam("resourceIds") String resourceIds,
	                            HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		}
		id = id.trim();
		try {
			String[] resources = null;
			if (!TextUtils.isEmpty(resourceIds) && TextUtils.getTrimmedLength(resourceIds) > 0) {
				resources = resourceIds.split(",");
			}
			this.actionService.editResources(sessionInfo.getUserId(), id.trim(), resources);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 跳转到编辑Action页面
	 * @param id
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(@RequestParam("id") String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("admin/action/edit");
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		id = id.trim();
		try {
			Action action = this.actionService.get(sessionInfo.getUserId(), id);
			if (action == null) {
				throw new PmException("行为不存在");
			}
			mv.addObject("action", action);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	/**
	 * 执行编辑Action操作，返回Json
	 * @param action
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit(Action action, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(action.getId())) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		} else if (TextUtils.isEmpty(action.getName())) {
			j.setSuccess(false);
			j.setMsg("行为名称不能为空");
			return j;
		} else if (action.getLevel() == null || action.getLevel() < 1 || action.getLevel() > 9) {
			j.setSuccess(false);
			j.setMsg("行为等级错误");
			return j;
		}
		try {
			action = this.actionService.edit(sessionInfo.getUserId(), action);
			j.setSuccess(true);
			j.setObj(action);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 执行删除Action操作，返回Json
	 * @param id
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doDelete", method = RequestMethod.POST)
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
		id = id.trim();
		try {
			this.actionService.delete(sessionInfo.getUserId(), id);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
}
