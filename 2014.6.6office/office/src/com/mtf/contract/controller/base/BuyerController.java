package com.mtf.contract.controller.base;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Buyer;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.service.IBuyerService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.util.TextUtils;
// 声明本类为控制器
@Controller("baseBuyerController")
// 声明请求路径以/base/buyer开头
@RequestMapping("/base/buyer")
public class BuyerController {
	
	@Autowired
	private IBuyerService				buyerService;
	
	@Autowired
	public IBuyerService getBuyerService() {
		return buyerService;
	}

	@Autowired
	public void setBuyerService(IBuyerService buyerService) {
		this.buyerService = buyerService;
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
		return "base/buyer/edit";
	}

	/**
	 * 跳转到查询
	 * @return
	 */
	@RequestMapping("/toSearch")
	public ModelAndView toSearch() {
		return new ModelAndView("base/buyer/search");
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
	public DataGrid<Buyer> doSearch(Buyer buyer, HttpSession session) throws Exception {
		new PmException(session);
		DataGrid<Buyer> list = new DataGrid<Buyer>();
		list = this.buyerService.select(buyer);
		return list;
	}

	/**
	 * 跳转编辑
	 * @param buyerId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toEdit")
	public ModelAndView toEdit(@RequestParam("buyerId") String buyerId, HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView("base/buyer/edit");
		Buyer buyer = new Buyer();
		buyer.setId(buyerId);
		buyer = buyerService.get(buyer);
		buyer.setEditState(Constants.UPDATE_STATE);
		mv.addObject("buyer", buyer);
		return mv;
	}

	/**
	 * 编辑
	 * @param buyer
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	@ResponseBody
	public Json doEdit (Buyer buyer, HttpSession session) throws Exception {
		new PmException(session);
		Json j = new Json();
		if(TextUtils.isEmpty(buyer.getName(), j, "名称不能为空!"))
			return j;
		String editState = buyer.getEditState();
		try {
			if(null == editState || "".equals(editState)) {
				buyerService.insert(buyer);
			}
			else if("u".equals(editState)) {
				if(TextUtils.isEmpty(buyer.getId(), j, "编辑信息不完整,请重新入录!"))
					return j;
				buyerService.update(buyer);
			}
			j.setSuccess(true);
			j.setObj(buyer);
		} catch (PmException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return j;
	}

	/**
	 * 删除
	 * @param buyerId
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/doDelete" ,method = RequestMethod.POST)
	@ResponseBody
	public Json doDelete(@RequestParam("buyerId") String buyerId, HttpSession session) throws Exception {
		Buyer buyer = new Buyer();
		buyer.setId(buyerId);
		buyerService.delete(buyer);
		Json j = new Json();
		j.setSuccess(true);
		return j;
	}
}
