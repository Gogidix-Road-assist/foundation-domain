package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationRequest;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.SummarizationRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface SummarizationRequestPersistenceMapper {

    @Mapping(source = "uuid", target = "id")
    SummarizationRequest toDomain(SummarizationRequestEntity entity);

    @Mapping(source = "id", target = "uuid")
    SummarizationRequestEntity toEntity(SummarizationRequest domain);

    List<SummarizationRequest> toDomainList(List<SummarizationRequestEntity> entities);

    @Mapping(source = "id", target = "uuid")
    void updateEntityFromDomain(SummarizationRequest domain, @MappingTarget SummarizationRequestEntity entity);
}
