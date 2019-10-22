package com.enel.nemgen.aemo.market;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.enel.nemgen.aemo.market.model.GetMarketPriceRequest;
import com.enel.nemgen.aemo.market.service.IMarketPriceService;
import com.enel.nemgen.aemo.market.service.MarketPriceService;
import com.enel.nemgen.common.lambda.proxy.ProxyRequestHandler;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyRequest;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse;
import com.enel.nemgen.common.model.ValidationResult;
import com.enel.nemgen.common.utils.StringExtensions;

public class GetMarketPriceHandler  extends ProxyRequestHandler{
	
	public IMarketPriceService getService() {
		return new MarketPriceService();
	}
	
	@Override 
	protected ApiGatewayProxyResponse processRequest(ApiGatewayProxyRequest request, Context context) {
		String method = StringExtensions.ToLowerTrimmedOrEmpty(request.getHttpMethod());
		switch(method) {
			case "get":{
				ValidationResult<GetMarketPriceRequest> vResult = getService().validateInputs(keyValuesToLowerCase(request.getQueryStringParameters()));
				if(vResult.isValid()) {
					return getService().getMarketPrice(vResult.getResult());
				}
				return super.getInvalidRequestResponse(vResult.getErrorCode(), vResult.getDescription());					
			}
			default:{
				return super.getInvalidMethodResponse(this.getClass().getSimpleName(), request.getHttpMethod(), request.getResource());
			}
		}
	}
	
	@Override
	protected List<String> getRequiredHeaders(String resource){
		return null;
	}
	
	@Override
	protected List<String> getRequiredQueryParams(String resource){
		return StringExtensions.getList(MarketPriceService.PARAM_START_DATE, MarketPriceService.PARAM_END_DATE, MarketPriceService.PARAM_REGION);
	}

}
