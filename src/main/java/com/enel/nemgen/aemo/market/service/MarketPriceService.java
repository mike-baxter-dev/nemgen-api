package com.enel.nemgen.aemo.market.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.enel.nemgen.aemo.market.dao.IMarketPriceRepository;
import com.enel.nemgen.aemo.market.dao.MarketPriceDao;
import com.enel.nemgen.aemo.market.model.GetMarketPriceRequest;
import com.enel.nemgen.aemo.market.model.MarketPrice;
import com.enel.nemgen.common.dao.Logger;
import com.enel.nemgen.common.lambda.proxy.ProxyRequestHandler;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse;
import com.enel.nemgen.common.model.ErrorCodes;
import com.enel.nemgen.common.model.ParseResult;
import com.enel.nemgen.common.model.ValidationResult;
import com.enel.nemgen.common.utils.DateExtensions;

public class MarketPriceService implements IMarketPriceService{
	
	public static final String PARAM_START_DATE = "start_date";
	public static final String PARAM_END_DATE = "end_date";
	public static final String PARAM_REGION = "region";
	public static final String PARAM_TYPE = "type";
	
	public IMarketPriceRepository getRepository() {
		return new MarketPriceDao();
	}
	
	public ValidationResult<GetMarketPriceRequest> validateInputs(Map<String, String> queryParams){
		ParseResult<LocalDateTime> sd = DateExtensions.tryParseDate(queryParams.get(PARAM_START_DATE));
		ParseResult<LocalDateTime> ed = DateExtensions.tryParseDate(queryParams.get(PARAM_END_DATE));
		if(!sd.isValid()) {
			return ValidationResult.getFailedResult(ErrorCodes.INVALID_PARAMETER_VALUE, "Parameter start_date was not in the expected format");
		}
		if(!ed.isValid()) {
			return ValidationResult.getFailedResult(ErrorCodes.INVALID_PARAMETER_VALUE, "Parameter end_date was not in the expected format");
		}
		String type = queryParams.get(PARAM_TYPE);
		if(type.equals("")){
			return ValidationResult.getFailedResult(ErrorCodes.INVALID_PARAMETER_VALUE, "Parameter type was not one of the expected values");
		}
		return ValidationResult.getValidResult(new GetMarketPriceRequest(sd.getResult(), ed.getResult(), queryParams.get(PARAM_REGION), type));
	}
	
	public ApiGatewayProxyResponse getMarketPrice(GetMarketPriceRequest request){
		List<MarketPrice> prices = null;
		switch(request.getType()) {
			case "5-min":{
				prices = getRepository().getMarketPrice5Min(request);
			}
			case "30-min":{
				prices = getRepository().getMarketPrice30Min(request);
			}
		}
		if(prices == null) {
			Logger.logError("MarketPriceService.getMarketPrice", "Failed to get the Market Prices for request parameters: "+request.toString());
			return ProxyRequestHandler.getInternalErrorResponse();
		}
		Logger.logInfo("Successfully retrieved "+prices.size()+" Market Price records");
		return ProxyRequestHandler.getSuccessResponse(prices);
	}

}
