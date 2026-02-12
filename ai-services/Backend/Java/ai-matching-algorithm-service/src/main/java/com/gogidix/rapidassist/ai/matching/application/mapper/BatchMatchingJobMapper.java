package com.gogidix.rapidassist.ai.matching.application.mapper;

import com.gogidix.rapidassist.ai.matching.application.dto.BatchMatchingJobDto;
import com.gogidix.rapidassist.ai.matching.domain.model.BatchMatchingJob;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct Mapper for BatchMatchingJob DTO.
 */
@Mapper(componentModel = "spring")
public interface BatchMatchingJobMapper {

    BatchMatchingJobDto toDto(BatchMatchingJob domain);

    BatchMatchingJob toDomain(BatchMatchingJobDto dto);

    void updateDomainFromDto(BatchMatchingJobDto dto, @MappingTarget BatchMatchingJob domain);

    List<BatchMatchingJobDto> toDtoList(List<BatchMatchingJob> domains);

    List<BatchMatchingJob> toDomainList(List<BatchMatchingJobDto> dtos);
}
