package com.gogidix.rapidassist.rate.limit.policy.service.application.mapper;

import com.gogidix.rapidassist.rate.limit.policy.service.application.dto.response.RateLimitPolicyResponseDto;
import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting between RateLimitPolicy entities and DTOs.
 *
 * <p>This mapper uses compile-time code generation to create efficient
 * mapping code between domain entities and DTOs.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RateLimitPolicyMapper {

    /**
     * Converts a RateLimitPolicy entity to a ResponseDto.
     *
     * @param policy the policy entity
     * @return the response DTO
     */
    RateLimitPolicyResponseDto toResponseDto(RateLimitPolicy policy);

    /**
     * Converts a list of RateLimitPolicy entities to Response DTOs.
     *
     * @param policies the list of policy entities
     * @return the list of response DTOs
     */
    List<RateLimitPolicyResponseDto> toResponseDtoList(List<RateLimitPolicy> policies);

    /**
     * Converts a RateLimitConfig entity to a DTO.
     *
     * @param config the config entity
     * @return the config DTO
     */
    RateLimitPolicyResponseDto.RateLimitConfigDto toConfigDto(RateLimitPolicy.RateLimitConfig config);

    /**
     * Converts a Scope entity to a DTO.
     *
     * @param scope the scope entity
     * @return the scope DTO
     */
    RateLimitPolicyResponseDto.ScopeDto toScopeDto(RateLimitPolicy.Scope scope);
}
