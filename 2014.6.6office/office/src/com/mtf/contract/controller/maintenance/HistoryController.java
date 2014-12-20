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
import com.mtf.contract.model.History;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.service.HistoryService;
import com.mtf.contract.util.CommonUtil;


@Controller("historyController")
@RequestMapping("/maintenance/history")
public class HistoryController {
	
	private static final Logger		logger	= Logger.getLogger(HistoryController.class);
	
	@Autowired
	private HistoryService historyService;

	@Autowired
	public HistoryService getHistoryService() {
		return historyService;
	}

	@Autowired
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	/**
	 * 跳转查询
	 * @param userinfor
	 * @param session
	 * @return
	 * @throws PmException
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch(History history,HttpSession session)throws PmException{
	ModelAndView mv = new ModelAndView("maintenance/user/SearchForVersion");
	return mv;
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
	public DataGrid<History> doSearch(History history,HttpSession session)throws PmException{
		DataGrid<History> list = historyService.select(history);
		return list;
	}
	
	
	

}
