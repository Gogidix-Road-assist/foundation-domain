package com.gogidix.rapidassist.ai.matching.application.mapper;

import com.gogidix.rapidassist.ai.matching.application.dto.MatchingResultDto;
import com.gogidix.rapidassist.ai.matching.domain.model.MatchingResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct Mapper for MatchingResult DTO.
 */
@Mapper(componentModel = "spring")
public interface MatchingResultMapper {

    MatchingResultDto toDto(MatchingResult domain);

    MatchingResult toDomain(MatchingResultDto dto);

    void updateDomainFromDto(MatchingResultDto dto, @MappingTarget MatchingResult domain);

    List<MatchingResultDto> toDtoList(List<MatchingResult> domains);

    List<MatchingResult> toDomainList(List<MatchingResultDto> dtos);
}
