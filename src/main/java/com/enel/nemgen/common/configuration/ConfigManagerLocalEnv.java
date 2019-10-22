package com.enel.nemgen.common.configuration;

import com.enel.nemgen.common.utils.StringExtensions;

public class ConfigManagerLocalEnv implements IConfigManager{

	private String lambda_env_key = "";

	@Override
	public void loadConfig(String lambdaAlias) throws Exception {
		// depends on environment variables with correct prefix
		lambda_env_key = System.getenv(ConfigManager.ENV_LOCAL_CONFIG_KEY);
		if(StringExtensions.IsNullOrWhitespace(lambda_env_key)) {
			throw new Exception("Invalid configuration - expected a non empty value for environment variable "+ConfigManager.ENV_LOCAL_CONFIG_KEY);
		}
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String getConfigSetting(String key) {
		return System.getenv(lambda_env_key+key);
	}

	@Override
	public String getType() {
		return "Local Machine Environment Variables";
	}
}
