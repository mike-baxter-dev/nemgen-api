package com.enel.nemgen.common.configuration;

import com.amazonaws.services.lambda.runtime.Context;
import com.enel.nemgen.common.dao.Logger;
import com.enel.nemgen.common.utils.StringExtensions;

public class ConfigManager {
	
	public static String ENV_AWS_SECRETS_MANAGER_KEY = "aws_sm_configuration_key";
	public static String ENV_LOCAL_CONFIG_KEY = "lambda_dev_env_config_key";
	
	public static String DB_URL_KEY = "db_url";
	public static String DB_USER_KEY = "db_username";
	public static String DB_PASSWORD_KEY = "db_password";
	public static String DB_DEFAULT_SCHEMA_KEY = "default_schema";
	public static String DB_DEFAULT_SCHEMA = "oms_owner";
	
	
	private static IConfigManager configManager;
	
	public static boolean setConfigManager(Context context){
		String functionName = context.getFunctionName();
		String functionArn = context.getInvokedFunctionArn();
		Integer idx = functionArn.lastIndexOf(":", functionArn.length());	
		String alias = functionArn.substring(idx+1, functionArn.length());
		
		if (functionName.equals(alias) || alias.equals("EXAMPLE")) {
			alias = "$LATEST";
		}
		Logger.logInfo("ConfigManager.setConfigManager - Resolved lambda alias = " + alias);
		configManager = resolveConfigurationProvider();
		try {
			configManager.loadConfig(alias);
		}
		catch(Exception ex){
			Logger.logError("ConfigManager.setConfigManager", "An Error occurred loading configuration from provider "+configManager.getType(), ex);
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static IConfigManager resolveConfigurationProvider() {
		String LOCAL_CONFIG_KEY = System.getenv(ENV_LOCAL_CONFIG_KEY);
		if(!StringExtensions.IsNullOrWhitespace(LOCAL_CONFIG_KEY)){
			Logger.logInfo("Configuration provider is Local Machine - config key="+LOCAL_CONFIG_KEY);
			return new ConfigManagerLocalEnv();
		}
		String AWS_SECRETS_MANAGER_KEY = System.getenv(ENV_AWS_SECRETS_MANAGER_KEY);
		if(!StringExtensions.IsNullOrWhitespace(AWS_SECRETS_MANAGER_KEY)) {
			Logger.logInfo("Configuration provider is AWS Secrets - config key="+AWS_SECRETS_MANAGER_KEY);
			return new ConfigManagerAwsSecrets();
		}
		Logger.logError("ConfigManager.resolveConfigurationProvider", "None of the expected Environment variables were valid for resolution of the configuration provider. Valid options are ["+ENV_AWS_SECRETS_MANAGER_KEY+"] or ["+ENV_LOCAL_CONFIG_KEY+"].");
		return null;
	}
	
	public static String getConfigSetting(String key) {
		return isValid() ? configManager.getConfigSetting(key) : null;
	}
	
	public static boolean isValid() {
		return configManager != null && configManager.isValid();
	}
	
	public static void invalidate() {
		configManager = null;
	}
	
	public static String getType() {
		return configManager == null ? "NULL" : configManager.getType();
	}
	
	public static String getDefaultSchema() {
		String defaultSchema = getConfigSetting(DB_DEFAULT_SCHEMA_KEY);
		return StringExtensions.IsNullOrEmpty(defaultSchema) ? DB_DEFAULT_SCHEMA : defaultSchema;
	}
}
