package com.enel.nemgen.common.model;

import com.enel.nemgen.common.utils.StringExtensions;

public class ParseResult<T> {
	
	private T result;
	private String input;
	private boolean parsedOk;
	
	private ParseResult(T result, String input) {
		this.result = result;
		this.input = input;
		this.parsedOk = true;
	}
	
	private ParseResult(String input) {
		this.input = input;
		this.parsedOk = false;
	}
	
	public boolean isNullOrEmpty() {
		return StringExtensions.IsNullOrEmpty(this.input);
	}
	
	public boolean isValid() {
		return parsedOk;
	}
	
	public T getResult() {
		return result;
	}
	
	public String getInput() {
		return input;
	}

	public static <T> ParseResult<T> getFailedResult(String input){
		return new ParseResult<T>(input);
	}
	
	public static <T> ParseResult<T> getSuccessResult(T result, String input){
		return new ParseResult<T>(result, input);
	}
}
