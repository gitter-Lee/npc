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
import com.mtf.contract.model.Unit;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.PageForm;
import com.mtf.contract.model.common.Pair;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IUnitService;
import com.mtf.contract.util.Constants;

/**
 * 单位相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("commonUnitController")
@RequestMapping("/common/unit")
public class UnitController {

	private static final Logger		logger	= Logger.getLogger(UnitController.class);
	
	private IUnitService			unitService;
	
	
	@Autowired
	public void setUnitService(IUnitService unitService) {
		this.unitService = unitService;
	}
	
	@RequestMapping("/doDataGrid")
	@ResponseBody
	public DataGrid<Unit> doDataGrid(PageForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Unit> result = new DataGrid<Unit>();
		try {
			List<Unit> list = this.unitService.listAll(sessionInfo.getUserId(), form);
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
	
	@RequestMapping("/doList")
	@ResponseBody
	public List<Unit> doList(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		List<Unit> list = null;
		try {
			list = this.unitService.listAllAvailable(sessionInfo.getUserId(), null);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return list;
	}

}
