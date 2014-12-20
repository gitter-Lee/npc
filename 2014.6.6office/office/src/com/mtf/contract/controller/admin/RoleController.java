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
import com.mtf.contract.model.Role;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.PageForm;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IRoleService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.TextUtils;

/**
 * 角色相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("adminRoleController")
@RequestMapping("/admin/role")
public class RoleController {

	private static final Logger		logger	= Logger.getLogger(RoleController.class);
	
	private IRoleService			roleService;
	
	@Autowired
	public void setRoleService(IRoleService roleService) {
		this.roleService = roleService;
	}
	
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor(true));
		binder.registerCustomEditor(Integer.class, new IntegerEditor(true));
		binder.registerCustomEditor(Double.class, new DoubleEditor(true));
	}
	
	@RequestMapping("/toAdd")
	public ModelAndView toAdd(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("admin/role/add");
		return mv;
	}
	
	/**
	 * 执行添加Role操作，返回Json
	 * @param role
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	@ResponseBody
	public Json doAdd(Role role, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		String userId = sessionInfo.getUserId();
		role.setUserId(userId);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(role.getName())) {
			j.setSuccess(false);
			j.setMsg("角色名称不能为空");
			return j;
		} else if (role.getLevel() == null || role.getLevel() < 1 || role.getLevel() > 9) {
			j.setSuccess(false);
			j.setMsg("角色等级错误");
			return j;
		}
		// set to system role
		role.setSystem(1);
		try {
			role = this.roleService.add(sessionInfo.getUserId(), role);
			j.setSuccess(true);
			j.setObj(role);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 跳转到编辑Role页面
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
		ModelAndView mv = new ModelAndView("admin/role/edit");
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		id = id.trim();
		try {
			Role role = this.roleService.get(sessionInfo.getUserId(), id);
			if (role == null) {
				throw new PmException("角色不存在");
			}
			mv.addObject("role", role);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	/**
	 * 执行编辑Role操作，返回Json
	 * @param role
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit(Role role, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(role.getId())) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		} else if (TextUtils.isEmpty(role.getName())) {
			j.setSuccess(false);
			j.setMsg("角色名称不能为空");
			return j;
		} else if (role.getLevel() == null || role.getLevel() < 1 || role.getLevel() > 9) {
			j.setSuccess(false);
			j.setMsg("角色等级错误");
			return j;
		}
		// set to system role
		role.setSystem(1);
		try {
			role = this.roleService.edit(sessionInfo.getUserId(), role);
			j.setSuccess(true);
			j.setObj(role);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 执行删除Role操作，返回Json
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
			throw new PmException("参数错误");
		}
		id = id.trim();
		try {
			this.roleService.delete(sessionInfo.getUserId(), id);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
	
	/**
	 * 跳转到搜索Role页面
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("admin/role/search");
		return mv;
	}
	
	/**
	 * 执行搜索Role操作，返回DataGrid
	 * @param form
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Role> doSearch(PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Role> result = new DataGrid<Role>();
		try {
			List<Role> list = this.roleService.listAll(sessionInfo.getUserId(), form, 1);
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
	
	@RequestMapping(value="/doSearch4User", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Role> doSearch4User(PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Role> result = new DataGrid<Role>();
		try {
			List<Role> list = this.roleService.listAllAvailable(sessionInfo.getUserId(), form, null);
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
	
	/**
	 * 跳转到编辑Role Action页面
	 * @param id
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toEditActions")
	public ModelAndView toEditActions(@RequestParam("id") String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("admin/role/editActions");
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		id = id.trim();
		try {
			Role role = this.roleService.getWithActions(sessionInfo.getUserId(), id);
			if (role == null) {
				throw new PmException("角色不存在");
			}
			mv.addObject("role", role);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	/**
	 * 执行搜索Role Action操作，返回DataGrid
	 * @param id
	 * @param form
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doSearchActions", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Action> doSearchActions(@RequestParam("id") String id, PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Action> result = new DataGrid<Action>();
		// validate
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		id = id.trim();
		try {
			Role role = this.roleService.getWithActions(sessionInfo.getUserId(), id);
			if (role == null) {
				throw new PmException("角色不存在");
			}
			List<Action> actions = role.getActions();
			if (actions != null && !actions.isEmpty()) {
				result.setRows(actions);
				result.setTotal(actions.size());
			}
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
	/**
	 * 执行编辑Role Action操作，返回Json
	 * @param id
	 * @param actionIds
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value = "/doEditActions", method = RequestMethod.POST)
	@ResponseBody
	public Json doEditResources(@RequestParam("id") String id,
	                            @RequestParam("actionIds") String actionIds,
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
			String[] actions = null;
			if (!TextUtils.isEmpty(actionIds) && TextUtils.getTrimmedLength(actionIds) > 0) {
				actions = actionIds.split(",");
			}
			this.roleService.editActions(sessionInfo.getUserId(), id, actions);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
}
