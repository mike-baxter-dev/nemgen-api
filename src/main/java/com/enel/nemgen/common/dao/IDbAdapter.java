package com.enel.nemgen.common.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.BiFunction;

public interface IDbAdapter {
	public <R, U> R executeQuery(String sql, List<Object> params, U criteria, BiFunction<ResultSet, U, R> resultHandler);
	public Integer executeUpdate(String sql, List<Object> params);
}
