package com.mtf.contract.controller.maintenance;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Items;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.service.ItemsService;

@Controller("itemsController")
@RequestMapping("/maintenance/items")
public class ItemsController {
	
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

	/**
	 * 查询子订单列表
	 * @param form
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/doSearch", method=RequestMethod.POST)
	@ResponseBody
	public DataGrid<Items> doSearch(Items items, HttpSession session) throws Exception {
		new PmException(session);
		DataGrid<Items> list = new DataGrid<Items>();
		list = this.itemsService.select(items);
		return list;
	}
}
