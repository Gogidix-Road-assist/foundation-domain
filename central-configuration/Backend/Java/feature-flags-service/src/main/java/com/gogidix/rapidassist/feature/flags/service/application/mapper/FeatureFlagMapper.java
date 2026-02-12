package com.gogidix.rapidassist.feature.flags.service.application.mapper;

import com.gogidix.rapidassist.feature.flags.service.application.dto.response.FeatureFlagResponseDto;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting between FeatureFlag entities and DTOs.
 *
 * <p>This mapper uses compile-time code generation to create efficient
 * mapping code between domain entities and DTOs.
 *
 * <p>MapStruct will generate the implementation at compile time.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FeatureFlagMapper {

    /**
     * Converts a domain FeatureFlag to a Response DTO.
     *
     * @param featureFlag the domain entity
     * @return the response DTO
     */
    @Mapping(target = "percentage", expression = "java(featureFlag.percentageRollout() != null ? featureFlag.percentageRollout().percentage() : null)")
    @Mapping(target = "percentageBucketingKey", expression = "java(featureFlag.percentageRollout() != null ? featureFlag.percentageRollout().bucketingKey() : null)")
    FeatureFlagResponseDto toResponseDto(FeatureFlag featureFlag);

    /**
     * Converts a list of domain FeatureFlags to Response DTOs.
     *
     * @param featureFlags the list of domain entities
     * @return the list of response DTOs
     */
    List<FeatureFlagResponseDto> toResponseDtoList(List<FeatureFlag> featureFlags);
}
