package com.gogidix.rapidassist.config.service.application.service;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationDataType;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;
import com.gogidix.rapidassist.config.service.domain.port.in.ConfigurationCommand;
import com.gogidix.rapidassist.config.service.domain.port.out.ConfigurationCacheStore;
import com.gogidix.rapidassist.config.service.domain.port.out.ConfigurationStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ComprehensiveConfigurationService.
 * Tests the business logic layer with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class ComprehensiveConfigurationServiceTest {

    @Mock
    @Qualifier("mongoConfigurationStore")
    private ConfigurationStore configurationStore;

    @Mock
    @Qualifier("redisConfigurationCacheStore")
    private ConfigurationCacheStore cacheStore;

    private ComprehensiveConfigurationService service;

    private static final String TENANT_ID = "test-tenant";
    private static final String CONFIG_KEY = "test.config";
    private static final String ENVIRONMENT = "production";
    private static final String NAMESPACE = "api-gateway";
    private static final String USER = "test-user";

    @BeforeEach
    void setUp() {
        service = new ComprehensiveConfigurationService();
        // Use reflection to inject mocks
        try {
            var storeField = ComprehensiveConfigurationService.class.getDeclaredField("configurationStore");
            storeField.setAccessible(true);
            storeField.set(service, configurationStore);

            var cacheField = ComprehensiveConfigurationService.class.getDeclaredField("cacheStore");
            cacheField.setAccessible(true);
            cacheField.set(service, cacheStore);
        } catch (Exception e) {
            fail("Failed to inject mocks: " + e.getMessage());
        }
    }

    @Test
    void createConfiguration_Success() {
        // Given
        ConfigurationCommand.CreateConfigurationCommand command = new ConfigurationCommand.CreateConfigurationCommand(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "test-value", Configuration.ConfigurationDataType.STRING,
            false, false, null, "Test configuration",
            Set.of("tag1"), Map.of(),
            null, "Initial creation", USER
        );

        Configuration config = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "test-value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
        when(configurationStore.save(any(Configuration.class)))
            .thenReturn(CompletableFuture.completedFuture(config));
        when(configurationStore.saveChange(any(ConfigurationChange.class)))
            .thenReturn(CompletableFuture.completedFuture(ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.CREATE, null, 1,
                null, "test-value", USER, null
            )));
        when(cacheStore.put(any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(cacheStore.evictByNamespace(any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Configuration> result = service.createConfiguration(command);

        // Then
        assertNotNull(result);
        Configuration savedConfig = result.join();
        assertNotNull(savedConfig);
        assertEquals(TENANT_ID, savedConfig.tenantId());
        assertEquals(CONFIG_KEY, savedConfig.configKey());

        verify(configurationStore).save(any(Configuration.class));
        verify(configurationStore).saveChange(any(ConfigurationChange.class));
        verify(cacheStore).put(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE), any());
    }

    @Test
    void createConfiguration_ConfigurationAlreadyExists_ThrowsException() {
        // Given
        ConfigurationCommand.CreateConfigurationCommand command = new ConfigurationCommand.CreateConfigurationCommand(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "test-value", Configuration.ConfigurationDataType.STRING,
            false, false, null, "Test configuration",
            Set.of("tag1"), Map.of(),
            null, "Initial creation", USER
        );

        Configuration existingConfig = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "old-value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(existingConfig)));

        // When & Then
        // Service wraps exceptions: IllegalStateException -> RuntimeException -> CompletionException
        CompletableFuture<Configuration> result = service.createConfiguration(command);
        Exception exception = assertThrows(java.util.concurrent.CompletionException.class, result::join);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getCause() instanceof IllegalStateException);
    }

    @Test
    void updateConfiguration_Success() {
        // Given
        Configuration existingConfig = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "old-value", Configuration.ConfigurationDataType.STRING, USER
        );

        ConfigurationCommand.UpdateConfigurationCommand command = new ConfigurationCommand.UpdateConfigurationCommand(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "new-value", USER, "Update for testing", false
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(existingConfig)));

        ArgumentCaptor<Configuration> configCaptor = ArgumentCaptor.forClass(Configuration.class);
        when(configurationStore.save(configCaptor.capture()))
            .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

        when(configurationStore.saveChange(any(ConfigurationChange.class)))
            .thenReturn(CompletableFuture.completedFuture(ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 1, 2,
                "old-value", "new-value", USER, "Update for testing"
            )));

        when(cacheStore.put(any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(cacheStore.evictByNamespace(any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Optional<Configuration>> result = service.updateConfiguration(command);

        // Then
        assertNotNull(result);
        Optional<Configuration> updated = result.join();
        assertTrue(updated.isPresent());
        assertEquals("new-value", updated.get().value());

        verify(configurationStore).save(any(Configuration.class));
        verify(configurationStore).saveChange(any(ConfigurationChange.class));
    }

    @Test
    void updateConfiguration_ConfigurationNotFound_ReturnsEmpty() {
        // Given
        ConfigurationCommand.UpdateConfigurationCommand command = new ConfigurationCommand.UpdateConfigurationCommand(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "new-value", USER, "Update for testing", false
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // When
        CompletableFuture<Optional<Configuration>> result = service.updateConfiguration(command);

        // Then
        assertNotNull(result);
        Optional<Configuration> updated = result.join();
        assertFalse(updated.isPresent());

        verify(configurationStore, never()).save(any(Configuration.class));
    }

    @Test
    void deleteConfiguration_Success() {
        // Given
        Configuration existingConfig = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(existingConfig)));

        ArgumentCaptor<Configuration> configCaptor = ArgumentCaptor.forClass(Configuration.class);
        when(configurationStore.save(configCaptor.capture()))
            .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

        when(configurationStore.saveChange(any(ConfigurationChange.class)))
            .thenReturn(CompletableFuture.completedFuture(ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.DELETE, 1, null,
                "value", null, USER, "Configuration deleted"
            )));

        when(cacheStore.evict(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(cacheStore.evictByNamespace(any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Boolean> result = service.deleteConfiguration(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE, USER);

        // Then
        assertNotNull(result);
        Boolean deleted = result.join();
        assertTrue(deleted);

        ArgumentCaptor<Configuration> savedConfig = ArgumentCaptor.forClass(Configuration.class);
        verify(configurationStore).save(savedConfig.capture());
        assertEquals(Configuration.ConfigurationStatus.INACTIVE, savedConfig.getValue().status());
    }

    @Test
    void deleteConfiguration_ConfigurationNotFound_ReturnsFalse() {
        // Given
        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // When
        CompletableFuture<Boolean> result = service.deleteConfiguration(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE, USER);

        // Then
        assertNotNull(result);
        Boolean deleted = result.join();
        assertFalse(deleted);

        verify(configurationStore, never()).save(any(Configuration.class));
    }

    @Test
    void getConfiguration_ReturnsCachedValue() {
        // Given
        Configuration config = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "cached-value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(cacheStore.get(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(config)));

        // When
        CompletableFuture<Optional<Configuration>> result = service.getConfiguration(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE);

        // Then
        assertNotNull(result);
        Optional<Configuration> found = result.join();
        assertTrue(found.isPresent());
        assertEquals("cached-value", found.get().value());

        verify(configurationStore, never()).findByKeyAndEnvironment(any(), any(), any(), any());
    }

    @Test
    void getConfiguration_CacheMiss_ReturnsFromStore() {
        // Given
        Configuration config = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "db-value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(cacheStore.get(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(config)));
        when(cacheStore.put(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE), eq(config)))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Optional<Configuration>> result = service.getConfiguration(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE);

        // Then
        assertNotNull(result);
        Optional<Configuration> found = result.join();
        assertTrue(found.isPresent());
        assertEquals("db-value", found.get().value());

        verify(configurationStore).findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE));
        verify(cacheStore).put(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE), eq(config));
    }

    @Test
    void validateConfigurationValue_ValidString_ReturnsTrue() {
        // Given
        String value = "test string";
        ConfigurationSchema schema = new ConfigurationSchema.StringSchema(
            ConfigurationDataType.STRING, true, null,
            Set.of(), Map.of(), 1, 100, null, null
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertTrue(result.join());
    }

    @Test
    void validateConfigurationValue_StringTooShort_ReturnsFalse() {
        // Given
        String value = "ab";
        ConfigurationSchema schema = new ConfigurationSchema.StringSchema(
            ConfigurationDataType.STRING, true, null,
            Set.of(), Map.of(), 5, 100, null, null
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertFalse(result.join());
    }

    @Test
    void getConfigurationHistory_ReturnsAllVersions() {
        // Given
        Configuration v1 = Configuration.create(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE, "v1", Configuration.ConfigurationDataType.STRING, USER);
        Configuration v2 = Configuration.create(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE, "v2", Configuration.ConfigurationDataType.STRING, USER);

        when(configurationStore.findAllVersions(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT)))
            .thenReturn(CompletableFuture.completedFuture(List.of(v1, v2)));

        // When
        CompletableFuture<List<Configuration>> result = service.getConfigurationHistory(TENANT_ID, CONFIG_KEY, ENVIRONMENT);

        // Then
        assertNotNull(result);
        List<Configuration> history = result.join();
        assertEquals(2, history.size());
    }

    @Test
    void getPendingApprovals_ReturnsPendingChanges() {
        // Given
        // Create a PENDING change manually since create() returns AUTO_APPROVED
        ConfigurationChange pendingChange = new ConfigurationChange(
            null, TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "change-123", ConfigurationChange.ChangeType.UPDATE,
            1, 2, "old", "new", USER, Instant.now(),
            "Update", ConfigurationChange.ApprovalStatus.PENDING,
            null, null, null, null, null, Map.of(), Set.of(), Set.of()
        );

        when(configurationStore.findPendingApprovals(eq(TENANT_ID)))
            .thenReturn(CompletableFuture.completedFuture(List.of(pendingChange)));

        // When
        CompletableFuture<List<ConfigurationChange>> result = service.getPendingApprovals(TENANT_ID);

        // Then
        assertNotNull(result);
        List<ConfigurationChange> pending = result.join();
        assertEquals(1, pending.size());
        assertEquals(ConfigurationChange.ApprovalStatus.PENDING, pending.get(0).approvalStatus());
    }

    @Test
    void approveConfigurationChange_Success() {
        // Given
        String changeId = "change-123";
        ConfigurationChange pendingChange = ConfigurationChange.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            ConfigurationChange.ChangeType.UPDATE, 1, 2,
            "old", "new", USER, "Update"
        );

        when(configurationStore.findChangeById(eq(changeId)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(pendingChange)));

        ArgumentCaptor<ConfigurationChange> changeCaptor = ArgumentCaptor.forClass(ConfigurationChange.class);
        when(configurationStore.saveChange(changeCaptor.capture()))
            .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

        // When
        CompletableFuture<ConfigurationChange> result = service.approveConfigurationChange(changeId, USER);

        // Then
        assertNotNull(result);
        ConfigurationChange approved = result.join();
        assertEquals(ConfigurationChange.ApprovalStatus.APPROVED, approved.approvalStatus());

        verify(configurationStore).saveChange(any(ConfigurationChange.class));
    }

    @Test
    void approveConfigurationChange_ChangeNotFound_ThrowsException() {
        // Given
        String changeId = "change-123";
        when(configurationStore.findChangeById(eq(changeId)))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // When & Then - CompletableFuture wraps exceptions in CompletionException
        CompletableFuture<ConfigurationChange> result = service.approveConfigurationChange(changeId, USER);
        Exception exception = assertThrows(java.util.concurrent.CompletionException.class, result::join);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void validateConfigurations_AllValid_ReturnsTrue() {
        // Given
        Configuration config1 = Configuration.create(
            TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByStatus(eq(TENANT_ID), eq(Configuration.ConfigurationStatus.ACTIVE)))
            .thenReturn(CompletableFuture.completedFuture(List.of(config1)));

        // When
        CompletableFuture<Boolean> result = service.validateConfigurations(TENANT_ID, ENVIRONMENT);

        // Then
        assertNotNull(result);
        assertTrue(result.join());
    }

    @Test
    void rollbackConfiguration_Success() {
        // Given
        Configuration v1 = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "old-value", Configuration.ConfigurationDataType.STRING, USER
        );
        Configuration v2 = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "new-value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findAllVersions(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT)))
            .thenReturn(CompletableFuture.completedFuture(List.of(v1, v2)));

        Configuration currentConfig = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "current-value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(currentConfig)));

        ArgumentCaptor<Configuration> configCaptor = ArgumentCaptor.forClass(Configuration.class);
        when(configurationStore.save(configCaptor.capture()))
            .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

        when(configurationStore.saveChange(any(ConfigurationChange.class)))
            .thenReturn(CompletableFuture.completedFuture(ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.ROLLBACK, 2, 3,
                "current-value", "old-value", USER, "Rolled back to version 1"
            )));

        when(cacheStore.put(any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(cacheStore.evictByNamespace(any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Optional<Configuration>> result = service.rollbackConfiguration(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE, 1, USER
        );

        // Then
        assertNotNull(result);
        Optional<Configuration> rolledBack = result.join();
        assertTrue(rolledBack.isPresent());
        assertEquals("old-value", rolledBack.get().value());

        verify(configurationStore).save(any(Configuration.class));
        verify(configurationStore).saveChange(any(ConfigurationChange.class));
    }

    @Test
    void rollbackConfiguration_TargetVersionNotFound_ThrowsException() {
        // Given
        Configuration v1 = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findAllVersions(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT)))
            .thenReturn(CompletableFuture.completedFuture(List.of(v1)));

        // When & Then
        CompletableFuture<Optional<Configuration>> result = service.rollbackConfiguration(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE, 999, USER
        );
        Exception exception = assertThrows(java.util.concurrent.CompletionException.class, result::join);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getCause() instanceof IllegalArgumentException);
    }

    @Test
    void rejectConfigurationChange_Success() {
        // Given
        String changeId = "change-123";
        String reason = "Not approved";
        ConfigurationChange pendingChange = ConfigurationChange.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            ConfigurationChange.ChangeType.UPDATE, 1, 2,
            "old", "new", USER, "Update"
        );

        when(configurationStore.findChangeById(eq(changeId)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(pendingChange)));

        ArgumentCaptor<ConfigurationChange> changeCaptor = ArgumentCaptor.forClass(ConfigurationChange.class);
        when(configurationStore.saveChange(changeCaptor.capture()))
            .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

        // When
        CompletableFuture<ConfigurationChange> result = service.rejectConfigurationChange(changeId, USER, reason);

        // Then
        assertNotNull(result);
        ConfigurationChange rejected = result.join();
        assertEquals(ConfigurationChange.ApprovalStatus.REJECTED, rejected.approvalStatus());

        verify(configurationStore).saveChange(any(ConfigurationChange.class));
    }

    @Test
    void rejectConfigurationChange_ChangeNotFound_ThrowsException() {
        // Given
        String changeId = "change-123";
        when(configurationStore.findChangeById(eq(changeId)))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // When & Then
        CompletableFuture<ConfigurationChange> result = service.rejectConfigurationChange(changeId, USER, "Not approved");
        Exception exception = assertThrows(java.util.concurrent.CompletionException.class, result::join);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void bulkUpdateConfigurations_Success() {
        // Given
        ConfigurationCommand.BulkUpdateCommand command = new ConfigurationCommand.BulkUpdateCommand(
            TENANT_ID,
            List.of(new ConfigurationCommand.ConfigurationUpdate(
                CONFIG_KEY, ENVIRONMENT, NAMESPACE, "new-value"
            )),
            USER,
            "Bulk update test"
        );

        Configuration existingConfig = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "old-value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(existingConfig)));

        ArgumentCaptor<Configuration> configCaptor = ArgumentCaptor.forClass(Configuration.class);
        when(configurationStore.save(configCaptor.capture()))
            .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

        when(configurationStore.saveChange(any(ConfigurationChange.class)))
            .thenReturn(CompletableFuture.completedFuture(ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 1, 2,
                "old-value", "new-value", USER, "Bulk update test"
            )));

        when(cacheStore.put(any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(cacheStore.evictByNamespace(any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(cacheStore.evictByTenant(any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<List<Configuration>> result = service.bulkUpdateConfigurations(command);

        // Then
        assertNotNull(result);
        List<Configuration> updated = result.join();
        assertEquals(1, updated.size());
        assertEquals("new-value", updated.get(0).value());
    }

    @Test
    void getConfigurationsByNamespace_ReturnsCachedConfigs() {
        // Given
        Configuration config1 = Configuration.create(
            TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );

        when(cacheStore.getByNamespace(eq(TENANT_ID), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(List.of(config1)));

        // When
        CompletableFuture<List<Configuration>> result = service.getConfigurationsByNamespace(TENANT_ID, ENVIRONMENT, NAMESPACE);

        // Then
        assertNotNull(result);
        List<Configuration> configs = result.join();
        assertEquals(1, configs.size());

        verify(configurationStore, never()).findByNamespace(any(), any(), any());
    }

    @Test
    void getConfigurationsByNamespace_CacheMiss_ReturnsFromStore() {
        // Given
        Configuration config1 = Configuration.create(
            TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );

        when(cacheStore.getByNamespace(eq(TENANT_ID), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(List.of()));
        when(configurationStore.findByNamespace(eq(TENANT_ID), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(List.of(config1)));
        when(cacheStore.put(any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<List<Configuration>> result = service.getConfigurationsByNamespace(TENANT_ID, ENVIRONMENT, NAMESPACE);

        // Then
        assertNotNull(result);
        List<Configuration> configs = result.join();
        assertEquals(1, configs.size());

        verify(configurationStore).findByNamespace(eq(TENANT_ID), eq(ENVIRONMENT), eq(NAMESPACE));
    }

    @Test
    void getConfigurationsByTenant_ReturnsConfigs() {
        // Given
        Configuration config1 = Configuration.create(
            TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByTenant(eq(TENANT_ID)))
            .thenReturn(CompletableFuture.completedFuture(List.of(config1)));

        // When
        CompletableFuture<List<Configuration>> result = service.getConfigurationsByTenant(TENANT_ID);

        // Then
        assertNotNull(result);
        List<Configuration> configs = result.join();
        assertEquals(1, configs.size());
    }

    @Test
    void getConfigurationsWithTags_ReturnsMatchingConfigs() {
        // Given
        Configuration config1 = Configuration.create(
            TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByTags(eq(TENANT_ID), anyList()))
            .thenReturn(CompletableFuture.completedFuture(List.of(config1)));

        // When
        CompletableFuture<List<Configuration>> result = service.getConfigurationsWithTags(TENANT_ID, List.of("tag1", "tag2"));

        // Then
        assertNotNull(result);
        List<Configuration> configs = result.join();
        assertEquals(1, configs.size());
    }

    @Test
    void searchConfigurations_ReturnsMatchingConfigs() {
        // Given
        Configuration config1 = Configuration.create(
            TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.searchByKeyword(eq(TENANT_ID), eq("test")))
            .thenReturn(CompletableFuture.completedFuture(List.of(config1)));

        // When
        CompletableFuture<List<Configuration>> result = service.searchConfigurations(TENANT_ID, "test");

        // Then
        assertNotNull(result);
        List<Configuration> configs = result.join();
        assertEquals(1, configs.size());
    }

    @Test
    void getConfigurationsUpdatedSince_ReturnsRecentConfigs() {
        // Given
        Instant since = Instant.now().minusSeconds(3600);
        Configuration config1 = Configuration.create(
            TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findUpdatedSince(eq(TENANT_ID), eq(since)))
            .thenReturn(CompletableFuture.completedFuture(List.of(config1)));

        // When
        CompletableFuture<List<Configuration>> result = service.getConfigurationsUpdatedSince(TENANT_ID, since);

        // Then
        assertNotNull(result);
        List<Configuration> configs = result.join();
        assertEquals(1, configs.size());
    }

    @Test
    void getConfigurationChanges_ReturnsChanges() {
        // Given
        ConfigurationChange change1 = ConfigurationChange.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            ConfigurationChange.ChangeType.CREATE, null, 1,
            null, "value", USER, "Create"
        );

        when(configurationStore.findChangesByConfiguration(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(List.of(change1)));

        // When
        CompletableFuture<List<ConfigurationChange>> result = service.getConfigurationChanges(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE);

        // Then
        assertNotNull(result);
        List<ConfigurationChange> changes = result.join();
        assertEquals(1, changes.size());
    }

    @Test
    void getChangesByTimeRange_ReturnsChangesInRange() {
        // Given
        Instant from = Instant.now().minusSeconds(7200);
        Instant to = Instant.now();
        ConfigurationChange change1 = ConfigurationChange.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            ConfigurationChange.ChangeType.CREATE, null, 1,
            null, "value", USER, "Create"
        );

        when(configurationStore.findChangesByTimeRange(eq(TENANT_ID), eq(from), eq(to)))
            .thenReturn(CompletableFuture.completedFuture(List.of(change1)));

        // When
        CompletableFuture<List<ConfigurationChange>> result = service.getChangesByTimeRange(TENANT_ID, from, to);

        // Then
        assertNotNull(result);
        List<ConfigurationChange> changes = result.join();
        assertEquals(1, changes.size());
    }

    @Test
    void getChangeById_ReturnsChange() {
        // Given
        String changeId = "change-123";
        ConfigurationChange change1 = ConfigurationChange.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            ConfigurationChange.ChangeType.CREATE, null, 1,
            null, "value", USER, "Create"
        );

        when(configurationStore.findChangeById(eq(changeId)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(change1)));

        // When
        CompletableFuture<Optional<ConfigurationChange>> result = service.getChangeById(changeId);

        // Then
        assertNotNull(result);
        Optional<ConfigurationChange> change = result.join();
        assertTrue(change.isPresent());
    }

    @Test
    void getActiveConfigurationsForEnvironment_ReturnsActiveConfigs() {
        // Given
        Configuration config1 = Configuration.create(
            TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );
        Configuration config2 = Configuration.create(
            TENANT_ID, "config2", "development", NAMESPACE,
            "value2", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByStatus(eq(TENANT_ID), eq(Configuration.ConfigurationStatus.ACTIVE)))
            .thenReturn(CompletableFuture.completedFuture(List.of(config1, config2)));

        // When
        CompletableFuture<List<Configuration>> result = service.getActiveConfigurationsForEnvironment(TENANT_ID, ENVIRONMENT);

        // Then
        assertNotNull(result);
        List<Configuration> configs = result.join();
        assertEquals(1, configs.size());
        assertEquals(ENVIRONMENT, configs.get(0).environment());
    }

    @Test
    void getConfigurationsByStatus_ReturnsConfigsWithStatus() {
        // Given
        Configuration config1 = Configuration.create(
            TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
            "value1", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByStatus(eq(TENANT_ID), eq(Configuration.ConfigurationStatus.ACTIVE)))
            .thenReturn(CompletableFuture.completedFuture(List.of(config1)));

        // When
        CompletableFuture<List<Configuration>> result = service.getConfigurationsByStatus(TENANT_ID, Configuration.ConfigurationStatus.ACTIVE);

        // Then
        assertNotNull(result);
        List<Configuration> configs = result.join();
        assertEquals(1, configs.size());
    }

    @Test
    void validateConfigurationValue_ValidNumber_ReturnsTrue() {
        // Given
        Integer value = 42;
        ConfigurationSchema schema = new ConfigurationSchema.NumberSchema(
            ConfigurationDataType.NUMBER, true, null,
            Set.of(), Map.of(), 1.0, 100.0, null, null, null
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertTrue(result.join());
    }

    @Test
    void validateConfigurationValue_NumberTooSmall_ReturnsFalse() {
        // Given
        Integer value = 0;
        ConfigurationSchema schema = new ConfigurationSchema.NumberSchema(
            ConfigurationDataType.NUMBER, true, null,
            Set.of(), Map.of(), 1.0, 100.0, null, null, null
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertFalse(result.join());
    }

    @Test
    void validateConfigurationValue_NumberTooLarge_ReturnsFalse() {
        // Given
        Integer value = 101;
        ConfigurationSchema schema = new ConfigurationSchema.NumberSchema(
            ConfigurationDataType.NUMBER, true, null,
            Set.of(), Map.of(), 1.0, 100.0, null, null, null
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertFalse(result.join());
    }

    @Test
    void validateConfigurationValue_ValidBoolean_ReturnsTrue() {
        // Given
        Boolean value = true;
        ConfigurationSchema schema = new ConfigurationSchema.BooleanSchema(
            ConfigurationDataType.BOOLEAN, true, null,
            Set.of(), Map.of()
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertTrue(result.join());
    }

    @Test
    void validateConfigurationValue_InvalidBoolean_ReturnsFalse() {
        // Given
        String value = "not a boolean";
        ConfigurationSchema schema = new ConfigurationSchema.BooleanSchema(
            ConfigurationDataType.BOOLEAN, true, null,
            Set.of(), Map.of()
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertFalse(result.join());
    }

    @Test
    void validateConfigurationValue_ValidArray_ReturnsTrue() {
        // Given
        List<String> value = List.of("item1", "item2", "item3");
        ConfigurationSchema.StringSchema itemSchema = new ConfigurationSchema.StringSchema(
            ConfigurationDataType.STRING, true, null,
            Set.of(), Map.of(), null, null, null, null
        );
        ConfigurationSchema schema = new ConfigurationSchema.ArraySchema(
            ConfigurationDataType.ARRAY, true, null,
            Set.of(), Map.of(), itemSchema, 1, 10, false
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertTrue(result.join());
    }

    @Test
    void validateConfigurationValue_ArrayTooSmall_ReturnsFalse() {
        // Given
        List<String> value = List.of();
        ConfigurationSchema.StringSchema itemSchema = new ConfigurationSchema.StringSchema(
            ConfigurationDataType.STRING, true, null,
            Set.of(), Map.of(), null, null, null, null
        );
        ConfigurationSchema schema = new ConfigurationSchema.ArraySchema(
            ConfigurationDataType.ARRAY, true, null,
            Set.of(), Map.of(), itemSchema, 1, 10, false
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertFalse(result.join());
    }

    @Test
    void validateConfigurationValue_ArrayTooLarge_ReturnsFalse() {
        // Given
        List<String> value = List.of("item1", "item2", "item3", "item4", "item5", "item6");
        ConfigurationSchema.StringSchema itemSchema = new ConfigurationSchema.StringSchema(
            ConfigurationDataType.STRING, true, null,
            Set.of(), Map.of(), null, null, null, null
        );
        ConfigurationSchema schema = new ConfigurationSchema.ArraySchema(
            ConfigurationDataType.ARRAY, true, null,
            Set.of(), Map.of(), itemSchema, 1, 5, false
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertFalse(result.join());
    }

    @Test
    void validateConfigurationValue_StringTooLong_ReturnsFalse() {
        // Given
        String value = "This string is way too long for the maximum length constraint";
        ConfigurationSchema schema = new ConfigurationSchema.StringSchema(
            ConfigurationDataType.STRING, true, null,
            Set.of(), Map.of(), 1, 20, null, null
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertFalse(result.join());
    }

    @Test
    void validateConfigurationValue_StringPatternMismatch_ReturnsFalse() {
        // Given
        String value = "invalid-email";
        ConfigurationSchema schema = new ConfigurationSchema.StringSchema(
            ConfigurationDataType.STRING, true, null,
            Set.of(), Map.of(), null, null, "^[A-Za-z0-9+_.-]+@(.+)$", null
        );

        // When
        CompletableFuture<Boolean> result = service.validateConfigurationValue(value, schema);

        // Then
        assertNotNull(result);
        assertFalse(result.join());
    }

    @Test
    void updateConfiguration_DeprecatedConfigWithoutForce_ThrowsException() {
        // Given
        Configuration existingConfig = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "old-value", Configuration.ConfigurationDataType.STRING, USER
        );

        // Manually set status to DEPRECATED
        Configuration deprecatedConfig = new Configuration(
            existingConfig.id(),
            existingConfig.tenantId(),
            existingConfig.configKey(),
            existingConfig.environment(),
            existingConfig.namespace(),
            existingConfig.version(),
            existingConfig.value(),
            existingConfig.dataType(),
            existingConfig.encrypted(),
            existingConfig.required(),
            existingConfig.defaultValue(),
            existingConfig.description(),
            existingConfig.tags(),
            existingConfig.metadata(),
            existingConfig.schema(),
            Configuration.ConfigurationStatus.DEPRECATED,
            existingConfig.createdBy(),
            existingConfig.createdAt(),
            existingConfig.updatedBy(),
            existingConfig.updatedAt(),
            existingConfig.lastValidatedAt(),
            existingConfig.validationErrors()
        );

        ConfigurationCommand.UpdateConfigurationCommand command = new ConfigurationCommand.UpdateConfigurationCommand(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "new-value", USER, "Update", false
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(deprecatedConfig)));

        // When & Then
        CompletableFuture<Optional<Configuration>> result = service.updateConfiguration(command);
        Exception exception = assertThrows(java.util.concurrent.CompletionException.class, result::join);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getCause() instanceof IllegalStateException);
    }

    @Test
    void deleteConfiguration_RequiredConfig_ThrowsException() {
        // Given
        Configuration existingConfig = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "value", Configuration.ConfigurationDataType.STRING, USER
        );

        // Manually set required to true
        Configuration requiredConfig = new Configuration(
            existingConfig.id(),
            existingConfig.tenantId(),
            existingConfig.configKey(),
            existingConfig.environment(),
            existingConfig.namespace(),
            existingConfig.version(),
            existingConfig.value(),
            existingConfig.dataType(),
            existingConfig.encrypted(),
            true, // required
            existingConfig.defaultValue(),
            existingConfig.description(),
            existingConfig.tags(),
            existingConfig.metadata(),
            existingConfig.schema(),
            existingConfig.status(),
            existingConfig.createdBy(),
            existingConfig.createdAt(),
            existingConfig.updatedBy(),
            existingConfig.updatedAt(),
            existingConfig.lastValidatedAt(),
            existingConfig.validationErrors()
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(requiredConfig)));

        // When
        CompletableFuture<Boolean> result = service.deleteConfiguration(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE, USER);

        // Then
        assertNotNull(result);
        Boolean deleted = result.join();
        assertFalse(deleted);

        verify(configurationStore, never()).save(any(Configuration.class));
    }

    @Test
    void createConfiguration_WithSchemaValidation_Success() {
        // Given
        ConfigurationSchema.StringSchema schema = new ConfigurationSchema.StringSchema(
            ConfigurationDataType.STRING, true, null,
            Set.of(), Map.of(), 1, 100, null, null
        );

        ConfigurationCommand.CreateConfigurationCommand command = new ConfigurationCommand.CreateConfigurationCommand(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "valid-string-value", Configuration.ConfigurationDataType.STRING,
            false, false, null, "Test configuration",
            Set.of("tag1"), Map.of(),
            schema, "Initial creation", USER
        );

        Configuration config = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "valid-string-value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
        when(configurationStore.save(any(Configuration.class)))
            .thenReturn(CompletableFuture.completedFuture(config));
        when(configurationStore.saveChange(any(ConfigurationChange.class)))
            .thenReturn(CompletableFuture.completedFuture(ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.CREATE, null, 1,
                null, "valid-string-value", USER, null
            )));
        when(cacheStore.put(any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(cacheStore.evictByNamespace(any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Configuration> result = service.createConfiguration(command);

        // Then
        assertNotNull(result);
        Configuration savedConfig = result.join();
        assertNotNull(savedConfig);
        assertEquals(TENANT_ID, savedConfig.tenantId());
        assertEquals(CONFIG_KEY, savedConfig.configKey());
    }

    @Test
    void createConfiguration_SchemaValidationFails_ThrowsException() {
        // Given
        ConfigurationSchema.StringSchema schema = new ConfigurationSchema.StringSchema(
            ConfigurationDataType.STRING, true, null,
            Set.of(), Map.of(), 5, 100, null, null
        );

        ConfigurationCommand.CreateConfigurationCommand command = new ConfigurationCommand.CreateConfigurationCommand(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "ab", Configuration.ConfigurationDataType.STRING,
            false, false, null, "Test configuration",
            Set.of("tag1"), Map.of(),
            schema, "Initial creation", USER
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // When & Then
        CompletableFuture<Configuration> result = service.createConfiguration(command);
        Exception exception = assertThrows(java.util.concurrent.CompletionException.class, result::join);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getCause() instanceof IllegalArgumentException);
    }

    @Test
    void createConfiguration_WithDeprecatedExistingConfig_Success() {
        // Given
        Configuration existingConfig = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "old-value", Configuration.ConfigurationDataType.STRING, USER
        );

        // Manually set status to DEPRECATED
        Configuration deprecatedConfig = new Configuration(
            existingConfig.id(),
            existingConfig.tenantId(),
            existingConfig.configKey(),
            existingConfig.environment(),
            existingConfig.namespace(),
            existingConfig.version(),
            existingConfig.value(),
            existingConfig.dataType(),
            existingConfig.encrypted(),
            existingConfig.required(),
            existingConfig.defaultValue(),
            existingConfig.description(),
            existingConfig.tags(),
            existingConfig.metadata(),
            existingConfig.schema(),
            Configuration.ConfigurationStatus.DEPRECATED,
            existingConfig.createdBy(),
            existingConfig.createdAt(),
            existingConfig.updatedBy(),
            existingConfig.updatedAt(),
            existingConfig.lastValidatedAt(),
            existingConfig.validationErrors()
        );

        ConfigurationCommand.CreateConfigurationCommand command = new ConfigurationCommand.CreateConfigurationCommand(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "new-value", Configuration.ConfigurationDataType.STRING,
            false, false, null, "Test configuration",
            Set.of("tag1"), Map.of(),
            null, "Recreate deprecated config", USER
        );

        Configuration newConfig = Configuration.create(
            TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
            "new-value", Configuration.ConfigurationDataType.STRING, USER
        );

        when(configurationStore.findByKeyAndEnvironment(eq(TENANT_ID), eq(CONFIG_KEY), eq(ENVIRONMENT), eq(NAMESPACE)))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(deprecatedConfig)));
        when(configurationStore.save(any(Configuration.class)))
            .thenReturn(CompletableFuture.completedFuture(newConfig));
        when(configurationStore.saveChange(any(ConfigurationChange.class)))
            .thenReturn(CompletableFuture.completedFuture(ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.CREATE, null, 1,
                null, "new-value", USER, null
            )));
        when(cacheStore.put(any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));
        when(cacheStore.evictByNamespace(any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Configuration> result = service.createConfiguration(command);

        // Then
        assertNotNull(result);
        Configuration savedConfig = result.join();
        assertNotNull(savedConfig);
        assertEquals("new-value", savedConfig.value());
    }
}
