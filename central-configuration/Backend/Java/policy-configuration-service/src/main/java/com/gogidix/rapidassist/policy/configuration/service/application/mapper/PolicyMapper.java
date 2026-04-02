package com.gogidix.rapidassist.policy.configuration.service.application.mapper;

import com.gogidix.rapidassist.policy.configuration.service.application.dto.response.PolicyResponseDto;
import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting between Policy entities and DTOs.
 *
 * <p>This mapper uses compile-time code generation to create efficient
 * mapping code between domain entities and DTOs.
 *
 * <p>MapStruct will generate the implementation at compile time.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PolicyMapper {

    /**
     * Converts a domain Policy to a Response DTO.
     *
     * @param policy the domain entity
     * @return the response DTO
     */
    @Mapping(target = "type", expression = "java(policy.type().name())")
    @Mapping(target = "status", expression = "java(policy.status().name())")
    @Mapping(target = "scope", expression = "java(scopeToMap(policy.scope()))")
    @Mapping(target = "constraints", expression = "java(constraintsToMap(policy.constraints()))")
    PolicyResponseDto toResponseDto(Policy policy);

    /**
     * Converts a list of domain Policies to Response DTOs.
     *
     * @param policies the list of domain entities
     * @return the list of response DTOs
     */
    List<PolicyResponseDto> toResponseDtoList(List<Policy> policies);

    /**
     * Helper method to convert PolicyScope to Map.
     */
    default java.util.Map<String, Object> scopeToMap(Policy.PolicyScope scope) {
        if (scope == null) {
            return java.util.Map.of();
        }
        return java.util.Map.of(
            "type", scope.type().name(),
            "entityTypes", scope.entityTypes(),
            "entityIds", scope.entityIds(),
            "attributes", scope.attributes()
        );
    }

    /**
     * Helper method to convert PolicyConstraints to Map.
     */
    default java.util.Map<String, Object> constraintsToMap(Policy.PolicyConstraints constraints) {
        if (constraints == null) {
            return java.util.Map.of();
        }
        return java.util.Map.of(
            "maxRetries", constraints.maxRetries(),
            "timeoutMs", constraints.timeoutMs(),
            "requireApproval", constraints.requireApproval(),
            "requiredRoles", constraints.requiredRoles()
        );
    }
}
