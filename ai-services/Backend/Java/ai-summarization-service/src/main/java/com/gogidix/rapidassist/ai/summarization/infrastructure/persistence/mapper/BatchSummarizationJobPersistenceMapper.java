package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.summarization.domain.model.BatchSummarizationJob;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.BatchSummarizationJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface BatchSummarizationJobPersistenceMapper {

    @Mapping(source = "uuid", target = "id")
    BatchSummarizationJob toDomain(BatchSummarizationJobEntity entity);

    @Mapping(source = "id", target = "uuid")
    BatchSummarizationJobEntity toEntity(BatchSummarizationJob domain);

    List<BatchSummarizationJob> toDomainList(List<BatchSummarizationJobEntity> entities);
}
