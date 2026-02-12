package com.gogidix.rapidassist.ai.report.interfaces.rest.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request to cancel a report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelReportRequest {

    private String reason;
}
