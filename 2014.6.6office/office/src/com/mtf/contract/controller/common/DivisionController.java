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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Division;
import com.mtf.contract.model.common.Pair;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IDivisionService;
import com.mtf.contract.util.Constants;

/**
 * 部门相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("commonDivisionController")
@RequestMapping("/common/division")
public class DivisionController {

	private static final Logger		logger	= Logger.getLogger(DivisionController.class);
	
	private IDivisionService		divisionService;
	
	
	@Autowired
	public void setDivisionService(IDivisionService divisionService) {
		this.divisionService = divisionService;
	}
	
	@RequestMapping("/doList")
	@ResponseBody
	public List<Division> doList(@RequestParam(value="tag", required=false) Integer tag, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		List<Division> result = null;
		try {
			result = this.divisionService.listAvailable(sessionInfo.getUserId(), tag);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param tag
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/doListPair")
	@ResponseBody
	public List<Pair<String, String>> doListPair(@RequestParam(value="tag", required=false) Integer tag, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		List<Pair<String, String>> list = null;
		try {
			List<Division> divisionList = this.divisionService.listAvailable(sessionInfo.getUserId(), tag);
			if (divisionList != null && !divisionList.isEmpty()) {
				list = new ArrayList<Pair<String, String>>(divisionList.size());
				for (Division division : divisionList) {
					list.add(new Pair<String, String>(division.getId(), division.getName()));
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
