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
package com.mtf.contract.controller.contract.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.controller.contract.ContractController;
import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Contract;
import com.mtf.contract.model.Items;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.model.impl.ContractImpl;
import com.mtf.contract.service.ContractService;
import com.mtf.contract.service.ItemsService;
import com.mtf.contract.util.CommonUtil;
import com.mtf.contract.util.Constants;


/**
 * Description
 *
 * @author BillQi
 * @version 1.0	2013-8-14	BillQi		created.
 * @version <ver>
 */
@Controller("manufacturingConsignmentContractController")
@RequestMapping("/contract/manufacturingConsignment")
public class ManufacturingConsignmentContractController extends ContractController{
	
	@Autowired
	private ContractService contractService;

	@Autowired
	private ItemsService itemsService;
	
	@Autowired
	public ItemsService getItemsService() {
		return itemsService;
	}

	@Autowired
	public void setItemsService(ItemsService itemsService) {
		this.itemsService = itemsService;
	}
	@Autowired
	public ContractService getContractService() {
		return contractService;
	}
	@Autowired
	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}
	
	/**
	 * 跳转页面
	 * @return
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch(ContractImpl contractImpl,HttpSession session) {
		contractImpl.setViewPath("contract/manufacturingConsignment/searchManufacturingConsignmentContract");
		return super.toSearch(contractImpl,session);
	}
	
	/**
	 * 跳转编辑
	 * @param contractImpl
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(ContractImpl contractImpl,HttpSession session) throws Exception {
		contractImpl.setViewPath("contract/manufacturingConsignment/editManufacturingConsignmentContract");
		return super.toEdit(contractImpl, session);
	}
	
	/**
	 * 编辑
	 * @param contractImpl
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit (ContractImpl contractImpl, HttpSession session) throws Exception {
		return super.doEdit(contractImpl, session);
	}
	
	/**
	 * 查询
	 * @param form
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<ContractImpl> doSearch(ContractImpl contractImpl, HttpSession session) throws Exception {
		return super.doSearch(contractImpl, session);
	}
	
	/**
	 * 编辑
	 * @param contractImpl
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/submitApprove", method = RequestMethod.GET)
	public ModelAndView submitApprove (ContractImpl contractImpl, HttpSession session) throws Exception {
		contractImpl.setViewPath("contract/manufacturingConsignment/searchManufacturingConsignmentContract");
		return super.submitApprove(contractImpl, session);
	}
	
	/**
	 * 合同变更
	 */
	@RequestMapping(value = "/modifyContract", method = RequestMethod.GET)
	public ModelAndView modifyContract(ContractImpl contractImpl, HttpSession session) {
		contractImpl.setViewPath("contract/manufacturingConsignment/editManufacturingConsignmentContract");
		return super.modifyContract(contractImpl, session);
	}
}
