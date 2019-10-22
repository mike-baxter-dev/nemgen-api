package com.enel.nemgen.common.lambda.proxy.model;

import java.util.Objects;

public class LambdaException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1496836229134688836L;
	private final ApiGatewayProxyResponse response;

    public LambdaException(ApiGatewayProxyResponse response) {
        Objects.requireNonNull(response);
        this.response = response;
    }

    public ApiGatewayProxyResponse getResponse() {
        return response;
    }
}