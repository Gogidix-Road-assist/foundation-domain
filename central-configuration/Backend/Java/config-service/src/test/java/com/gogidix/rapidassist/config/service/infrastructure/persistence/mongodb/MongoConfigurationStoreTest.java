package com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationDataType;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MongoConfigurationStore.
 * Tests MongoDB repository adapter with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MongoConfigurationStore Tests")
class MongoConfigurationStoreTest {

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private ConfigurationChangeRepository changeRepository;

    private MongoConfigurationStore store;

    private static final String TENANT_ID = "test-tenant";
    private static final String CONFIG_KEY = "test.config";
    private static final String ENVIRONMENT = "production";
    private static final String NAMESPACE = "api-gateway";
    private static final String USER = "admin";

    @BeforeEach
    void setUp() {
        store = new MongoConfigurationStore();
        ReflectionTestUtils.setField(store, "configurationRepository", configurationRepository);
        ReflectionTestUtils.setField(store, "changeRepository", changeRepository);
    }

    @Nested
    @DisplayName("Save Configuration")
    class SaveConfigurationTests {

        @Test
        @DisplayName("Should save configuration successfully")
        void save_Success() {
            // Given
            Configuration config = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            ConfigurationDocument doc = ConfigurationDocument.fromDomain(config);
            when(configurationRepository.save(any(ConfigurationDocument.class)))
                .thenReturn(doc);

            // When
            CompletableFuture<Configuration> result = store.save(config);

            // Then
            assertNotNull(result);
            Configuration saved = result.join();
            assertEquals(TENANT_ID, saved.tenantId());
            assertEquals(CONFIG_KEY, saved.configKey());
            verify(configurationRepository).save(any(ConfigurationDocument.class));
        }

        @Test
        @DisplayName("Should throw exception when save fails")
        void save_RepositoryThrows_ThrowsRuntimeException() {
            // Given
            Configuration config = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            when(configurationRepository.save(any(ConfigurationDocument.class)))
                .thenThrow(new RuntimeException("Database error"));

            // When & Then
            CompletableFuture<Configuration> result = store.save(config);
            assertThrows(RuntimeException.class, result::join);
        }
    }

    @Nested
    @DisplayName("Find Configuration By Id")
    class FindByIdTests {

        @Test
        @DisplayName("Should find configuration by id")
        void findById_Found() {
            // Given
            String id = "config-123";
            Configuration config = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );
            ConfigurationDocument doc = ConfigurationDocument.fromDomain(config);

            when(configurationRepository.findById(id))
                .thenReturn(Optional.of(doc));

            // When
            CompletableFuture<Optional<Configuration>> result = store.findById(id);

            // Then
            assertNotNull(result);
            Optional<Configuration> found = result.join();
            assertTrue(found.isPresent());
            assertEquals(TENANT_ID, found.get().tenantId());
        }

        @Test
        @DisplayName("Should return empty when not found")
        void findById_NotFound() {
            // Given
            when(configurationRepository.findById("non-existent"))
                .thenReturn(Optional.empty());

            // When
            CompletableFuture<Optional<Configuration>> result = store.findById("non-existent");

            // Then
            assertNotNull(result);
            Optional<Configuration> found = result.join();
            assertFalse(found.isPresent());
        }
    }

    @Nested
    @DisplayName("Find Configuration By Key And Environment")
    class FindByKeyAndEnvironmentTests {

        @Test
        @DisplayName("Should find configuration by key and environment")
        void findByKeyAndEnvironment_Found() {
            // Given
            Configuration config = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );
            ConfigurationDocument doc = ConfigurationDocument.fromDomain(config);

            when(configurationRepository.findByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE))
                .thenReturn(Optional.of(doc));

            // When
            var result = store.findByKeyAndEnvironment(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE);

            // Then
            assertNotNull(result);
            Optional<Configuration> found = result.join();
            assertTrue(found.isPresent());
            assertEquals(CONFIG_KEY, found.get().configKey());
        }

        @Test
        @DisplayName("Should return empty on repository exception")
        void findByKeyAndEnvironment_Exception() {
            // Given
            when(configurationRepository.findByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
                any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

            // When
            var result = store.findByKeyAndEnvironment(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE);

            // Then
            assertNotNull(result);
            Optional<Configuration> found = result.join();
            assertFalse(found.isPresent());
        }
    }

    @Nested
    @DisplayName("Find By Tenant")
    class FindByTenantTests {

        @Test
        @DisplayName("Should find all configurations for tenant")
        void findByTenant_ReturnsConfigurations() {
            // Given
            Configuration config1 = Configuration.create(
                TENANT_ID, "config1", ENVIRONMENT, NAMESPACE,
                "value1", Configuration.ConfigurationDataType.STRING, USER
            );
            Configuration config2 = Configuration.create(
                TENANT_ID, "config2", ENVIRONMENT, NAMESPACE,
                "value2", Configuration.ConfigurationDataType.STRING, USER
            );

            when(configurationRepository.findByTenantId(TENANT_ID))
                .thenReturn(List.of(
                    ConfigurationDocument.fromDomain(config1),
                    ConfigurationDocument.fromDomain(config2)
                ));

            // When
            CompletableFuture<List<Configuration>> result = store.findByTenant(TENANT_ID);

            // Then
            assertNotNull(result);
            List<Configuration> configs = result.join();
            assertEquals(2, configs.size());
        }

        @Test
        @DisplayName("Should return empty list on exception")
        void findByTenant_Exception() {
            // Given
            when(configurationRepository.findByTenantId(TENANT_ID))
                .thenThrow(new RuntimeException("Database error"));

            // When
            CompletableFuture<List<Configuration>> result = store.findByTenant(TENANT_ID);

            // Then
            assertNotNull(result);
            List<Configuration> configs = result.join();
            assertTrue(configs.isEmpty());
        }
    }

    @Nested
    @DisplayName("Find By Namespace")
    class FindByNamespaceTests {

        @Test
        @DisplayName("Should find configurations by namespace")
        void findByNamespace_ReturnsConfigurations() {
            // Given
            Configuration config = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            when(configurationRepository.findByTenantIdAndEnvironmentAndNamespace(
                TENANT_ID, ENVIRONMENT, NAMESPACE))
                .thenReturn(List.of(ConfigurationDocument.fromDomain(config)));

            // When
            var result = store.findByNamespace(TENANT_ID, ENVIRONMENT, NAMESPACE);

            // Then
            assertNotNull(result);
            List<Configuration> configs = result.join();
            assertEquals(1, configs.size());
        }
    }

    @Nested
    @DisplayName("Find All Versions")
    class FindAllVersionsTests {

        @Test
        @DisplayName("Should find all versions of configuration")
        void findAllVersions_ReturnsAllVersions() {
            // Given
            Configuration v1 = Configuration.create(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "v1", Configuration.ConfigurationDataType.STRING, USER).withVersion(1);
            Configuration v2 = Configuration.create(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "v2", Configuration.ConfigurationDataType.STRING, USER).withVersion(2);

            when(configurationRepository.findAllVersions(TENANT_ID, CONFIG_KEY, ENVIRONMENT))
                .thenReturn(List.of(
                    ConfigurationDocument.fromDomain(v1),
                    ConfigurationDocument.fromDomain(v2)
                ));

            // When
            var result = store.findAllVersions(TENANT_ID, CONFIG_KEY, ENVIRONMENT);

            // Then
            assertNotNull(result);
            List<Configuration> versions = result.join();
            assertEquals(2, versions.size());
        }
    }

    @Nested
    @DisplayName("Save Configuration Change")
    class SaveChangeTests {

        @Test
        @DisplayName("Should save configuration change")
        void saveChange_Success() {
            // Given
            ConfigurationChange change = ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 1, 2,
                "old", "new", USER, "Update"
            );

            ConfigurationChangeDocument doc = ConfigurationChangeDocument.fromDomain(change);
            when(changeRepository.save(any(ConfigurationChangeDocument.class)))
                .thenReturn(doc);

            // When
            CompletableFuture<ConfigurationChange> result = store.saveChange(change);

            // Then
            assertNotNull(result);
            ConfigurationChange saved = result.join();
            assertEquals(TENANT_ID, saved.tenantId());
            verify(changeRepository).save(any(ConfigurationChangeDocument.class));
        }
    }

    @Nested
    @DisplayName("Find Pending Approvals")
    class FindPendingApprovalsTests {

        @Test
        @DisplayName("Should find pending approvals")
        void findPendingApprovals_ReturnsPending() {
            // Given
            ConfigurationChange change = new ConfigurationChange(
                null, TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "change-123", ConfigurationChange.ChangeType.UPDATE,
                1, 2, "old", "new", USER, Instant.now(),
                "Update", ConfigurationChange.ApprovalStatus.PENDING,
                null, null, null, null, null,
                null, null, null
            );

            when(changeRepository.findByTenantIdAndApprovalStatus(
                TENANT_ID, ConfigurationChange.ApprovalStatus.PENDING))
                .thenReturn(List.of(ConfigurationChangeDocument.fromDomain(change)));

            // When
            var result = store.findPendingApprovals(TENANT_ID);

            // Then
            assertNotNull(result);
            List<ConfigurationChange> pending = result.join();
            assertEquals(1, pending.size());
            assertEquals(ConfigurationChange.ApprovalStatus.PENDING, pending.get(0).approvalStatus());
        }
    }

    @Nested
    @DisplayName("Find By Status")
    class FindByStatusTests {

        @Test
        @DisplayName("Should find configurations by status")
        void findByStatus_ReturnsConfigurations() {
            // Given
            Configuration config = Configuration.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "test-value", Configuration.ConfigurationDataType.STRING, USER
            );

            when(configurationRepository.findByTenantIdAndStatus(
                TENANT_ID, Configuration.ConfigurationStatus.ACTIVE))
                .thenReturn(List.of(ConfigurationDocument.fromDomain(config)));

            // When
            var result = store.findByStatus(TENANT_ID, Configuration.ConfigurationStatus.ACTIVE);

            // Then
            assertNotNull(result);
            List<Configuration> configs = result.join();
            assertEquals(1, configs.size());
            assertEquals(Configuration.ConfigurationStatus.ACTIVE, configs.get(0).status());
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperationTests {

        @Test
        @DisplayName("Should delete configuration by id")
        void deleteById_Success() {
            // Given
            String id = "config-123";
            doNothing().when(configurationRepository).deleteById(id);

            // When
            CompletableFuture<Boolean> result = store.deleteById(id);

            // Then
            assertNotNull(result);
            assertTrue(result.join());
            verify(configurationRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should delete configuration by key and environment")
        void deleteByKeyAndEnvironment_Success() {
            // Given
            doNothing().when(configurationRepository)
                .deleteByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
                    TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE);

            // When
            var result = store.deleteByKeyAndEnvironment(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE);

            // Then
            assertNotNull(result);
            assertTrue(result.join());
        }

        @Test
        @DisplayName("Should return false on delete exception")
        void deleteByKeyAndEnvironment_Exception() {
            // Given
            doThrow(new RuntimeException("Database error")).when(configurationRepository)
                .deleteByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
                    any(), any(), any(), any());

            // When
            var result = store.deleteByKeyAndEnvironment(TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE);

            // Then
            assertNotNull(result);
            assertFalse(result.join());
        }
    }
}
