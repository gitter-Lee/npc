package com.mtf.contract.controller.maintenance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.mtf.contract.editor.DateEditor;
import com.mtf.contract.editor.DoubleEditor;
import com.mtf.contract.editor.IntegerEditor;
import com.mtf.contract.exception.PmException;
import com.mtf.contract.model.Goods;
import com.mtf.contract.model.Unit;
import com.mtf.contract.model.common.DataGrid;
import com.mtf.contract.model.common.Json;
import com.mtf.contract.model.common.PageForm;
import com.mtf.contract.model.common.SessionInfo;
import com.mtf.contract.service.IGoodsService;
import com.mtf.contract.service.IUnitService;
import com.mtf.contract.util.Constants;
import com.mtf.contract.model.common.Pair;




@Controller("maintenanceGoodsController")
@RequestMapping("/maintenance/goods")
public class GoodsController {
	private static final Logger		logger	= Logger.getLogger(GoodsController.class);
	
	private IGoodsService goodsService;
	private IUnitService unitService;
	
	@Autowired
	public void setUnitService(IUnitService unitService) {
		this.unitService = unitService;
	}
	@Autowired
	public void setGoodsService(IGoodsService goodsService) {
		this.goodsService = goodsService;
	}
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor(null, true));
		binder.registerCustomEditor(Integer.class, new IntegerEditor(true));
		binder.registerCustomEditor(Double.class, new DoubleEditor(true));
	}
	@RequestMapping("/toSearch")
	public ModelAndView toSearch(HttpSession session) throws PmException {
		if (session == null || session.getAttribute(Constants.SESSION_INFO) == null) {
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		ModelAndView mv=new ModelAndView("maintenance/goods/search");
		//file unit
		List<Pair<String, String>> pairUnitList = null;
		List<Unit> unitList=unitService.listAllAvailable(sessionInfo.getUserId(), null);
		if(unitList!=null){
			pairUnitList = new ArrayList<Pair<String, String>>(unitList.size());
			for (Unit unit : unitList) {
				pairUnitList.add(new Pair<String, String>(unit.getId(), unit.getName()));
			}
			mv.addObject("unitData", JSONObject.toJSONString(pairUnitList));
		}
		return mv;
	}
	
	@RequestMapping("/doSearch")
	@ResponseBody
	public DataGrid<Goods> doSearch(PageForm form,Goods goods,HttpSession session) throws PmException{
		if(session==null||session.getAttribute(Constants.SESSION_INFO)==null){
			throw new PmException(PmException.CODE_NOSESSION);
		}
		if(null==goods.getCommodity()||goods.getCommodity().trim()==""){
			goods.setCommodity(null);
		}
		SessionInfo sessionInfo=(SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		DataGrid<Goods> result=new DataGrid<Goods>();
		try{
			result=goodsService.select(sessionInfo.getUserId(), form, goods);
		}catch(PmException e){
			throw e;
		}catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
	@RequestMapping("/doAdd")
	@ResponseBody
	public Json doAdd(Goods goods,HttpSession session) throws PmException{
		if(session==null||session.getAttribute(Constants.SESSION_INFO)==null){
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo=(SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		Json json=new Json();
		Goods dbgoods=null;
		if(null==goods.getStatus()){
			goods.setStatus(0);
		}
		try{
		dbgoods=goodsService.insert(sessionInfo.getUserId(), goods);
		
		}catch(PmException e){
			throw e;
		}catch (Exception e) {
			throw e;
		}
		if(dbgoods!=null){
			json.setSuccess(true);
			json.setMsg("success");
			json.setObj(dbgoods);
		}else{
			json.setMsg("fail");
		}
		
		return json;
	}
	
	@RequestMapping("/doEdit")
	@ResponseBody
	public Json doEdit(Goods goods,HttpSession session) throws PmException{
		if(session==null||session.getAttribute(Constants.SESSION_INFO)==null){
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo=(SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		int flag=0;
		Json json=new Json();
		try{
		flag=goodsService.update(sessionInfo.getUserId(), goods);
		}catch(PmException e){
			throw e;
		}catch (Exception e) {
			throw e;
		}
		if(flag==1){
			json.setSuccess(true);
			json.setMsg("update success");
		}
		
		return json;
	}
	
	@RequestMapping("/doDelete")
	@ResponseBody
	public Json doDelete(Goods goods,HttpSession session) throws PmException{
		if(session==null||session.getAttribute(Constants.SESSION_INFO)==null){
			throw new PmException(PmException.CODE_NOSESSION);
		}
		SessionInfo sessionInfo=(SessionInfo) session.getAttribute(Constants.SESSION_INFO);
		int flag=0;
		Json json=new Json();
		try{
		flag=goodsService.delete(sessionInfo.getUserId(), goods);
		}catch(PmException e){
			throw e;
		}catch (Exception e) {
			throw e;
		}
		if(flag==1){
			json.setSuccess(true);
			json.setMsg("删除成功");
		}
		return json;
	}

}
