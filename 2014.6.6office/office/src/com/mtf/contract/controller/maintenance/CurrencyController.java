package com.mtf.contract.controller.maintenance;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.util.CommonUtil;
import com.mtf.contract.model.Contract;
import com.mtf.contract.model.Currency;
import com.mtf.contract.service.CurrencyService;

/*
**********************************************
 * 项目名称：contract(合同管理系统)
 * 模块名称：控制层 -> 币种
 * 作者:     Auto
 * 日期:     2013/7/24
**********************************************
*/
@Controller("currencyController")
@RequestMapping("/maintenance/currency")
public class CurrencyController {

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	@Autowired
	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	/**
	 * 跳转页面
	 * @return
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch() {
		return new ModelAndView("/maintenance/currency/searchCurrency");
	}

	/**
	 * 查询
	 * @param form
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/doSearch")
	@ResponseBody
	public DataGrid<Currency> doSearch(Currency currency, HttpSession session) throws Exception {
		new PmException(session);
		DataGrid<Currency> list = new DataGrid<Currency>();
		list = this.currencyService.select(currency);
		return list;
	}

	/**
	 * 跳转编辑
	 * @param currency
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(Currency currency, HttpSession session) throws Exception {
		new PmException(session);
		ModelAndView mv = new ModelAndView("");
		String editState = currency.getEditState();
		if ("u".equals(editState)) {
		currency = currencyService.get(currency);
		}
		currency.setEditState(editState);
		mv.addObject("currency", currency);
		return mv;
	}

	/**
	 * 编辑
	 * @param currency
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit(Currency currency, HttpSession session) throws Exception {
		new PmException(session);
		Json j = new Json();
		String editState = currency.getEditState();
		try {
			if ("i".equals(editState)) {
				CommonUtil.setCommonField(currency, session);
				currencyService.insert(currency);
			} else if ("u".equals(editState)) {
				CommonUtil.setCommonField(currency, session);
				currencyService.update(currency);
			} else if ("d".equals(editState)) {
				currencyService.delete(currency);
			}
			j.setSuccess(true);
			j.setObj(currency);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}
	
	/**
	 * 编辑
	 * @param currency
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/option", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<Currency> option(Currency currency, HttpSession session) throws Exception {
		DataGrid<Currency> listCurrency = currencyService.select(currency);
		return listCurrency;
	}

}

