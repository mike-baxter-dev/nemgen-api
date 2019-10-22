package com.enel.nemgen.aemo.market.model;

import java.time.LocalDateTime;

import com.enel.nemgen.common.utils.DateExtensions;

public class GetMarketPriceRequest {
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String region;
	private String type;
	
	public GetMarketPriceRequest(
			LocalDateTime startDate, 
			LocalDateTime endDate,
			String region,
			String type) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.region = region;
		this.type = type;
	}
	
	public LocalDateTime getStartDate() {
		return startDate;
	}
	
	public LocalDateTime getEndDate() {
		return endDate;
	}
	
	public String getRegion() {
		return region;
	}
	
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "[start_date="+DateExtensions.ldtToString(this.startDate)+
				"end_date="+DateExtensions.ldtToString(this.endDate)+
				"region="+this.region+"]";
	}
}
