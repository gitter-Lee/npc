package com.mtf.demo.dao;

import java.util.HashMap;

import com.mtf.demo.model.db.DbUser;

public interface UserMapper {
	
	DbUser get(String id);
	
	DbUser getByNameAndPassword(HashMap<String, Object> params);
}