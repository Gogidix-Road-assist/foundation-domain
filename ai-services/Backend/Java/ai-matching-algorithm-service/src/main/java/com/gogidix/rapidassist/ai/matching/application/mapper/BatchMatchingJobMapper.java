package com.gogidix.rapidassist.ai.matching.application.mapper;

import com.gogidix.rapidassist.ai.matching.application.dto.BatchMatchingJobDto;
import com.gogidix.rapidassist.ai.matching.domain.model.BatchMatchingJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct Mapper for BatchMatchingJob DTO.
 */
@Mapper(componentModel = "spring")
public interface BatchMatchingJobMapper {

    @Mapping(target = "jobId", ignore = true)
    @Mapping(target = "totalCount", ignore = true)
    @Mapping(target = "processedCount", ignore = true)
    @Mapping(target = "successCount", ignore = true)
    @Mapping(target = "failureCount", ignore = true)
    @Mapping(target = "minSimilarityThreshold", ignore = true)
    @Mapping(target = "batchSize", ignore = true)
    @Mapping(target = "configuration", ignore = true)
    BatchMatchingJobDto toDto(BatchMatchingJob domain);

    @Mapping(target = "jobCode", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "totalRecords", ignore = true)
    @Mapping(target = "processedRecords", ignore = true)
    @Mapping(target = "successfulMatches", ignore = true)
    @Mapping(target = "failedRecords", ignore = true)
    @Mapping(target = "progressPercentage", ignore = true)
    @Mapping(target = "jobParameters", ignore = true)
    @Mapping(target = "estimatedCompletionAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BatchMatchingJob toDomain(BatchMatchingJobDto dto);

    void updateDomainFromDto(BatchMatchingJobDto dto, @MappingTarget BatchMatchingJob domain);

    List<BatchMatchingJobDto> toDtoList(List<BatchMatchingJob> domains);

    List<BatchMatchingJob> toDomainList(List<BatchMatchingJobDto> dtos);
}
