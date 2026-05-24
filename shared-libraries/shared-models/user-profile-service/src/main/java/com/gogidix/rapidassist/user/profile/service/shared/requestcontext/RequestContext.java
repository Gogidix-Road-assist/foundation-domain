package com.gogidix.rapidassist.user.profile.service.shared.requestcontext;

public class RequestContext {
    private String requestId;
    private String correlationId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}