package com.gogidix.rapidassist.release.rollout.config.service.application.mapper;

import com.gogidix.rapidassist.release.rollout.config.service.application.dto.response.ReleaseRolloutResponseDto;
import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting between ReleaseRollout entities and DTOs.
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
public interface ReleaseRolloutMapper {

    /**
     * Converts a domain ReleaseRollout to a Response DTO.
     *
     * @param rollout the domain entity
     * @return the response DTO
     */
    ReleaseRolloutResponseDto toResponseDto(ReleaseRollout rollout);

    /**
     * Converts a list of domain ReleaseRollouts to Response DTOs.
     *
     * @param rollouts the list of domain entities
     * @return the list of response DTOs
     */
    List<ReleaseRolloutResponseDto> toResponseDtoList(List<ReleaseRollout> rollouts);
}
