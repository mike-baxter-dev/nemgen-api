package com.enel.nemgen.common.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.BiFunction;

import com.enel.nemgen.common.dao.ConnectionPool;
import com.enel.nemgen.common.dao.DbNullValue;
import com.enel.nemgen.common.dao.IDbAdapter;
import com.enel.nemgen.common.dao.Logger;

public class DbAdapter implements IDbAdapter{
	
	private static final String COMMON_DAO_EXECUTE_UPDATE = "CommonDao.executeUpdate";
	private static final String SQL_EXCEPTION_OCCURRED_WITH_QUERY = "SqlException occurred with query: ";
	private static final String COMMON_DAO_EXECUTE_QUERY = "CommonDao.executeQuery";
	private static final String EXCEPTION_OCCURRED_WITH_QUERY = "Exception occurred with query: ";
	private static final String CONNECTION_POOL_GET_CONNECTION = "ConnectionPool.getConnection";
	private static final String INTIALISE_POOL = "ConnectionPool.initialise";

	@Override
	public <R, U> R executeQuery(String sql, List<Object> params, U criteria,
			BiFunction<ResultSet, U, R> resultHandler) {
		String err = INTIALISE_POOL;
			
		try( Connection connection = ConnectionPool.getConnection();
				PreparedStatement pSt = connection.prepareStatement(sql);) {
			
			err = CONNECTION_POOL_GET_CONNECTION;
		
			setParameters(pSt, params);
			err = pSt.toString();
			ResultSet resultSet = pSt.executeQuery();
			return resultHandler.apply(resultSet, criteria);
		} catch (SQLException e) {
			Logger.logError(COMMON_DAO_EXECUTE_QUERY, SQL_EXCEPTION_OCCURRED_WITH_QUERY + err, (Exception) e);
		} catch (Exception e) {
			Logger.logError(COMMON_DAO_EXECUTE_QUERY, EXCEPTION_OCCURRED_WITH_QUERY + err, (Exception) e);
		} 
		return null;
	}

	@Override
	public Integer executeUpdate(String sql, List<Object> params) {
		String err = INTIALISE_POOL;
		err = CONNECTION_POOL_GET_CONNECTION;
		try (Connection connection = ConnectionPool.getConnection();
			PreparedStatement	pSt = connection.prepareStatement(sql);){
			err = "CommonDao.setParameters";
			setParameters(pSt, params);
			err = pSt.toString();
			return pSt.executeUpdate();
		} catch (SQLException e) {
			Logger.logError(COMMON_DAO_EXECUTE_UPDATE, "SqlException occurred with update: " + err, (Exception) e);
		} catch (Exception e) {
			Logger.logError(COMMON_DAO_EXECUTE_UPDATE, "Exception occurred with update: " + err, (Exception) e);
		} 
		return null;
	}

	protected void setParameters(PreparedStatement pSt, List<Object> inParams) throws SQLException {
		if (inParams == null) {
			return;
		}
			int ndx = 1;
			for (Object param : inParams) {
				if (String.class.isInstance(param)) {
					pSt.setString(ndx, (String) param);
				} else if (Double.class.isInstance(param)) {
					pSt.setDouble(ndx, (Double) param);
				} else if (BigDecimal.class.isInstance(param)) {
					pSt.setBigDecimal(ndx, (BigDecimal) param);
				} else if (Long.class.isInstance(param)) {
					pSt.setLong(ndx, (Long) param);
				} else if (Integer.class.isInstance(param)) {
					pSt.setInt(ndx, (Integer) param);
				} else if (LocalDate.class.isInstance(param)) {
					pSt.setDate(ndx, Date.valueOf((LocalDate) param));
				} else if (LocalDateTime.class.isInstance(param)) {
					pSt.setTimestamp(ndx, Timestamp.valueOf((LocalDateTime) param));
				} else if (java.sql.Timestamp.class.isInstance(param)) {
					pSt.setTimestamp(ndx, (java.sql.Timestamp) param);
				} else if (java.sql.Date.class.isInstance(param)) {
					pSt.setDate(ndx, (java.sql.Date) param);
				} else if (java.util.Date.class.isInstance(param)) {
					LocalDateTime ldt = LocalDateTime.ofInstant(((java.util.Date) param).toInstant(),
							ZoneId.systemDefault());
					pSt.setTimestamp(ndx, Timestamp.valueOf(ldt));
				} else if (DbNullValue.class.isInstance(param)) {
					pSt.setNull(ndx, ((DbNullValue) param).getSqlType());
				}
				ndx++;
			}
	}
	
}
