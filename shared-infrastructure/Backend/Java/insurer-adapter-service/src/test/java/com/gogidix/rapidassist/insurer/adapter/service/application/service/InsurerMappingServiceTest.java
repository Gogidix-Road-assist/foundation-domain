package com.gogidix.rapidassist.insurer.adapter.service.application.service;

import com.gogidix.rapidassist.insurer.adapter.service.application.dto.CreateInsurerMappingRequest;
import com.gogidix.rapidassist.insurer.adapter.service.application.dto.InsurerMappingResponse;
import com.gogidix.rapidassist.insurer.adapter.service.application.dto.UpdateInsurerMappingRequest;
import com.gogidix.rapidassist.insurer.adapter.service.domain.model.InsurerMapping;
import com.gogidix.rapidassist.insurer.adapter.service.domain.port.out.InsurerMappingRepository;
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
class InsurerMappingServiceTest {

    @Mock
    private InsurerMappingRepository repository;

    @InjectMocks
    private InsurerMappingService service;

    private CreateInsurerMappingRequest createRequest;
    private UpdateInsurerMappingRequest updateRequest;
    private InsurerMapping existingMapping;

    @BeforeEach
    void setUp() {
        createRequest = new CreateInsurerMappingRequest();
        createRequest.setInsurerCode("INS-A");
        createRequest.setInsurerName("Insurer A");
        createRequest.setAdapterType("REST_API");
        createRequest.setEnabled(true);

        updateRequest = new UpdateInsurerMappingRequest();
        updateRequest.setInsurerCode("INS-A");
        updateRequest.setInsurerName("Updated Insurer A");
        updateRequest.setAdapterType("REST_API");
        updateRequest.setEnabled(true);

        existingMapping = new InsurerMapping();
        existingMapping.setId("mapping-1");
        existingMapping.setTenantId("tenant-1");
        existingMapping.setInsurerCode("INS-A");
        existingMapping.setInsurerName("Insurer A");
        existingMapping.setAdapterType("REST_API");
        existingMapping.setEnabled(true);
    }

    @Test
    void testCreate_TenantIdPropagated() {
        when(repository.findByTenantIdAndInsurerCode("tenant-1", "INS-A")).thenReturn(Optional.empty());
        when(repository.save(any(InsurerMapping.class))).thenAnswer(invocation -> {
            InsurerMapping mapping = invocation.getArgument(0);
            assertThat(mapping.getTenantId()).isEqualTo("tenant-1");
            mapping.setId("mapping-1");
            return mapping;
        });

        InsurerMappingResponse response = service.create("tenant-1", createRequest);

        assertThat(response.getTenantId()).isEqualTo("tenant-1");
        assertThat(response.getInsurerCode()).isEqualTo("INS-A");
        verify(repository).save(any(InsurerMapping.class));
    }

    @Test
    void testCreate_DuplicateCodeInSameTenant_ThrowsException() {
        when(repository.findByTenantIdAndInsurerCode("tenant-1", "INS-A"))
                .thenReturn(Optional.of(existingMapping));

        assertThatThrownBy(() -> service.create("tenant-1", createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void testCreate_DuplicateCodeInDifferentTenant_Allowed() {
        when(repository.findByTenantIdAndInsurerCode("tenant-2", "INS-A")).thenReturn(Optional.empty());
        when(repository.save(any(InsurerMapping.class))).thenAnswer(invocation -> {
            InsurerMapping mapping = invocation.getArgument(0);
            assertThat(mapping.getTenantId()).isEqualTo("tenant-2");
            mapping.setId("mapping-2");
            return mapping;
        });

        InsurerMappingResponse response = service.create("tenant-2", createRequest);

        assertThat(response.getTenantId()).isEqualTo("tenant-2");
        verify(repository).save(any(InsurerMapping.class));
    }

    @Test
    void testFindByTenant_TenantIsolation() {
        InsurerMapping mapping1 = new InsurerMapping();
        mapping1.setId("mapping-1");
        mapping1.setTenantId("tenant-1");
        mapping1.setInsurerCode("INS-A");

        InsurerMapping mapping2 = new InsurerMapping();
        mapping2.setId("mapping-2");
        mapping2.setTenantId("tenant-2");
        mapping2.setInsurerCode("INS-A");

        when(repository.findByTenantId("tenant-1")).thenReturn(List.of(mapping1));
        when(repository.findByTenantId("tenant-2")).thenReturn(List.of(mapping2));

        List<InsurerMappingResponse> tenant1Results = service.findByTenant("tenant-1");
        assertThat(tenant1Results).hasSize(1);
        assertThat(tenant1Results.get(0).getTenantId()).isEqualTo("tenant-1");

        List<InsurerMappingResponse> tenant2Results = service.findByTenant("tenant-2");
        assertThat(tenant2Results).hasSize(1);
        assertThat(tenant2Results.get(0).getTenantId()).isEqualTo("tenant-2");
    }

    @Test
    void testFindByTenantAndId_CrossTenantAccessDenied() {
        when(repository.findByTenantIdAndId("tenant-1", "mapping-1"))
                .thenReturn(Optional.of(existingMapping));
        when(repository.findByTenantIdAndId("tenant-2", "mapping-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByTenantAndId("tenant-2", "mapping-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testUpdate_TenantIsolationVerified() {
        when(repository.findByTenantIdAndId("tenant-1", "mapping-1"))
                .thenReturn(Optional.of(existingMapping));
        when(repository.findByTenantIdAndInsurerCodeExcludingId("tenant-1", "INS-A", "mapping-1"))
                .thenReturn(Optional.empty());
        when(repository.save(any(InsurerMapping.class))).thenReturn(existingMapping);

        InsurerMappingResponse response = service.update("tenant-1", "mapping-1", updateRequest);

        assertThat(response.getTenantId()).isEqualTo("tenant-1");
        verify(repository).save(any(InsurerMapping.class));
    }

    @Test
    void testUpdate_CrossTenantAccessDenied() {
        when(repository.findByTenantIdAndId("tenant-2", "mapping-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update("tenant-2", "mapping-1", updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testDelete_TenantIsolationVerified() {
        when(repository.findByTenantIdAndId("tenant-1", "mapping-1"))
                .thenReturn(Optional.of(existingMapping));

        service.delete("tenant-1", "mapping-1");

        verify(repository).delete(existingMapping);
    }

    @Test
    void testDelete_CrossTenantAccessDenied() {
        when(repository.findByTenantIdAndId("tenant-2", "mapping-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("tenant-2", "mapping-1"))
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
        InsurerMapping mapping1 = new InsurerMapping();
        mapping1.setId("mapping-1");
        mapping1.setTenantId("tenant-1");
        mapping1.setInsurerCode("INS-A");
        mapping1.setInsurerName("Insurer Alpha");

        when(repository.searchByTenantId("tenant-1", "Alpha"))
                .thenReturn(List.of(mapping1));
        when(repository.searchByTenantId("tenant-2", "Alpha"))
                .thenReturn(List.of());

        List<InsurerMappingResponse> tenant1Results = service.searchByTenant("tenant-1", "Alpha");
        assertThat(tenant1Results).hasSize(1);

        List<InsurerMappingResponse> tenant2Results = service.searchByTenant("tenant-2", "Alpha");
        assertThat(tenant2Results).isEmpty();
    }

    @Test
    void testEnable_TenantIsolation() {
        existingMapping.setEnabled(false);
        when(repository.findByTenantIdAndId("tenant-1", "mapping-1"))
                .thenReturn(Optional.of(existingMapping));
        when(repository.save(any(InsurerMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InsurerMappingResponse response = service.enable("tenant-1", "mapping-1");

        assertThat(response.getEnabled()).isTrue();
        verify(repository).save(any(InsurerMapping.class));
    }

    @Test
    void testDisable_TenantIsolation() {
        existingMapping.setEnabled(true);
        when(repository.findByTenantIdAndId("tenant-1", "mapping-1"))
                .thenReturn(Optional.of(existingMapping));
        when(repository.save(any(InsurerMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InsurerMappingResponse response = service.disable("tenant-1", "mapping-1");

        assertThat(response.getEnabled()).isFalse();
        verify(repository).save(any(InsurerMapping.class));
    }
}
