package com.gogidix.rapidassist.ai.analytics.application.mapper;

import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

/**
 * Helper mapper for UUID conversions
 */
@Mapper(componentModel = "spring")
public interface UuidMapper {

    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    default UUID stringToUuid(String string) {
        return string != null ? UUID.fromString(string) : null;
    }

    default List<String> uuidListToStringList(List<UUID> uuids) {
        return uuids != null ? uuids.stream().map(UUID::toString).toList() : null;
    }

    default List<UUID> stringListToUuidList(List<String> strings) {
        return strings != null ? strings.stream().map(UUID::fromString).toList() : null;
    }
}
