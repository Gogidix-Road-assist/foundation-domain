/**
 * Async package for the shared request context library.
 *
 * <p>This package contains classes that enable request context propagation
 * across asynchronous boundaries, ensuring that tenant and tracing information
 * is available in async threads.</p>
 *
 * <p>Key classes in this package:</p>
 * <ul>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library
 *       .async.RequestContextTaskDecorator} -
 *       Task decorator that captures and propagates RequestContext to async
 *       threads</li>
 * </ul>
 *
 * <p>Usage example with Spring's TaskExecutor:</p>
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
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
package com.gogidix.rapidassist.shared.request.context.library.async;
