package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.matching.domain.model.PatternMatch;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.PatternMatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for PatternMatch.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PatternMatchPersistenceMapper {

    @Mapping(target = "id", ignore = true)
    PatternMatchEntity toEntity(PatternMatch domain);

    @Mapping(source = "uuid", target = "id")
    @Mapping(target = "uuid", ignore = true)
    PatternMatch toDomain(PatternMatchEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    void updateEntityFromDomain(PatternMatch domain, @MappingTarget PatternMatchEntity entity);

    List<PatternMatchEntity> toEntityList(List<PatternMatch> domains);

    List<PatternMatch> toDomainList(List<PatternMatchEntity> entities);
}
