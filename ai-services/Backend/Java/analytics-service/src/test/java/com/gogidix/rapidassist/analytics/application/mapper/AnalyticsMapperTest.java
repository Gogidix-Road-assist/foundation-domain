package com.gogidix.rapidassist.analytics.application.mapper;

import com.gogidix.rapidassist.analytics.application.dto.AnalyticsDto;
import com.gogidix.rapidassist.analytics.domain.model.Analytics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnalyticsMapper.
 * Tests MapStruct mapping between domain and DTO.
 */
@DisplayName("Analytics Mapper Tests")
class AnalyticsMapperTest {

    private AnalyticsMapper mapper;
    private Analytics testDomain;
    private AnalyticsDto testDto;
    private UUID analyticsId;
    private String tenantId;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AnalyticsMapper.class);

        analyticsId = UUID.randomUUID();
        tenantId = "tenant-123";

        testDomain = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .dataSource("DATABASE")
                .metrics(Map.of("views", 1000, "clicks", 500))
                .dimensions(Map.of("region", "US"))
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .computedAt(LocalDateTime.now())
                .status("PENDING")
                .totalRecords(1500)
                .aggregationValue(1000.0)
                .aggregationType("SUM")
                .computedBy("system")
                .description("Test analytics")
                .metadata(Map.of("version", "1.0"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testDto = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("USER_ENGAGEMENT")
                .dataSource("DATABASE")
                .metrics(Map.of("views", 1000, "clicks", 500))
                .dimensions(Map.of("region", "US"))
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now())
                .computedAt(LocalDateTime.now())
                .status("PENDING")
                .totalRecords(1500)
                .aggregationValue(1000.0)
                .aggregationType("SUM")
                .computedBy("system")
                .description("Test analytics")
                .metadata(Map.of("version", "1.0"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should map domain to DTO correctly")
    void shouldMapDomainToDtoCorrectly() {
        // When
        AnalyticsDto result = mapper.toDto(testDomain);

        // Then
        assertNotNull(result);
        assertEquals(testDomain.getId(), result.getId());
        assertEquals(testDomain.getTenantId(), result.getTenantId());
        assertEquals(testDomain.getAnalyticsType(), result.getAnalyticsType());
        assertEquals(testDomain.getDataSource(), result.getDataSource());
        assertEquals(testDomain.getMetrics(), result.getMetrics());
        assertEquals(testDomain.getDimensions(), result.getDimensions());
        assertEquals(testDomain.getStartTime(), result.getStartTime());
        assertEquals(testDomain.getEndTime(), result.getEndTime());
        assertEquals(testDomain.getComputedAt(), result.getComputedAt());
        assertEquals(testDomain.getStatus(), result.getStatus());
        assertEquals(testDomain.getTotalRecords(), result.getTotalRecords());
        assertEquals(testDomain.getAggregationValue(), result.getAggregationValue());
        assertEquals(testDomain.getAggregationType(), result.getAggregationType());
        assertEquals(testDomain.getComputedBy(), result.getComputedBy());
        assertEquals(testDomain.getDescription(), result.getDescription());
        assertEquals(testDomain.getMetadata(), result.getMetadata());
        assertEquals(testDomain.getCreatedAt(), result.getCreatedAt());
        assertEquals(testDomain.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map DTO to domain correctly")
    void shouldMapDtoToDomainCorrectly() {
        // When
        Analytics result = mapper.toDomain(testDto);

        // Then
        assertNotNull(result);
        assertEquals(testDto.getId(), result.getId());
        assertEquals(testDto.getTenantId(), result.getTenantId());
        assertEquals(testDto.getAnalyticsType(), result.getAnalyticsType());
        assertEquals(testDto.getDataSource(), result.getDataSource());
        assertEquals(testDto.getMetrics(), result.getMetrics());
        assertEquals(testDto.getDimensions(), result.getDimensions());
        assertEquals(testDto.getStartTime(), result.getStartTime());
        assertEquals(testDto.getEndTime(), result.getEndTime());
        assertEquals(testDto.getComputedAt(), result.getComputedAt());
        assertEquals(testDto.getStatus(), result.getStatus());
        assertEquals(testDto.getTotalRecords(), result.getTotalRecords());
        assertEquals(testDto.getAggregationValue(), result.getAggregationValue());
        assertEquals(testDto.getAggregationType(), result.getAggregationType());
        assertEquals(testDto.getComputedBy(), result.getComputedBy());
        assertEquals(testDto.getDescription(), result.getDescription());
        assertEquals(testDto.getMetadata(), result.getMetadata());
        assertEquals(testDto.getCreatedAt(), result.getCreatedAt());
        assertEquals(testDto.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map list of domains to list of DTOs")
    void shouldMapListOfDomainsToListOfDtos() {
        // Given
        Analytics domain1 = Analytics.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_1")
                .build();
        Analytics domain2 = Analytics.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_2")
                .build();
        Analytics domain3 = Analytics.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType("TYPE_3")
                .build();

        List<Analytics> domains = List.of(domain1, domain2, domain3);

        // When
        List<AnalyticsDto> result = mapper.toDtoList(domains);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(domain1.getId(), result.get(0).getId());
        assertEquals(domain2.getId(), result.get(1).getId());
        assertEquals(domain3.getId(), result.get(2).getId());
        assertEquals(domain1.getAnalyticsType(), result.get(0).getAnalyticsType());
        assertEquals(domain2.getAnalyticsType(), result.get(1).getAnalyticsType());
        assertEquals(domain3.getAnalyticsType(), result.get(2).getAnalyticsType());
    }

    @Test
    @DisplayName("Should return empty list when mapping empty domain list")
    void shouldReturnEmptyListWhenMappingEmptyDomainList() {
        // Given
        List<Analytics> emptyList = List.of();

        // When
        List<AnalyticsDto> result = mapper.toDtoList(emptyList);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return null when mapping null domain")
    void shouldReturnNullWhenMappingNullDomain() {
        // When
        AnalyticsDto result = mapper.toDto(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when mapping null DTO")
    void shouldReturnNullWhenMappingNullDto() {
        // When
        Analytics result = mapper.toDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should update domain from DTO")
    void shouldUpdateDomainFromDto() {
        // Given
        Analytics domainToUpdate = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .analyticsType("OLD_TYPE")
                .status("PENDING")
                .build();

        AnalyticsDto updateDto = AnalyticsDto.builder()
                .analyticsType("NEW_TYPE")
                .status("COMPLETED")
                .aggregationValue(2000.0)
                .build();

        // When
        mapper.updateDomainFromDto(updateDto, domainToUpdate);

        // Then
        assertEquals("NEW_TYPE", domainToUpdate.getAnalyticsType());
        assertEquals("COMPLETED", domainToUpdate.getStatus());
        assertEquals(2000.0, domainToUpdate.getAggregationValue());
        assertEquals(analyticsId, domainToUpdate.getId()); // ID should remain unchanged
        assertEquals(tenantId, domainToUpdate.getTenantId()); // TenantId should remain unchanged
    }

    @Test
    @DisplayName("Should map domain with null values")
    void shouldMapDomainWithNullValues() {
        // Given
        Analytics domainWithNulls = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .metrics(null)
                .dimensions(null)
                .metadata(null)
                .build();

        // When
        AnalyticsDto result = mapper.toDto(domainWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(analyticsId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertNull(result.getMetrics());
        assertNull(result.getDimensions());
        assertNull(result.getMetadata());
    }

    @Test
    @DisplayName("Should map DTO with null values")
    void shouldMapDtoWithNullValues() {
        // Given
        AnalyticsDto dtoWithNulls = AnalyticsDto.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .metrics(null)
                .dimensions(null)
                .metadata(null)
                .build();

        // When
        Analytics result = mapper.toDomain(dtoWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(analyticsId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertNull(result.getMetrics());
        assertNull(result.getDimensions());
        assertNull(result.getMetadata());
    }

    @Test
    @DisplayName("Should map complex metrics structure")
    void shouldMapComplexMetricsStructure() {
        // Given
        Map<String, Object> complexMetrics = Map.of(
                "views", 10000,
                "clicks", 5000,
                "conversions", 250,
                "revenue", 50000.0,
                "ctr", 0.5,
                "cr", 0.05
        );

        Analytics domainWithComplexMetrics = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .metrics(complexMetrics)
                .build();

        // When
        AnalyticsDto result = mapper.toDto(domainWithComplexMetrics);

        // Then
        assertNotNull(result);
        assertEquals(complexMetrics, result.getMetrics());
        assertEquals(6, result.getMetrics().size());
        assertEquals(10000, result.getMetrics().get("views"));
        assertEquals(50000.0, result.getMetrics().get("revenue"));
    }

    @Test
    @DisplayName("Should map complex dimensions structure")
    void shouldMapComplexDimensionsStructure() {
        // Given
        Map<String, Object> complexDimensions = Map.of(
                "region", "US",
                "country", "United States",
                "city", "New York",
                "device", "mobile",
                "browser", "chrome"
        );

        Analytics domainWithComplexDimensions = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .dimensions(complexDimensions)
                .build();

        // When
        AnalyticsDto result = mapper.toDto(domainWithComplexDimensions);

        // Then
        assertNotNull(result);
        assertEquals(complexDimensions, result.getDimensions());
        assertEquals(5, result.getDimensions().size());
        assertEquals("US", result.getDimensions().get("region"));
        assertEquals("mobile", result.getDimensions().get("device"));
    }

    @Test
    @DisplayName("Should map empty collections")
    void shouldMapEmptyCollections() {
        // Given
        Analytics domainWithEmptyCollections = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .metrics(Map.of())
                .dimensions(Map.of())
                .metadata(Map.of())
                .build();

        // When
        AnalyticsDto result = mapper.toDto(domainWithEmptyCollections);

        // Then
        assertNotNull(result);
        assertNotNull(result.getMetrics());
        assertNotNull(result.getDimensions());
        assertNotNull(result.getMetadata());
        assertTrue(result.getMetrics().isEmpty());
        assertTrue(result.getDimensions().isEmpty());
        assertTrue(result.getMetadata().isEmpty());
    }

    @Test
    @DisplayName("Should preserve all timestamp fields")
    void shouldPreserveAllTimestampFields() {
        // Given
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
        LocalDateTime computedAt = LocalDateTime.of(2024, 2, 1, 10, 30, 0);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 2, 1, 10, 30, 0);

        Analytics domainWithTimestamps = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .startTime(startTime)
                .endTime(endTime)
                .computedAt(computedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // When
        AnalyticsDto result = mapper.toDto(domainWithTimestamps);

        // Then
        assertNotNull(result);
        assertEquals(startTime, result.getStartTime());
        assertEquals(endTime, result.getEndTime());
        assertEquals(computedAt, result.getComputedAt());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(updatedAt, result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map all aggregation types")
    void shouldMapAllAggregationTypes() {
        // Given
        String[] aggregationTypes = {"SUM", "AVERAGE", "COUNT", "MIN", "MAX", "MEDIAN"};

        for (String aggregationType : aggregationTypes) {
            Analytics domain = Analytics.builder()
                    .id(UUID.randomUUID())
                    .tenantId(tenantId)
                    .aggregationType(aggregationType)
                    .build();

            // When
            AnalyticsDto result = mapper.toDto(domain);

            // Then
            assertEquals(aggregationType, result.getAggregationType());
        }
    }

    @Test
    @DisplayName("Should map all status values")
    void shouldMapAllStatusValues() {
        // Given
        String[] statuses = {"PENDING", "PROCESSING", "COMPLETED", "FAILED", "CANCELLED"};

        for (String status : statuses) {
            Analytics domain = Analytics.builder()
                    .id(UUID.randomUUID())
                    .tenantId(tenantId)
                    .status(status)
                    .build();

            // When
            AnalyticsDto result = mapper.toDto(domain);

            // Then
            assertEquals(status, result.getStatus());
        }
    }

    @Test
    @DisplayName("Should handle large numeric values")
    void shouldHandleLargeNumericValues() {
        // Given
        Analytics domainWithLargeValues = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .totalRecords(Integer.MAX_VALUE)
                .aggregationValue(Double.MAX_VALUE)
                .build();

        // When
        AnalyticsDto result = mapper.toDto(domainWithLargeValues);

        // Then
        assertEquals(Integer.MAX_VALUE, result.getTotalRecords());
        assertEquals(Double.MAX_VALUE, result.getAggregationValue());
    }

    @Test
    @DisplayName("Should handle negative values")
    void shouldHandleNegativeValues() {
        // Given
        Analytics domainWithNegativeValues = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .totalRecords(-1)
                .aggregationValue(-999.99)
                .build();

        // When
        AnalyticsDto result = mapper.toDto(domainWithNegativeValues);

        // Then
        assertEquals(-1, result.getTotalRecords());
        assertEquals(-999.99, result.getAggregationValue());
    }

    @Test
    @DisplayName("Should handle zero values")
    void shouldHandleZeroValues() {
        // Given
        Analytics domainWithZeroValues = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .totalRecords(0)
                .aggregationValue(0.0)
                .build();

        // When
        AnalyticsDto result = mapper.toDto(domainWithZeroValues);

        // Then
        assertEquals(0, result.getTotalRecords());
        assertEquals(0.0, result.getAggregationValue());
    }

    @Test
    @DisplayName("Should map decimal values accurately")
    void shouldMapDecimalValuesAccurately() {
        // Given
        double preciseValue = 1234.56789;
        Analytics domainWithDecimal = Analytics.builder()
                .id(analyticsId)
                .tenantId(tenantId)
                .aggregationValue(preciseValue)
                .build();

        // When
        AnalyticsDto result = mapper.toDto(domainWithDecimal);

        // Then
        assertEquals(preciseValue, result.getAggregationValue(), 0.00001);
    }
}
