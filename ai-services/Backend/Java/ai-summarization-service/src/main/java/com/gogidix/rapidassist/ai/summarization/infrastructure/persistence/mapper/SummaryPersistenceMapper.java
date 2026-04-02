package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.summarization.domain.model.Summary;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.SummaryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface SummaryPersistenceMapper {

    @Mapping(source = "uuid", target = "id")
    Summary toDomain(SummaryEntity entity);

    @Mapping(source = "id", target = "uuid")
    SummaryEntity toEntity(Summary domain);

    List<Summary> toDomainList(List<SummaryEntity> entities);
}
