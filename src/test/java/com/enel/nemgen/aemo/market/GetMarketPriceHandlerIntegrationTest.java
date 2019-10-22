package com.enel.nemgen.aemo.market;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.enel.nemgen.TestContext;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyRequest;
import com.enel.nemgen.common.lambda.proxy.model.ApiGatewayProxyResponse;
import com.enel.nemgen.common.lambda.proxy.model.Authorizer;
import com.enel.nemgen.common.lambda.proxy.model.RequestContext;
import com.enel.nemgen.common.model.HttpCodes;

public class GetMarketPriceHandlerIntegrationTest {
	@BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
    }

    private Context createContext() {
        TestContext ctx = new TestContext();
        ctx.setFunctionName("GetMarketPrice");
        return ctx;
    }
    
    //@Test
    public void testGetMarketPriceHandlerValidationFail() {
    	GetMarketPriceHandler handler = new GetMarketPriceHandler();
        Context ctx = createContext();
        
        ApiGatewayProxyRequest request = new ApiGatewayProxyRequest();
    	request.setResource("/market-price");
    	request.setPath("/market-price");
    	request.setHttpMethod("GET");
    	RequestContext reqContext = new RequestContext();
    	Authorizer authorizer = new Authorizer();
    	authorizer.setPrincipalId("1");
    	reqContext.setAuthorizer(authorizer);
    	request.setRequestContext(reqContext);
    	Map<String, String> headers = new HashMap<String, String>();
    	headers.put("Origin", "http://www.posessed" );
    	request.setHeaders(headers);
    	Map<String, String> queryParams = new HashMap<String, String>();
    	queryParams.put("start_date", "XXX");
    	queryParams.put("end_date", "2019-10-22 12:00");
    	queryParams.put("region", "NSW1");
    	request.setQueryStringParameters(queryParams);
        ApiGatewayProxyResponse output = handler.handleRequest(request, ctx);

        Assert.assertTrue(output.getStatusCode() == HttpCodes.BAD_REQUEST);
        String expectedBody = "{\"code\":400,\"message\":\"Parameter start_date was not in the expected format\",\"message_key\":\"invalid_parameter_value\"}";
        Assert.assertTrue(expectedBody.equals(output.getBody()));
    }
    
    //@Test
    public void testGetMarketPriceHandlerFetch() {
    	GetMarketPriceHandler handler = new GetMarketPriceHandler();
        Context ctx = createContext();
        
        ApiGatewayProxyRequest request = new ApiGatewayProxyRequest();
    	request.setResource("/market-price");
    	request.setPath("/market-price");
    	request.setHttpMethod("GET");
    	RequestContext reqContext = new RequestContext();
    	Authorizer authorizer = new Authorizer();
    	authorizer.setPrincipalId("1");
    	reqContext.setAuthorizer(authorizer);
    	request.setRequestContext(reqContext);
    	Map<String, String> headers = new HashMap<String, String>();
    	headers.put("Origin", "http://www.posessed" );
    	request.setHeaders(headers);
    	Map<String, String> queryParams = new HashMap<String, String>();
    	queryParams.put("start_date", "2019-10-21 00:00");
    	queryParams.put("end_date", "2019-10-21 12:00");
    	queryParams.put("region", "NSW1");
    	request.setQueryStringParameters(queryParams);
        ApiGatewayProxyResponse output = handler.handleRequest(request, ctx);

        Assert.assertTrue(output.getStatusCode() == HttpCodes.OK);
        System.out.println(output.toString());
    }
    
}
