package com.mtf.contract.controller.contract;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.jtds.jdbc.DateTime;

import org.openflashchart.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.exception.PmException;

import com.mtf.contract.model.Application;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.model.impl.ApplicationImpl;
import com.mtf.contract.model.impl.PaymentImpl;
import com.mtf.contract.service.ApplicationService;
import com.mtf.contract.service.PaymentService;
import com.mtf.contract.service.common.CommonService;
import com.mtf.contract.util.CommonUtil;
import com.mtf.contract.util.Constants;

@Controller("paymentController")
@RequestMapping("/contract/payment")
public class PaymentController extends ContractController{

	//TODO
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private ApplicationService applicationService;
	
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
	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	/**
	 * 跳转页面
	 * @return
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch(PaymentImpl paymentImpl,HttpSession session) {
		paymentImpl.setViewPath("base/payment/searchPayment");
		ModelAndView mv=new ModelAndView(paymentImpl.getViewPath());
		mv.addObject("payment", paymentImpl);
		return mv;
	}
	

	
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<PaymentImpl> doSearch(PaymentImpl paymentImpl, HttpSession session) throws Exception {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		String userId = sessionInfo.getUserId();
		//  用户等级 1 员工 2 业务经理 3 事业经理 4 总经理
		Integer userLevel = sessionInfo.getUserLever();
		paymentImpl.setUserLevel(userLevel);
		List<String> listEmployee = sessionInfo.getListEmployee();
		if (listEmployee == null) {
			listEmployee = new ArrayList<>();
			listEmployee.add(userId);
		}
		// 如果为领导那么查询下属员工
		paymentImpl.setListEmployee(listEmployee);
		// 如果为领导那么查询已提交的合同
		// 审批状态集合(saved:已保存, submit:已提交, reject1:一级驳回, reject2:二级驳回, approval1:一级审批完毕, approval2:二级审批完毕)
		List<String> listApproveState = new ArrayList<>();
		if (userLevel.equals(0)) {
			listApproveState.add("paying");
			listApproveState.add("payed");
			//listApproveState.add("saved");
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
		} else if (userLevel.equals(4)) {
			listApproveState.add("approval1");
			listApproveState.add("approval2");
			listApproveState.add("approval3");
			listApproveState.add("paying");
			listApproveState.add("payed");
		}
		// 每次查询用员工集合是下一级的所有审批的员工编号和本级自己本身员工编号
		paymentImpl.setListApproveState(listApproveState);
		DataGrid<PaymentImpl> list = new DataGrid<PaymentImpl>();
		list = this.paymentService.select(paymentImpl);
		return list;
	}
	
	@RequestMapping(value="/doSearchItem", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<PaymentImpl> doSearchItem(PaymentImpl paymentImpl, HttpSession session) throws Exception {
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		String userId = sessionInfo.getUserId();
		DataGrid<PaymentImpl> list = new DataGrid<PaymentImpl>();
		list = this.paymentService.selectItem(paymentImpl);
		return list;
	}
	//查看历史记录
	@RequestMapping("/toViewPaymentItem")
	public ModelAndView toViewPaymentItem(PaymentImpl paymentImpl,HttpSession session) throws Exception {
		
		paymentImpl.setViewPath("base/payment/editPaymentForm");
		ModelAndView mv = new ModelAndView(paymentImpl.getViewPath());
		String editState = paymentImpl.getEditState();
		String field2=paymentImpl.getField2();
		if ("pay".equals(editState) || "p".equals(editState)) {
			paymentImpl = paymentService.get(paymentImpl);
		}
		paymentImpl.setEditState(editState);
		paymentImpl.setField2(field2);
		//只是查看
		mv.addObject("application", paymentImpl);
		return mv;
	}
	
	@RequestMapping(value = "/doChart")
	public void doChart(PaymentImpl paymentImpl,HttpServletResponse response) throws Exception{
			Long id= paymentImpl.getId();
			/*System.err.println("---------___---------");
			System.err.println(id);*/

			List<PaymentImpl> list = new ArrayList<PaymentImpl>();
			//PaymentImpl paymentImpldb=new PaymentImpl();
			//paymentImpldb.setId(paymentImpl.getId());
			
			//paymentImpl.setMasterContractNo("001001");
			list = (List<PaymentImpl>) this.paymentService.selectList(paymentImpl);
			int s=list.size();
			//System.err.println(s);

			//int max = 50;
			List<String> data = new ArrayList<String>();
			List<String> data2 = new ArrayList<String>();
			List<String> data3 = new ArrayList<String>();
			double t=Double.parseDouble(list.get(0).getTotalPrice());
			
			double mpAdvancePayment=0.00;
				if(list.get(0).getMpAdvancePayment().equals("")){
					mpAdvancePayment=0.00;
				}else if(!list.get(0).getMpAdvancePayment().equals("")){
					mpAdvancePayment=Double.parseDouble(list.get(0).getMpAdvancePayment());
				}
					
			//预付款
			//double tp=mpAdvancePayment + importPrepaidAmounts;
				double tp=mpAdvancePayment ;
			for(int i = 0; i < s; i++) {
				//System.err.println("++++++++++++++");
				//double t=Double.parseDouble(list.get(0).getTotalPrice());
				
				double a=0.00;
				double b=0.00;
				double c=0.00;
				
				if(list.get(i).getMpPayment().equals("")){
					a=0.00;
				}else if(!list.get(i).getMpPayment().equals("")){
					a=Double.parseDouble(list.get(i).getMpPayment());
				}
				
				if(list.get(i).getCpPayment().equals("")){
					b=0.00;
				}else if(!list.get(i).getCpPayment().equals("")){
					b=Double.parseDouble(list.get(i).getCpPayment());
				}
				
				if(list.get(i).getImportPayment().equals("")){
					c=0.00;
				}else if(!list.get(i).getImportPayment().equals("")){
					c=Double.parseDouble(list.get(i).getImportPayment());
				}

				double d=a+b+c;
				double up=t-d;
				/*System.err.println(a);
				System.err.println(b);
				System.err.println(c);
				System.err.println(d);*/
				//System.err.println(up);
				if(i==0){
					data.add(Double.toString(t-tp));
				}else{
					data.add(Double.toString(up-tp));
				}
				//data2.add(Double.toString(tp));
				
				//data.add(Double.toString(d));
				//data2.add(Double.toString(Math.random() * max / 2));
			}
			
			//double totalPrice =Double.parseDouble(list.get(0).getTotalPrice());

			Graph g = new Graph();
			// Spoon sales, March 2007
			g.title("付款管理表一览", "{font-size: 25px;}");
			g.set_data(data);
			g.set_data(data2);
			g.set_data(data3);
			//第一个“2”是线的粗细
			//最后一位是字体大小
			g.line(2, "0x9933CC", "总未付款", 13, 100);
			g.line_hollow("2", "10", "0x80a033", "总预付款"+tp, "13");
			g.line_hollow("2", "4", "#0080ff", "总金额"+t, "13");
			
			
			
			// label each point with its value
			List<String> labels = new ArrayList<String>();
			DateFormat format = new SimpleDateFormat("yy.MM.dd\nHH:mm:ss"); 
			for(int i = 0; i < s; i++) {
				String strDate = format.format(list.get(i).getModifiedDate());
				/*System.err.println("+++++++++++++++++");
				System.err.println(strDate);*/
				//String StrI=i+"";
				labels.add(strDate);
			}
			/*labels.add("a");
			labels.add("b");
			labels.add("c");
			labels.add("d");
			labels.add("e");
			labels.add("f");
			labels.add("g");
			labels.add("h");
			labels.add("i");
			labels.add("j");
			labels.add("k");
			labels.add("l");*/
			g.set_x_labels(labels);
			//第四个“1”是显示的增量
			g.set_x_label_style("12", "#FF0000", 0, 1, "");
			g.set_x_legend("时 代 万 恒", 12, "#736AFF");
			//g.set_x_offset(false);
			
			
			// set the Y max
			
			//int wfk=(int)(totalPrice-mpAdvancePayment-importPrepaidAmounts);
			g.set_y_max((int)t);
			// label every 20 (0,20,40,60)
			g.y_label_steps(6);
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(g.render());
		}
	
}
