package com.mtf.demo.model.db;

import java.util.List;

public class DbRole {

	private String			id;
	private List<String>	uris;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getUris() {
		return uris;
	}

	public void setPassword(List<String> uris) {
		this.uris = uris;
	}
}