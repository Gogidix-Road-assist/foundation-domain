package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for organization hierarchy response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationHierarchyResponseDto {

    private String id;
    private String ancestorId;
    private String descendantId;
    private Integer depth;
    private String ancestorName;
    private String descendantName;
}
