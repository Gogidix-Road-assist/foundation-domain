package com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request context holding tenant and user information
 * Set by TenantInterceptor from HTTP headers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestContext {

    private String tenantId;
    private String userId;
    private String correlationId;
    private String requestId;
}
