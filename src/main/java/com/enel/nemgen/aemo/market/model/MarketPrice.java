package com.enel.nemgen.aemo.market.model;

import java.time.LocalDateTime;

import com.enel.nemgen.common.utils.DateExtensions;

public class MarketPrice {
	private String settlement_date;
	private String region_id;
	private int period_id;
	private double price;
	
	public MarketPrice(LocalDateTime settlementDate, String regionId, int periodId, double price) {
		this.settlement_date = DateExtensions.FormatLocalDateTime_Ymd_Hms(settlementDate);
		this.region_id = regionId;
		this.period_id = periodId;
		this.price = price;
	}
	
	public String getSettlementDate() {
		return this.settlement_date;
	}
	
	public String getRegionId() {
		return this.region_id;
	}
	
	public int getPeriodId() {
		return this.period_id;
	}
	
	public double getPrice() {
		return this.price;
	}
}
