package com.enel.nemgen.aemo.market.dao;

import java.util.List;

import com.enel.nemgen.aemo.market.model.GetMarketPriceRequest;
import com.enel.nemgen.aemo.market.model.MarketPrice;

public interface IMarketPriceRepository {
	public List<MarketPrice> getMarketPrice(GetMarketPriceRequest request);
}
