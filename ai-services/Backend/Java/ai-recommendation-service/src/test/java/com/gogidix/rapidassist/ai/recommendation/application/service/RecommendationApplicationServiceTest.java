package com.gogidix.rapidassist.ai.recommendation.application.service;

import com.gogidix.rapidassist.ai.recommendation.application.command.CreateRecommendationRequestCommand;
import com.gogidix.rapidassist.ai.recommendation.application.command.UpdateUserPreferenceCommand;
import com.gogidix.rapidassist.ai.recommendation.application.dto.RecommendationResultDto;
import com.gogidix.rapidassist.ai.recommendation.application.dto.UserPreferenceDto;
import com.gogidix.rapidassist.ai.recommendation.application.mapper.RecommendationResultMapper;
import com.gogidix.rapidassist.ai.recommendation.application.mapper.UserPreferenceMapper;
import com.gogidix.rapidassist.ai.recommendation.application.query.GetRecommendationQuery;
import com.gogidix.rapidassist.ai.recommendation.application.query.GetUserPreferencesQuery;
import com.gogidix.rapidassist.ai.recommendation.domain.model.*;
import com.gogidix.rapidassist.ai.recommendation.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RecommendationApplicationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Recommendation Application Service Tests")
class RecommendationApplicationServiceTest {

    @Mock
    private RecommendationRequestRepositoryPort requestRepository;

    @Mock
    private RecommendationResultRepositoryPort resultRepository;

    @Mock
    private UserPreferenceRepositoryPort preferenceRepository;

    @Mock
    private ItemSimilarityRepositoryPort similarityRepository;

    @Mock
    private RecommendationResultMapper resultMapper;

    @Mock
    private UserPreferenceMapper preferenceMapper;

    @InjectMocks
    private RecommendationApplicationService service;

    private final String tenantId = "tenant-123";
    private final String userId = "user-123";
    private final String itemType = "product";
    private final UUID resultId = UUID.randomUUID();

    @Test
    @DisplayName("Should create recommendation successfully")
    void shouldCreateRecommendationSuccessfully() {
        // Given
        CreateRecommendationRequestCommand command = CreateRecommendationRequestCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .recommendationType(RecommendationType.COLLABORATIVE_FILTERING)
                .itemType(itemType)
                .limit(10)
                .ttlMinutes(60)
                .build();

        RecommendationRequest request = RecommendationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .build();

        RecommendationResult result = RecommendationResult.builder()
                .id(resultId)
                .tenantId(tenantId)
                .userId(userId)
                .items(new ArrayList<>())
                .totalResults(0)
                .confidenceScore(0.0)
                .status(RecommendationStatus.COMPLETED)
                .build();

        RecommendationResultDto resultDto = RecommendationResultDto.builder()
                .id(resultId)
                .userId(userId)
                .build();

        when(preferenceRepository.findByUserId(eq(tenantId), eq(userId)))
                .thenReturn(Collections.emptyList());
        when(requestRepository.save(eq(tenantId), any(RecommendationRequest.class)))
                .thenReturn(request);
        when(resultRepository.save(eq(tenantId), any(RecommendationResult.class)))
                .thenReturn(result);
        when(resultMapper.toDto(any(RecommendationResult.class)))
                .thenReturn(resultDto);

        // When
        RecommendationResultDto response = service.createRecommendation(command);

        // Then
        assertNotNull(response);
        assertEquals(resultId, response.getId());
        verify(requestRepository, times(3)).save(eq(tenantId), any(RecommendationRequest.class));
        verify(resultRepository, times(1)).save(eq(tenantId), any(RecommendationResult.class));
        verify(resultMapper, times(1)).toDto(any(RecommendationResult.class));
    }

    @Test
    @DisplayName("Should get recommendation by id")
    void shouldGetRecommendationById() {
        // Given
        GetRecommendationQuery query = GetRecommendationQuery.builder()
                .tenantId(tenantId)
                .resultId(resultId)
                .build();

        RecommendationResult result = RecommendationResult.builder()
                .id(resultId)
                .tenantId(tenantId)
                .userId(userId)
                .status(RecommendationStatus.COMPLETED)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        RecommendationResultDto resultDto = RecommendationResultDto.builder()
                .id(resultId)
                .userId(userId)
                .build();

        when(resultRepository.findById(tenantId, resultId)).thenReturn(Optional.of(result));
        when(resultMapper.toDto(result)).thenReturn(resultDto);

        // When
        RecommendationResultDto response = service.getRecommendation(query);

        // Then
        assertNotNull(response);
        assertEquals(resultId, response.getId());
        verify(resultRepository, times(1)).findById(tenantId, resultId);
        verify(resultMapper, times(1)).toDto(result);
    }

    @Test
    @DisplayName("Should throw exception when recommendation not found")
    void shouldThrowExceptionWhenRecommendationNotFound() {
        // Given
        GetRecommendationQuery query = GetRecommendationQuery.builder()
                .tenantId(tenantId)
                .resultId(resultId)
                .build();

        when(resultRepository.findById(tenantId, resultId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.getRecommendation(query));
        verify(resultRepository, times(1)).findById(tenantId, resultId);
    }

    @Test
    @DisplayName("Should throw exception when recommendation is expired")
    void shouldThrowExceptionWhenRecommendationIsExpired() {
        // Given
        GetRecommendationQuery query = GetRecommendationQuery.builder()
                .tenantId(tenantId)
                .resultId(resultId)
                .build();

        RecommendationResult result = RecommendationResult.builder()
                .id(resultId)
                .tenantId(tenantId)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(resultRepository.findById(tenantId, resultId)).thenReturn(Optional.of(result));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.getRecommendation(query));
        verify(resultRepository, times(1)).findById(tenantId, resultId);
    }

    @Test
    @DisplayName("Should update user preference when exists")
    void shouldUpdateUserPreferenceWhenExists() {
        // Given
        String itemId = "product-123";
        UpdateUserPreferenceCommand command = UpdateUserPreferenceCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .itemType(itemType)
                .itemId(itemId)
                .scoreDelta(0.2)
                .build();

        UserPreference existingPref = UserPreference.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .itemType(itemType)
                .itemId(itemId)
                .preferenceScore(0.5)
                .interactionCount(3)
                .build();

        UserPreferenceDto prefDto = UserPreferenceDto.builder()
                .id(existingPref.getId())
                .userId(userId)
                .itemId(itemId)
                .build();

        when(preferenceRepository.findByUserAndItem(eq(tenantId), eq(userId), eq(itemType), eq(itemId)))
                .thenReturn(Optional.of(existingPref));
        when(preferenceRepository.save(eq(tenantId), any(UserPreference.class)))
                .thenReturn(existingPref);
        when(preferenceMapper.toDto(any(UserPreference.class)))
                .thenReturn(prefDto);

        // When
        UserPreferenceDto response = service.updateUserPreference(command);

        // Then
        assertNotNull(response);
        assertEquals(0.7, existingPref.getPreferenceScore(), 0.001);
        assertEquals(4, existingPref.getInteractionCount());
        verify(preferenceRepository, times(1)).findByUserAndItem(eq(tenantId), eq(userId), eq(itemType), eq(itemId));
        verify(preferenceRepository, times(1)).save(eq(tenantId), any(UserPreference.class));
        verify(preferenceMapper, times(1)).toDto(any(UserPreference.class));
    }

    @Test
    @DisplayName("Should create user preference when not exists")
    void shouldCreateUserPreferenceWhenNotExists() {
        // Given
        String itemId = "product-123";
        UpdateUserPreferenceCommand command = UpdateUserPreferenceCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .itemType(itemType)
                .itemId(itemId)
                .preferenceKey("category")
                .preferenceValue("electronics")
                .scoreDelta(0.5)
                .metadata("{}")
                .build();

        UserPreferenceDto prefDto = UserPreferenceDto.builder()
                .userId(userId)
                .itemId(itemId)
                .build();

        when(preferenceRepository.findByUserAndItem(eq(tenantId), eq(userId), eq(itemType), eq(itemId)))
                .thenReturn(Optional.empty());
        when(preferenceRepository.save(eq(tenantId), any(UserPreference.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));
        when(preferenceMapper.toDto(any(UserPreference.class)))
                .thenReturn(prefDto);

        // When
        UserPreferenceDto response = service.updateUserPreference(command);

        // Then
        assertNotNull(response);
        verify(preferenceRepository, times(1)).findByUserAndItem(eq(tenantId), eq(userId), eq(itemType), eq(itemId));
        verify(preferenceRepository, times(1)).save(eq(tenantId), any(UserPreference.class));
        verify(preferenceMapper, times(1)).toDto(any(UserPreference.class));
    }

    @Test
    @DisplayName("Should get user preferences")
    void shouldGetUserPreferences() {
        // Given
        GetUserPreferencesQuery query = GetUserPreferencesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .itemType(itemType)
                .build();

        UserPreference pref1 = UserPreference.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .itemType(itemType)
                .itemId("product-1")
                .build();

        UserPreference pref2 = UserPreference.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .itemType(itemType)
                .itemId("product-2")
                .build();

        UserPreferenceDto dto1 = UserPreferenceDto.builder()
                .id(pref1.getId())
                .itemId("product-1")
                .build();

        UserPreferenceDto dto2 = UserPreferenceDto.builder()
                .id(pref2.getId())
                .itemId("product-2")
                .build();

        when(preferenceRepository.findByUserIdAndItemType(eq(tenantId), eq(userId), eq(itemType)))
                .thenReturn(Arrays.asList(pref1, pref2));
        when(preferenceMapper.toDto(pref1)).thenReturn(dto1);
        when(preferenceMapper.toDto(pref2)).thenReturn(dto2);

        // When
        List<UserPreferenceDto> response = service.getUserPreferences(query);

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        verify(preferenceRepository, times(1)).findByUserIdAndItemType(eq(tenantId), eq(userId), eq(itemType));
        verify(preferenceMapper, times(2)).toDto(any(UserPreference.class));
    }

    @Test
    @DisplayName("Should get all user preferences when item type is null")
    void shouldGetAllUserPreferencesWhenItemTypeIsNull() {
        // Given
        GetUserPreferencesQuery query = GetUserPreferencesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .itemType(null)
                .build();

        UserPreference pref = UserPreference.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .build();

        UserPreferenceDto dto = UserPreferenceDto.builder()
                .id(pref.getId())
                .build();

        when(preferenceRepository.findByUserId(eq(tenantId), eq(userId)))
                .thenReturn(Collections.singletonList(pref));
        when(preferenceMapper.toDto(any(UserPreference.class)))
                .thenReturn(dto);

        // When
        List<UserPreferenceDto> response = service.getUserPreferences(query);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(preferenceRepository, times(1)).findByUserId(eq(tenantId), eq(userId));
        verify(preferenceMapper, times(1)).toDto(any(UserPreference.class));
    }

    @Test
    @DisplayName("Should delete expired results")
    void shouldDeleteExpiredResults() {
        // Given
        RecommendationResult expired1 = RecommendationResult.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        RecommendationResult expired2 = RecommendationResult.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .expiresAt(LocalDateTime.now().minusHours(2))
                .build();

        when(resultRepository.findExpiredResults(tenantId))
                .thenReturn(Arrays.asList(expired1, expired2));
        doNothing().when(resultRepository).delete(eq(tenantId), any(UUID.class));

        // When
        int deletedCount = service.deleteExpiredResults(tenantId);

        // Then
        assertEquals(2, deletedCount);
        verify(resultRepository, times(1)).findExpiredResults(tenantId);
        verify(resultRepository, times(2)).delete(eq(tenantId), any(UUID.class));
    }

    @Test
    @DisplayName("Should return zero when no expired results")
    void shouldReturnZeroWhenNoExpiredResults() {
        // Given
        when(resultRepository.findExpiredResults(tenantId))
                .thenReturn(Collections.emptyList());

        // When
        int deletedCount = service.deleteExpiredResults(tenantId);

        // Then
        assertEquals(0, deletedCount);
        verify(resultRepository, times(1)).findExpiredResults(tenantId);
        verify(resultRepository, never()).delete(eq(tenantId), any(UUID.class));
    }

    @Test
    @DisplayName("Should handle null score delta in update command")
    void shouldHandleNullScoreDeltaInUpdateCommand() {
        // Given
        String itemId = "product-123";
        UpdateUserPreferenceCommand command = UpdateUserPreferenceCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .itemType(itemType)
                .itemId(itemId)
                .scoreDelta(null)
                .build();

        UserPreference existingPref = UserPreference.builder()
                .id(UUID.randomUUID())
                .preferenceScore(0.5)
                .build();

        UserPreferenceDto prefDto = UserPreferenceDto.builder()
                .id(existingPref.getId())
                .build();

        when(preferenceRepository.findByUserAndItem(eq(tenantId), eq(userId), eq(itemType), eq(itemId)))
                .thenReturn(Optional.of(existingPref));
        when(preferenceRepository.save(eq(tenantId), any(UserPreference.class)))
                .thenReturn(existingPref);
        when(preferenceMapper.toDto(any(UserPreference.class)))
                .thenReturn(prefDto);

        // When
        service.updateUserPreference(command);

        // Then
        assertEquals(0.6, existingPref.getPreferenceScore(), 0.001);
    }

    @Test
    @DisplayName("Should handle null limit in create command")
    void shouldHandleNullLimitInCreateCommand() {
        // Given
        CreateRecommendationRequestCommand command = CreateRecommendationRequestCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .recommendationType(RecommendationType.COLLABORATIVE_FILTERING)
                .itemType(itemType)
                .limit(null)
                .build();

        RecommendationRequest request = RecommendationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .build();

        RecommendationResult result = RecommendationResult.builder()
                .id(resultId)
                .tenantId(tenantId)
                .build();

        RecommendationResultDto resultDto = RecommendationResultDto.builder()
                .id(resultId)
                .build();

        when(preferenceRepository.findByUserId(eq(tenantId), eq(userId)))
                .thenReturn(Collections.emptyList());
        when(requestRepository.save(eq(tenantId), any(RecommendationRequest.class)))
                .thenReturn(request);
        when(resultRepository.save(eq(tenantId), any(RecommendationResult.class)))
                .thenReturn(result);
        when(resultMapper.toDto(any(RecommendationResult.class)))
                .thenReturn(resultDto);

        // When
        RecommendationResultDto response = service.createRecommendation(command);

        // Then
        assertNotNull(response);
        verify(requestRepository, times(3)).save(eq(tenantId), any(RecommendationRequest.class));
    }
}
