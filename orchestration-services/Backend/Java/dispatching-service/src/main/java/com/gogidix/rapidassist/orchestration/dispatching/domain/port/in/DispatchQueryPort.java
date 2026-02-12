package com.gogidix.rapidassist.orchestration.dispatching.domain.port.in;

import com.gogidix.rapidassist.orchestration.dispatching.application.dto.response.DispatchResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Input port for dispatch queries
 * This is the interface that the application layer implements
 */
public interface DispatchQueryPort {

    CompletableFuture<Optional<DispatchResponseDto>> getById(String dispatchId);

    CompletableFuture<List<DispatchResponseDto>> getByRequestId(String requestId);

    CompletableFuture<List<DispatchResponseDto>> getByTenantId(String tenantId);

    CompletableFuture<List<DispatchResponseDto>> getActiveByTenantId(String tenantId);
}
