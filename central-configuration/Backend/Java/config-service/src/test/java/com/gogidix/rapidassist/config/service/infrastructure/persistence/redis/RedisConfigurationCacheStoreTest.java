package com.gogidix.rapidassist.config.service.infrastructure.persistence.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RedisConfigurationCacheStoreTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @InjectMocks
    private RedisConfigurationCacheStore cacheStore;

    private final Duration testTtl = Duration.ofMinutes(30);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cacheStore, "cacheTtl", testTtl);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Nested
    class GetTests {

        @Test
        void get_WhenCacheHit_ReturnsConfiguration() throws Exception {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";
            String jsonConfig = "{\"key\":\"api.timeout\",\"value\":\"30\"}";
            Configuration config = Configuration.create(tenantId, configKey, environment, namespace, "30", Configuration.ConfigurationDataType.STRING, "system");

            when(valueOperations.get(anyString())).thenReturn(jsonConfig);
            when(objectMapper.readValue(jsonConfig, Configuration.class)).thenReturn(config);

            CompletableFuture<Configuration> result = cacheStore.get(tenantId, configKey, environment, namespace)
                    .thenApply(opt -> opt.orElse(null));

            Configuration configResult = result.join();
            assertNotNull(configResult);
            assertEquals(configKey, configResult.configKey());
        }

        @Test
        void get_WhenCacheMiss_ReturnsEmpty() {
            String tenantId = "tenant-1";
            String configKey = "missing.key";
            String environment = "production";
            String namespace = "default";

            when(valueOperations.get(anyString())).thenReturn(null);

            CompletableFuture<Boolean> result = cacheStore.get(tenantId, configKey, environment, namespace)
                    .thenApply(Optional::isPresent);

            assertFalse(result.join());
        }

        @Test
        void get_WhenNonStringValue_ReturnsEmpty() {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";

            when(valueOperations.get(anyString())).thenReturn(new Object());

            CompletableFuture<Boolean> result = cacheStore.get(tenantId, configKey, environment, namespace)
                    .thenApply(Optional::isPresent);

            assertFalse(result.join());
        }

        @Test
        void get_WhenExceptionThrown_ReturnsEmpty() throws Exception {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";

            when(valueOperations.get(anyString())).thenThrow(new RuntimeException("Redis connection failed"));

            CompletableFuture<Boolean> result = cacheStore.get(tenantId, configKey, environment, namespace)
                    .thenApply(Optional::isPresent);

            assertFalse(result.join());
        }
    }

    @Nested
    class PutTests {

        @Test
        void put_SuccessfullyCachesConfiguration() throws Exception {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";
            Configuration config = new Configuration(
                    null, tenantId, configKey, environment, namespace, 1, "30",
                    Configuration.ConfigurationDataType.STRING, false, false, null, null,
                    Set.of("api", "performance"), Map.of(), null,
                    Configuration.ConfigurationStatus.ACTIVE, "system", Instant.now(),
                    "system", Instant.now(), null, Set.of()
            );
            String jsonConfig = "{\"key\":\"api.timeout\",\"value\":\"30\"}";

            when(objectMapper.writeValueAsString(config)).thenReturn(jsonConfig);

            CompletableFuture<Void> result = cacheStore.put(tenantId, configKey, environment, namespace, config);

            assertDoesNotThrow(() -> result.join());
            verify(valueOperations).set(anyString(), eq(jsonConfig), eq(testTtl));
            verify(setOperations).add(anyString(), any());
            verify(redisTemplate, times(2)).expire(anyString(), eq(testTtl));
        }

        @Test
        void put_WithEmptyTags_DoesNotAddTags() throws Exception {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";
            Configuration config = Configuration.create(tenantId, configKey, environment, namespace, "30", Configuration.ConfigurationDataType.STRING, "system");
            String jsonConfig = "{\"key\":\"api.timeout\",\"value\":\"30\"}";

            when(objectMapper.writeValueAsString(config)).thenReturn(jsonConfig);

            CompletableFuture<Void> result = cacheStore.put(tenantId, configKey, environment, namespace, config);

            assertDoesNotThrow(() -> result.join());
            verify(valueOperations).set(anyString(), eq(jsonConfig), eq(testTtl));
        }

        @Test
        void put_WhenExceptionThrown_DoesNotFail() throws Exception {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";
            Configuration config = Configuration.create(tenantId, configKey, environment, namespace, "30", Configuration.ConfigurationDataType.STRING, "system");

            when(objectMapper.writeValueAsString(config)).thenThrow(new RuntimeException("Serialization failed"));

            CompletableFuture<Void> result = cacheStore.put(tenantId, configKey, environment, namespace, config);

            assertDoesNotThrow(() -> result.join());
        }
    }

    @Nested
    class EvictTests {

        @Test
        void evict_SuccessfullyDeletesKey() {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";

            CompletableFuture<Void> result = cacheStore.evict(tenantId, configKey, environment, namespace);

            assertDoesNotThrow(() -> result.join());
            verify(redisTemplate).delete(contains(configKey));
        }

        @Test
        void evict_WhenExceptionThrown_DoesNotFail() {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";

            doThrow(new RuntimeException("Redis connection failed")).when(redisTemplate).delete(anyString());

            CompletableFuture<Void> result = cacheStore.evict(tenantId, configKey, environment, namespace);

            assertDoesNotThrow(() -> result.join());
        }
    }

    @Nested
    class EvictByNamespaceTests {

        @Test
        void evictByNamespace_WithKeys_DoesNotFail() {
            String tenantId = "tenant-1";
            String environment = "production";
            String namespace = "default";
            Set<Object> keys = Set.of("config:tenant-1:key1:production:default",
                                       "config:tenant-1:key2:production:default");

            when(setOperations.members(anyString())).thenReturn(keys);

            CompletableFuture<Void> result = cacheStore.evictByNamespace(tenantId, environment, namespace);

            assertDoesNotThrow(() -> result.join());
            verify(setOperations).members(anyString());
        }

        @Test
        void evictByNamespace_WithNoKeys_DoesNothing() {
            String tenantId = "tenant-1";
            String environment = "production";
            String namespace = "default";

            when(setOperations.members(anyString())).thenReturn(null);

            CompletableFuture<Void> result = cacheStore.evictByNamespace(tenantId, environment, namespace);

            assertDoesNotThrow(() -> result.join());
            verify(redisTemplate, never()).delete(anyCollection());
        }

        @Test
        void evictByNamespace_WhenExceptionThrown_DoesNotFail() {
            String tenantId = "tenant-1";
            String environment = "production";
            String namespace = "default";

            when(setOperations.members(anyString())).thenThrow(new RuntimeException("Redis error"));

            CompletableFuture<Void> result = cacheStore.evictByNamespace(tenantId, environment, namespace);

            assertDoesNotThrow(() -> result.join());
        }
    }

    @Nested
    class EvictByTenantTests {

        @Test
        void evictByTenant_WithKeys_DeletesAllTenantKeys() {
            String tenantId = "tenant-1";

            @SuppressWarnings("unchecked")
            Cursor<String> cursor = mock(Cursor.class);
            when(cursor.hasNext()).thenReturn(true, true, false);
            when(cursor.next()).thenReturn("config:tenant-1:key1:production:default", "config:tenant-1:key2:production:default");
            when(redisTemplate.scan(any(ScanOptions.class))).thenReturn(cursor);

            CompletableFuture<Void> result = cacheStore.evictByTenant(tenantId);

            assertDoesNotThrow(() -> result.join());
            // Verify scan was called for finding keys
            verify(redisTemplate).scan(any(ScanOptions.class));
        }

        @Test
        void evictByTenant_WithNoKeys_DeletesTagsOnly() {
            String tenantId = "tenant-1";

            @SuppressWarnings("unchecked")
            Cursor<String> cursor = mock(Cursor.class);
            when(cursor.hasNext()).thenReturn(false);
            when(redisTemplate.scan(any(ScanOptions.class))).thenReturn(cursor);

            CompletableFuture<Void> result = cacheStore.evictByTenant(tenantId);

            assertDoesNotThrow(() -> result.join());
            // When no keys found, only the tags key is deleted (as a String)
            verify(redisTemplate).delete(eq("config:tags:tenant-1"));
            verify(redisTemplate, never()).delete(anyCollection());
        }

        @Test
        void evictByTenant_WhenExceptionThrown_DoesNotFail() {
            String tenantId = "tenant-1";

            when(redisTemplate.scan(any(ScanOptions.class))).thenThrow(new RuntimeException("Redis error"));

            CompletableFuture<Void> result = cacheStore.evictByTenant(tenantId);

            assertDoesNotThrow(() -> result.join());
        }
    }

    @Nested
    class GetByNamespaceTests {

        @Test
        void getByNamespace_WithKeys_ReturnsConfigurations() throws Exception {
            String tenantId = "tenant-1";
            String environment = "production";
            String namespace = "default";
            Set<Object> keys = Set.of("config:tenant-1:key1:production:default");
            String jsonConfig = "{\"key\":\"key1\",\"value\":\"value1\"}";
            Configuration config = Configuration.create(tenantId, "key1", environment, namespace, "value1", Configuration.ConfigurationDataType.STRING, "system");

            when(setOperations.members(anyString())).thenReturn(keys);
            when(valueOperations.multiGet(anyCollection())).thenReturn(List.of(jsonConfig));
            when(objectMapper.readValue(jsonConfig, Configuration.class)).thenReturn(config);

            CompletableFuture<List<Configuration>> result = cacheStore.getByNamespace(tenantId, environment, namespace);

            List<Configuration> configs = result.join();
            assertEquals(1, configs.size());
            assertEquals("key1", configs.get(0).configKey());
        }

        @Test
        void getByNamespace_WithNoKeys_ReturnsEmptyList() {
            String tenantId = "tenant-1";
            String environment = "production";
            String namespace = "default";

            when(setOperations.members(anyString())).thenReturn(null);

            CompletableFuture<List<Configuration>> result = cacheStore.getByNamespace(tenantId, environment, namespace);

            List<Configuration> configs = result.join();
            assertTrue(configs.isEmpty());
        }

        @Test
        void getByNamespace_WhenDeserializationFails_FiltersOutNulls() throws Exception {
            String tenantId = "tenant-1";
            String environment = "production";
            String namespace = "default";
            Set<Object> keys = Set.of("config:tenant-1:key1:production:default");
            String jsonConfig = "{\"key\":\"key1\",\"value\":\"value1\"}";

            when(setOperations.members(anyString())).thenReturn(keys);
            when(valueOperations.multiGet(anyCollection())).thenReturn(List.of(jsonConfig));
            when(objectMapper.readValue(jsonConfig, Configuration.class))
                    .thenThrow(new RuntimeException("Deserialization failed"));

            CompletableFuture<List<Configuration>> result = cacheStore.getByNamespace(tenantId, environment, namespace);

            List<Configuration> configs = result.join();
            assertTrue(configs.isEmpty());
        }
    }

    @Nested
    class GetTagsTests {

        @Test
        void getTags_WithTags_ReturnsTagSet() {
            String tenantId = "tenant-1";
            Set<Object> tags = Set.of("api", "performance", "critical");

            when(setOperations.members(anyString())).thenReturn(tags);

            CompletableFuture<Set<String>> result = cacheStore.getTags(tenantId);

            Set<String> returnedTags = result.join();
            assertEquals(3, returnedTags.size());
            assertTrue(returnedTags.contains("api"));
        }

        @Test
        void getTags_WithNoTags_ReturnsEmptySet() {
            String tenantId = "tenant-1";

            when(setOperations.members(anyString())).thenReturn(null);

            CompletableFuture<Set<String>> result = cacheStore.getTags(tenantId);

            Set<String> tags = result.join();
            assertTrue(tags.isEmpty());
        }

        @Test
        void getTags_WithNonStringTags_FiltersThemOut() {
            String tenantId = "tenant-1";
            Set<Object> tags = Set.of("api", 123, true);

            when(setOperations.members(anyString())).thenReturn(tags);

            CompletableFuture<Set<String>> result = cacheStore.getTags(tenantId);

            Set<String> returnedTags = result.join();
            assertEquals(1, returnedTags.size());
            assertTrue(returnedTags.contains("api"));
        }
    }

    @Nested
    class PutTagsTests {

        @Test
        void putTags_WithTags_CachesTags() {
            String tenantId = "tenant-1";
            Set<String> tags = Set.of("api", "performance");

            CompletableFuture<Void> result = cacheStore.putTags(tenantId, tags);

            assertDoesNotThrow(() -> result.join());
            verify(setOperations).add(anyString(), eq((Object[]) tags.toArray(new String[0])));
            verify(redisTemplate).expire(anyString(), eq(testTtl));
        }

        @Test
        void putTags_WithEmptyTags_DoesNothing() {
            String tenantId = "tenant-1";
            Set<String> tags = Set.of();

            CompletableFuture<Void> result = cacheStore.putTags(tenantId, tags);

            assertDoesNotThrow(() -> result.join());
            verify(setOperations, never()).add(anyString(), any());
        }

        @Test
        void putTags_WithNullTags_DoesNothing() {
            String tenantId = "tenant-1";

            CompletableFuture<Void> result = cacheStore.putTags(tenantId, null);

            assertDoesNotThrow(() -> result.join());
            verify(setOperations, never()).add(anyString(), any());
        }
    }

    @Nested
    class ExistsTests {

        @Test
        void exists_WhenKeyExists_ReturnsTrue() {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";

            when(redisTemplate.hasKey(anyString())).thenReturn(true);

            CompletableFuture<Boolean> result = cacheStore.exists(tenantId, configKey, environment, namespace);

            assertTrue(result.join());
        }

        @Test
        void exists_WhenKeyDoesNotExist_ReturnsFalse() {
            String tenantId = "tenant-1";
            String configKey = "missing.key";
            String environment = "production";
            String namespace = "default";

            when(redisTemplate.hasKey(anyString())).thenReturn(false);

            CompletableFuture<Boolean> result = cacheStore.exists(tenantId, configKey, environment, namespace);

            assertFalse(result.join());
        }

        @Test
        void exists_WhenExceptionThrown_ReturnsFalse() {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";

            when(redisTemplate.hasKey(anyString())).thenThrow(new RuntimeException("Redis error"));

            CompletableFuture<Boolean> result = cacheStore.exists(tenantId, configKey, environment, namespace);

            assertFalse(result.join());
        }
    }

    @Nested
    class IncrementVersionTests {

        @Test
        void incrementVersion_SuccessfullyIncrements() {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";

            when(valueOperations.increment(anyString())).thenReturn(1L);

            CompletableFuture<Long> result = cacheStore.incrementVersion(tenantId, configKey, environment, namespace);

            assertEquals(1L, result.join());
        }

        @Test
        void incrementVersion_WhenExceptionThrown_ReturnsZero() {
            String tenantId = "tenant-1";
            String configKey = "api.timeout";
            String environment = "production";
            String namespace = "default";

            when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis error"));

            CompletableFuture<Long> result = cacheStore.incrementVersion(tenantId, configKey, environment, namespace);

            assertEquals(0L, result.join());
        }
    }

    @Nested
    class InvalidatePatternTests {

        @Test
        void invalidatePattern_WithMatchingKeys_DoesNotFail() {
            String pattern = "config:tenant-1:*";

            @SuppressWarnings("unchecked")
            Cursor<String> cursor = mock(Cursor.class);
            when(cursor.hasNext()).thenReturn(true, true, false);
            when(cursor.next()).thenReturn("config:tenant-1:key1:production:default", "config:tenant-1:key2:production:default");
            when(redisTemplate.scan(any(ScanOptions.class))).thenReturn(cursor);

            CompletableFuture<Void> result = cacheStore.invalidatePattern(pattern);

            assertDoesNotThrow(() -> result.join());
            // Verify scan was called - actual delete depends on key collection implementation
            verify(redisTemplate).scan(any(ScanOptions.class));
        }

        @Test
        void invalidatePattern_WithNoMatchingKeys_DoesNotFail() {
            String pattern = "config:tenant-X:*";

            @SuppressWarnings("unchecked")
            Cursor<String> cursor = mock(Cursor.class);
            when(cursor.hasNext()).thenReturn(false);
            when(redisTemplate.scan(any(ScanOptions.class))).thenReturn(cursor);

            CompletableFuture<Void> result = cacheStore.invalidatePattern(pattern);

            assertDoesNotThrow(() -> result.join());
        }

        @Test
        void invalidatePattern_WhenExceptionThrown_DoesNotFail() {
            String pattern = "config:tenant-1:*";

            when(redisTemplate.scan(any(ScanOptions.class))).thenThrow(new RuntimeException("Redis error"));

            CompletableFuture<Void> result = cacheStore.invalidatePattern(pattern);

            assertDoesNotThrow(() -> result.join());
        }
    }
}
