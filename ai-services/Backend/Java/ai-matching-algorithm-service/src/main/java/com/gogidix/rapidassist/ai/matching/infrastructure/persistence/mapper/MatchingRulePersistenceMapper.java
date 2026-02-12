package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingRule;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.MatchingRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for MatchingRule.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MatchingRulePersistenceMapper {

    @Mapping(target = "id", ignore = true)
    MatchingRuleEntity toEntity(MatchingRule domain);

    @Mapping(source = "uuid", target = "id")
    MatchingRule toDomain(MatchingRuleEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    void updateEntityFromDomain(MatchingRule domain, @MappingTarget MatchingRuleEntity entity);

    List<MatchingRuleEntity> toEntityList(List<MatchingRule> domains);

    List<MatchingRule> toDomainList(List<MatchingRuleEntity> entities);
}
