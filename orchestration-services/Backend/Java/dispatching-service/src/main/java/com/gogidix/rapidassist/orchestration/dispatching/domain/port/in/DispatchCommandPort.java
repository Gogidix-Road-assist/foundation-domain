package com.gogidix.rapidassist.orchestration.dispatching.domain.port.in;

import com.gogidix.rapidassist.orchestration.dispatching.application.command.CreateDispatchCommand;
import com.gogidix.rapidassist.orchestration.dispatching.application.command.UpdateDispatchStatusCommand;
import com.gogidix.rapidassist.orchestration.dispatching.application.dto.response.DispatchResponseDto;

import java.util.concurrent.CompletableFuture;

/**
 * Input port for dispatch commands
 * This is the interface that the application layer implements
 */
public interface DispatchCommandPort {

    CompletableFuture<DispatchResponseDto> create(CreateDispatchCommand command);

    CompletableFuture<DispatchResponseDto> updateStatus(UpdateDispatchStatusCommand command);

    CompletableFuture<Void> delete(String dispatchId);
}
