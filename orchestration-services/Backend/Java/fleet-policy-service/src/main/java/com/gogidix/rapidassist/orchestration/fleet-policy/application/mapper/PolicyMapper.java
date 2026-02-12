package com.gogidix.rapidassist.orchestration.fleet_policy.application.mapper;

import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request.PolicyRequestDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request.PolicyRuleRequestDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response.*;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyCompliance;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyRule;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for Policy domain entities and DTOs
 */
@Mapper(componentModel = "spring")
public interface PolicyMapper {

    // Policy mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "rules", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Policy toEntity(PolicyRequestDto dto);

    @Mapping(target = "ruleCount", source = "rules", qualifiedByName = "countRules")
    @Mapping(target = "effective", source = ".", qualifiedByName = "isEffective")
    PolicyResponseDto toResponseDto(Policy entity);

    List<PolicyResponseDto> toPolicyResponseDtoList(List<Policy> entities);

    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PolicyRule toRuleEntity(PolicyRuleRequestDto dto);

    PolicyRuleResponseDto toRuleResponseDto(PolicyRule entity);

    List<PolicyRuleResponseDto> toRuleResponseDtoList(List<PolicyRule> entities);

    @Mapping(target = "overdue", source = ".", qualifiedByName = "isOverdue")
    @Mapping(target = "requiresEscalation", source = ".", qualifiedByName = "requiresEscalation")
    PolicyViolationResponseDto toViolationResponseDto(PolicyViolation entity);

    List<PolicyViolationResponseDto> toViolationResponseDtoList(List<PolicyViolation> entities);

    @Mapping(target = "compliancePercentage", source = ".", qualifiedByName = "calculateCompliancePercentage")
    @Mapping(target = "actionOverdue", source = ".", qualifiedByName = "isActionOverdue")
    PolicyComplianceResponseDto toComplianceResponseDto(PolicyCompliance entity);

    List<PolicyComplianceResponseDto> toComplianceResponseDtoList(List<PolicyCompliance> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(PolicyRequestDto dto, @MappingTarget Policy entity);

    // Custom mapping methods
    @Named("countRules")
    default Integer countRules(List<PolicyRule> rules) {
        return rules != null ? rules.size() : 0;
    }

    @Named("isEffective")
    default Boolean isEffective(Policy policy) {
        return policy.isEffective();
    }

    @Named("isOverdue")
    default Boolean isOverdue(PolicyViolation violation) {
        return violation.isActionOverdue();
    }

    @Named("requiresEscalation")
    default Boolean requiresEscalation(PolicyViolation violation) {
        return violation.requiresEscalation();
    }

    @Named("calculateCompliancePercentage")
    default Double calculateCompliancePercentage(PolicyCompliance compliance) {
        return compliance.getCompliancePercentage();
    }

    @Named("isActionOverdue")
    default Boolean isActionOverdue(PolicyCompliance compliance) {
        return compliance.isActionOverdue();
    }
}
