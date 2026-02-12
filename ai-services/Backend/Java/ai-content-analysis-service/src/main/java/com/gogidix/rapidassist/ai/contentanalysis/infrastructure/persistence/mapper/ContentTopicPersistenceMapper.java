package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentTopic;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ContentTopicEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for ContentTopic persistence
 */
@Mapper(componentModel = "spring")
public interface ContentTopicPersistenceMapper {

    ContentTopic toDomain(ContentTopicEntity entity);
    ContentTopicEntity toEntity(ContentTopic domain);
}
