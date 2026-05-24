package com.gogidix.rapidassist.country.localization.config.service.application.mapper;

import com.gogidix.rapidassist.country.localization.config.service.application.dto.response.CountryLocalizationResponseDto;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting between CountryLocalization entities and DTOs.
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
public interface CountryLocalizationMapper {

    /**
     * Converts a domain CountryLocalization to a Response DTO.
     *
     * @param localization the domain entity
     * @return the response DTO
     */
    CountryLocalizationResponseDto toResponseDto(CountryLocalization localization);

    /**
     * Converts a list of domain CountryLocalizations to Response DTOs.
     *
     * @param localizations the list of domain entities
     * @return the list of response DTOs
     */
    List<CountryLocalizationResponseDto> toResponseDtoList(List<CountryLocalization> localizations);
}
