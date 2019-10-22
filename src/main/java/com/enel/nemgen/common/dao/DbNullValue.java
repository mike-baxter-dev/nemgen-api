package com.enel.nemgen.common.dao;

public class DbNullValue {

	private int myType;
	
	public DbNullValue(int myType) {
		this.myType = myType;
	}
	
	public int getSqlType() {
		return this.myType;
	}
}
