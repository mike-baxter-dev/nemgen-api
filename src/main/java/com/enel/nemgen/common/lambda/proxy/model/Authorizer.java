package com.enel.nemgen.common.lambda.proxy.model;

public class Authorizer {
	
	private String principalId;
	
	public Authorizer() { }
	
	public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }
    
    public Authorizer(String principalId) {
    	this.principalId = principalId;
    }
}

