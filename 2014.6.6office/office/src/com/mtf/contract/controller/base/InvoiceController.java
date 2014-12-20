package com.mtf.contract.controller.base;

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
import com.mtf.contract.model.Invoice;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.impl.ContractImpl;
import com.mtf.contract.service.IInvoiceService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.TextUtils;
// 声明本类为控制器
@Controller("baseInvoiceController")
// 声明请求路径以/base/invoice开头
@RequestMapping("/base/invoice")
public class InvoiceController extends ContractController{
	
	@Autowired
	private IInvoiceService				invoiceService;
	
	@Autowired
	public IInvoiceService getIInvoiceService() {
		return invoiceService;
	}

	@Autowired
	public void setIInvoiceService(IInvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	/**
	 * 跳转到添加
	 * @param session
	 * @return
	 * @throws PmException
	 */
	// 声明捕捉方法"toAdd"
	@RequestMapping("/toAdd")
	public String toAdd(HttpSession session) throws PmException {
		new PmException(session);
		return "base/invoice/edit";
	}

	/**
	 * 跳转到查询
	 * @return
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch() {
		return new ModelAndView("base/invoice/search");
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
	public DataGrid<Invoice> doSearch(Invoice invoice, HttpSession session) throws Exception {
		new PmException(session);
		DataGrid<Invoice> list = new DataGrid<Invoice>();
		list = this.invoiceService.select(invoice);
		return list;
	}

	/**
	 * 跳转编辑
	 * @param invoiceId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(ContractImpl contractImpl,HttpSession session) throws Exception {
		//contractImpl.setViewPath("contract/clothesPurchase/hello");
		contractImpl.setViewPath("base/invoice/editApplicationForm");
		return super.toEdit(contractImpl, session);
	}
	
	/**
	 * 编辑
	 * @param invoice
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit (Invoice invoice, HttpSession session) throws Exception {
		new PmException(session);
		Json j = new Json();
		if(TextUtils.isEmpty(invoice.getName(), j, "名称不能为空!"))
			return j;
		if(TextUtils.isEmpty(invoice.getBalance(), j, "余额不能为空!"))
			return j;
		if(TextUtils.isDigitsOnly(invoice.getBalance(), j, "余额必须为数字!"))
			return j;
		if(TextUtils.isEmpty(invoice.getCustomerId(), j, "客户编号不能为空!"))
			return j;
		String editState = invoice.getEditState();
		try {
			if(null == editState || "".equals(editState)) {
				invoiceService.insert(invoice);
			}
			else if("u".equals(editState)) {
				if(TextUtils.isEmpty(invoice.getId(), j, "编辑信息不完整,请重新入录!"))
					return j;
				invoiceService.update(invoice);
			}
			j.setSuccess(true);
			j.setObj(invoice);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}

	/**
	 * 删除
	 * @param invoiceId
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doDelete" ,method = RequestMethod.POST)
	@ResponseBody
	public Json doDelete(@RequestParam("invoiceId") String invoiceId, HttpSession session) throws Exception {
		Invoice invoice = new Invoice();
		invoice.setId(invoiceId);
		invoiceService.delete(invoice);
		Json j = new Json();
		j.setSuccess(true);
		return j;
	}
}
