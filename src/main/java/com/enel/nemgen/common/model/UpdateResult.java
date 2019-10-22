package com.enel.nemgen.common.model;

public enum UpdateResult {
	Failed,
	NotAllowed,
	Succeeded;
	
	private String code = "";
	private String reason = "";

    public void setReason(String code, String reason) {
    	this.code = code;
        this.reason = reason;
    }

    public String getCode() { return code; }
    public String getReason() { return reason; }
    
    public static UpdateResult getNotAllowedResult(String code, String reason) {
		UpdateResult result = UpdateResult.NotAllowed;
		result.setReason(code, reason);
		return result;
	}
    
    public static UpdateResult getFailedResult(String code, String reason) {
		UpdateResult result = UpdateResult.Failed;
		result.setReason(code, reason);
		return result;
	}
}
