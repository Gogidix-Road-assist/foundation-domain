package com.gogidix.rapidassist.orchestration.monitoringservice.domain.repository;

import com.gogidix.rapidassist.orchestration.monitoringservice.domain.port.out.EntityRepositoryPort;

/**
 * Domain repository interface for Entity
 * Follows Hexagonal Architecture - this is a domain-specific interface
 */
public interface EntityRepository extends EntityRepositoryPort {
    // All methods inherited from EntityRepositoryPort
}
