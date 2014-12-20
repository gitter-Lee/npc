package com.mtf.contract.dao;

import com.mtf.contract.model.ActionTree2Action;

public interface ActionTree2ActionMapper {

	int deleteById(String id);

	int insert(ActionTree2Action record);

	ActionTree2Action selectById(String id);

	int updateById(ActionTree2Action record);
}