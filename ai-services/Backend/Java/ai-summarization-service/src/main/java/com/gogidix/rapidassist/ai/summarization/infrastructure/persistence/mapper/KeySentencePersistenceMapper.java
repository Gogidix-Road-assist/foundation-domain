package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.summarization.domain.model.KeySentence;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.KeySentenceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface KeySentencePersistenceMapper {

    @Mapping(source = "uuid", target = "id")
    KeySentence toDomain(KeySentenceEntity entity);

    @Mapping(source = "id", target = "uuid")
    KeySentenceEntity toEntity(KeySentence domain);

    List<KeySentence> toDomainList(List<KeySentenceEntity> entities);
}
