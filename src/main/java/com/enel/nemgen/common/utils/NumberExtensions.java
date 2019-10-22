package com.enel.nemgen.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enel.nemgen.common.model.ParseResult;

public class NumberExtensions {
	
	static final Pattern doublePattern = Pattern.compile(
		    "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
		    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
		    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
		    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*"); // http://docs.oracle.com/javase/6/docs/api/java/lang/Double.html#valueOf%28java.lang.String%29
	
	
	public static boolean isDouble(String s)
	{
		Matcher matcher = doublePattern.matcher(s);
	    return matcher.matches();
	}
	
	public static ParseResult<Integer> tryParseInt(String input){
		return tryParseInt(input, null);
	}
	
	public static ParseResult<Integer> tryParseInt(String input, Integer defaultIfNullOrEmpty){
		if(StringExtensions.IsNullOrWhitespace(input)) {
			return defaultIfNullOrEmpty == null ? ParseResult.getFailedResult(input) : ParseResult.getSuccessResult(defaultIfNullOrEmpty, input);
		}
		try {
			Integer result = Integer.parseInt(input);
			return ParseResult.getSuccessResult(result, input);
		}
		catch(NumberFormatException ex){
			return ParseResult.getFailedResult(input);
		}
	}
	
	public static ParseResult<Long> tryParseLong(String input){
		return tryParseLong(input, null);
	}
	
	public static ParseResult<Long> tryParseLong(String input, Long defaultIfNullOrEmpty){
		if(StringExtensions.IsNullOrWhitespace(input)) {
			return defaultIfNullOrEmpty == null ? ParseResult.getFailedResult(input) : ParseResult.getSuccessResult(defaultIfNullOrEmpty, input);
		}
		try {
			Long result = Long.parseLong(input);
			return ParseResult.getSuccessResult(result, input);
		}
		catch(NumberFormatException ex){
			return ParseResult.getFailedResult(input);
		}
	}
	
	public static ParseResult<Double> tryParseDouble(String input){
		return tryParseDouble(input, null);
	}
	
	public static ParseResult<Double> tryParseDouble(String input, Double defaultIfNullOrEmpty){
		if(StringExtensions.IsNullOrWhitespace(input)) {
			return defaultIfNullOrEmpty == null ? ParseResult.getFailedResult(input) : ParseResult.getSuccessResult(defaultIfNullOrEmpty, input);
		}
		try {
			Double result = Double.parseDouble(input);
			return ParseResult.getSuccessResult(result, input);
		}
		catch(NumberFormatException ex){
			return ParseResult.getFailedResult(input);
		}
	}

}
