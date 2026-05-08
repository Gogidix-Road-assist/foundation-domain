package com.gogidix.rapidassist.shared.request.context.library.autoconfigure;

import com.gogidix.rapidassist.shared.request.context.library.async
        .RequestContextTaskDecorator;
import com.gogidix.rapidassist.shared.request.context.library.constant
        .TenantConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for async task execution with RequestContext propagation.
 *
 * <p>This configuration provides a TaskExecutor that automatically propagates
 * RequestContext to asynchronous threads, maintaining multi-tenancy and tracing
 * context across async boundaries.</p>
 *
 * <p>To enable async support, set:</p>
 * <pre>
 * gogidix.request-context.async.enabled=true
 * </pre>
 */
@Configuration
@ConditionalOnProperty(prefix = "gogidix.request-context.async",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class AsyncConfig {

    private static final Logger log = LoggerFactory.getLogger(
            AsyncConfig.class);

    /**
     * Default async task executor with RequestContext propagation.
     *
     * <p>ThreadPool configuration:</p>
     * <ul>
     *   <li>Core pool size:
     *       {@link TenantConstants#DEFAULT_CORE_POOL_SIZE}</li>
     *   <li>Max pool size:
     *       {@link TenantConstants#DEFAULT_MAX_POOL_SIZE}</li>
     *   <li>Queue capacity:
     *       {@link TenantConstants#DEFAULT_QUEUE_CAPACITY}</li>
     *   <li>Thread name prefix: async-</li>
     * </ul>
     *
     * @return configured ThreadPoolTaskExecutor
     */
    @Bean(name = "taskExecutor")
    @ConditionalOnMissingBean(name = "taskExecutor")
    @SuppressWarnings("java:S1075") // Suppress thread prefix literal check
    public TaskExecutor taskExecutor() {
        log.info("Initializing RequestContext-aware TaskExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setTaskDecorator(new RequestContextTaskDecorator());
        executor.setCorePoolSize(TenantConstants.DEFAULT_CORE_POOL_SIZE);
        executor.setMaxPoolSize(TenantConstants.DEFAULT_MAX_POOL_SIZE);
        executor.setQueueCapacity(TenantConstants.DEFAULT_QUEUE_CAPACITY);
        executor.setThreadNamePrefix("async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(
                TenantConstants.DEFAULT_AWAIT_TERMINATION_SECONDS);
        executor.initialize();
        return executor;
    }

    /**
     * Alias for Spring's async annotation support.
     *
     * <p>This bean is automatically used by Spring's @Async annotation.</p>
     *
     * @return the configured executor
     */
    @Bean(name = "requestContextAsyncExecutor")
    @ConditionalOnMissingBean(name = "requestContextAsyncExecutor")
    @SuppressWarnings("java:S1186") // Suppress empty method body warning
    public Executor requestContextAsyncExecutor() {
        return taskExecutor();
    }
}
