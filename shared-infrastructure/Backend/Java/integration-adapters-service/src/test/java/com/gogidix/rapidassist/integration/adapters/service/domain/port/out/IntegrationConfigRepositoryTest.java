package com.gogidix.rapidassist.integration.adapters.service.domain.port.out;

import com.gogidix.rapidassist.integration.adapters.service.domain.model.IntegrationConfig;
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
class IntegrationConfigRepositoryTest {

    @Autowired
    private IntegrationConfigRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private IntegrationConfig config1;
    private IntegrationConfig config2;
    private IntegrationConfig config3;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        config1 = new IntegrationConfig();
        config1.setTenantId("tenant-1");
        config1.setProvider("stripe");
        config1.setProviderName("Stripe Payments");
        config1.setApiEndpoint("https://api.stripe.com");
        config1.setEnabled(true);
        config1.setSyncStatus("ACTIVE");

        config2 = new IntegrationConfig();
        config2.setTenantId("tenant-1");
        config2.setProvider("paypal");
        config2.setProviderName("PayPal");
        config2.setApiEndpoint("https://api.paypal.com");
        config2.setEnabled(true);
        config2.setSyncStatus("ACTIVE");

        config3 = new IntegrationConfig();
        config3.setTenantId("tenant-2");
        config3.setProvider("stripe");
        config3.setProviderName("Stripe Payments");
        config3.setApiEndpoint("https://api.stripe.com");
        config3.setEnabled(true);
        config3.setSyncStatus("ACTIVE");
    }

    @Test
    void testTenantIsolation_FindByTenantId() {
        repository.save(config1);
        repository.save(config2);
        repository.save(config3);

        List<IntegrationConfig> tenant1Results = repository.findByTenantId("tenant-1");
        assertThat(tenant1Results).hasSize(2);
        assertThat(tenant1Results).allMatch(c -> c.getTenantId().equals("tenant-1"));

        List<IntegrationConfig> tenant2Results = repository.findByTenantId("tenant-2");
        assertThat(tenant2Results).hasSize(1);
        assertThat(tenant2Results.get(0).getTenantId()).isEqualTo("tenant-2");
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndId() {
        repository.save(config1);
        repository.save(config3);

        String config1Id = config1.getId();
        String config3Id = config3.getId();

        assertThat(repository.findByTenantIdAndId("tenant-1", config1Id)).isPresent();
        assertThat(repository.findByTenantIdAndId("tenant-1", config3Id)).isEmpty();

        assertThat(repository.findByTenantIdAndId("tenant-2", config3Id)).isPresent();
        assertThat(repository.findByTenantIdAndId("tenant-2", config1Id)).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndProvider() {
        repository.save(config1);
        repository.save(config2);
        repository.save(config3);

        assertThat(repository.findByTenantIdAndProvider("tenant-1", "stripe")).isPresent();
        assertThat(repository.findByTenantIdAndProvider("tenant-2", "stripe")).isPresent();

        assertThat(repository.findByTenantIdAndProvider("tenant-1", "paypal")).isPresent();
        assertThat(repository.findByTenantIdAndProvider("tenant-2", "paypal")).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndEnabled() {
        config1.setEnabled(true);
        config2.setEnabled(false);
        repository.save(config1);
        repository.save(config2);
        repository.save(config3);

        List<IntegrationConfig> tenant1Enabled = repository.findByTenantIdAndEnabled("tenant-1", true);
        assertThat(tenant1Enabled).hasSize(1);
        assertThat(tenant1Enabled.get(0).getProvider()).isEqualTo("stripe");

        List<IntegrationConfig> tenant1Disabled = repository.findByTenantIdAndEnabled("tenant-1", false);
        assertThat(tenant1Disabled).hasSize(1);
        assertThat(tenant1Disabled.get(0).getProvider()).isEqualTo("paypal");

        List<IntegrationConfig> tenant2Enabled = repository.findByTenantIdAndEnabled("tenant-2", true);
        assertThat(tenant2Enabled).hasSize(1);
        assertThat(tenant2Enabled.get(0).getTenantId()).isEqualTo("tenant-2");
    }

    @Test
    void testTenantIsolation_SearchByTenantId() {
        repository.save(config1);
        repository.save(config2);
        repository.save(config3);

        List<IntegrationConfig> tenant1Results = repository.searchByTenantId("tenant-1", "Stripe");
        assertThat(tenant1Results).hasSize(1);
        assertThat(tenant1Results.get(0).getTenantId()).isEqualTo("tenant-1");

        List<IntegrationConfig> tenant2Results = repository.searchByTenantId("tenant-2", "Stripe");
        assertThat(tenant2Results).hasSize(1);
        assertThat(tenant2Results.get(0).getTenantId()).isEqualTo("tenant-2");

        List<IntegrationConfig> tenant1Paypal = repository.searchByTenantId("tenant-1", "PayPal");
        assertThat(tenant1Paypal).hasSize(1);
        assertThat(tenant1Paypal.get(0).getProvider()).isEqualTo("paypal");

        List<IntegrationConfig> tenant2Paypal = repository.searchByTenantId("tenant-2", "PayPal");
        assertThat(tenant2Paypal).isEmpty();
    }

    @Test
    void testTenantIsolation_CountByTenantId() {
        repository.save(config1);
        repository.save(config2);
        repository.save(config3);

        assertThat(repository.countByTenantId("tenant-1")).isEqualTo(2);
        assertThat(repository.countByTenantId("tenant-2")).isEqualTo(1);
        assertThat(repository.countByTenantId("tenant-3")).isEqualTo(0);
    }

    @Test
    void testCannotAccessOtherTenantData() {
        repository.save(config1);
        repository.save(config3);

        IntegrationConfig newConfig = new IntegrationConfig();
        newConfig.setTenantId("tenant-3");
        newConfig.setProvider("square");
        newConfig.setProviderName("Square");
        newConfig.setApiEndpoint("https://api.square.com");
        newConfig.setEnabled(true);
        newConfig.setSyncStatus("ACTIVE");
        repository.save(newConfig);

        assertThat(repository.findByTenantId("tenant-1")).hasSize(1);
        assertThat(repository.findByTenantId("tenant-2")).hasSize(1);
        assertThat(repository.findByTenantId("tenant-3")).hasSize(1);
    }

    @Test
    void testCrossTenantDuplicateProvidersAllowed() {
        repository.save(config1);

        IntegrationConfig config = repository.findByTenantIdAndProvider("tenant-1", "stripe");
        assertThat(config).isPresent();

        config = repository.findByTenantIdAndProvider("tenant-2", "stripe");
        assertThat(config).isEmpty();

        repository.save(config3);

        config = repository.findByTenantIdAndProvider("tenant-2", "stripe");
        assertThat(config).isPresent();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndSyncStatus() {
        config1.setSyncStatus("ACTIVE");
        config2.setSyncStatus("PENDING");
        config3.setSyncStatus("ACTIVE");
        repository.save(config1);
        repository.save(config2);
        repository.save(config3);

        List<IntegrationConfig> tenant1Active = repository.findByTenantIdAndSyncStatus("tenant-1", "ACTIVE");
        assertThat(tenant1Active).hasSize(1);
        assertThat(tenant1Active.get(0).getProvider()).isEqualTo("stripe");

        List<IntegrationConfig> tenant1Pending = repository.findByTenantIdAndSyncStatus("tenant-1", "PENDING");
        assertThat(tenant1Pending).hasSize(1);
        assertThat(tenant1Pending.get(0).getProvider()).isEqualTo("paypal");

        List<IntegrationConfig> tenant2Active = repository.findByTenantIdAndSyncStatus("tenant-2", "ACTIVE");
        assertThat(tenant2Active).hasSize(1);
        assertThat(tenant2Active.get(0).getTenantId()).isEqualTo("tenant-2");
    }

    @Test
    void testTenantIsolation_FindEnabledByTenantIdOrderByLastSync() {
        config1.setEnabled(true);
        config2.setEnabled(false);
        config3.setEnabled(true);
        repository.save(config1);
        repository.save(config2);
        repository.save(config3);

        List<IntegrationConfig> tenant1Enabled = repository.findEnabledByTenantIdOrderByLastSync("tenant-1");
        assertThat(tenant1Enabled).hasSize(1);
        assertThat(tenant1Enabled.get(0).getProvider()).isEqualTo("stripe");

        List<IntegrationConfig> tenant2Enabled = repository.findEnabledByTenantIdOrderByLastSync("tenant-2");
        assertThat(tenant2Enabled).hasSize(1);
        assertThat(tenant2Enabled.get(0).getTenantId()).isEqualTo("tenant-2");
    }
}
