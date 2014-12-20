package com.mtf.contract.controller.contract;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Items;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.model.impl.BudgetImpl;
import com.mtf.contract.model.impl.ContractImpl;
import com.mtf.contract.service.AttachmentService;
import com.mtf.contract.service.ContractService;
import com.mtf.contract.service.ItemsService;
import com.mtf.contract.service.common.CommonService;
import com.mtf.contract.util.CommonUtil;
import com.mtf.contract.util.Constants;

@Controller("contractController")
public class ContractController {

	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private ContractService contractService;
	
	@Autowired
	private ItemsService itemsService;
	
	@Autowired
	private CommonService commonService;
	
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
	public ModelAndView toSearch(ContractImpl contractImpl,HttpSession session) {
		ModelAndView mv=new ModelAndView(contractImpl.getViewPath());
		mv.addObject("contract", contractImpl);
		return mv;
	}

	/**
	 * 查询
	 * @param contractImpl
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public DataGrid<ContractImpl> doSearch(ContractImpl contractImpl, HttpSession session) throws Exception {
		new PmException(session);
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		// 查看历史记录状态是否存在,如果不存在,那么赋默认值
		String flgHistory = contractImpl.getFlgHistory();
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
	
		} else if (userLevel.equals(1)) {
			listApproveState.add("submit");
			listApproveState.add("commit");
			listApproveState.add("confirm");
			listApproveState.add("reject1");
			listApproveState.add("approval1");
			listApproveState.add("reject2");
			listApproveState.add("approval2");
			listApproveState.add("reject3");
			listApproveState.add("approval3");
		} else if (userLevel.equals(2)) {
			listApproveState.add("submit");
			listApproveState.add("commit");
			listApproveState.add("confirm");
			listApproveState.add("reject1");
			listApproveState.add("approval1");
			listApproveState.add("reject2");
			listApproveState.add("approval2");
			listApproveState.add("reject3");
			listApproveState.add("approval3");
		} else if (userLevel.equals(3)) {
			listApproveState.add("submit");
			listApproveState.add("commit");
			listApproveState.add("confirm");
			listApproveState.add("reject1");
			listApproveState.add("approval1");
			listApproveState.add("reject2");
			listApproveState.add("approval2");
			listApproveState.add("reject3");
			listApproveState.add("approval3");
		} else if (userLevel.equals(4)) {
			listApproveState.add("commit");
		}
		// 每次查询用员工集合是下一级的所有审批的员工编号和本级自己本身员工编号
		contractImpl.setListApproveState(listApproveState);
		DataGrid<ContractImpl> list = new DataGrid<ContractImpl>();
		list = this.contractService.select(contractImpl);
		return list;
	}

	/**
	 * 跳转编辑
	 * @param contractImpl
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public ModelAndView toEdit(ContractImpl contractImpl, HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView(contractImpl.getViewPath());
		String editState = contractImpl.getEditState();
		// 创建预算表对象
		BudgetImpl budget=new BudgetImpl();
		budget.setType("b");
		budget.setEditState(editState);
		// 创建核算表对象
		BudgetImpl budgetAcc=new BudgetImpl();
		budgetAcc.setType("a");
		budgetAcc.setEditState(editState);
		String field2=contractImpl.getField2();
		// u:合同修改状态，p:合同预览状态，c:合同复制状态
		if ("u".equals(editState)||"p".equals(editState) || "c".equals(editState)) {
			contractImpl = contractService.get(contractImpl);
			Long contractId = contractImpl.getId();
			Items items = new Items();
			items.setContractId(contractId);
			// 搜索合同类型货品列表
			items.setTagItem("c");
			List<Items> listItems = itemsService.find(items);
			contractImpl.setListItems(listItems);
		}
		contractImpl.setEditState(editState);
		contractImpl.setField2(field2);
		mv.addObject("contract", contractImpl);
		return mv;
	}

	/**
	 * 编辑
	 * @param contractImpl
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public Json doEdit (ContractImpl contractImpl, HttpSession session) throws Exception {
		Json j = new Json();
		String editState = contractImpl.getEditState();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		//String signature = sessionInfo.getSignature();
		try {
			// 编辑状态(增:i,删:d,改:u,查:s,提交:su(submit), 审批:a(approval))
			if ("i".equals(editState)) {
				CommonUtil.setCommonField(contractImpl, session);
				// saved:已保存, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
				contractImpl.setApproveState("saved");
				contractImpl.setApproverId(contractImpl.getUserId());
				contractImpl.setApprover(contractImpl.getModifiedUser());
				contractImpl.setApproverDate(contractImpl.getModifiedDate());
				contractImpl.setApproverId0(contractImpl.getUserId());
				contractImpl.setApprover0(contractImpl.getModifiedUser());
				contractImpl.setApproverDate0(contractImpl.getModifiedDate());
				// 签章及公章
				//contractImpl.setSignature(signature);
				// 计算最终审批人等级
				caculateFinalArrrover(contractImpl);
				contractService.insert(contractImpl);
			} else if ("u".equals(editState)) {
				CommonUtil.setCommonField(contractImpl, session);
				contractImpl.setApproveState("saved");
				contractImpl.setApproverId(contractImpl.getUserId());
				contractImpl.setApprover(contractImpl.getModifiedUser());
				contractImpl.setApproverDate(contractImpl.getModifiedDate());
				//contractImpl.setSignature(signature);
				// 计算最终审批人等级
				caculateFinalArrrover(contractImpl);
				contractService.update(contractImpl);
			} else if ("d".equals(editState)) {
				contractService.delete(contractImpl);
			} else if ("su".equals(editState)) {
				// saved:已保存, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
				if (contractImpl.getId()==null) {
					contractImpl.setEditState("i");
					CommonUtil.setCommonField(contractImpl, session);
					contractImpl.setApproveState("submit");
					contractImpl.setApproverId(contractImpl.getUserId());
					contractImpl.setApprover(contractImpl.getModifiedUser());
					contractImpl.setApproverDate(contractImpl.getModifiedDate());
					contractImpl.setApproverId0(contractImpl.getUserId());
					contractImpl.setApprover0(contractImpl.getModifiedUser());
					contractImpl.setApproverDate0(contractImpl.getModifiedDate());
					//contractImpl.setSignature(signature);
					// 计算最终审批人等级
					caculateFinalArrrover(contractImpl);
					contractService.insert(contractImpl);
				} else {
					CommonUtil.setCommonField(contractImpl, session);
					contractImpl.setApproveState("submit");
					contractImpl.setApproverId(contractImpl.getUserId());
					contractImpl.setApprover(contractImpl.getModifiedUser());
					contractImpl.setApproverDate(contractImpl.getModifiedDate());
					//contractImpl.setSignature(signature);
					// 计算最终审批人等级
					caculateFinalArrrover(contractImpl);
					contractService.update(contractImpl);
				}
			}
			j.setSuccess(true);
			j.setObj(contractImpl);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}

	/**
	 * 审批
	 * @param contractImpl
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public ModelAndView submitApprove (ContractImpl contractImpl, HttpSession session) throws Exception {
		try {
			CommonUtil.setCommonField(contractImpl, session);
			SessionInfo sessionInfo = (SessionInfo) contractImpl.getObject();
			//String signature = sessionInfo.getSignature();
			//String cachet = sessionInfo.getCachet();
			// 取得用户等级
			Integer userLevel = sessionInfo.getUserLever();
			contractImpl.setApprover(contractImpl.getModifiedUser());
			contractImpl.setApproverId(contractImpl.getUserId());
			contractImpl.setApproverDate(contractImpl.getModifiedDate());
			//避免中文乱码
			String rejectReason = contractImpl.getRejectReason();
			if (rejectReason != null) {
				byte[] rejectReasonByte = rejectReason.getBytes("ISO-8859-1"); 
				rejectReason = new String(rejectReasonByte,"UTF-8"); 
				contractImpl.setRejectReason(rejectReason);
			}
			String approveState = contractImpl.getApproveState();
			contractImpl.setApproveState(approveState + userLevel);
			// 取得需要赋值的审批人字段
			Class<?> clazz = contractImpl.getClass();
			Class<?> clazzSuper = clazz.getSuperclass();
			Field approver = clazzSuper.getDeclaredField("approver" + userLevel);
			Field approverDate = clazzSuper.getDeclaredField("approverDate" + userLevel);
			Field approverId = clazzSuper.getDeclaredField("approverId" + userLevel);
			approver.setAccessible(true);
			approverDate.setAccessible(true);
			approverId.setAccessible(true);
			approver.set(contractImpl, contractImpl.getApprover());
			approverDate.set(contractImpl, contractImpl.getApproverDate());
			approverId.set(contractImpl, contractImpl.getUserId());
			//contractImpl.setSignature(signature);
			//contractImpl.setCachet(cachet);
			if(sessionInfo.getUserLever().toString().equals(contractImpl.getFinalApprover()) ){
				contractImpl.setApproveState("commit");
			}
			contractService.update(contractImpl);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return this.toSearch(contractImpl,session);
	}
	
	/**
	 * 计算最终审批人等级
	 * @param contract
	 */
	public void caculateFinalArrrover(ContractImpl contractImpl) {
		Integer finalApprover = 1;
		// 计算当前最终审批人等级
		String totalValue1 = contractImpl.getTotalValue();
		if (!"".equals(totalValue1)) {
			Double totalValue2 = Double.valueOf(contractImpl.getTotalValue());
			// 单笔发票100万元以内,最终审批人为业务经理
			if (totalValue2 < 1000000) {
				finalApprover = 1;
				// 单笔发票100-300万元,最终审批人为事务部经理
			} else if (totalValue2 >= 1000000 && totalValue2 < 3000000) {
				finalApprover = 2;
				// 单笔发票300万以上,最终审批人为总经理
			} else if (totalValue2 >= 3000000) {
				finalApprover = 3;
			}
			contractImpl.setFinalApprover(String.valueOf(finalApprover));
		}
	}
	
	
	/**
	 * 合同变更
	 */
	@RequestMapping(value = "/modifyContract", method = RequestMethod.GET)
	public ModelAndView modifyContract(ContractImpl contractImpl, HttpSession session) {
		ModelAndView mv = new ModelAndView(contractImpl.getViewPath());
		try {
			contractImpl = contractService.get(contractImpl);
			// 决算表对象
			Long contractId = contractImpl.getId();
			Items items = new Items();
			items.setContractId(contractId);
			// 搜索合同类型货品列表
			List<Items> listItems = itemsService.find(items);
			contractImpl.setListItems(listItems);
			contractImpl.setEditState("i");
			contractImpl.setApproveState("saved");
			CommonUtil.setCommonField(contractImpl, session);
			contractService.insert(contractImpl, listItems);
			// budget:已提交预算, saved:已提交审批, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
			contractImpl.setEditState("u");
			mv.addObject("contract", contractImpl);
		} catch (PmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
}
