package com.mtf.contract.controller.contract.impl;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mtf.contract.controller.contract.ContractController;
import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.impl.BudgetImpl;
import com.mtf.contract.model.impl.ContractImpl;
import com.mtf.contract.util.CommonUtil;
import com.mtf.contract.model.Budget;
import com.mtf.contract.service.BudgetService;

/*
**********************************************
 * 项目名称：contract(合同管理系统)
 * 模块名称：控制层 -> 合同
 * 作者:     Auto
 * 日期:     2013-09-26
**********************************************
*/
@Controller("budgetController")
@RequestMapping("/contract/budget")
public class BudgetController {

	@Autowired
	private BudgetService budgetService;

	@Autowired
	public BudgetService getBudgetService() {
		return budgetService;
	}

	@Autowired
	public void setBudgetService(BudgetService budgetService) {
		this.budgetService = budgetService;
	}

	/**
	 * 跳转页面
	 * @return
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch(BudgetImpl budgetImpl,HttpSession session) {
		ModelAndView mv=new ModelAndView("contract/sale/searchSaleBudgetContract");
		mv.addObject("contract", budgetImpl);
		return mv;
	}

	/**
	 * 查询
	 * @param form
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="doSearch", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<BudgetImpl> doSearch(BudgetImpl budgetImpl, HttpSession session) throws Exception {
		new PmException(session);
		DataGrid<BudgetImpl> list = new DataGrid<BudgetImpl>();
		list = this.budgetService.select(budgetImpl);
		return list;
	}

	/**
	 * 跳转编辑
	 * @param budgetImpl
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(BudgetImpl budgetImpl, HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView("contract/sale/editBudgetSale");
		String editState = budgetImpl.getEditState();
		String type = budgetImpl.getType();
		String contractNo=budgetImpl.getContractNo();
		BudgetImpl budgetAcc=new BudgetImpl();
		budgetAcc.setType("b");
		if (editState.equals("u")) {
			budgetImpl = budgetService.getBudgetDetail(budgetImpl);
		}
		// 查询预算表数据
		Long superPudgetId = budgetImpl.getBudgetId();
		if (null != superPudgetId && editState.equals("u")&&"a".equals(type)) {
			budgetAcc.setId(budgetImpl.getBudgetId());
			budgetAcc = budgetService.getBudgetDetail(budgetAcc);
			budgetAcc.setContractNo(contractNo);
			mv.addObject("budgetAcc", budgetAcc);
		}
		budgetImpl.setEditState(editState);
		budgetImpl.setType(type);
		budgetImpl.setContractNo(contractNo);
		mv.addObject("budget", budgetImpl);
		return mv;
	}
	
	@RequestMapping("/doEdit")
	@ResponseBody
	public Json doEdit(BudgetImpl budgetImpl,HttpSession session) throws Exception{
		Json j = new Json();
		String editState = budgetImpl.getEditState();
		try {
			if ("i".equals(editState)) {
				CommonUtil.setCommonField(budgetImpl, session);
				// budget:已提交预算, saved:已提交审批, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
				budgetImpl.setApproveState("save");
				budgetImpl.setApprover(budgetImpl.getModifiedUser());
				budgetImpl.setApproverDate(budgetImpl.getModifiedDate());
				budgetImpl.setApprover0(budgetImpl.getModifiedUser());
				budgetImpl.setApproverDate0(budgetImpl.getModifiedDate());
				budgetService.insert(budgetImpl);
			} else if ("u".equals(editState)) {
				CommonUtil.setCommonField(budgetImpl, session);
				budgetImpl.setApproveState("budget");
				budgetImpl.setApprover(budgetImpl.getModifiedUser());
				budgetImpl.setApproverDate(budgetImpl.getModifiedDate());
				budgetService.update(budgetImpl);
			} else if ("d".equals(editState)) {
				CommonUtil.setCommonField(budgetImpl, session);
				// budget:已提交预算, saved:已提交审批, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
				budgetImpl.setApprover(budgetImpl.getModifiedUser());
				budgetImpl.setApproverDate(budgetImpl.getModifiedDate());
				budgetService.delete(budgetImpl);
			} else if ("su".equals(editState)){
				ContractImpl contractImpl=new ContractImpl();
				CommonUtil.setCommonField(budgetImpl, session);
				// budget:已提交预算, saved:已提交审批, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
				budgetImpl.setApproveState("budget");
				budgetImpl.setApprover(budgetImpl.getModifiedUser());
				budgetImpl.setApproverDate(budgetImpl.getModifiedDate());
				budgetImpl.setApprover0(budgetImpl.getModifiedUser());
				budgetImpl.setApproverDate0(budgetImpl.getModifiedDate());
				contractImpl.setApproveState("budget");
				contractImpl.setFlag("s");
				contractImpl.setContractNo(budgetImpl.getContractNo());
				contractImpl.setApprover(budgetImpl.getModifiedUser());
				contractImpl.setApproverDate(budgetImpl.getModifiedDate());
				contractImpl.setApprover0(budgetImpl.getModifiedUser());
				contractImpl.setApproverId(budgetImpl.getUserId());
				contractImpl.setApproverId0(budgetImpl.getUserId());
				contractImpl.setApproverDate0(budgetImpl.getModifiedDate());
				if(budgetImpl.getType().equals("b")){
				budgetService.insert(budgetImpl,contractImpl);
				}else if(budgetImpl.getType().equals("a")){
					budgetService.insert(budgetImpl);
				}
			}
			j.setSuccess(true);
			j.setObj(budgetImpl);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}

}

