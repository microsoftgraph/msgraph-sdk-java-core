package com.microsoft.graph.httpcore.middlewareoption;

public class AuthOptions implements IMiddlewareControl {
	private String user;
	
	public AuthOptions() {
		this("");
	}
	
	public AuthOptions(String user) {
		this.user = user;
	}
	
	public String getUser() {
		return user;
	}
}
