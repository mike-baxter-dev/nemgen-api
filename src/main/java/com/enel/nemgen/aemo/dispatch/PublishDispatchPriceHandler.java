package com.enel.nemgen.aemo.dispatch;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.enel.nemgen.aemo.dispatch.service.DispatchPriceService;
import com.enel.nemgen.aemo.dispatch.service.IDispatchPriceService;
import com.enel.nemgen.common.lambda.proxy.ProxyRequestHandler;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyRequest;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse;
import com.enel.nemgen.common.model.ValidationResult;
import com.enel.nemgen.common.utils.StringExtensions;

public class PublishDispatchPriceHandler  extends ProxyRequestHandler{

	public IDispatchPriceService getService() {
		return new DispatchPriceService();
	}
	
	@Override
	protected ApiGatewayProxyResponse processRequest(ApiGatewayProxyRequest request, Context context) {
		String method = StringExtensions.ToLowerTrimmedOrEmpty(request.getHttpMethod());
		switch(method) {
			case "post":{
				ValidationResult<Object> vResult = getService().validateInputs(keyValuesToLowerCase(request.getQueryStringParameters()));
				if(vResult.isValid()) {
					return getService().publishDispatchPrice(vResult.getResult(), context);
				}
				return super.getInvalidRequestResponse(vResult.getErrorCode(), vResult.getDescription());					
			}
			default:{
				return super.getInvalidMethodResponse(this.getClass().getSimpleName(), request.getHttpMethod(), request.getResource());
			}
		}
	}

	@Override
	protected List<String> getRequiredHeaders(String resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<String> getRequiredQueryParams(String resource) {
		// TODO add required params ...
		return StringExtensions.getList("start_date", "end_date");
	}

}
