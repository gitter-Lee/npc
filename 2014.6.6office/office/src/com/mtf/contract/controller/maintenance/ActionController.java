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
import org.springframework.web.bind.annotation.ResponseBody;

import com.mtf.contract.editor.DateEditor;
import com.mtf.contract.editor.DoubleEditor;
import com.mtf.contract.editor.IntegerEditor;
import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Action;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.PageForm;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IActionService;
import com.mtf.contract.util.Constants;

/**
 * 行为相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("maintenanceActionController")
@RequestMapping("/maintenance/action")
public class ActionController {

	private static final Logger		logger	= Logger.getLogger(ActionController.class);
	
	private IActionService			actionService;
	
	@Autowired
	public void setActionService(IActionService actionService) {
		this.actionService = actionService;
	}
	
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor(true));
		binder.registerCustomEditor(Integer.class, new IntegerEditor(true));
		binder.registerCustomEditor(Double.class, new DoubleEditor(true));
	}
	
	/**
	 * 执行搜索非系统的Action，返回DataGrid
	 * @param form 搜索表单
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping(value="/doSearchNonSystem", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Action> doSearchNonSystem(PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Action> result = new DataGrid<Action>();
		try {
			List<Action> list = this.actionService.listAll(sessionInfo.getUserId(), form, 0);
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
}
