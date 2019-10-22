package com.enel.nemgen.common.configuration;

public interface IConfigManager {

	public void loadConfig(String lambdaAlias) throws Exception;
    
    public boolean isValid();
    
    public String getConfigSetting(String key);
    
    public String getType();
}
