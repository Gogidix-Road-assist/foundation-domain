package com.gogidix.rapidassist.ai.fraud.application.mapper;

import com.gogidix.rapidassist.ai.fraud.application.dto.FraudDetectionDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudAlertDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudCaseDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudPatternDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudRuleDto;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudDetection;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudAlert;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudCase;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudPattern;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for Fraud Detection domain
 */
@Mapper(componentModel = "spring")
public interface FraudDetectionMapper {

    // FraudDetection mappings
    FraudDetectionDto toDto(FraudDetection detection);
    FraudDetection toEntity(FraudDetectionDto dto);
    List<FraudDetectionDto> toDtoList(List<FraudDetection> detections);
    void updateEntityFromDto(FraudDetectionDto dto, @MappingTarget FraudDetection detection);

    // FraudAlert mappings
    FraudAlertDto toDto(FraudAlert alert);
    FraudAlert toEntity(FraudAlertDto dto);
    List<FraudAlertDto> toAlertDtoList(List<FraudAlert> alerts);

    // FraudCase mappings
    FraudCaseDto toDto(FraudCase caseEntity);
    FraudCase toEntity(FraudCaseDto dto);
    List<FraudCaseDto> toCaseDtoList(List<FraudCase> cases);

    // FraudPattern mappings
    FraudPatternDto toDto(FraudPattern pattern);
    FraudPattern toEntity(FraudPatternDto dto);
    List<FraudPatternDto> toPatternDtoList(List<FraudPattern> patterns);

    // FraudRule mappings
    FraudRuleDto toDto(FraudRule rule);
    FraudRule toEntity(FraudRuleDto dto);
    List<FraudRuleDto> toRuleDtoList(List<FraudRule> rules);
}
