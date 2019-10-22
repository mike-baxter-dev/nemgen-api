package com.enel.nemgen.aemo.dispatch.service;

import java.time.LocalDateTime;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.enel.nemgen.aemo.market.model.GetMarketPriceRequest;
import com.enel.nemgen.aemo.market.service.IMarketPriceService;
import com.enel.nemgen.aemo.market.service.MarketPriceService;
import com.enel.nemgen.common.lambda.proxy.ProxyRequestHandler;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse;
import com.enel.nemgen.common.model.ValidationResult;

public class DispatchPriceService implements IDispatchPriceService{
	
	public IMarketPriceService getMarketPriceService() {
		return new MarketPriceService();
	}
	
	public ValidationResult<Object> validateInputs(Map<String, String> queryParams){
		return ValidationResult.getValidResult();
	}
	
	public ApiGatewayProxyResponse publishDispatchPrice(Object request, Context context) {
		// Get market prices by calling the MarketPriceService directly
		IMarketPriceService marketPriceService = getMarketPriceService();
		GetMarketPriceRequest marketPriceRequest = new GetMarketPriceRequest(LocalDateTime.now(), LocalDateTime.now(), "NSW1", "5-min");
		marketPriceService.getMarketPrice(marketPriceRequest);
		// adapt
		// push to sqs
		return ProxyRequestHandler.getSimpleSuccessResponse("Publish of dispatch price data to sqs was successful");

	}

}
