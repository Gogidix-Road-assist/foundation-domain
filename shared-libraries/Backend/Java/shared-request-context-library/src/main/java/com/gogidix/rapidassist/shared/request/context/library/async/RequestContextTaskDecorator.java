package com.gogidix.rapidassist.shared.request.context.library.async;

import com.gogidix.rapidassist.shared.request.context.library.domain
        .RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain
        .RequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskDecorator;

/**
 * TaskDecorator that captures the current RequestContext and propagates it to
 * async threads.
 *
 * <p>This decorator ensures that request context (tenant, user, correlation
 * IDs) is available in asynchronous execution threads, maintaining
 * traceability and multi-tenancy support.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * @Configuration
 * public class AsyncConfig {
 *     @Bean
 *     public TaskExecutor taskExecutor() {
 *         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 *         executor.setTaskDecorator(new RequestContextTaskDecorator());
 *         executor.initialize();
 *         return executor;
 *     }
 * }
 * }</pre>
 */
public class RequestContextTaskDecorator implements TaskDecorator {

    private static final Logger log = LoggerFactory.getLogger(
            RequestContextTaskDecorator.class);

    /**
     * Decorates the given runnable to capture and propagate the current
     * RequestContext.
     *
     * <p>This method captures the RequestContext from the current thread before
     * execution and makes it available to the async thread during
     * execution.</p>
     *
     * @param runnable the runnable to decorate
     * @return a decorated runnable that propagates the RequestContext
     */
    @Override
    public Runnable decorate(final Runnable runnable) {
        // Capture the context from the parent thread
        RequestContext capturedContext = RequestContextHolder.get().orElse(null);

        return () -> {
            try {
                if (capturedContext != null) {
                    // Set the context in the async thread
                    RequestContextHolder.set(capturedContext);
                    log.trace("Propagated RequestContext to async thread: "
                                    + "tenantId={}, userId={}, requestId={}",
                            capturedContext.tenantId(),
                            capturedContext.userId(),
                            capturedContext.requestId());
                } else {
                    log.trace("No RequestContext to propagate to async "
                                    + "thread");
                }
                // Execute the actual task
                runnable.run();
            } finally {
                // Always clear the context after execution
                RequestContextHolder.clear();
                log.trace("Cleared RequestContext from async thread");
            }
        };
    }
}
