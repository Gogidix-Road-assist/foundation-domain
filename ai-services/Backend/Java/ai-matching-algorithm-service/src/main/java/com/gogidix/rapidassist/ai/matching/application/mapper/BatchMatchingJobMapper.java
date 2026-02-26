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
    @Mapping(target = "totalCount", source = "totalRecords")
    @Mapping(target = "processedCount", source = "processedRecords")
    @Mapping(target = "successCount", source = "successfulMatches")
    @Mapping(target = "failureCount", source = "failedRecords")
    @Mapping(target = "minSimilarityThreshold", ignore = true)
    @Mapping(target = "batchSize", ignore = true)
    @Mapping(target = "configuration", source = "jobParameters")
    BatchMatchingJobDto toDto(BatchMatchingJob domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalRecords", source = "totalCount")
    @Mapping(target = "processedRecords", source = "processedCount")
    @Mapping(target = "successfulMatches", source = "successCount")
    @Mapping(target = "failedRecords", source = "failureCount")
    @Mapping(target = "progressPercentage", ignore = true)
    @Mapping(target = "jobParameters", source = "configuration")
    @Mapping(target = "estimatedCompletionAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", source = "completedAt")
    BatchMatchingJob toDomain(BatchMatchingJobDto dto);

    void updateDomainFromDto(BatchMatchingJobDto dto, @MappingTarget BatchMatchingJob domain);

    List<BatchMatchingJobDto> toDtoList(List<BatchMatchingJob> domains);

    List<BatchMatchingJob> toDomainList(List<BatchMatchingJobDto> dtos);
}
