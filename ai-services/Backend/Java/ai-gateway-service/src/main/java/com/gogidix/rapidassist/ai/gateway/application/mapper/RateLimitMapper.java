package com.gogidix.rapidassist.ai.gateway.application.mapper;

import com.gogidix.rapidassist.ai.gateway.application.dto.RateLimitDto;
import com.gogidix.rapidassist.ai.gateway.domain.model.RateLimit;
import com.gogidix.rapidassist.ai.gateway.domain.model.RateLimitStatus;
import com.gogidix.rapidassist.ai.gateway.domain.model.LimitType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for RateLimit DTO.
 */
@Mapper(componentModel = "spring")
public interface RateLimitMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "rateLimitStatusToString")
    @Mapping(target = "limitType", source = "limitType", qualifiedByName = "limitTypeToString")
    RateLimitDto toDto(RateLimit domain);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToRateLimitStatus")
    @Mapping(target = "limitType", source = "limitType", qualifiedByName = "stringToLimitType")
    RateLimit toDomain(RateLimitDto dto);

    @Named("stringToRateLimitStatus")
    default RateLimitStatus stringToRateLimitStatus(String status) {
        if (status == null) return null;
        try {
            return RateLimitStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("stringToLimitType")
    default LimitType stringToLimitType(String limitType) {
        if (limitType == null) return null;
        try {
            return LimitType.valueOf(limitType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("rateLimitStatusToString")
    default String rateLimitStatusToString(RateLimitStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("limitTypeToString")
    default String limitTypeToString(LimitType limitType) {
        return limitType != null ? limitType.name() : null;
    }
}
