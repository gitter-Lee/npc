package com.mtf.contract.dao;

import com.mtf.contract.model.ActionTree;

public interface ActionTreeMapper {

	int deleteById(String id);

	int insert(ActionTree record);

	ActionTree selectById(String id);

	int updateById(ActionTree record);
}