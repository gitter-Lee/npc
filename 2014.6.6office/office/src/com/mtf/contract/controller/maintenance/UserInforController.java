package com.mtf.contract.controller.maintenance;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.UserInfor;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.impl.UserInforImpl;
import com.mtf.contract.service.UserInforService;
import com.mtf.contract.util.CommonUtil;


@Controller("userInfoController")
@RequestMapping("/maintenance/userInfo")
public class UserInforController {
	
	private static final Logger		logger	= Logger.getLogger(UserInforController.class);
	
	@Autowired
	private UserInforService userinforService;

	@Autowired
	public UserInforService getUserinforService() {
		return userinforService;
	}

	@Autowired
	public void setUserinforService(UserInforService userinforService) {
		this.userinforService = userinforService;
	}
	
	/**
	 * 跳转查询
	 * @param userinfor
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch(UserInfor userinfor,HttpSession session)throws PmException{
	ModelAndView mv = new ModelAndView("maintenance/userInfor/userInfo");
	return mv;
	}
	
	/**
	 * 编辑
	 * @param userinfor
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public Json doEdit(UserInforImpl userInforImpl,HttpSession session)throws PmException{
		Json json = new Json();
		String editState = userInforImpl.getEditState();
		String type = userInforImpl.getType();
		String flag = userInforImpl.getFlag();
		// 添加
		if ("i".equals(editState)) {
			// 创建新的对象查询
			if("s".equals(flag)){
				
				UserInforImpl userInfor = new UserInforImpl();
				userInfor.setType(type);
				userInfor.setFlag(flag);
				userInfor = userinforService.get(userInfor);
				if (null != userInfor) {
					json.setMsg("当前类型值已经存在,请重新填写");
					json.setSuccess(false);
					return json;
				}
			}
			
			
			CommonUtil.setCommonField(userInforImpl, session);
			userinforService.insert(userInforImpl);
			json.setSuccess(true);
		// 修改
		}else if ("u".equals(editState)) {
			userInforImpl.setModifyDate(new Date());
			userinforService.update(userInforImpl);
			json.setSuccess(true);
			
		// 删除
		}else if ("d".equals(editState)) {
			userinforService.delete(userInforImpl);
			json.setSuccess(true);
			json.setMsg("删除成功");
		}{
			
		}
		return json;
		
	}
	
	/**
	 * 查询
	 * @param userInforImpl
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/doSearch")
	@ResponseBody
	public DataGrid<UserInforImpl> doSearch(UserInforImpl userInforImpl,HttpSession session)throws PmException{
		DataGrid<UserInforImpl> list = userinforService.select(userInforImpl);
		return list;
	}
	
	
	

}
