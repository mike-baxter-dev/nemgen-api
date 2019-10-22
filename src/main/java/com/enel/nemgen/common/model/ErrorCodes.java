package com.enel.nemgen.common.model;

public class ErrorCodes {
	public static final String INTERNAL_SERVER_ERROR = "internal_server_error";
	public static final String INVALID_METADATA_TYPE = "invalid_metadata_type";
	public static final String INVALID_LANGUAGE_HEADER ="invalid_language_header";
	public static final String REQUIRED_PARAM_MISSING ="required_parameter_missing";
	public static final String REQUIRED_HEADER_MISSING ="required_request_header_missing";
	public static final String INVALID_RESOURCE_PATH = "invalid_resource_path";
	public static final String INVALID_HTTP_METHOD = "invalid_http_method";
	public static final String RESOURCE_NOT_FOUND = "resource_not_found";
	public static final String INVALID_PARAMETER_VALUE = "invalid_parameter_value";
	public static final String RULE_CONFLICT = "rule_conflict"; // Generic code
	public static final String RULE_CONFLICT_UNSUPPORTED_OPERATION = "unsupported_operation"; // Generic code
	public static final String INSUFFICIENT_USER_PERMISSION = "insufficient_user_permission";
}
