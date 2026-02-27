package com.gogidix.rapidassist.insurer.adapter.service.domain.port.out;

import com.gogidix.rapidassist.insurer.adapter.service.domain.model.InsurerMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InsurerMappingRepository extends JpaRepository<InsurerMapping, String> {

    @Query("SELECT i FROM InsurerMapping i WHERE i.tenantId = :tenantId")
    List<InsurerMapping> findByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT i FROM InsurerMapping i WHERE i.tenantId = :tenantId AND i.id = :id")
    Optional<InsurerMapping> findByTenantIdAndId(@Param("tenantId") String tenantId, @Param("id") String id);

    @Query("SELECT i FROM InsurerMapping i WHERE i.tenantId = :tenantId AND i.insurerCode = :code")
    Optional<InsurerMapping> findByTenantIdAndInsurerCode(@Param("tenantId") String tenantId, @Param("code") String code);

    @Query("SELECT i FROM InsurerMapping i WHERE i.tenantId = :tenantId AND i.adapterType = :adapterType")
    List<InsurerMapping> findByTenantIdAndAdapterType(@Param("tenantId") String tenantId, @Param("adapterType") String adapterType);

    @Query("SELECT i FROM InsurerMapping i WHERE i.tenantId = :tenantId AND i.enabled = :enabled")
    List<InsurerMapping> findByTenantIdAndEnabled(@Param("tenantId") String tenantId, @Param("enabled") Boolean enabled);

    @Query("SELECT i FROM InsurerMapping i WHERE i.tenantId = :tenantId AND i.insurerCode LIKE %:search% OR i.insurerName LIKE %:search%")
    List<InsurerMapping> searchByTenantId(@Param("tenantId") String tenantId, @Param("search") String search);

    @Query("SELECT COUNT(i) FROM InsurerMapping i WHERE i.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT i FROM InsurerMapping i WHERE i.tenantId = :tenantId AND i.insurerCode = :code AND i.id != :id")
    Optional<InsurerMapping> findByTenantIdAndInsurerCodeExcludingId(@Param("tenantId") String tenantId, @Param("code") String code, @Param("id") String id);

    @Override
    default List<InsurerMapping> findAll() {
        throw new UnsupportedOperationException("Use findByTenantId(String tenantId) - tenant filtering required");
    }

    @Override
    default Optional<InsurerMapping> findById(String id) {
        throw new UnsupportedOperationException("Use findByTenantIdAndId(String tenantId, String id) - tenant filtering required");
    }
}
