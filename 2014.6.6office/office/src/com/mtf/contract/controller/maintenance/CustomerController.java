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

import com.mtf.contract.editor.DateEditor;
import com.mtf.contract.editor.DoubleEditor;
import com.mtf.contract.editor.IntegerEditor;
import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Customer;
import com.mtf.contract.service.CustomerService;
import com.mtf.contract.util.CommonUtil;
import com.mtf.contract.util.Constants;

import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.util.TextUtils;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.util.TextUtils;
import com.mtf.contract.exception.PmException;


/**
 * Description
 *
 * @author BillQi
 * @version 1.0	2013-7-15	BillQi		created.
 * @version <ver>
 */
@Controller("maintenanceCustomerController")
@RequestMapping("/maintenance/customer")
public class CustomerController {

	private static final Logger		logger	= Logger.getLogger(CustomerController.class);
	
	private CustomerService customerService;
	private MessageSource			messages;
	
	@Autowired
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Autowired
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor("yyyy-MM-dd HH:mm:ss", true));
		binder.registerCustomEditor(Integer.class, new IntegerEditor(true));
		binder.registerCustomEditor(Double.class, new DoubleEditor(true));
	}
	
	/**
	 * 跳转增加
	 * @param session
	 * @return
	 * @throws Exception
	 */
	/*@RequestMapping("/toAdd")
	public String toAdd(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		return "maintenance/customer/add";
	}*/
	
	/**
	 * test
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toAdd")
	public String toAdd(HttpSession session) throws PmException {
		
		return "maintenance/customer/test";
	}
	
	/**
	 * 跳转查找
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toSearch")
	public String toSearch(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		return "maintenance/customer/search";
	}
	
	/**
	 * 增加客户
	 * @param customer
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	@ResponseBody
	public Json doAdd(Customer customer, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		Json j = new Json();
		// validate
		if (TextUtils.isEmpty(customer.getName())) {
			j.setSuccess(false);
			j.setMsg("The name of resource can not be empty!");
			return j;
		}
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {	
			j.setSuccess(false);
			j.setMsg("No Session");
			return j;
		}
		
		try {
			
			customer.setEditState(Constants.INSERT_STATE);
			CommonUtil.setCommonField(customer, session);
			customer = this.customerService.insert(customer);
			j.setSuccess(true);
			j.setObj(customer);
		}
		catch (PmException e) {
			throw e;
		}
		catch (Exception e) {
			throw e;
		}

		return j;
	}
	
	/**
	 * 查询
	 * @param customer
	 * @param session
	 * @return
	 * @throws PmException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/doDataGridSearch", method = RequestMethod.POST)
	@ResponseBody
	 DataGrid<Customer> doDataGridSearch(Customer customer , HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		DataGrid<Customer> result=null;
		try {
			result = this.customerService.select(customer);
			if (result == null) {
				result = new DataGrid<Customer>();
			}
		}
		catch (PmException e) {
			throw e;
		}
		catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	
	/**
	 * 编辑
	 * @param customer
	 * @param session
	 * @return
	 * @throws PmException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit(Customer customer, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		Json j = new Json();
		try {
			customer.setEditState(Constants.UPDATE_STATE);
			CommonUtil.setCommonField(customer, session);
			customer = this.customerService.update(customer);
			j.setSuccess(true);
			j.setObj(customer);
		}
		catch (PmException e) {
			throw e;
		}
		catch (Exception e) {
			throw e;
		}
		return j;
	}
	
	/**
	 * 删除
	 * @param customer
	 * @param session
	 * @return
	 * @throws PmException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/doDelete", method = RequestMethod.GET)
	@ResponseBody
	public Json doDelete(@RequestParam("id") Long id, HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		int result = 0;
		
		Json json = new Json();
		
		try {
			result = customerService.delete(id);
		}
		catch (PmException e) {
			throw e;
		}
		catch (Exception e) {
			throw e;
		}
		if (result != 0) {
			json.setSuccess(true);
			json.setMsg(this.messages.getMessage("p_main.maintenance.customer.controller.message.deletesuccessfully", null, null));
		} else {
			json.setMsg(this.messages.getMessage("p_main.maintenance.customer.controller.message.deletefailed", null, null));
			json.setSuccess(false);
		}
		return json;
	}
}


