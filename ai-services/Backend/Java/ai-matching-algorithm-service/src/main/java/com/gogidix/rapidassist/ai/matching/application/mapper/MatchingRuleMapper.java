package com.gogidix.rapidassist.ai.matching.application.mapper;

import com.gogidix.rapidassist.ai.matching.application.dto.MatchingRuleDto;
import com.gogidix.rapidassist.ai.matching.domain.model.MatchingRule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct Mapper for MatchingRule DTO.
 */
@Mapper(componentModel = "spring")
public interface MatchingRuleMapper {

    MatchingRuleDto toDto(MatchingRule domain);

    MatchingRule toDomain(MatchingRuleDto dto);

    void updateDomainFromDto(MatchingRuleDto dto, @MappingTarget MatchingRule domain);

    List<MatchingRuleDto> toDtoList(List<MatchingRule> domains);

    List<MatchingRule> toDomainList(List<MatchingRuleDto> dtos);
}
