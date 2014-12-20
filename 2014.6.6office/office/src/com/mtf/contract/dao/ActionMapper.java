package com.mtf.contract.dao;

import java.util.HashMap;
import java.util.List;

import com.mtf.contract.model.Action;

public interface ActionMapper {

	int deleteById(String id);

	int insert(Action record);

	Action selectById(String id);

	int updateById(Action record);

	int insertResource(HashMap<String, String> param);

	int countByName(String name);

	List<Action> selectAll(HashMap<String, Object> param);

	List<Action> selectAllWithResources();

	List<Action> selectByIdWithResources(String id);
}