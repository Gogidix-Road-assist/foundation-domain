package com.gogidix.rapidassist.ai.contentanalysis.application.service;

import com.gogidix.rapidassist.ai.contentanalysis.application.command.AnalyzeContentCommand;
import com.gogidix.rapidassist.ai.contentanalysis.application.dto.ContentAnalysisDto;
import com.gogidix.rapidassist.ai.contentanalysis.application.mapper.ContentAnalysisMapper;
import com.gogidix.rapidassist.ai.contentanalysis.application.port.out.*;
import com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentAnalysis;
import com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentMetrics;
import com.gogidix.rapidassist.ai.contentanalysis.domain.model.SentimentAnalysis;
import com.gogidix.rapidassist.ai.contentanalysis.domain.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ContentAnalysisApplicationService
 */
@ExtendWith(MockitoExtension.class)
class ContentAnalysisApplicationServiceTest {

    @Mock
    private ContentAnalysisRepositoryPort analysisRepository;

    @Mock
    private ContentTopicRepositoryPort topicRepository;

    @Mock
    private AnalysisRequestRepositoryPort requestRepository;

    @Mock
    private ContentAnalysisMapper mapper;

    @InjectMocks
    private ContentAnalysisApplicationService applicationService;

    private String tenantId = "test-tenant";

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId(tenantId);
    }

    @Test
    void testAnalyzeContent() {
        AnalyzeContentCommand command = AnalyzeContentCommand.builder()
                .tenantId(tenantId)
                .contentId("content-1")
                .contentType("article")
                .contentTitle("Test Article")
                .contentBody("This is a test article content.")
                .contentLanguage("en")
                .build();

        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .contentId("content-1")
                .status(ContentAnalysis.AnalysisStatus.COMPLETED)
                .build();

        when(analysisRepository.save(any(ContentAnalysis.class))).thenReturn(analysis);
        when(mapper.toDto(any(ContentAnalysis.class))).thenReturn(ContentAnalysisDto.builder()
                .id(analysis.getId())
                .contentId(analysis.getContentId())
                .status("COMPLETED")
                .build());

        ContentAnalysisDto result = applicationService.analyzeContent(command);

        assertNotNull(result);
        assertEquals("content-1", result.getContentId());
        assertEquals("COMPLETED", result.getStatus());
        verify(analysisRepository, times(1)).save(any(ContentAnalysis.class));
    }

    @Test
    void testGetAnalysis() {
        UUID analysisId = UUID.randomUUID();
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(analysisId)
                .tenantId(tenantId)
                .contentId("content-1")
                .status(ContentAnalysis.AnalysisStatus.COMPLETED)
                .build();

        when(analysisRepository.findByIdAndTenantId(analysisId, tenantId))
                .thenReturn(Optional.of(analysis));
        when(mapper.toDto(any(ContentAnalysis.class))).thenReturn(ContentAnalysisDto.builder()
                .id(analysisId)
                .contentId("content-1")
                .status("COMPLETED")
                .build());

        var query = com.gogidix.rapidassist.ai.contentanalysis.application.query.GetContentAnalysisQuery.builder()
                .tenantId(tenantId)
                .analysisId(analysisId)
                .build();

        ContentAnalysisDto result = applicationService.getAnalysis(query);

        assertNotNull(result);
        assertEquals(analysisId, result.getId());
        verify(analysisRepository, times(1)).findByIdAndTenantId(analysisId, tenantId);
    }

    @Test
    void testGetAnalysisNotFound() {
        UUID analysisId = UUID.randomUUID();

        when(analysisRepository.findByIdAndTenantId(analysisId, tenantId))
                .thenReturn(Optional.empty());

        var query = com.gogidix.rapidassist.ai.contentanalysis.application.query.GetContentAnalysisQuery.builder()
                .tenantId(tenantId)
                .analysisId(analysisId)
                .build();

        assertThrows(IllegalArgumentException.class, () -> applicationService.getAnalysis(query));
    }

    @Test
    void testDeleteAnalysis() {
        UUID analysisId = UUID.randomUUID();
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(analysisId)
                .tenantId(tenantId)
                .contentId("content-1")
                .build();

        when(analysisRepository.findByIdAndTenantId(analysisId, tenantId))
                .thenReturn(Optional.of(analysis));
        doNothing().when(topicRepository).deleteByContentIdAndTenantId("content-1", tenantId);
        doNothing().when(analysisRepository).deleteByIdAndTenantId(analysisId, tenantId);

        applicationService.deleteAnalysis(tenantId, analysisId);

        verify(topicRepository, times(1)).deleteByContentIdAndTenantId("content-1", tenantId);
        verify(analysisRepository, times(1)).deleteByIdAndTenantId(analysisId, tenantId);
    }

    @Test
    void testDeleteAnalysisNotFound() {
        UUID analysisId = UUID.randomUUID();

        when(analysisRepository.findByIdAndTenantId(analysisId, tenantId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> applicationService.deleteAnalysis(tenantId, analysisId));
    }
}
