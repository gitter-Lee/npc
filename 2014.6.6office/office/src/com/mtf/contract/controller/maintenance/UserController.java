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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

import com.alibaba.fastjson.JSONObject;
import com.mtf.contract.editor.DateEditor;
import com.mtf.contract.editor.DoubleEditor;
import com.mtf.contract.editor.IntegerEditor;
import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.History;
import com.mtf.contract.model.Role;
import com.mtf.contract.model.User;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.Pair;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.model.page.UserSearchDataGridItem;
import com.mtf.contract.model.page.UserSearchForm;
import com.mtf.contract.service.HistoryService;
import com.mtf.contract.service.IUser2DivisionService;
import com.mtf.contract.service.IUserService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.TextUtils;

/**
 * 用户相关入口控制器
 *
 * @author Wade.Zhu
 * @version 1.0	2013-4-25	Wade.Zhu		created.
 * @version <ver>
 */
@Controller("maintenanceUserController")
@RequestMapping("/maintenance/user")
public class UserController {

	private static final Logger		logger	= Logger.getLogger(UserController.class);
	
	private IUserService			userService;
	private MessageSource			messages;
	private IUser2DivisionService	user2DivisionService;
	private HistoryService historyService;

	@Autowired
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}
	
	@Autowired
	public void setUser2DivisionService(IUser2DivisionService user2DivisionService) {
		this.user2DivisionService = user2DivisionService;
	}
	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
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
	
	@RequestMapping("/toAdd")
	public ModelAndView toAdd(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		ModelAndView mv = new ModelAndView("maintenance/user/editUser");
		User user = new User();
		user.setEditState("i");
		mv.addObject("user", user);
		return mv;
	}
	
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	@ResponseBody
	public Json doAdd(User user, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(user.getLoginName())) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.uidpwdempty", null, null));
			return j;
		} else if (TextUtils.getTrimmedLength(user.getLoginName()) < 3 || !TextUtils.isPrintableAsciiOnly(user.getLoginName().trim())) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.invalid.loginname", null, null));
			return j;
		} else if (TextUtils.indexOf(user.getLoginName().trim(), ' ') != -1) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.invalid.loginname.whitechar", null, null));
			return j;
		} else if (TextUtils.isEmpty(user.getPassword())) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.uidpwdempty", null, null));
			return j;
		} else if (TextUtils.getTrimmedLength(user.getPassword()) < 6 || TextUtils.getTrimmedLength(user.getPassword()) > 30) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.pwdlengthinvalid", null, null));
			return j;
		} else if (TextUtils.isEmpty(user.getUserProfile().getFullname())) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.uidpwdempty", null, null));
			return j;
		}
		user.setUserId(sessionInfo.getUserId());
		try {
			user = this.userService.add(sessionInfo.getUserId(), user);
			j.setSuccess(true);
			j.setObj(user);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(@RequestParam("id") String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/user/editUser");
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		try {
			User user = this.userService.getWithProfile(sessionInfo.getUserId(), id.trim());
			if (user == null) {
				throw new PmException("用户不存在");
			}
			user.setEditState("u");
			mv.addObject("user", user);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit(User user, HttpSession session) throws PmException {
		String editState = user.getEditState();
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(user.getLoginName())) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.uidpwdempty", null, null));
			return j;
		} else if (TextUtils.getTrimmedLength(user.getLoginName()) < 3 || !TextUtils.isPrintableAsciiOnly(user.getLoginName().trim())) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.invalid.loginname", null, null));
			return j;
		} else if (TextUtils.indexOf(user.getLoginName().trim(), ' ') != -1) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.invalid.loginname.whitechar", null, null));
			return j;
		}
		user.setUserId(sessionInfo.getUserId());
		try {
			if (editState.equals("i")) {
				this.userService.add(sessionInfo.getUserId(), user);
			} else if (editState.equals("u")) {
				if (TextUtils.isEmpty(user.getId())) {
					j.setSuccess(false);
					j.setMsg("参数错误");
					return j;
				}
				this.userService.edit(sessionInfo.getUserId(), user);
			}
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	@RequestMapping("/toPassword")
	public ModelAndView toPassword(@RequestParam("id") String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/user/password");
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		if ("460f4f88-52c2-4d50-8275-0d0739e6237d".equalsIgnoreCase(id.trim())) {
			throw new PmException("内置用户不能编辑");
		}
		try {
			User user = this.userService.getWithProfile(sessionInfo.getUserId(), id.trim());
			if (user == null) {
				throw new PmException("用户不存在");
			}
			mv.addObject("user", user);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	@RequestMapping(value = "/doPassword", method = RequestMethod.POST)
	@ResponseBody
	public Json doPassword(User user, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(user.getId())) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		}
		if ("460f4f88-52c2-4d50-8275-0d0739e6237d".equalsIgnoreCase(user.getId())) {
			j.setSuccess(false);
			j.setMsg("内置用户不能编辑");
		}
		if (TextUtils.isEmpty(user.getPassword())) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.pwdempty", null, null));
			return j;
		} else if (TextUtils.getTrimmedLength(user.getPassword()) < 6 || TextUtils.getTrimmedLength(user.getPassword()) > 30) {
			j.setSuccess(false);
			j.setMsg(this.messages.getMessage("c_user.pwdlength", null, null));
			return j;
		}
		user.setUserId(sessionInfo.getUserId());
		try {
			this.userService.editPassword(sessionInfo.getUserId(), null, user);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	@RequestMapping("/toEditRoles")
	public ModelAndView toEditRoles(@RequestParam("id") String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/user/editRoles");
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		User user = null;
		try {
			user = this.userService.getWithRoles(sessionInfo.getUserId(), id.trim());
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		if (user == null) {
			throw new PmException("用户不存在");
		}
		
		mv.addObject("user", user);
		if (user.getRoles() != null && !user.getRoles().isEmpty()) {
			mv.addObject("roleList", JSONObject.toJSON(user.getRoles()).toString());
		}
		
		return mv;
	}
	
	@RequestMapping("/doSearchRoles")
	@ResponseBody
	public DataGrid<Role> doSearchRoles(@RequestParam("id") String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			throw new PmException("参数错误");
		}
		DataGrid<Role> result = new DataGrid<Role>();
		try {
			User user = this.userService.getWithRoles(sessionInfo.getUserId(), id);
			if (user == null) {
				throw new PmException("用户不存在");
			}
			if (user.getRoles() != null && !user.getRoles().isEmpty()) {
				//new add
				/*for(int i = 0; i < user.getRoles().size(); i++){
					System.out.println("++++");
					System.out.println(user.getRoles().get(i).getId());
				}*/
				result.setRows(user.getRoles());
				result.setTotal(user.getRoles().size());
			}
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	@RequestMapping("/toEditActions")
	public ModelAndView toEditActions(@RequestParam("userId") String userId, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/user/editActions");
		if (TextUtils.isEmpty(userId) || TextUtils.getTrimmedLength(userId) == 0) {
			throw new PmException("参数错误");
		}
		try {
			User user = this.userService.get(sessionInfo.getUserId(), userId);
			if (user == null) {
				throw new PmException("用户不存在");
			}
			mv.addObject("user", user);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return mv;
	}
	
	@RequestMapping(value = "/doEditRoles", method = RequestMethod.POST)
	@ResponseBody
	public Json doEditRoles(@RequestParam("userId") String userId,
	                        @RequestParam("roleIds") String roleIds,
	                        HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(userId) || TextUtils.getTrimmedLength(userId) == 0) {
			j.setSuccess(false);
			j.setMsg("参数错误");
			return j;
		}
		try {
			String[] roles = null;
			if (!TextUtils.isEmpty(roleIds) && TextUtils.getTrimmedLength(roleIds) > 0) {
				roles = roleIds.split(",");
				//new add2
				User user = this.userService.getWithRoles(sessionInfo.getUserId(), userId);
				if (user == null) {
					throw new PmException("用户不存在");
				}
				//new add
				History history1 = new History();
				history1.setUserIds(userId);
				History history2 = this.historyService.getMax(history1);
				int version = 0;
				if(history2!= null ){
					version = history2.getVersionNO();
					version++;
				}else{
					version =1;
				}
			
				
				if (user.getRoles() != null && !user.getRoles().isEmpty()) {
					//new add
					for(int i = 0; i < user.getRoles().size(); i++){
						String rid =user.getRoles().get(i).getId();
						History history = new History();
						history.setId(UUID.randomUUID().toString());
						history.setUserIds(userId);
						history.setType("r");
						history.setCurrentFlag("n");
						history.setPreCodeId(rid);
						history.setAddUser(sessionInfo.getUserId());
						history.setAddIp(sessionInfo.getIp());
						history.setAddDate(new Date());
						//version
						history.setVersionNO(version);
						this.historyService.insertForVersioin(history);
					}
				}
				//new add
				for(int i=0;i<roles.length;i++){
					History history = new History();
					history.setId(UUID.randomUUID().toString());
					history.setUserIds(userId);
					history.setType("r");
					history.setCurrentFlag("y");
					history.setCodeId(roles[i]);
					history.setAddUser(sessionInfo.getUserId());
					history.setAddIp(sessionInfo.getIp());
					history.setAddDate(new Date());
					
					//version
					history.setVersionNO(version);
					this.historyService.insertForVersioin(history);
				}
				
				
				
			}
			this.userService.editRoles(sessionInfo.getUserId(), userId.trim(), roles);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}

	@RequestMapping("/toSearch")
	public ModelAndView toSearch(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/user/search");
		return mv;
	}
	
	@RequestMapping("/toSearchForApprove")
	public ModelAndView toSearchForApprove(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/user/SearchForApprove");
		return mv;
	}
	
	@RequestMapping(value="/doSearchForApprove", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<UserSearchDataGridItem> doSearchForApprove(UserSearchForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<UserSearchDataGridItem> result = null;
		try {
			result = this.userService.list(sessionInfo.getUserId(), form);
			if (result == null) {
				result = new DataGrid<UserSearchDataGridItem>();
			}
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/*@RequestMapping(value="/doSearchForVersion", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<History> doSearchForVersion(History history, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<History> result = null;
		try {
			//result = this.historyService.listForVersion(sessionInfo.getUserId(), history);
			result = this.historyService.select(history);
			if (result == null) {
				result = new DataGrid<History>();
			}
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return result;
	}*/
	
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<UserSearchDataGridItem> doSearch(UserSearchForm form, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<UserSearchDataGridItem> result = null;
		try {
			result = this.userService.list(sessionInfo.getUserId(), form);
			if (result == null) {
				result = new DataGrid<UserSearchDataGridItem>();
			}
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return result;
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
	
	@RequestMapping("/doApproveForUserRegister")
	public ModelAndView doApproveForUserRegister(User user,HttpSession session) throws PmException{
		SessionInfo sessionInfo =(SessionInfo)session.getAttribute(Constants.SESSION_INFO);
		try {
			user.setModifyUser(sessionInfo.getLoginName());
			user.setModifyDate(new Date());
			userService.updateForApproveStatus(user, session);
			
			User dbuer = this.userService.getByLoginName(sessionInfo.getUserId(), user.getLoginName());
			if("0".equals(dbuer.getStatus())){
				String userId = dbuer.getId();
				String[] roles = new String[]{"9678860e-d0b3-4e53-8661-89f0f2adf42f"};
				this.userService.editRoles(sessionInfo.getUserId(),userId.trim(),roles);
				
				String departmentId = dbuer.getDepartmentId();
				String[] divisions = new String[]{departmentId};
				String[] leaders = null;
				String mainIndex="";
				this.user2DivisionService.edit(sessionInfo.getUserId(), userId.trim(), divisions, leaders,mainIndex);
				
			}
			
			
		} catch (PmException e) {
			e.printStackTrace();
		}
		
		
		return this.toSearch(session);
	}
}
