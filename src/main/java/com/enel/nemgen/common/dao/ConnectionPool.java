package com.enel.nemgen.common.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

import com.enel.nemgen.common.configuration.ConfigManager;

public class ConnectionPool {
	private static ConnectionPool datasource;
	private static BasicDataSource ds;

	private ConnectionPool() throws  SQLException {
			
		ds = new BasicDataSource();
		ds.setDriverClassName("org.postgresql.Driver");
		
		ds.setUsername(ConfigManager.getConfigSetting(ConfigManager.DB_USER_KEY));
		ds.setPassword(ConfigManager.getConfigSetting(ConfigManager.DB_PASSWORD_KEY));
		ds.setUrl(ConfigManager.getConfigSetting(ConfigManager.DB_URL_KEY));
				
		if(!Logger.isInitialised()) {
			throw new SQLException("Lambda Logger has not been initialised! - ensure logging is initialised before creating connection pool");
		}
		Logger.logInfo("User = "+ds.getUsername()+" URL = "+ds.getUrl());

		// the settings below are optional -- dbcp can work with defaults
		ds.setMinIdle(1);
		ds.setMaxIdle(5);
		ds.setMaxOpenPreparedStatements(36);

	}

	public static void initialise() throws  SQLException {
		if (datasource == null) {
			datasource = new ConnectionPool();

		} else {
			;
		}
	}

	public static Connection getConnection() throws SQLException {
		initialise();
		return ds.getConnection();

	}
}