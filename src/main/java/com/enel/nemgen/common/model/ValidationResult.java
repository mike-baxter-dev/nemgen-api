package com.enel.nemgen.common.model;

public class ValidationResult<T> {

	private String errorCode;
	private String description;
	private boolean validated;
	private T result;

	private ValidationResult(T result) {
		this.result = result;
		validated = true;
	}

	private ValidationResult(String errorCode, String description) {
		validated = false;
		this.errorCode = errorCode;
		this.description = description;
	}
	
	private ValidationResult() {
		validated = true;
	}

	public Boolean isValid() {
		return validated;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getDescription() {
		return this.description;
	}

	public T getResult() {
		return this.result;
	}

	public static <T>ValidationResult<T> getFailedResult(String errorCode, String description) {
		return new ValidationResult<T>(errorCode, description);
	}

	public static <T> ValidationResult<T> getValidResult(T result) {
		return new ValidationResult<T>(result);
	}
	
	public static <T> ValidationResult<T> getValidResult() {
		return new ValidationResult<T>();
	}
}
