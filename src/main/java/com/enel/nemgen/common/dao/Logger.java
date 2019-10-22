package com.enel.nemgen.common.dao;

import java.util.UUID;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

// Class to encapsulate the lambda logger
public class Logger {
	private static LambdaLogger logger;
	private static String activityId;
	
	public static void setLogger(LambdaLogger lambdaLogger, UUID activityGuid) {
		logger = lambdaLogger;
		activityId = activityGuid.toString();
	}
	
	public static void logInfo(String info) {
		logger.log(getActivityId()+info);
	}
	
	public static void logError(String method, String message) {
		logger.log(getActivityId()+"Error! ["+method+"] "+message);
	}
	
	public static void logError(String method, String message, Exception ex) {
		logger.log(getActivityId()+"Error! ["+method+"] "+message+" "+ getExceptionDetails(ex, "")+" "+ex.getStackTrace());
	}
	
	private static String getActivityId() {
		return "[ActivityId:"+activityId+"] ";
	}
	
	public static boolean isInitialised() {
		return logger != null;
	}
	
	private static String getExceptionDetails(Throwable ex, String currentDetail) {
		if(ex == null) {
			return currentDetail;
		}
		currentDetail +="["+ex.getMessage()+"]";
		return getExceptionDetails(ex.getCause(), currentDetail);
	}
	
}