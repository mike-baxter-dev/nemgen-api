package com.enel.nemgen.common.lambda.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.enel.nemgen.common.configuration.ConfigManager;
import com.enel.nemgen.common.dao.Logger;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyRequest;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse;
import com.enel.nemgen.common.lambda.proxy.model.LambdaException;
import com.enel.nemgen.common.lambda.proxy.model.ResponseWrapper;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse.ApiGatewayProxyResponseBuilder;
import com.enel.nemgen.common.model.ErrorCodes;
import com.enel.nemgen.common.model.HttpCodes;
import com.enel.nemgen.common.utils.StringExtensions;

public abstract class ProxyRequestHandler implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse> {

	private static final String ADMIN_RESOURCE_PING = "PING";
	private static final String ADMIN_RESOURCE_DYNAMO_DB_REFRESH_EVENT = "DDB_CONFIG_REFRESH";
    private static final String ORIGIN_HEADER = "origin";    
    protected static final String AUTH_HEADER = "authorization";
    
    // *** Dont use instance variables:
    // You need to ensure your instance variables are thread safe and can be accessed by multiple threads when it comes to Lambda. 
    // Limit your instance variable writes to initialization - once only
    
    public ProxyRequestHandler() {
    }
    
    protected abstract ApiGatewayProxyResponse processRequest(ApiGatewayProxyRequest request, Context context);
    protected abstract List<String> getRequiredHeaders(String resource);
    protected abstract List<String> getRequiredQueryParams(String resource);
    
    @Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest request, Context context) {
    	System.out.println("Entered ProxyRequestHandler method handleRequest");
	    ApiGatewayProxyResponse response;
	    Map<String, String> headers = null;
	    Map<String, String> queryParams = null;
		try {
			Logger.setLogger(context.getLogger(), UUID.randomUUID());
            String method = StringExtensions.ToLowerTrimmedOrEmpty(request.getHttpMethod());
            String resource = request.getResource();
            Logger.logInfo("Method: " + method + " | Resource: " + resource);
            switch (StringExtensions.ToUpperTrimmedOrEmpty(getResourceIdentifier(resource))) {
	    		case ADMIN_RESOURCE_PING: {
	    			return ping(request, context);
	    		}
	    		case ADMIN_RESOURCE_DYNAMO_DB_REFRESH_EVENT: {
	    			return doConfigRefresh(request, context);
	    		}
    			default: {  
    				setEnvironment(context);
		            headers = request.getHeaders() == null ? (new  HashMap<String, String>()) : keyValuesToLowerCase(request.getHeaders());
		            ValidateHeaders(request.getResource(), headers);
		            queryParams = request.getQueryStringParameters() == null ? (new  HashMap<String, String>()) : keyValuesToLowerCase(request.getQueryStringParameters());
		            ValidateQueryParams(request.getResource(), queryParams);
		            // Inherited classes will implement the business logic and return applicable response types
		            response = processRequest(request, context);		            
    			}
            }
        }
        catch(LambdaException e) {
        	Logger.logInfo("LambdaException was caught: "+e.getMessage());
            response = e.getResponse();
        }
        catch (Exception ex) {
        	Logger.logInfo("Generic Exception was caught: "+ex.getMessage());
            response = getInternalErrorResponse(ex);
        }
		boolean isOptionsMethod = StringExtensions.ToLowerTrimmedOrEmpty(request.getHttpMethod()).equals("options");
        if (!isOptionsMethod) {
        	boolean hasOriginHeader = headers != null && headers.get(ORIGIN_HEADER) != null;
        	if (hasOriginHeader) {
        		Map<String, String> responseHeaders = response.getHeaders();
                responseHeaders.put("Access-Control-Allow-Origin", (String)headers.get(ORIGIN_HEADER));
                response = response.builder()
                        .withHeaders(responseHeaders)
                        .build();
        	}
        }
        if(response.getStatusCode() != HttpCodes.OK) {
        	Logger.logInfo("Request was unsuccessful - response code "+response.getStatusCode()+". Response body: "+response.getBody());
        }
        Logger.logInfo(String.format("Completed Method "+request.getHttpMethod()+". Response: %s with size %s.\n", response.getStatusCode(), response.getBody()!= null?response.getBody().length():"0 (no response body)"));
        return response;
	}
   
    
    protected ApiGatewayProxyResponse ping(ApiGatewayProxyRequest request, Context context) throws Exception{
    	setEnvironment(context);
    	ResponseWrapper wrapper = new ResponseWrapper(200, "OK", "OK", "pong");
    	String response = wrapper.toJson();
    	Logger.logInfo("Ping Event - returning response: "+response);
    	return new ApiGatewayProxyResponseBuilder()
                .withStatusCode(200)
                .withBody(response) 
                .build();
    }
    
    protected ApiGatewayProxyResponse doConfigRefresh(ApiGatewayProxyRequest request, Context context) throws Exception{
    	Logger.logInfo("Config refresh event starting... Invoking ConfigManager methods invalidate and setEnvironment");
    	ConfigManager.invalidate();
    	setEnvironment(context);
    	ResponseWrapper wrapper = new ResponseWrapper(200, "OK", "OK", ConfigManager.getType()+" config refreshed successfully.");
    	String response = wrapper.toJson();
    	Logger.logInfo(ConfigManager.getType()+" config refresh event finished - returning response: "+response);
    	return new ApiGatewayProxyResponseBuilder()
                .withStatusCode(200)
                .withBody(response) 
                .build();
    }
    
    public String getAccessToken(ApiGatewayProxyRequest request) {
    	return request.getHeaders() == null ? null : keyValuesToLowerCase(request.getHeaders()).get(AUTH_HEADER);
    }
    
    public void ValidateQueryParams(String resource, Map<String, String> queryParams) throws LambdaException{
    	String missingParam = validateKeyAndValue(this.getRequiredQueryParams(resource), queryParams);
    	if(missingParam != null && !missingParam.trim().isEmpty()) {
    		this.throwMissingParamException(missingParam);
    	}
    }
    
    public void ValidateHeaders(String resource, Map<String, String> headers) throws LambdaException{
    	String missingHeader = validateKeyAndValue(this.getRequiredHeaders(resource), headers);
    	if(missingHeader != null && !missingHeader.trim().isEmpty()) {
    		this.throwMissingHeaderException(missingHeader);
    	}
    }
    
    private String validateKeyAndValue(List<String> expectedParams, Map<String, String> paramsToCheck) {
    	if(expectedParams != null) {    		
	    	for(String param : expectedParams) {
	    		if(param == null || param.trim().isEmpty()) { continue; }
	    		String paramLc = StringExtensions.ToLowerTrimmed(param);
				if(!paramsToCheck.containsKey(paramLc)) {
					return param;
				}
				String paramValue = paramsToCheck.get(paramLc);
				if(paramValue == null || paramValue.trim().isEmpty()) {
					return param;
				}
			}
    	}
    	return "";
    }
    
    @SuppressWarnings("unchecked")
	public static String getErrorBody(int responseCode, String error_code, String description) {
		JSONObject errorBody = new JSONObject();
		// Note, as per Enernoc standards -  mandatory fields are code, message_key and message
		errorBody.put("code", responseCode);
		errorBody.put("message_key", error_code);
		errorBody.put("message", description);
		return errorBody.toJSONString();
	}
    
    public static String getStandardMessageBody(Object result) {
    	return ResponseWrapper.getSuccessWrapper(result).toJson();
    }
    public static String getSimpleSuccessMessageBody(String message) {
		return ResponseWrapper.getSimpleSuccessMessageWrapper(message).toJson();
	}
    
	public static String getSimpleMessageBody(int responseCode, String message) {
		return ResponseWrapper.getSimpleMessageWrapper(responseCode, message).toJson();
	}
	
	public static ApiGatewayProxyResponse getSimpleSuccessResponse(String message) {
		return new ApiGatewayProxyResponseBuilder()
            .withStatusCode(HttpCodes.OK)
            .withBody(getSimpleSuccessMessageBody(message))
            .build(); 
	}
	
	public static ApiGatewayProxyResponse getSuccessResponse(Object result) {
		return new ApiGatewayProxyResponseBuilder()
            .withStatusCode(HttpCodes.OK)
            .withBody(getStandardMessageBody(result))
            .build(); 
	}
    
    public ApiGatewayProxyResponse getInvalidMethodResponse(String handlerName, String method, String path) {
    	Logger.logError(handlerName+".processRequest", "Handler recieved request with invalid method ("+method+") for resource path "+path);
		return new ApiGatewayProxyResponseBuilder()
        .withStatusCode(HttpCodes.BAD_REQUEST)
        .withBody(getErrorBody(HttpCodes.BAD_REQUEST, ErrorCodes.INVALID_HTTP_METHOD, "Invalid http method - Method '"+method+"' is not supported by the Lambda request handler"))
        .build();
    }
    
    public ApiGatewayProxyResponse getInvalidResourcePathResponse(String handlerName, String path) {
    	Logger.logError(handlerName+".processRequest", "Invalid resource path - "+handlerName+" recieved a request for an unrecognised resource path: "+path);
    	return new ApiGatewayProxyResponseBuilder()
                .withStatusCode(HttpCodes.BAD_REQUEST)
                .withBody(getErrorBody(HttpCodes.BAD_REQUEST, ErrorCodes.INVALID_RESOURCE_PATH, "Invalid resource path - "+handlerName+" recieved a request for an unrecognised resource path"))
                .build();
    }
	
    public static ApiGatewayProxyResponse getInternalErrorResponse() {
    	return getInternalErrorResponse(null);
    }
    
	public static ApiGatewayProxyResponse getInternalErrorResponse(Exception ex) {	
		if(ex != null) {
			Logger.logError("MetadataHandler.getInternalErrorResponse", "Unexpected error during request processing.", ex);
		}
	    return new ApiGatewayProxyResponseBuilder()
	            .withStatusCode(HttpCodes.INTERNAL_ERROR)
	            .withBody(getErrorBody(HttpCodes.INTERNAL_ERROR, ErrorCodes.INTERNAL_SERVER_ERROR, "Request processing failed due to an internal server error")) 
	            .build();  		
 
	}
    
    protected void throwMissingParamException(String paramName) throws LambdaException{
		ApiGatewayProxyResponse badRequest = getMissingParamResponse(paramName);
        throw new LambdaException(badRequest);
	} 
    
    protected ApiGatewayProxyResponse getMissingParamResponse(String paramName) {
    	ApiGatewayProxyResponse badRequest = new ApiGatewayProxyResponseBuilder()
                .withStatusCode(HttpCodes.BAD_REQUEST)
                .withBody(getErrorBody(HttpCodes.BAD_REQUEST, ErrorCodes.REQUIRED_PARAM_MISSING, "Required parameter '"+paramName+"' was not supplied"))
                .build();
    	return badRequest;
    }
    
    protected ApiGatewayProxyResponse getInvalidParamResponse(String paramName, String paramValue, String expectedType){
		ApiGatewayProxyResponse badRequest = new ApiGatewayProxyResponseBuilder()
                .withStatusCode(HttpCodes.BAD_REQUEST)
                .withBody(getErrorBody(HttpCodes.BAD_REQUEST, ErrorCodes.INVALID_PARAMETER_VALUE, "Supplied parameter '"+paramName+"' could not be converted to the expected type("+expectedType+"). Parameter value:"+paramValue))
                .build();
        return badRequest;
	}
    
    protected ApiGatewayProxyResponse getInvalidRequestResponse(String errorCode, String validationMessage){
		ApiGatewayProxyResponse badRequest = new ApiGatewayProxyResponseBuilder()
                .withStatusCode(HttpCodes.BAD_REQUEST)
                .withBody(getErrorBody(HttpCodes.BAD_REQUEST, errorCode, validationMessage))
                .build();
        return badRequest;
	}
    
    protected void throwMissingHeaderException(String headerName) throws LambdaException{
		ApiGatewayProxyResponse badRequest = new ApiGatewayProxyResponseBuilder()
                .withStatusCode(HttpCodes.BAD_REQUEST)
                .withBody(getErrorBody(HttpCodes.BAD_REQUEST, ErrorCodes.REQUIRED_HEADER_MISSING, "Required request header '"+headerName+"' was not supplied"))
                .build();
        throw new LambdaException(badRequest);
	}
	
	protected static <T> Map<String, T> keyValuesToLowerCase(Map<String, T> map) {
    	Map<String, T> result = new HashMap<String, T>();
    	if(map == null) {return result;}
    	for (Map.Entry<String, T> entry : map.entrySet()) {
    		result.put(StringExtensions.ToLowerTrimmed(entry.getKey()), (entry.getValue()));
		}
    	return result;
    }
	
	// Resolve the last part of the resource path, e.g. for input "/test/grid-structure" the result would be "grid-structure"
	protected String getResourceIdentifier(String resourcePath) {
		if(resourcePath != null) {
			int ndx = resourcePath.lastIndexOf("/");
			if(ndx > -1) {
				return resourcePath.substring(ndx+1);
			}
		}
		return resourcePath;
	}
	
	public void setEnvironment(Context context) throws Exception{
		if(ConfigManager.isValid()) {
			// Calling this every time may have significant overheads => only reload configuration if needed
			Logger.logInfo("setEnvironment - Configuration has already been initialised. Exiting from method.");
			return;
		}
		if(!ConfigManager.setConfigManager(context)) {
			Logger.logError("setEnvironment", "An error occurred during initialisation of the configuration manager. Check that the lambda configuration is correct for the environment and that the configuration provider is valid.");
			throw new Exception("Failed to load configuration settings");
		}
	}
	
}