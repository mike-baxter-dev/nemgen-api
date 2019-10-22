package com.enel.nemgen.aemo.dispatch.service;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse;
import com.enel.nemgen.common.model.ValidationResult;

public interface IDispatchPriceService {
	public ValidationResult<Object> validateInputs(Map<String, String> queryParams);
	public ApiGatewayProxyResponse publishDispatchPrice(Object request, Context context);
}
