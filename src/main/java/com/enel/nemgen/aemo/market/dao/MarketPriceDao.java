package com.enel.nemgen.aemo.market.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.enel.nemgen.aemo.market.model.GetMarketPriceRequest;
import com.enel.nemgen.aemo.market.model.MarketPrice;
import com.enel.nemgen.common.dao.CommonDao;
import com.enel.nemgen.common.dao.Logger;

public class MarketPriceDao extends CommonDao implements IMarketPriceRepository{
	
	public List<MarketPrice> getMarketPrice5Min(GetMarketPriceRequest request){
		String sql = "SELECT settlementdate, regionid, periodid, rrp\r\n" + 
				"FROM mms_owner.tradingprice\r\n" + 
				"WHERE settlementdate >= ? AND settlementdate >= ? AND regionid = ?\r\n" +
				"ORDER BY periodid";
		;
	    return getDbAdapter().executeQuery(
	    		sql, super.getParamList(request.getStartDate(), request.getEndDate(), request.getRegion()), 
	    		"MarketPriceDao.getMarketPrice", 
	    		MarketPriceDao::processMarketPriceResults);
	}
	
	public List<MarketPrice> getMarketPrice30Min(GetMarketPriceRequest request){
		String sql = "SELECT settlementdate, regionid, periodid, rrp\r\n" + 
				"FROM mms_owner.tradingprice\r\n" + 
				"WHERE settlementdate >= ? AND settlementdate >= ? AND regionid = ?\r\n" +
				"ORDER BY periodid";
		;
	    return getDbAdapter().executeQuery(
	    		sql, super.getParamList(request.getStartDate(), request.getEndDate(), request.getRegion()), 
	    		"MarketPriceDao.getMarketPrice", 
	    		MarketPriceDao::processMarketPriceResults);
	}
		
	private static List<MarketPrice> processMarketPriceResults(ResultSet resultSet,String caller) {
		List<MarketPrice> marketPrices = new ArrayList<>();
		try {
			Logger.logInfo("MarketPriceDao.processMarketPrices: invoked by "+caller);
			while(resultSet.next()) {
				marketPrices.add(new MarketPrice(
						resultSet.getTimestamp("settlementdate").toLocalDateTime(),
						resultSet.getString("regionid"),
						resultSet.getInt("periodid"),
						resultSet.getDouble("rrp")));
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return marketPrices;
	}

}
