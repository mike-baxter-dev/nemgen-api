package com.enel.nemgen.common.configuration;

import java.util.Base64;
import java.util.HashMap;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.InvalidParameterException;
import com.amazonaws.services.cognitoidentity.model.ResourceNotFoundException;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.enel.nemgen.common.dao.Logger;
import com.enel.nemgen.common.utils.StringExtensions;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigManagerAwsSecrets implements IConfigManager{

	static String SM_PG_DRIVER_KEY = "postgresql";
	static String SM_DEFAULT_USER_KEY = "lambda-ws-user";
	static String SM_ENV_TARGET_USER_OVERRIDE_KEY = "aws_sm_configuration_user_key";
	static HashMap<String, String> config = null;
	static boolean isLoaded = false;
	
	@Override
	public void loadConfig(String lambdaAlias) throws Exception {
		String secretName = resolveSecretName(lambdaAlias); // e.g. "latest/ams/postgresql/lambda-ws-user";
	    Logger.logInfo("ConfigManagerAwsSecrets.loadConfig - Resolved seceret name = " + secretName);
	    
	    String region = Regions.fromName(System.getenv("AWS_DEFAULT_REGION")).getName(); // e.g. "ap-southeast-2";
	    
	    Logger.logInfo("ConfigManagerAwsSecrets.loadConfig - Resolved region = " + region);
	    
	    // Create a Secrets Manager client
	    AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
	                                    .withRegion(region)
	                                    .build();
	    
	    String secretInfo = "";
	    
	    GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
	                    .withSecretId(secretName);
	    GetSecretValueResult getSecretValueResult = null;

	    try {
	        getSecretValueResult = client.getSecretValue(getSecretValueRequest);
	        if (getSecretValueResult.getSecretString() != null) {
	        	secretInfo = getSecretValueResult.getSecretString();
		    }
		    else {
		    	Logger.logInfo("**encrypted** Decoding Binary...");
		    	secretInfo = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
		    }
	        //secret = " Secret Name = "+getSecretValueResult.getName()+" Secret String = "+getSecretValueResult.getSecretString();
	        final ObjectMapper objectMapper = new ObjectMapper();

            @SuppressWarnings("unchecked")
			final HashMap<String, String> secretMap  = objectMapper.readValue(secretInfo, HashMap.class);

            String url = String.format("jdbc:postgresql://%s:%s/%s", secretMap.get("host"), secretMap.get("port"), secretMap.get("db_name"));
            secretMap.put(ConfigManager.DB_URL_KEY, url);
            secretMap.put(ConfigManager.DB_USER_KEY, secretMap.get("username"));
            secretMap.put(ConfigManager.DB_PASSWORD_KEY, secretMap.get("password"));
            
            
            Logger.logInfo("Secret url = "+secretMap.get(ConfigManager.DB_URL_KEY));
            Logger.logInfo("Secret username = "+secretMap.get(ConfigManager.DB_USER_KEY));
            Logger.logInfo("Default DB schema = "+secretMap.get(ConfigManager.DB_DEFAULT_SCHEMA_KEY));
            config = secretMap;
            isLoaded = true;
	        
	    } catch (DecryptionFailureException e) {
	        Logger.logError("ConfigManagerAwsSecrets.loadConfig", "Failed during decryption of the secret");
	        throw e;
	    } catch (InternalServiceErrorException e) {
	    	Logger.logError("ConfigManagerAwsSecrets.loadConfig", "Failed due to internal error in AWS SM service");
	        throw e;
	    } catch (InvalidParameterException e) {
	    	Logger.logError("ConfigManagerAwsSecrets.loadConfig", "Failed due to an invalid value for one of the provided parameters");
	        throw e;
	    } catch (InvalidRequestException e) {
	    	Logger.logError("ConfigManagerAwsSecrets.loadConfig", "Failed due to a parameter value that is not valid for the current state of the resource");
	        throw e;
	    } catch (ResourceNotFoundException e) {
	    	Logger.logError("ConfigManagerAwsSecrets.loadConfig", "Failed because the service could not find the requested resource");
	        throw e;
	    }
	    catch (Exception e) {
	    	Logger.logError("ConfigManagerAwsSecrets.loadConfig", "Failed due to unexpected error!");
	        throw e;
	    }
		
	}
	
	private static String resolveSecretName(String alias){
		String part1 = StringExtensions.ToLowerTrimmed(alias).replace("$", "");
		String part2 = System.getenv(ConfigManager.ENV_AWS_SECRETS_MANAGER_KEY);
		String part4 = StringExtensions.IsNullOrWhitespace(System.getenv(SM_ENV_TARGET_USER_OVERRIDE_KEY)) ? SM_DEFAULT_USER_KEY : System.getenv(SM_ENV_TARGET_USER_OVERRIDE_KEY);
		return part1+"/"+part2+"/"+SM_PG_DRIVER_KEY+"/"+part4;
	}

	@Override
	public boolean isValid() {
		return isLoaded && config != null;
	}

	@Override
	public String getConfigSetting(String key) {
		if(isValid()) {
			return config.get(key);
		}
		return null;
	}

	@Override
	public String getType() {
		return "AWS Secrets Manager";
	}

}
