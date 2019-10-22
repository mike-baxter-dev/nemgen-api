package com.enel.nemgen.common.dao;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommonDao {
	
	public IDbAdapter getDbAdapter() {
		return new DbAdapter();
	}
	
	protected List<Object> getParamList(Object... args) {
        List<Object> params = new ArrayList<>();
        for (Object arg : args) {
        	params.add(arg);
        }
        return params;
	}
	
	protected static boolean parseBoolY_N(String y_n) {
		return (y_n != null && y_n.trim().toUpperCase().equals("Y"));
	}		
	
	public LocalDateTime getLocalTimestamp() {		
	    String sql =   " select * from oms_owner.dbf_get_local_timestamp()";
	    return getDbAdapter().executeQuery(sql, new ArrayList<Object>(), "", CommonDao::callLocalTimestamp);
	}
	
	private static LocalDateTime callLocalTimestamp(ResultSet resultSet,String caller) {
		try {
			Logger.logInfo("CommonDao.callLocalTimestamp: invoked by "+caller);
			if(resultSet.next()) {
				return resultSet.getTimestamp(1).toLocalDateTime();
			}else {
				throw new RuntimeException("No result returned for localTimestamp query");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	
	

	


}
