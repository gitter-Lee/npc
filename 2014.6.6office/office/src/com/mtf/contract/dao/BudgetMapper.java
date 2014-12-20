package com.mtf.contract.dao;

import com.mtf.contract.dao.common.CommonMapper;
import com.mtf.contract.model.impl.BudgetImpl;
/*
 **********************************************
 * 项目名称：contract(合同管理系统)
 * 模块名称：模型层 -> 合同
 * 作者:     Auto
 * 日期:     2013-10-10
 **********************************************
 */
public interface BudgetMapper extends CommonMapper {
	
	/**
	 * 更新合同总金额
	 * @param budget
	 * @return
	 */
	public String sumFOB(BudgetImpl budget);

}