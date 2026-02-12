package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.AnalysisRequest;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.AnalysisRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for AnalysisRequest persistence
 */
@Mapper(componentModel = "spring")
public interface AnalysisRequestPersistenceMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "analysisType", source = "analysisType", qualifiedByName = "stringToAnalysisType")
    AnalysisRequest toDomain(AnalysisRequestEntity entity);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "analysisType", source = "analysisType", qualifiedByName = "analysisTypeToString")
    AnalysisRequestEntity toEntity(AnalysisRequest domain);

    @Named("statusToString")
    static String statusToString(AnalysisRequest.RequestStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    static AnalysisRequest.RequestStatus stringToStatus(String status) {
        return status != null ? AnalysisRequest.RequestStatus.valueOf(status) : null;
    }

    @Named("analysisTypeToString")
    static String analysisTypeToString(AnalysisRequest.AnalysisType type) {
        return type != null ? type.name() : null;
    }

    @Named("stringToAnalysisType")
    static AnalysisRequest.AnalysisType stringToAnalysisType(String type) {
        return type != null ? AnalysisRequest.AnalysisType.valueOf(type) : null;
    }
}
