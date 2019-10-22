package com.enel.nemgen;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.enel.nemgen.aemo.market.GetMarketPriceHandler;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyRequest;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse;
import com.enel.nemgen.common.lambda.proxy.model.Authorizer;
import com.enel.nemgen.common.lambda.proxy.model.RequestContext;
import com.enel.nemgen.common.model.ErrorCodes;
import com.enel.nemgen.common.model.HttpCodes;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerTest {


    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }
    
    @Test
    public void testMapManipulation() {
    	LinkedHashMap<String, String> queryStringParameters = new LinkedHashMap<String, String>();
    	queryStringParameters.put("Level", null);
    	Map<String, String> params = keyValuesToLowerCase(queryStringParameters);
    	
    	Assert.assertTrue(params.containsKey("level"));
    }
    protected static <T> Map<String, T> keyValuesToLowerCase(Map<String, T> map) {
    	Map<String, T> result = new HashMap<String, T>();
    	for (Map.Entry<String, T> entry : map.entrySet()) {
    		result.put(getKeyLc(entry.getKey()), (T)(entry.getValue()));
		}
    	return result;
    }
	
	private static String getKeyLc(String key) {
		if(key == null) {
			return UUID.randomUUID().toString().toLowerCase();
		}
		return key.trim().toLowerCase();
	}
}
