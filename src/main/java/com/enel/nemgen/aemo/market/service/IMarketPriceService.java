package com.enel.nemgen.aemo.market.service;

import java.util.Map;

import com.enel.nemgen.aemo.market.model.GetMarketPriceRequest;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse;
import com.enel.nemgen.common.model.ValidationResult;

public interface IMarketPriceService {
	public ValidationResult<GetMarketPriceRequest> validateInputs(Map<String, String> queryParams);
	public ApiGatewayProxyResponse getMarketPrice(GetMarketPriceRequest request);

}
