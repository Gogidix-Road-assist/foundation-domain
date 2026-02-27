package com.gogidix.rapidassist.insurer.adapter.service.domain.port.out;

import com.gogidix.rapidassist.insurer.adapter.service.domain.model.InsurerMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class InsurerMappingRepositoryTest {

    @Autowired
    private InsurerMappingRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private InsurerMapping mapping1;
    private InsurerMapping mapping2;
    private InsurerMapping mapping3;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        mapping1 = new InsurerMapping();
        mapping1.setTenantId("tenant-1");
        mapping1.setInsurerCode("INS-A");
        mapping1.setInsurerName("Insurer A");
        mapping1.setAdapterType("REST_API");
        mapping1.setEnabled(true);

        mapping2 = new InsurerMapping();
        mapping2.setTenantId("tenant-1");
        mapping2.setInsurerCode("INS-B");
        mapping2.setInsurerName("Insurer B");
        mapping2.setAdapterType("SOAP_API");
        mapping2.setEnabled(true);

        mapping3 = new InsurerMapping();
        mapping3.setTenantId("tenant-2");
        mapping3.setInsurerCode("INS-A");
        mapping3.setInsurerName("Insurer A");
        mapping3.setAdapterType("REST_API");
        mapping3.setEnabled(true);
    }

    @Test
    void testTenantIsolation_FindByTenantId() {
        repository.save(mapping1);
        repository.save(mapping2);
        repository.save(mapping3);

        List<InsurerMapping> tenant1Results = repository.findByTenantId("tenant-1");
        assertThat(tenant1Results).hasSize(2);
        assertThat(tenant1Results).allMatch(m -> m.getTenantId().equals("tenant-1"));

        List<InsurerMapping> tenant2Results = repository.findByTenantId("tenant-2");
        assertThat(tenant2Results).hasSize(1);
        assertThat(tenant2Results.get(0).getTenantId()).isEqualTo("tenant-2");
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndId() {
        repository.save(mapping1);
        repository.save(mapping3);

        String mapping1Id = mapping1.getId();
        String mapping3Id = mapping3.getId();

        assertThat(repository.findByTenantIdAndId("tenant-1", mapping1Id)).isPresent();
        assertThat(repository.findByTenantIdAndId("tenant-1", mapping3Id)).isEmpty();

        assertThat(repository.findByTenantIdAndId("tenant-2", mapping3Id)).isPresent();
        assertThat(repository.findByTenantIdAndId("tenant-2", mapping1Id)).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndInsurerCode() {
        repository.save(mapping1);
        repository.save(mapping3);

        assertThat(repository.findByTenantIdAndInsurerCode("tenant-1", "INS-A")).isPresent();
        assertThat(repository.findByTenantIdAndInsurerCode("tenant-2", "INS-A")).isPresent();

        assertThat(repository.findByTenantIdAndInsurerCode("tenant-1", "INS-B")).isPresent();
        assertThat(repository.findByTenantIdAndInsurerCode("tenant-2", "INS-B")).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndAdapterType() {
        repository.save(mapping1);
        repository.save(mapping2);
        repository.save(mapping3);

        List<InsurerMapping> tenant1Rest = repository.findByTenantIdAndAdapterType("tenant-1", "REST_API");
        assertThat(tenant1Rest).hasSize(1);
        assertThat(tenant1Rest.get(0).getInsurerCode()).isEqualTo("INS-A");

        List<InsurerMapping> tenant2Rest = repository.findByTenantIdAndAdapterType("tenant-2", "REST_API");
        assertThat(tenant2Rest).hasSize(1);
        assertThat(tenant2Rest.get(0).getTenantId()).isEqualTo("tenant-2");

        List<InsurerMapping> tenant1Soap = repository.findByTenantIdAndAdapterType("tenant-1", "SOAP_API");
        assertThat(tenant1Soap).hasSize(1);
        assertThat(tenant1Soap.get(0).getInsurerCode()).isEqualTo("INS-B");

        List<InsurerMapping> tenant2Soap = repository.findByTenantIdAndAdapterType("tenant-2", "SOAP_API");
        assertThat(tenant2Soap).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndEnabled() {
        mapping1.setEnabled(true);
        mapping2.setEnabled(false);
        repository.save(mapping1);
        repository.save(mapping2);
        repository.save(mapping3);

        List<InsurerMapping> tenant1Enabled = repository.findByTenantIdAndEnabled("tenant-1", true);
        assertThat(tenant1Enabled).hasSize(1);
        assertThat(tenant1Enabled.get(0).getInsurerCode()).isEqualTo("INS-A");

        List<InsurerMapping> tenant1Disabled = repository.findByTenantIdAndEnabled("tenant-1", false);
        assertThat(tenant1Disabled).hasSize(1);
        assertThat(tenant1Disabled.get(0).getInsurerCode()).isEqualTo("INS-B");

        List<InsurerMapping> tenant2Enabled = repository.findByTenantIdAndEnabled("tenant-2", true);
        assertThat(tenant2Enabled).hasSize(1);
        assertThat(tenant2Enabled.get(0).getTenantId()).isEqualTo("tenant-2");
    }

    @Test
    void testTenantIsolation_SearchByTenantId() {
        repository.save(mapping1);
        repository.save(mapping2);
        repository.save(mapping3);

        List<InsurerMapping> tenant1Results = repository.searchByTenantId("tenant-1", "INS");
        assertThat(tenant1Results).hasSize(2);
        assertThat(tenant1Results).allMatch(m -> m.getTenantId().equals("tenant-1"));

        List<InsurerMapping> tenant2Results = repository.searchByTenantId("tenant-2", "INS");
        assertThat(tenant2Results).hasSize(1);
        assertThat(tenant2Results.get(0).getTenantId()).isEqualTo("tenant-2");

        List<InsurerMapping> tenant1AResults = repository.searchByTenantId("tenant-1", "Insurer A");
        assertThat(tenant1AResults).hasSize(1);
        assertThat(tenant1AResults.get(0).getInsurerCode()).isEqualTo("INS-A");
    }

    @Test
    void testTenantIsolation_CountByTenantId() {
        repository.save(mapping1);
        repository.save(mapping2);
        repository.save(mapping3);

        assertThat(repository.countByTenantId("tenant-1")).isEqualTo(2);
        assertThat(repository.countByTenantId("tenant-2")).isEqualTo(1);
        assertThat(repository.countByTenantId("tenant-3")).isEqualTo(0);
    }

    @Test
    void testCannotAccessOtherTenantData() {
        repository.save(mapping1);
        repository.save(mapping3);

        InsurerMapping newMapping = new InsurerMapping();
        newMapping.setTenantId("tenant-3");
        newMapping.setInsurerCode("INS-C");
        newMapping.setInsurerName("Insurer C");
        newMapping.setAdapterType("REST_API");
        newMapping.setEnabled(true);
        repository.save(newMapping);

        assertThat(repository.findByTenantId("tenant-1")).hasSize(1);
        assertThat(repository.findByTenantId("tenant-2")).hasSize(1);
        assertThat(repository.findByTenantId("tenant-3")).hasSize(1);
    }

    @Test
    void testCrossTenantDuplicateCodesAllowed() {
        repository.save(mapping1);

        InsurerMapping mapping = repository.findByTenantIdAndInsurerCode("tenant-1", "INS-A");
        assertThat(mapping).isPresent();

        mapping = repository.findByTenantIdAndInsurerCode("tenant-2", "INS-A");
        assertThat(mapping).isEmpty();

        repository.save(mapping3);

        mapping = repository.findByTenantIdAndInsurerCode("tenant-2", "INS-A");
        assertThat(mapping).isPresent();
    }
}
