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
import com.mtf.contract.model.User;
import com.mtf.contract.model.common.Pair;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IUserService;
import com.mtf.contract.util.Constants;

/**
 * 用户相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("commonUserController")
@RequestMapping("/common/user")
public class UserController {

	private static final Logger		logger	= Logger.getLogger(UserController.class);
	
	private IUserService			userService;
	
	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@RequestMapping("/doListPair")
	@ResponseBody
	public List<Pair<String, String>> doListPair(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		
		List<Pair<String, String>> list = null;
		try {
			List<User> users = this.userService.listAllAvailable(sessionInfo.getUserId(), null);
			
			if (users != null && !users.isEmpty()) {
				list = new ArrayList<Pair<String, String>>(users.size());
				for (User user : users) {
					list.add(new Pair<String, String>(user.getId(), user.getDisplayName()));
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
