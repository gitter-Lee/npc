package com.mtf.demo.service.impl;

import java.util.HashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mtf.demo.dao.UserMapper;
import com.mtf.demo.model.db.DbUser;
import com.mtf.demo.model.page.User;
import com.mtf.demo.service.IUserService;

@Service("userService")
public class UserServiceImpl implements IUserService {

	private UserMapper userMapper;

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Override
	public User get(String id) {
		DbUser u = userMapper.get(id);
		if (u != null) {
			User b = new User();
			b.setId(u.getId());
			b.setName(u.getName());
			return b;
		}
		return null;
	}

	@Override
	public User getByNameAndPassword(String name, String password) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("pwd", password);
		
		DbUser u = userMapper.getByNameAndPassword(params);
		if (u != null) {
			User b = new User();
			BeanUtils.copyProperties(u, b);
			return b;
		}
		return null;
	}
}
