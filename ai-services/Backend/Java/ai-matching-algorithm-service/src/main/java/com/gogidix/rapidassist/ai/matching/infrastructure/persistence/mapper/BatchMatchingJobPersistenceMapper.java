package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.matching.domain.model.BatchMatchingJob;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.BatchMatchingJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for BatchMatchingJob.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BatchMatchingJobPersistenceMapper {

    @Mapping(target = "id", ignore = true)
    BatchMatchingJobEntity toEntity(BatchMatchingJob domain);

    @Mapping(source = "uuid", target = "id")
    @Mapping(target = "uuid", ignore = true)
    BatchMatchingJob toDomain(BatchMatchingJobEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    void updateEntityFromDomain(BatchMatchingJob domain, @MappingTarget BatchMatchingJobEntity entity);

    List<BatchMatchingJobEntity> toEntityList(List<BatchMatchingJob> domains);

    List<BatchMatchingJob> toDomainList(List<BatchMatchingJobEntity> entities);
}
