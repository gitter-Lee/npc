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
import com.mtf.contract.model.Resource;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.PageForm;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.model.page.ResourceSearchForm;
import com.mtf.contract.service.ResourceService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.TextUtils;

/**
 * 资源相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("adminResourceController")
@RequestMapping("/admin/resource")
public class ResourceController {

	private static final Logger		logger	= Logger.getLogger(ResourceController.class);
	
	private ResourceService		resourceService;
	
	
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
	 * 跳转到添加Resource页面
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toAdd")
	public String toAdd(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		return "admin/resource/add";
	}
	
	/**
	 * 执行添加Resource操作，返回Json
	 * @param resource
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	@ResponseBody
	public Json doAdd(Resource resource, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(resource.getName())) {
			j.setSuccess(false);
			j.setMsg("资源名称不能为空");
			return j;
		} else if (TextUtils.isEmpty(resource.getUri())) {
			j.setSuccess(false);
			j.setMsg("资源路径不能为空");
			return j;
		} else if (resource.getLevel() == null || resource.getLevel() < 1 || resource.getLevel() > 9) {
			j.setSuccess(false);
			j.setMsg("资源等级错误");
			return j;
		}
		try {
			resource = this.resourceService.add(sessionInfo.getUserId(), resource);
			j.setSuccess(true);
			j.setObj(resource);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 跳转到搜索Resource页面
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toSearch")
	public String toSearch(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		return "admin/resource/search";
	}
	
	/**
	 * 执行搜索Resource操作，返回DataGrid
	 * @param form
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Resource> doSearch(ResourceSearchForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Resource> result = null;
		try {
			result = this.resourceService.list(sessionInfo.getUserId(), form);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
	/**
	 * 跳转到编辑Resource页面
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
		ModelAndView mv = new ModelAndView("admin/resource/edit");
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		id = id.trim();
		try {
			Resource resource = this.resourceService.get(sessionInfo.getUserId(), id);
			if (resource == null) {
				throw new PmException("资源不存在");
			}
			mv.addObject("resource", resource);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	/**
	 * 执行编辑Resource操作，返回Json
	 * @param resource
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit(Resource resource, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(resource.getId())) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		} else if (TextUtils.isEmpty(resource.getName())) {
			j.setSuccess(false);
			j.setMsg("资源名称不能为空");
			return j;
		} else if (TextUtils.isEmpty(resource.getUri())) {
			j.setSuccess(false);
			j.setMsg("资源路径不能为空");
			return j;
		} else if (resource.getLevel() == null || resource.getLevel() < 1 || resource.getLevel() > 9) {
			j.setSuccess(false);
			j.setMsg("资源等级错误");
			return j;
		}
		try {
			this.resourceService.edit(sessionInfo.getUserId(), resource);
			j.setSuccess(true);
			j.setObj(resource);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 执行删除Resource操作，返回Json
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
			this.resourceService.delete(sessionInfo.getUserId(), id);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
	
	/**
	 * @param form
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doSearch4Action", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Resource> doSearch4Action(PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Resource> result = new DataGrid<Resource>();
		try {
			List<Resource> list = this.resourceService.listAllUnsigned(sessionInfo.getUserId(), form);
			if (list != null && !list.isEmpty()) {
				result.setTotal(list.size());
				result.setRows(list);
			}
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
}
