package com.enel.nemgen.common.lambda.proxy.model;

import com.enel.nemgen.common.model.HttpCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ResponseWrapper {
	public int code;
	public String message_key;
	public String message;
	public Object data;
	
	public ResponseWrapper() {}
	public ResponseWrapper(int code, String messageKey, String message, Object data) {
		this.code = code;
		this.message_key = messageKey;
		this.message = message;
		this.data = data;
	}
	
	public String toJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}
	
	public static ResponseWrapper getSimpleSuccessMessageWrapper(String message) {
		return new ResponseWrapper(HttpCodes.OK, "OK", message, null);
	}
	
	public static ResponseWrapper getSimpleMessageWrapper(int code, String message) {
		return new ResponseWrapper(code, message, message, null);
	}
	
	public static ResponseWrapper getSuccessWrapper(Object data) {
		return new ResponseWrapper(HttpCodes.OK, "OK", "OK", data);
	}
	
	public static ResponseWrapper getSuccessWrapper(String message, Object data) {
		return new ResponseWrapper(HttpCodes.OK, "OK", message, data);
	}
	
	public String toJsonWithNull(){
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
//		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, convertToNull()).create();
		return gson.toJson(this);
	}
	
	public String toJsonWithNullAndHtmlEscaping(){
        Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping()
                .setPrettyPrinting().create();
        return gson.toJson(this);
    }
	
	public Object convertToNull() {
		return code;
	}
	
}
