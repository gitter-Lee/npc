package com.mtf.contract.controller.maintenance;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.History;
import com.mtf.contract.model.User2Division;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.HistoryService;
import com.mtf.contract.service.IUser2DivisionService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.TextUtils;


/**
 * 用户部门相关入口控制器
 *
 * @author shouren.sun
 * @version 1.0	2013-5-30	shouren.sun		created.
 * @version <ver>
 */
@Controller("maintenanceUser2DivisionController")
@RequestMapping("/maintenance/user2division")
public class User2DivisionController {

	private static final Logger		logger	= Logger.getLogger(User2ActionController.class);
	
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
	
	/**
	 * 执行部门添加用户操作
	 * @param user2division 部门用户信息
	 * @param session
	 * @return 执行结果
	 * @throws PmException
	 */
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	@ResponseBody
	public Json doAdd(User2Division user2division, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(user2division.getDivisionId()) || TextUtils.getTrimmedLength(user2division.getDivisionId()) == 0) {
			j.setSuccess(false);
			j.setMsg("部门ID不能为空");
			return j;
		}
		if (TextUtils.isEmpty(user2division.getUserId()) || TextUtils.getTrimmedLength(user2division.getUserId()) == 0) {
			j.setSuccess(false);
			j.setMsg("UserName不能为空");
			return j;
		}

		try {
			user2division = this.user2DivisionService.add(sessionInfo.getUserId(), user2division);
			j.setSuccess(true);
			j.setObj(user2division);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 执行部门删除用户操作
	 * @param id 部门用户信息编号
	 * @param session
	 * @return 执行结果
	 * @throws PmException
	 */
	@RequestMapping(value = "/doDelete", method = RequestMethod.POST)
	@ResponseBody
	public Json doDelete(String id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(id) || TextUtils.getTrimmedLength(id) == 0) {
			j.setSuccess(false);
			j.setMsg("ID不能为空");
			return j;
		}

		try {
			this.user2DivisionService.delete(sessionInfo.getUserId(), id);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 跳转到用户部门编辑页面
	 * @param userId 用户编号
	 * @param session
	 * @return 用户部门编辑页面
	 * @throws PmException
	 */
	@RequestMapping("/toEditDivisions")
	public ModelAndView toEditDivisions(@RequestParam("id") String userId, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv = new ModelAndView("maintenance/user2division/editDivisions");
		if (TextUtils.isEmpty(userId) || TextUtils.getTrimmedLength(userId) == 0) {
			throw new PmException("参数错误");
		}

		Map<String, List<User2Division>> divisions = null;
		
		try {
			divisions = user2DivisionService.listByUserId(sessionInfo.getUserId(), userId);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		mv.addObject("userDivisions", divisions.get("userDivisions"));
		mv.addObject("hasLeaderDivisions", divisions.get("hasLeaderDivisions"));
		mv.addObject("hasLeaderMainDivisons", divisions.get("hasLeaderMainDivisons"));
		mv.addObject("userId",userId);
		
		return mv;
	}
	
	/**
	 * 执行用户部门编辑操作
	 * @param userId 用户编号
	 * @param divisionIds 所属部门编号，多个部门以","分隔
	 * @param leaderIds 所在部门属于领导的部门编号，多个部门以","分隔
	 * @param session
	 * @return 执行结果
	 * @throws PmException
	 */
	@RequestMapping(value = "/doEditDivisions", method = RequestMethod.POST)
	@ResponseBody
	public Json doEditDivisions(@RequestParam("userId") String userId,
	                            String divisionIds,
	                            String leaderIds,String mainIndex,
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
			String[] divisions = null;
			String[] leaders = null;
			if (!TextUtils.isEmpty(divisionIds)) {
				divisions = divisionIds.split(",");
			}
			
			if(!TextUtils.isEmpty(leaderIds)){
				leaders = leaderIds.split(",");
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
			
			//new add
			Map<String, List<User2Division>> divisions1 = null;
			divisions1 = user2DivisionService.listByUserId(sessionInfo.getUserId(), userId);
			
			for(int i = 0; i < divisions1.get("userDivisions").size(); i++){
				String did =divisions1.get("userDivisions").get(i).getDivisionId();
				History history = new History();
				history.setId(UUID.randomUUID().toString());
				history.setUserIds(userId);
				history.setType("d");
				history.setCurrentFlag("n");
				history.setPreCodeId(did);
				history.setAddUser(sessionInfo.getUserId());
				history.setAddIp(sessionInfo.getIp());
				history.setAddDate(new Date());
				//version
				history.setVersionNO(version);
				this.historyService.insertForVersioin(history);
			}
			
			//new add
			for(int i=0;i<divisions.length;i++){
				History history = new History();
				history.setId(UUID.randomUUID().toString());
				history.setUserIds(userId);
				history.setType("d");
				history.setCurrentFlag("y");
				history.setCodeId(divisions[i]);
				history.setAddUser(sessionInfo.getUserId());
				history.setAddIp(sessionInfo.getIp());
				history.setAddDate(new Date());
				//version
				history.setVersionNO(version);
				this.historyService.insertForVersioin(history);
			}
			
			
			this.user2DivisionService.edit(sessionInfo.getUserId(), userId.trim(), divisions, leaders,mainIndex);
			j.setSuccess(true);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return j;
	}
	
	/**
	 * 根据部门编号获取部门用户信息列表
	 * @param divisionId 部门编号
	 * @param session
	 * @return 部门用户信息列表
	 * @throws PmException
	 */
	@RequestMapping("/doSearch")
	@ResponseBody
	public DataGrid<User2Division> doSearch(@RequestParam("divisionId") String divisionId,
	                                        HttpSession session) throws PmException {
		
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		if (TextUtils.isEmpty(divisionId) || TextUtils.getTrimmedLength(divisionId) == 0) {
			throw new PmException("divisionId不能为空");
		}
		
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<User2Division> result = new DataGrid<User2Division>();
		
		try {
			List<User2Division> list = this.user2DivisionService.listByDivisionIdWithUser(sessionInfo.getUserId(), divisionId);
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
