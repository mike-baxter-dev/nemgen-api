package com.enel.nemgen.common.model;

public class HttpCodes {
	public static final int OK = 200;
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	public static final int UNAUTHORIZED = 401;
	public static final int FORBIDDEN = 403;
	public static final int RULE_CONFLICT = 409; //"Request can not be completed due to business rule conflict";
	public static final int INVALID_CREDENTIALS = 430;
	public static final int INTERNAL_ERROR = 500;
}
