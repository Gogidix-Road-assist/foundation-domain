package com.gogidix.rapidassist.ai.categorization.application.service;

import com.gogidix.rapidassist.ai.categorization.application.port.out.CategorizationRequestRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.application.port.out.CategorizationResultRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationRequest;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategorizationService {

    private final CategorizationRequestRepositoryPort requestRepository;
    private final CategorizationResultRepositoryPort resultRepository;

    public CategorizationRequest createCategorizationRequest(String tenantId, String contentId, String contentType, String content, UUID taxonomyId) {
        log.info("Creating categorization request for content: {} in tenant: {}", contentId, tenantId);
        
        CategorizationRequest request = CategorizationRequest.create(tenantId, contentId, contentType, content, taxonomyId);
        return requestRepository.save(tenantId, request);
    }

    public CategorizationResult categorizeContent(String tenantId, String contentId, String contentType, String content, UUID taxonomyId) {
        log.info("Categorizing content: {} using taxonomy: {} in tenant: {}", contentId, taxonomyId, tenantId);
        
        // Create request
        CategorizationRequest request = createCategorizationRequest(tenantId, contentId, contentType, content, taxonomyId);
        
        // Create result
        CategorizationResult result = CategorizationResult.fromRequest(request);
        
        // Process categorization (simulated AI processing)
        processCategorization(result);
        
        // Update request status
        request.markAsCompleted();
        requestRepository.save(tenantId, request);
        
        return resultRepository.save(tenantId, result);
    }

    public CategorizationResult getCategorizationResult(String tenantId, UUID resultId) {
        return resultRepository.findById(tenantId, resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found: " + resultId));
    }

    public List<CategorizationResult> getResultsByContentId(String tenantId, String contentId) {
        return resultRepository.findByContentId(tenantId, contentId);
    }

    public List<CategorizationRequest> getPendingRequests(String tenantId) {
        return requestRepository.findPendingRequests(tenantId);
    }

    private void processCategorization(CategorizationResult result) {
        // Simulate AI-based categorization
        // In a real implementation, this would call an ML model
        
        long startTime = System.currentTimeMillis();
        
        // Simulate processing
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Add mock predictions
        result.addPrediction(
            UUID.randomUUID(),
            "Sample Category",
            "/sample/category",
            0.85
        );
        
        long processingTime = System.currentTimeMillis() - startTime;
        result.markAsCompleted("1.0.0", (double) processingTime);
    }
}
