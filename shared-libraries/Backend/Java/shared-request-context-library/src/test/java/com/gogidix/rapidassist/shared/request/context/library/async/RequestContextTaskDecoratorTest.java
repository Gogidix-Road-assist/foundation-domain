package com.gogidix.rapidassist.shared.request.context.library.async;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for RequestContextTaskDecorator.
 */
class RequestContextTaskDecoratorTest {

    private RequestContextTaskDecorator decorator;

    @BeforeEach
    void setUp() {
        decorator = new RequestContextTaskDecorator();
        RequestContextHolder.clear();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    void shouldPropagateContextToAsyncThread() throws InterruptedException {
        RequestContext context = RequestContext.builder()
                .correlationId("corr-123")
                .tenantId("tenant-456")
                .userId("user-789")
                .requestId("00000000-0000-0000-0000-000000000001")
                .country("US")
                .build();

        RequestContextHolder.set(context);

        AtomicReference<RequestContext> capturedContext = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Runnable decoratedRunnable = decorator.decorate(() -> {
            capturedContext.set(RequestContextHolder.get().orElse(null));
            latch.countDown();
        });

        decoratedRunnable.run();
        latch.await();

        assertThat(capturedContext.get()).isNotNull();
        assertThat(capturedContext.get().tenantId()).isEqualTo("tenant-456");
        assertThat(capturedContext.get().userId()).isEqualTo("user-789");
        assertThat(capturedContext.get().correlationId()).isEqualTo("corr-123");
        assertThat(capturedContext.get().requestId()).isEqualTo("00000000-0000-0000-0000-000000000001");
        assertThat(capturedContext.get().country()).isEqualTo("US");
    }

    @Test
    void shouldClearContextAfterAsyncExecution() throws InterruptedException {
        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .build();

        RequestContextHolder.set(context);

        CountDownLatch latch = new CountDownLatch(1);

        Runnable decoratedRunnable = decorator.decorate(() -> {
            latch.countDown();
        });

        decoratedRunnable.run();
        latch.await();

        // Decorator's finally block clears context after execution completes
        assertThat(RequestContextHolder.get().isEmpty()).isTrue();
    }

    @Test
    void shouldHandleNoContext() throws InterruptedException {
        // Ensure no context is set
        RequestContextHolder.clear();

        AtomicReference<Boolean> contextPresent = new AtomicReference<>(false);
        CountDownLatch latch = new CountDownLatch(1);

        Runnable decoratedRunnable = decorator.decorate(() -> {
            contextPresent.set(RequestContextHolder.get().isPresent());
            latch.countDown();
        });

        decoratedRunnable.run();
        latch.await();

        assertThat(contextPresent.get()).isFalse();
    }

    @Test
    void shouldWorkWithExecutorService() throws InterruptedException {
        RequestContext context = RequestContext.builder()
                .tenantId("tenant-async")
                .userId("user-async")
                .build();

        RequestContextHolder.set(context);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        AtomicReference<RequestContext> asyncContext = new AtomicReference<>();

        Runnable decoratedRunnable = decorator.decorate(() -> {
            asyncContext.set(RequestContextHolder.get().orElse(null));
            latch.countDown();
        });

        executor.submit(decoratedRunnable);
        latch.await();

        executor.shutdown();

        assertThat(asyncContext.get()).isNotNull();
        assertThat(asyncContext.get().tenantId()).isEqualTo("tenant-async");
        assertThat(asyncContext.get().userId()).isEqualTo("user-async");
    }

    @Test
    void shouldIsolateContextBetweenExecutions() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        AtomicReference<String> context1Tenant = new AtomicReference<>();
        AtomicReference<String> context2Tenant = new AtomicReference<>();

        // First context
        RequestContext context1 = RequestContext.builder()
                .tenantId("tenant-1")
                .build();
        RequestContextHolder.set(context1);

        Runnable decorated1 = decorator.decorate(() -> {
            RequestContextHolder.get().ifPresent(ctx -> context1Tenant.set(ctx.tenantId()));
            latch.countDown();
        });

        // Clear and set second context
        RequestContextHolder.clear();
        RequestContext context2 = RequestContext.builder()
                .tenantId("tenant-2")
                .build();
        RequestContextHolder.set(context2);

        Runnable decorated2 = decorator.decorate(() -> {
            RequestContextHolder.get().ifPresent(ctx -> context2Tenant.set(ctx.tenantId()));
            latch.countDown();
        });

        executor.submit(decorated1);
        executor.submit(decorated2);

        latch.await();
        executor.shutdown();

        assertThat(context1Tenant.get()).isEqualTo("tenant-1");
        assertThat(context2Tenant.get()).isEqualTo("tenant-2");
    }

    @Test
    void shouldPreserveAllContextFields() throws InterruptedException {
        RequestContext context = RequestContext.builder()
                .correlationId("test-corr")
                .tenantId("test-tenant")
                .userId("test-user")
                .requestId("00000000-0000-0000-0000-000000000002")
                .country("CA")
                .build();

        RequestContextHolder.set(context);

        AtomicReference<RequestContext> capturedContext = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Runnable decoratedRunnable = decorator.decorate(() -> {
            capturedContext.set(RequestContextHolder.get().orElse(null));
            latch.countDown();
        });

        decoratedRunnable.run();
        latch.await();

        assertThat(capturedContext.get()).isNotNull();
        assertThat(capturedContext.get().correlationId()).isEqualTo("test-corr");
        assertThat(capturedContext.get().tenantId()).isEqualTo("test-tenant");
        assertThat(capturedContext.get().userId()).isEqualTo("test-user");
        assertThat(capturedContext.get().requestId()).isEqualTo("00000000-0000-0000-0000-000000000002");
        assertThat(capturedContext.get().country()).isEqualTo("CA");
    }
}
