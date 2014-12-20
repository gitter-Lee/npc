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
package com.mtf.contract.controller.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Role;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.PageForm;
import com.mtf.contract.model.common.Pair;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IRoleService;
import com.mtf.contract.util.Constants;

/**
 * 角色相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("commonRoleController")
@RequestMapping("/common/role")
public class RoleController {

	private static final Logger		logger	= Logger.getLogger(RoleController.class);
	
	private IRoleService			roleService;
	
	@Autowired
	public void setRoleService(IRoleService roleService) {
		this.roleService = roleService;
	}
	
	@RequestMapping("/doDataGrid")
	@ResponseBody
	public DataGrid<Role> doDataGrid(PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Role> result = new DataGrid<Role>();
		try {
			List<Role> list = this.roleService.listAllAvailable(sessionInfo.getUserId(), form, 0);
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
	
	@RequestMapping("/doList")
	@ResponseBody
	public List<Role> doList(PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		List<Role> list = null;
		try {
			list = this.roleService.listAll(sessionInfo.getUserId(), form, null);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return list;
	}
	
	@RequestMapping("/doListPair")
	@ResponseBody
	public List<Pair<String, String>> doListPair(PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		List<Pair<String, String>> list = null;
		try {
			List<Role> roleList = this.roleService.listAll(sessionInfo.getUserId(), form, null);
			if (roleList != null && !roleList.isEmpty()) {
				list = new ArrayList<Pair<String, String>>(roleList.size());
				for (Role role : roleList) {
					list.add(new Pair<String, String>(role.getId(), role.getName()));
				}
			}
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return list;
	}
}
