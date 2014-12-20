package com.mtf.contract.controller.contract.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.controller.contract.ContractController;
import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Items;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.model.impl.BudgetImpl;
import com.mtf.contract.model.impl.ContractImpl;
import com.mtf.contract.service.AttachmentService;
import com.mtf.contract.service.BudgetService;
import com.mtf.contract.service.ContractService;
import com.mtf.contract.service.ItemsService;
import com.mtf.contract.service.common.CommonService;
import com.mtf.contract.util.CommonUtil;
import com.mtf.contract.util.Constants;

@Controller("salesContractController")
@RequestMapping("/contract/sale")
public class ContractSaleController extends ContractController{

	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private ContractService contractService;
	
	@Autowired
	private ItemsService itemsService;
	
	@Autowired
	private CommonService commonService;
	
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
	@Autowired
	public CommonService getCommonService() {
		return commonService;
	}
	@Autowired
	public AttachmentService getAttachmentService() {
		return attachmentService;
	}
	@Autowired
	public void setAttachmentService(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}
	@Autowired
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}

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
		contractImpl.setViewPath("contract/sale/searchSaleContract");
		return super.toSearch(contractImpl,session);
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
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		// 查看历史记录状态是否存在,如果不存在,那么赋默认值
		String flgHistory = contractImpl.getFlgHistory();
		// 查看预算或者决算表类型
		String budgetType=contractImpl.getBudgetType();
		if (null == flgHistory || flgHistory.equals("")) {
			// flgHistory 是否为历史版本(是:y,否:n)
			contractImpl.setFlgHistory("n");
		}
		String userId = sessionInfo.getUserId();
		//  用户等级 1 员工 2 业务经理 3 事业经理 4 总经理
		Integer userLevel = sessionInfo.getUserLever();
		contractImpl.setUserLevel(userLevel);
		List<String> listEmployee = sessionInfo.getListEmployee();
		if (listEmployee == null) {
			listEmployee = new ArrayList<>();
			listEmployee.add(userId);
		}
		// 如果为领导那么查询下属员工
		contractImpl.setListEmployee(listEmployee);
		// 如果为领导那么查询已提交的合同
		// 审批状态集合(saved:已保存, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕)
		List<String> listApproveState = new ArrayList<>();
		if (userLevel.equals(0)) {
			// 如果类型是决算表，只查询合同状态为审批完毕和外方确认状态
			if ("a".equals(budgetType)) {
				listApproveState.add("commit");
				listApproveState.add("confirm");
			}else {
				listApproveState.add("budget");
				listApproveState.add("save");
				listApproveState.add("saved");
				listApproveState.add("submit");
				listApproveState.add("commit");
				listApproveState.add("confirm");
				listApproveState.add("reject1");
				listApproveState.add("approval1");
				listApproveState.add("reject2");
				listApproveState.add("approval2");
				listApproveState.add("reject3");
				listApproveState.add("approval3");
			}
	
		} else if (userLevel.equals(1)) {
			if ("a".equals(budgetType)) {
				listApproveState.add("commit");
				listApproveState.add("confirm");
			}else {
			listApproveState.add("submit");
			listApproveState.add("commit");
			listApproveState.add("confirm");
			listApproveState.add("reject1");
			listApproveState.add("approval1");
			listApproveState.add("reject2");
			listApproveState.add("approval2");
			listApproveState.add("reject3");
			listApproveState.add("approval3");
			}
		} else if (userLevel.equals(2)) {
			if ("a".equals(budgetType)) {
				listApproveState.add("commit");
				listApproveState.add("confirm");
			}else {
			listApproveState.add("submit");
			listApproveState.add("commit");
			listApproveState.add("confirm");
			listApproveState.add("reject1");
			listApproveState.add("approval1");
			listApproveState.add("reject2");
			listApproveState.add("approval2");
			listApproveState.add("reject3");
			listApproveState.add("approval3");
			}
		} else if (userLevel.equals(3)) {
			if ("a".equals(budgetType)) {
				listApproveState.add("commit");
				listApproveState.add("confirm");
			}else {
			listApproveState.add("submit");
			listApproveState.add("commit");
			listApproveState.add("confirm");
			listApproveState.add("reject1");
			listApproveState.add("approval1");
			listApproveState.add("reject2");
			listApproveState.add("approval2");
			listApproveState.add("reject3");
			listApproveState.add("approval3");
			}
		} else if (userLevel.equals(4)) {
			listApproveState.add("commit");
			listApproveState.add("confirm");
		}
		// 每次查询用员工集合是下一级的所有审批的员工编号和本级自己本身员工编号
		contractImpl.setListApproveState(listApproveState);
		DataGrid<ContractImpl> list = new DataGrid<ContractImpl>();
		list = this.contractService.select(contractImpl);
		return list;
	}
	
	/**
	 * 跳转编辑
	 * @param contract
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(ContractImpl contractImpl, HttpSession session) throws Exception {
		new PmException(session);
		String budgetType = contractImpl.getBudgetType();
		String editState = contractImpl.getEditState();
		ModelAndView mv = new ModelAndView("contract/sale/editSaleContract");
		// 创建预算表对象
		BudgetImpl budget=new BudgetImpl();
		// 预决算表表示对象
		BudgetImpl budgetAcc=new BudgetImpl();
		budget.setEditState(editState);
		// u:编辑状态，p:合同预览 ,c:合同复制
		if ("u".equals(editState)|| "p".equals(editState) || "c".equals(editState)) {
			Long contractId = contractImpl.getId();
			String field2=contractImpl.getField2();
			// 预算类型: budget b,核算类型: business accounting a;
			budget.setType("b");
			budget.setContractId(contractId);
			budget = this.getBudgetDetail(budget);
			budget.setEditState(editState);
			contractImpl = contractService.get(contractImpl);
			List<BudgetImpl> listBudget = budgetService.search(budget);
			contractImpl.setListBudget(listBudget);
			// 如果为销货合同,那么添加预算表的搜索
			contractImpl.setField2(field2);
			mv.addObject("listBudget", listBudget);
		}
		contractImpl.setEditState(editState);
		contractImpl.setBudgetType(budgetType);
		mv.addObject("contract", contractImpl);
		if (budgetType == null || budgetType.equals("b")) {
			mv.addObject("budget", budget);
		} else if (budgetType.equals("a")) {
			budget.setType("a");
			mv.addObject("budget", budget);
		}
		return mv;
	}

	/**
	 * 编辑
	 * @param contract
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit (ContractImpl contractImpl, HttpSession session) throws Exception {
		Json j = new Json();
		String editState=contractImpl.getEditState();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		//String signature = sessionInfo.getSignature();
		// c:合同复制状态
		if("c".equals(editState)){
			CommonUtil.setCommonField(contractImpl, session);
			// saved:已保存, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
			contractImpl.setApproveState("saved");
			contractImpl.setApproverId(contractImpl.getUserId());
			contractImpl.setApprover(contractImpl.getModifiedUser());
			contractImpl.setApproverDate(contractImpl.getModifiedDate());
			contractImpl.setApproverId0(contractImpl.getUserId());
			contractImpl.setApprover0(contractImpl.getModifiedUser());
			contractImpl.setApproverDate0(contractImpl.getModifiedDate());
			contractImpl.setAddUser(contractImpl.getUserId());
			contractImpl.setAddDate(contractImpl.getModifiedDate());
			// 签章及公章
			//contractImpl.setSignature(signature);
			// 计算最终审批人等级
			caculateFinalArrrover(contractImpl);
			try{
				//  复用合同变更方法
				ModelAndView mv = modifyContract(contractImpl, session);
				ModelMap map = mv.getModelMap();
				contractImpl = (ContractImpl) map.get("contract");
				j.setObj(contractImpl);
			}catch(Exception e){
				j.setSuccess(false);
				return j;
			}
			j.setSuccess(true);
			return j;
		}else {
			return super.doEdit(contractImpl, session);
		}
	
	}
	
	/**
	 * 预算编辑
	 * @param contract
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doEditAccounting", method = RequestMethod.POST)
	@ResponseBody
	public Json doEditAccounting (BudgetImpl budgetImpl, HttpSession session) throws Exception {
		Json j = new Json();
		String editState = budgetImpl.getEditState();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		//String signature = sessionInfo.getSignature();
		try {
			if ("i".equals(editState)) {
				ContractImpl contractImpl=new ContractImpl();
				contractImpl.setEditState("i");
				CommonUtil.setCommonField(budgetImpl, session);
				CommonUtil.setCommonField(contractImpl, session);
				// budget:已提交预算, saved:已提交审批, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
				budgetImpl.setApproveState("save");
				budgetImpl.setApprover(budgetImpl.getModifiedUser());
				budgetImpl.setApproverDate(budgetImpl.getModifiedDate());
				budgetImpl.setApprover0(budgetImpl.getModifiedUser());
				budgetImpl.setApproverDate0(budgetImpl.getModifiedDate());
				contractImpl.setApproveState("save");
				contractImpl.setFlag("s");
				contractImpl.setContractNo(budgetImpl.getContractNo());
				contractImpl.setApprover(budgetImpl.getModifiedUser());
				contractImpl.setApproverDate(budgetImpl.getModifiedDate());
				contractImpl.setApprover0(budgetImpl.getModifiedUser());
				contractImpl.setApproverId(budgetImpl.getUserId());
				contractImpl.setApproverId0(budgetImpl.getUserId());
				contractImpl.setApproverDate0(budgetImpl.getModifiedDate());
				//contractImpl.setSignature(signature);
				if(budgetImpl.getType().equals("b")){
				budgetService.insert(budgetImpl,contractImpl);
				}else if(budgetImpl.getType().equals("a")){
					budgetService.insert(budgetImpl);
				}
			} else if ("u".equals(editState)) {
				CommonUtil.setCommonField(budgetImpl, session);
				budgetImpl.setApproveState("budget");
				budgetImpl.setApprover(budgetImpl.getModifiedUser());
				budgetImpl.setApproverDate(budgetImpl.getModifiedDate());
				budgetService.update(budgetImpl);
			} else if ("d".equals(editState)) {
				budgetService.delete(budgetImpl);
			} else if ("su".equals(editState)) {
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
				//contractImpl.setSignature(signature);
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
	
	/**
	 * 审批
	 * @param contract
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/submitApprove", method = RequestMethod.GET)
	public ModelAndView submitApprove (ContractImpl contractImpl, HttpSession session) throws Exception {
		String approveState = contractImpl.getApproveState();
		contractImpl.setViewPath("contract/sale/searchSaleContract");
		contractImpl.setBudgetType("b");
		super.submitApprove(contractImpl, session);
		BudgetImpl budgetImpl = new BudgetImpl();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Integer userLevel = sessionInfo.getUserLever();
		// 如果为一级审批状态那么复制预算到决算管理
		if (approveState.equals("approval") && userLevel.equals(1)) {
			budgetImpl.setContractId(contractImpl.getId());
			budgetService.copyBudget2Current(budgetImpl);
		}
		return this.toSearch(contractImpl, session);
	}
	
	/**
	 * 取得预算及预算对应的明细
	 * @param budget
	 * @return
	 * @throws Exception
	 */
	private BudgetImpl getBudgetDetail(BudgetImpl budget) throws Exception{
		Long budgetId = budget.getId();
		if (budgetId != null) {
			Items items = new Items();
			items.setBudgetId(budgetId);
			// 搜索预算类型货品列表
			// 预算(原辅材料(budget row):br
			items.setTagItem("br");
			List<Items> listItemsAcc = itemsService.find(items);
			budget.setListItemsAcc(listItemsAcc);
			// 包装材料(budget package):bp
			items.setTagItem("bp");
			List<Items> listItemsPack = itemsService.find(items);
			budget.setListItemsPack(listItemsPack);
		}
		return budget;
	}
	
	/**
	 * 合同变更 ||合同复制
	 */
	@RequestMapping(value = "/modifyContract", method = RequestMethod.GET)
	public ModelAndView modifyContract(ContractImpl contractImpl, HttpSession session) {
		String editState=contractImpl.getEditState();
		ModelAndView mv= new ModelAndView("contract/sale/editSaleContract");
		String budgetType=contractImpl.getBudgetType();
		try {
			// 预算表对象
			BudgetImpl budget =new BudgetImpl();
			Long contractId = contractImpl.getId();
			budget.setContractId(contractId);
			budget.setType("b");
			List<BudgetImpl> listBudget = budgetService.search(budget);
			// 预算及预算对应的明细列表
			List<BudgetImpl> listBudgetDetail = new ArrayList<>();
			for (int i = 0; i < listBudget.size(); i++) {
				budget = listBudget.get(i);
				// 取得预算及预算对应的明细
				budget = getBudgetDetail(budget);
				listBudgetDetail.add(budget);
			}
			// 合同复制状态
			if (!"c".equals(editState)) {
				contractImpl = contractService.get(contractImpl);
				contractImpl.setEditState("i");
				contractImpl.setApproveState("saved");
				CommonUtil.setCommonField(contractImpl, session);
			}
			Items items = new Items();
			items.setContractId(contractId);
			// 搜索合同类型货品列表
			items.setTagItem("c");
			List<Items> listItems = itemsService.find(items);
			contractImpl.setListItems(listItems);
			// budget:已提交预算, saved:已提交审批, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
			budgetService.insert(contractImpl, listBudgetDetail);
			contractImpl.setEditState("u");
			contractImpl.setBudgetType(budgetType);
			mv.addObject("contract", contractImpl);
		} catch (PmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	

}