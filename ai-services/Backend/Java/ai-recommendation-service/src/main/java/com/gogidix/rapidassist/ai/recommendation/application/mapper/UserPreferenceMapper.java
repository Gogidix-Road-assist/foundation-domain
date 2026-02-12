package com.gogidix.rapidassist.ai.recommendation.application.mapper;

import com.gogidix.rapidassist.ai.recommendation.application.dto.UserPreferenceDto;
import com.gogidix.rapidassist.ai.recommendation.domain.model.UserPreference;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for UserPreference.
 */
@Mapper(componentModel = "spring")
public interface UserPreferenceMapper {

    UserPreferenceDto toDto(UserPreference domain);

    UserPreference toDomain(UserPreferenceDto dto);

    List<UserPreferenceDto> toDtoList(List<UserPreference> domains);

    void updateDomainFromDto(UserPreferenceDto dto, @MappingTarget UserPreference domain);
}
