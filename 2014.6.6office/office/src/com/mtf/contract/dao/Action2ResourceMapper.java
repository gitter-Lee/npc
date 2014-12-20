package com.mtf.contract.dao;

import com.mtf.contract.model.Action2Resource;

public interface Action2ResourceMapper {

	int deleteById(String id);

	int insert(Action2Resource record);

	Action2Resource selectById(String id);

	int updateById(Action2Resource record);

	Action2Resource selectByResourceId(String resourceId);

	Action2Resource selectByResourceUri(String uri);

	void deleteByActionId(String actionId);
}