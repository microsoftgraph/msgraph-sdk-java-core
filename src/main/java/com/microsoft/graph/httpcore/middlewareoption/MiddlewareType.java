package com.microsoft.graph.httpcore.middlewareoption;

public enum MiddlewareType {
	
	//Authentication Middleware
	AUTHENTICATION,
	
	//Redirect Middleware
	REDIRECT,
	
	//Retry Middleware
	RETRY,
	
	//Not supported
	NOT_SUPPORTED
}
