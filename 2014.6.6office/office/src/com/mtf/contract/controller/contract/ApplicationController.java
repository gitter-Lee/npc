package com.mtf.contract.controller.contract;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.exception.PmException;

import com.mtf.contract.model.Items;
import com.mtf.contract.model.Payment;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.model.impl.ApplicationImpl;
import com.mtf.contract.model.impl.PaymentImpl;
import com.mtf.contract.service.ApplicationService;
import com.mtf.contract.service.ContractService;
import com.mtf.contract.service.ItemsService;
import com.mtf.contract.service.PaymentService;
import com.mtf.contract.service.common.CommonService;
import com.mtf.contract.util.CommonUtil;
import com.mtf.contract.util.Constants;

@Controller("applicationController")
@RequestMapping("/contract/application")
public class ApplicationController extends ContractController{

	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private ItemsService itemsService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	public CommonService getCommonService() {
		return commonService;
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
	
	
	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}
	@Autowired
	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}
	
	/**
	 * 跳转编辑
	 * @param invoiceId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(ApplicationImpl applicationImpl,HttpSession session) throws Exception {
		applicationImpl.setViewPath("base/invoice/editApplicationForm");
		ModelAndView mv = new ModelAndView(applicationImpl.getViewPath());
		String editState = applicationImpl.getEditState();
		String field2=applicationImpl.getField2();
		if ("u".equals(editState) || "ui".equals(editState) ||  "p".equals(editState)) {
			applicationImpl = applicationService.get(applicationImpl);
			
			if ("ui".equals(editState)) {
				//applicationImpl.setId(null);
				editState = "u";
			}
		}
		applicationImpl.setEditState(editState);
		applicationImpl.setField2(field2);
		mv.addObject("application", applicationImpl);
		return mv;
	}
	
	@RequestMapping("/toEditPayment")
	public ModelAndView toEditPayment(ApplicationImpl aplicationImpl,HttpSession session) throws Exception {
		
		aplicationImpl.setViewPath("base/payment/editPaymentForm");
	
		ModelAndView mv = new ModelAndView(aplicationImpl.getViewPath());
		String editState = aplicationImpl.getEditState();
		String field2=aplicationImpl.getField2();
		if ("pay".equals(editState) || "p".equals(editState)) {
			aplicationImpl = applicationService.get(aplicationImpl);
		}
		aplicationImpl.setEditState(editState);
		aplicationImpl.setField2(field2);
		mv.addObject("application", aplicationImpl);
		return mv;
	}
	
	/**
	 * 付款管理表编辑
	 * @param contract
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doEditPayment", method = RequestMethod.POST)
	@ResponseBody
	public Json doEditPayment (ApplicationImpl applicationImpl, HttpSession session) throws Exception {
		Json j = new Json();
		String editState = applicationImpl.getEditState();
		
		String dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		try {
			CommonUtil.setCommonField(applicationImpl, session);
			applicationImpl.setApprover(applicationImpl.getModifiedUser());
			applicationImpl.setApproverId(applicationImpl.getUserId());
			applicationImpl.setApproverDate(applicationImpl.getModifiedDate());
			applicationImpl.setPayUser(applicationImpl.getModifiedUser());
			applicationImpl.setPayDate(applicationImpl.getModifiedDate());
			applicationImpl.setAddDateContract(dateStr);			
			applicationImpl.setPayDate(applicationImpl.getModifiedDate());
			
			//判断是否预付款完成
			String paymentType = applicationImpl.getPaymentType();
			int paymentFlag = applicationImpl.getPaymentFlag();
			
			if("a".equals(paymentType) && 1==paymentFlag){
				applicationImpl.setAdvanceFlag(1);
			}
			//更新
			applicationService.update(applicationImpl);
			//拷贝
			PaymentImpl pi=new PaymentImpl();
			BeanUtils.copyProperties(applicationImpl,pi);
			paymentService.insert(pi);
			j.setSuccess(true);
			j.setObj(applicationImpl);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
	
	/**
	 * 跳转审批
	 * @param invoiceId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/topreview")
	public ModelAndView topreview(ApplicationImpl applicationImpl,HttpSession session) throws Exception {
		//contractImpl.setViewPath("contract/clothesPurchase/hello");
		applicationImpl.setViewPath("base/invoice/previewApplicationForm");
		//return super.toEdit(applicationImpl, session);
		ModelAndView mv = new ModelAndView(applicationImpl.getViewPath());
		String editState = applicationImpl.getEditState();
		String field2=applicationImpl.getField2();
		
			applicationImpl = applicationService.get(applicationImpl);
			Long contractId = applicationImpl.getId();
			// 如果为销货合同,那么添加预算表的搜索
			String flag = applicationImpl.getFlag();
			
		//applicationImpl.setEditState(editState);
		applicationImpl.setField2(field2);
		mv.addObject("application", applicationImpl);
		return mv;
	}
	/**
	 * 跳转页面
	 * @return
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch(ApplicationImpl applicationImpl,HttpSession session) {
		applicationImpl.setViewPath("base/invoice/searchApplication");
		ModelAndView mv=new ModelAndView(applicationImpl.getViewPath());
		mv.addObject("application", applicationImpl);
		return mv;
	}

	/**
	 * 申请表编辑
	 * @param contract
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doEditApplication", method = RequestMethod.POST)
	@ResponseBody
	public Json doEditApplication (ApplicationImpl applicationImpl, HttpSession session) throws Exception {
		
		String dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

		Json j = new Json();
		String editState = applicationImpl.getEditState();
		try {
			if ("i".equals(editState) || "ui".equals(editState)) {
				CommonUtil.setCommonField(applicationImpl, session);
				// budget:已提交预算, saved:已提交审批, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
				applicationImpl.setApproveState("saved");
				applicationImpl.setApprover(applicationImpl.getModifiedUser());
				applicationImpl.setApproverId(applicationImpl.getUserId());
				applicationImpl.setApproverDate(applicationImpl.getModifiedDate());
				applicationImpl.setApproverId0(applicationImpl.getUserId());
				applicationImpl.setApprover0(applicationImpl.getModifiedUser());
				applicationImpl.setApproverDate0(applicationImpl.getModifiedDate());
				applicationImpl.setAddDateContract(dateStr);
				//判断applicationType和paymentType
				commonSet(applicationImpl);
				
				applicationService.insert(applicationImpl);
			} else if ("u".equals(editState)) {
				CommonUtil.setCommonField(applicationImpl, session);
				//applicationImpl.setApproveState("budget");
				applicationImpl.setApproveState("saved");
				applicationImpl.setApprover(applicationImpl.getModifiedUser());
				applicationImpl.setApproverId(applicationImpl.getUserId());
				applicationImpl.setApproverDate(applicationImpl.getModifiedDate());
				applicationImpl.setAddDateContract(dateStr);
				
				
				//判断applicationType和paymentType
				commonSet(applicationImpl);
				applicationService.update(applicationImpl);
			} else if ("d".equals(editState)) {
				applicationService.delete(applicationImpl);
			} else if ("su".equals(editState)) {
				// saved:已保存, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
				if (applicationImpl.getId()==null) {
					applicationImpl.setEditState("i");
					CommonUtil.setCommonField(applicationImpl, session);
					applicationImpl.setApproveState("submit");
					applicationImpl.setApproverId(applicationImpl.getUserId());
					applicationImpl.setApprover(applicationImpl.getModifiedUser());
					applicationImpl.setApproverDate(applicationImpl.getModifiedDate());
					applicationImpl.setApproverId0(applicationImpl.getUserId());
					applicationImpl.setApprover0(applicationImpl.getModifiedUser());
					applicationImpl.setApproverDate0(applicationImpl.getModifiedDate());
					applicationImpl.setAddDateContract(dateStr);
					//判断applicationType和paymentType
					commonSet(applicationImpl);
					applicationService.insert(applicationImpl);
					
				} else {
					CommonUtil.setCommonField(applicationImpl, session);
					applicationImpl.setApproveState("submit");
					applicationImpl.setApproverId(applicationImpl.getUserId());
					applicationImpl.setApprover(applicationImpl.getModifiedUser());
					applicationImpl.setApproverDate(applicationImpl.getModifiedDate());
					applicationImpl.setAddDateContract(dateStr);
					
					//判断applicationType和paymentType
					commonSet(applicationImpl);
					applicationService.update(applicationImpl);
					
				}
			}else if("pay".equals(editState)){
				applicationImpl.setPayUser(applicationImpl.getModifiedUser());
				applicationImpl.setPayDate(applicationImpl.getModifiedDate());
				applicationService.updatePayment(applicationImpl);
			}
			j.setSuccess(true);
			j.setObj(applicationImpl);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
	
	/**
	 * 申请表编辑
	 * @param contract
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doPayment", method = RequestMethod.POST)
	@ResponseBody
	public Json doPayment (ApplicationImpl applicationImpl, HttpSession session) throws Exception {
		
		String dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		
		Json j = new Json();
		String editState = applicationImpl.getEditState();
		try {
		
				CommonUtil.setCommonField(applicationImpl, session);
				// budget:已提交预算, saved:已提交审批, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕
				applicationImpl.setPayUser(applicationImpl.getModifiedUser());
				applicationImpl.setPayDate(applicationImpl.getModifiedDate());
				applicationService.updatePayment(applicationImpl);
			
			
			j.setSuccess(true);
			j.setObj(applicationImpl);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
	
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<ApplicationImpl> doSearch(ApplicationImpl applicationImpl, HttpSession session) throws Exception {
		//return super.doSearch(contractImpl, session);
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		String userId = sessionInfo.getUserId();
		//  用户等级 1 员工 2 业务经理 3 事业经理 4 总经理
		Integer userLevel = sessionInfo.getUserLever();
		applicationImpl.setUserLevel(userLevel);
		List<String> listEmployee = sessionInfo.getListEmployee();
		if (listEmployee == null) {
			listEmployee = new ArrayList<>();
			listEmployee.add(userId);
		}
		// 如果为领导那么查询下属员工
		applicationImpl.setListEmployee(listEmployee);
		// 如果为领导那么查询已提交的合同
		// 审批状态集合(saved:已保存, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕)
		List<String> listApproveState = new ArrayList<>();
		if (userLevel.equals(0)) {
			listApproveState.add("budget");
			listApproveState.add("saved");
			listApproveState.add("submit");
			listApproveState.add("reject1");
			listApproveState.add("approval1");
			listApproveState.add("reject2");
			listApproveState.add("approval2");
			listApproveState.add("reject3");
			listApproveState.add("approval3");
			listApproveState.add("paying");
			listApproveState.add("payed");
		} else if (userLevel.equals(1)) {
			listApproveState.add("submit");
			listApproveState.add("approval1");
			listApproveState.add("approval2");
			listApproveState.add("reject2");
			listApproveState.add("reject3");
			listApproveState.add("approval3");
		} else if (userLevel.equals(2)) {
			listApproveState.add("approval1");
			listApproveState.add("approval2");
			listApproveState.add("approval3");
			listApproveState.add("reject3");
		} else if (userLevel.equals(3)) {
			listApproveState.add("approval1");
			listApproveState.add("approval2");
			listApproveState.add("approval3");
			listApproveState.add("reject2");
			listApproveState.add("reject3");
		} else if(userLevel.equals(4)) {
			listApproveState.add("approval1");
			listApproveState.add("approval2");
			listApproveState.add("approval3");
			listApproveState.add("paying");
			listApproveState.add("payed");
		}
		// 每次查询用员工集合是下一级的所有审批的员工编号和本级自己本身员工编号
		applicationImpl.setListApproveState(listApproveState);
		DataGrid<ApplicationImpl> list = new DataGrid<ApplicationImpl>();
		list = this.applicationService.select(applicationImpl);
		return list;
	}
	
	/**
	 * 审批
	 * @param contract
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/submitApprove", method = RequestMethod.GET)
	public ModelAndView submitApprove (ApplicationImpl applicationImpl, HttpSession session) throws Exception {
		applicationImpl.setViewPath("base/invoice/searchApplication");
		//return super.submitApprove(applicationImpl, session);
		String dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		try {
			CommonUtil.setCommonField(applicationImpl, session);
			SessionInfo sessionInfo = (SessionInfo) applicationImpl.getObject();
			// 取得用户等级
			Integer userLevel = sessionInfo.getUserLever();
			applicationImpl.setApprover(applicationImpl.getModifiedUser());
			applicationImpl.setApproverId(applicationImpl.getUserId());
			applicationImpl.setApproverDate(applicationImpl.getModifiedDate());
			//避免中文乱码
			String rejectReason = applicationImpl.getRejectReason();
			if (rejectReason != null) {
				byte[] rejectReasonByte = rejectReason.getBytes("ISO-8859-1"); 
				rejectReason = new String(rejectReasonByte,"UTF-8"); 
				applicationImpl.setRejectReason(rejectReason);
			}
			String approveState = applicationImpl.getApproveState();
			applicationImpl.setApproveState(approveState + userLevel);
			// 取得需要赋值的审批人字段
			Class<?> clazz = applicationImpl.getClass();
			Class<?> clazzSuper = clazz.getSuperclass();
			Field approver = clazzSuper.getDeclaredField("approver" + userLevel);
			Field approverDate = clazzSuper.getDeclaredField("approverDate" + userLevel);
			Field approverId = clazzSuper.getDeclaredField("approverId" + userLevel);
			approver.setAccessible(true);
			approverDate.setAccessible(true);
			approverId.setAccessible(true);
			approver.set(applicationImpl, applicationImpl.getApprover());
			approverDate.set(applicationImpl, applicationImpl.getApproverDate());
			approverId.set(applicationImpl, applicationImpl.getUserId());
			applicationImpl.setAddDateContract(dateStr);
			//
			applicationImpl.setPaymentFlag(0);
			
			applicationImpl.setMpPayment("");
			applicationImpl.setMpUnpaidAmount("");
			applicationImpl.setCpPayment("");
			applicationImpl.setCpUnpaidAmount("");
			applicationImpl.setImportPayment("");
			applicationImpl.setImportUnpaidAmount("");
			applicationService.update(applicationImpl);
			
				
			
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return this.toSearch(applicationImpl,session);
	}
	//共通方法——判断单据类型和付款类型
	private void commonSet(ApplicationImpl applicationImpl){
		String mpFaceAmount = applicationImpl.getMpFaceAmount();
		String mpAdvancePayment = applicationImpl.getMpAdvancePayment();
		String cpFaceAmount = applicationImpl.getCpFaceAmount();
		String cpAdvancePayment = applicationImpl.getCpAdvancePayment();
		String importContractAmount = applicationImpl.getImportContractAmount();
		String importAdvancePayment = applicationImpl.getImportAdvancePayment();
		
		if(mpFaceAmount != "" && mpFaceAmount != null || (mpAdvancePayment != "" && mpFaceAmount != null)){
			applicationImpl.setApplicationType("mp");
			if(mpFaceAmount == "" && mpAdvancePayment != "" ){
				applicationImpl.setPaymentType("a");
			}else{
				applicationImpl.setPaymentType("f");
			}
		}else if((cpFaceAmount != "" && cpFaceAmount != null) || cpAdvancePayment != "" && cpAdvancePayment != null){
			applicationImpl.setApplicationType("cp");
			if(cpFaceAmount == "" && cpAdvancePayment != ""){
				applicationImpl.setPaymentType("a");
			}else{
				applicationImpl.setPaymentType("f");
			}
		}else if((importContractAmount != "" && importContractAmount != null)|| importAdvancePayment != "" && importAdvancePayment != null){
			applicationImpl.setApplicationType("io");
			if(importContractAmount == "" && importAdvancePayment != ""){
				applicationImpl.setPaymentType("a");
			}else{
				applicationImpl.setPaymentType("f");
			}
		}
	}
	
}
