package com.gogidix.rapidassist.integration.adapters.service.application.service;

import com.gogidix.rapidassist.integration.adapters.service.application.dto.CreateIntegrationConfigRequest;
import com.gogidix.rapidassist.integration.adapters.service.application.dto.IntegrationConfigResponse;
import com.gogidix.rapidassist.integration.adapters.service.application.dto.UpdateIntegrationConfigRequest;
import com.gogidix.rapidassist.integration.adapters.service.domain.model.IntegrationConfig;
import com.gogidix.rapidassist.integration.adapters.service.domain.port.out.IntegrationConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegrationConfigServiceTest {

    @Mock
    private IntegrationConfigRepository repository;

    @InjectMocks
    private IntegrationConfigService service;

    private CreateIntegrationConfigRequest createRequest;
    private UpdateIntegrationConfigRequest updateRequest;
    private IntegrationConfig existingConfig;

    @BeforeEach
    void setUp() {
        createRequest = new CreateIntegrationConfigRequest();
        createRequest.setProvider("stripe");
        createRequest.setProviderName("Stripe Payments");
        createRequest.setApiEndpoint("https://api.stripe.com");
        createRequest.setEnabled(true);

        updateRequest = new UpdateIntegrationConfigRequest();
        updateRequest.setProvider("stripe");
        updateRequest.setProviderName("Updated Stripe Payments");
        updateRequest.setApiEndpoint("https://api.stripe.com/v2");
        updateRequest.setEnabled(true);

        existingConfig = new IntegrationConfig();
        existingConfig.setId("config-1");
        existingConfig.setTenantId("tenant-1");
        existingConfig.setProvider("stripe");
        existingConfig.setProviderName("Stripe Payments");
        existingConfig.setApiEndpoint("https://api.stripe.com");
        existingConfig.setEnabled(true);
        existingConfig.setSyncStatus("ACTIVE");
    }

    @Test
    void testCreate_TenantIdPropagated() {
        when(repository.findByTenantIdAndProvider("tenant-1", "stripe")).thenReturn(Optional.empty());
        when(repository.save(any(IntegrationConfig.class))).thenAnswer(invocation -> {
            IntegrationConfig config = invocation.getArgument(0);
            assertThat(config.getTenantId()).isEqualTo("tenant-1");
            assertThat(config.getSyncStatus()).isEqualTo("PENDING");
            config.setId("config-1");
            return config;
        });

        IntegrationConfigResponse response = service.create("tenant-1", createRequest);

        assertThat(response.getTenantId()).isEqualTo("tenant-1");
        assertThat(response.getProvider()).isEqualTo("stripe");
        verify(repository).save(any(IntegrationConfig.class));
    }

    @Test
    void testCreate_DuplicateProviderInSameTenant_ThrowsException() {
        when(repository.findByTenantIdAndProvider("tenant-1", "stripe"))
                .thenReturn(Optional.of(existingConfig));

        assertThatThrownBy(() -> service.create("tenant-1", createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void testCreate_DuplicateProviderInDifferentTenant_Allowed() {
        when(repository.findByTenantIdAndProvider("tenant-2", "stripe")).thenReturn(Optional.empty());
        when(repository.save(any(IntegrationConfig.class))).thenAnswer(invocation -> {
            IntegrationConfig config = invocation.getArgument(0);
            assertThat(config.getTenantId()).isEqualTo("tenant-2");
            config.setId("config-2");
            return config;
        });

        IntegrationConfigResponse response = service.create("tenant-2", createRequest);

        assertThat(response.getTenantId()).isEqualTo("tenant-2");
        verify(repository).save(any(IntegrationConfig.class));
    }

    @Test
    void testFindByTenant_TenantIsolation() {
        IntegrationConfig config1 = new IntegrationConfig();
        config1.setId("config-1");
        config1.setTenantId("tenant-1");
        config1.setProvider("stripe");

        IntegrationConfig config2 = new IntegrationConfig();
        config2.setId("config-2");
        config2.setTenantId("tenant-2");
        config2.setProvider("stripe");

        when(repository.findByTenantId("tenant-1")).thenReturn(List.of(config1));
        when(repository.findByTenantId("tenant-2")).thenReturn(List.of(config2));

        List<IntegrationConfigResponse> tenant1Results = service.findByTenant("tenant-1");
        assertThat(tenant1Results).hasSize(1);
        assertThat(tenant1Results.get(0).getTenantId()).isEqualTo("tenant-1");

        List<IntegrationConfigResponse> tenant2Results = service.findByTenant("tenant-2");
        assertThat(tenant2Results).hasSize(1);
        assertThat(tenant2Results.get(0).getTenantId()).isEqualTo("tenant-2");
    }

    @Test
    void testFindByTenantAndId_CrossTenantAccessDenied() {
        when(repository.findByTenantIdAndId("tenant-2", "config-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByTenantAndId("tenant-2", "config-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testUpdate_TenantIsolationVerified() {
        when(repository.findByTenantIdAndId("tenant-1", "config-1"))
                .thenReturn(Optional.of(existingConfig));
        when(repository.findByTenantIdAndProviderExcludingId("tenant-1", "stripe", "config-1"))
                .thenReturn(Optional.empty());
        when(repository.save(any(IntegrationConfig.class))).thenReturn(existingConfig);

        IntegrationConfigResponse response = service.update("tenant-1", "config-1", updateRequest);

        assertThat(response.getTenantId()).isEqualTo("tenant-1");
        verify(repository).save(any(IntegrationConfig.class));
    }

    @Test
    void testUpdate_CrossTenantAccessDenied() {
        when(repository.findByTenantIdAndId("tenant-2", "config-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update("tenant-2", "config-1", updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testDelete_TenantIsolationVerified() {
        when(repository.findByTenantIdAndId("tenant-1", "config-1"))
                .thenReturn(Optional.of(existingConfig));

        service.delete("tenant-1", "config-1");

        verify(repository).delete(existingConfig);
    }

    @Test
    void testDelete_CrossTenantAccessDenied() {
        when(repository.findByTenantIdAndId("tenant-2", "config-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("tenant-2", "config-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");

        verify(repository, never()).delete(any());
    }

    @Test
    void testCountByTenant_TenantIsolation() {
        when(repository.countByTenantId("tenant-1")).thenReturn(5L);
        when(repository.countByTenantId("tenant-2")).thenReturn(3L);

        assertThat(service.countByTenant("tenant-1")).isEqualTo(5L);
        assertThat(service.countByTenant("tenant-2")).isEqualTo(3L);
    }

    @Test
    void testSearchByTenant_TenantIsolation() {
        IntegrationConfig config1 = new IntegrationConfig();
        config1.setId("config-1");
        config1.setTenantId("tenant-1");
        config1.setProvider("stripe");
        config1.setProviderName("Stripe Payments");

        when(repository.searchByTenantId("tenant-1", "Stripe"))
                .thenReturn(List.of(config1));
        when(repository.searchByTenantId("tenant-2", "Stripe"))
                .thenReturn(List.of());

        List<IntegrationConfigResponse> tenant1Results = service.searchByTenant("tenant-1", "Stripe");
        assertThat(tenant1Results).hasSize(1);

        List<IntegrationConfigResponse> tenant2Results = service.searchByTenant("tenant-2", "Stripe");
        assertThat(tenant2Results).isEmpty();
    }

    @Test
    void testEnable_TenantIsolation() {
        existingConfig.setEnabled(false);
        when(repository.findByTenantIdAndId("tenant-1", "config-1"))
                .thenReturn(Optional.of(existingConfig));
        when(repository.save(any(IntegrationConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IntegrationConfigResponse response = service.enable("tenant-1", "config-1");

        assertThat(response.getEnabled()).isTrue();
        verify(repository).save(any(IntegrationConfig.class));
    }

    @Test
    void testDisable_TenantIsolation() {
        existingConfig.setEnabled(true);
        when(repository.findByTenantIdAndId("tenant-1", "config-1"))
                .thenReturn(Optional.of(existingConfig));
        when(repository.save(any(IntegrationConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IntegrationConfigResponse response = service.disable("tenant-1", "config-1");

        assertThat(response.getEnabled()).isFalse();
        verify(repository).save(any(IntegrationConfig.class));
    }

    @Test
    void testUpdateSyncStatus_TenantIsolation() {
        when(repository.findByTenantIdAndId("tenant-1", "config-1"))
                .thenReturn(Optional.of(existingConfig));
        when(repository.save(any(IntegrationConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IntegrationConfigResponse response = service.updateSyncStatus("tenant-1", "config-1", "CONNECTED");

        assertThat(response.getSyncStatus()).isEqualTo("CONNECTED");
        assertThat(response.getLastSyncAt()).isNotNull();
        verify(repository).save(any(IntegrationConfig.class));
    }

    @Test
    void testTestConnection_TenantIsolation() {
        when(repository.findByTenantIdAndId("tenant-1", "config-1"))
                .thenReturn(Optional.of(existingConfig));
        when(repository.save(any(IntegrationConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IntegrationConfigResponse response = service.testConnection("tenant-1", "config-1");

        assertThat(response.getSyncStatus()).isEqualTo("CONNECTED");
        assertThat(response.getLastSyncAt()).isNotNull();
        verify(repository).save(any(IntegrationConfig.class));
    }
}
