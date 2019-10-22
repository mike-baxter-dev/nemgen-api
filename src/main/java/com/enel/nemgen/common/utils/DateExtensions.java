package com.enel.nemgen.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.enel.nemgen.common.model.ParseResult;

public class DateExtensions {
	
	public static ParseResult<LocalDateTime> tryParseDate(String input){
		return tryParseDate(input, null);
	}
	
	public static ParseResult<LocalDateTime> tryParseDate(String input, LocalDateTime defaultIfNullOrEmpty){
		if(StringExtensions.IsNullOrWhitespace(input)) {
			return defaultIfNullOrEmpty == null ? ParseResult.getFailedResult(input) : ParseResult.getSuccessResult(defaultIfNullOrEmpty, input);
		}
		try {
			LocalDateTime result = ConvertToDate(input);
			return ParseResult.getSuccessResult(result, input);
		}
		catch(DateTimeParseException  ex){
			return ParseResult.getFailedResult(input);
		}
	}
	
	public static LocalDateTime ConvertToDate(String strDate) {
		if(strDate == null || strDate.trim().isEmpty()) {
			return LocalDateTime.MIN;
		}
		String wsDate = cleanDateTimeString(strDate);
		DateTimeFormatter wsDateFormat = DateTimeFormatter.ofPattern(getAnticipatedFormat(wsDate)); //YYYY-MM-DDTHH:MI 
		return LocalDateTime.parse(wsDate, wsDateFormat);
	}
    
    private static String cleanDateTimeString(String strDate) {
    	if(strDate != null) {
    		// Replace the T character as it will cause parsing to fail e.g. "yyyy-MM-ddTHH:mm:ss" ->"yyyy-MM-dd HH:mm:ss"
    		String result = strDate.trim().replace('T', ' ');
    		return result.length() == 10 ? result+" 00:00" : result;
    	}
    	return "";
    }
     
    private static String getAnticipatedFormat(String strDate) {
    	if(strDate != null) {
    		int len = strDate.length();
    		switch(len) {
    			case 10:{
    				return "yyyy-MM-dd 00:00";
    			}
    			case 16:{
    				return "yyyy-MM-dd HH:mm";
    			}
    			case 19:{
    				return "yyyy-MM-dd HH:mm:ss";
    			}
    			case 21:{
    				return "yyyy-MM-dd HH:mm:ss.S";
    			}
    			case 22:{
    				return "yyyy-MM-dd HH:mm:ss.SS";
    			}
    			case 23:{
    				return "yyyy-MM-dd HH:mm:ss.SSS";
    			}
    			case 24:{
    				return "yyyy-MM-dd HH:mm:ss.SSSS";
    			}
    			case 25:{
    				return "yyyy-MM-dd HH:mm:ss.SSSSS";
    			}
    			default:{
    				return "yyyy-MM-dd HH:mm:ss.SSSSSS";
    			}
    		}
    	}
    	return "";
    }
	
	public static String FormatLocalDateTime_YmdHm(LocalDateTime aDate) {
		DateTimeFormatter PG_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return aDate.format(PG_TIMESTAMP_FORMAT);
	}
	
	public static String FormatLocalDateTime_Ymd(LocalDateTime aDate) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return aDate.format(dateFormat);
	}
	
	public static String FormatLocalDateTime_Hm(LocalDateTime aDate) {
		DateTimeFormatter PG_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
		return aDate.format(PG_TIMESTAMP_FORMAT);
	}
	
	public static String FormatLocalDateTime_Ymd_Hms(LocalDateTime aDate) {
		DateTimeFormatter PG_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return aDate.format(PG_TIMESTAMP_FORMAT);
	}
	
	public static String ldtToString(LocalDateTime aDate) {
		if(aDate == null) {
			return "null";
		}
		return FormatLocalDateTime_Ymd_Hms(aDate);
	}
}
