package com.mtf.demo.service;

import com.mtf.demo.model.page.User;

public interface IUserService {
	
	User get(String id);
	
	User getByNameAndPassword(String name, String password);
}
