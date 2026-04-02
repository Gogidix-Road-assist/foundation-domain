package com.gogidix.rapidassist.database.management.service.adapters.persistence;

import com.gogidix.rapidassist.database.management.service.domain.model.BackupInfo;
import com.gogidix.rapidassist.database.management.service.domain.port.out.BackupRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of BackupRepository with tenant isolation.
 * For production, replace with MongoDB or JPA implementation.
 * All operations are tenant-scoped to ensure proper multi-tenancy isolation.
 */
@Repository
public class InMemoryBackupRepository implements BackupRepository {

    // Structure: Map<tenantId, Map<backupId, BackupInfo>>
    private final Map<String, Map<String, BackupInfo>> tenantBackups = new ConcurrentHashMap<>();

    // Structure: Map<tenantId, Map<connectionId, List<backupId>>>
    private final Map<String, Map<String, List<String>>> tenantConnectionBackups = new ConcurrentHashMap<>();

    @Override
    public BackupInfo save(BackupInfo backup) {
        if (backup.getId() == null) {
            backup.setId(UUID.randomUUID().toString());
        }

        String tenantId = backup.getTenantId();
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId is required");
        }

        // Get or create tenant-specific maps
        Map<String, BackupInfo> backups = tenantBackups.computeIfAbsent(
            tenantId, k -> new ConcurrentHashMap<>());
        Map<String, List<String>> connectionBackups = tenantConnectionBackups.computeIfAbsent(
            tenantId, k -> new ConcurrentHashMap<>());

        backups.put(backup.getId(), backup);

        // Update connection index
        connectionBackups.computeIfAbsent(backup.getConnectionId(), k -> new ArrayList<>())
            .add(backup.getId());

        return backup;
    }

    @Override
    public Optional<BackupInfo> findByIdAndTenantId(String id, String tenantId) {
        Map<String, BackupInfo> backups = tenantBackups.get(tenantId);
        if (backups == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(backups.get(id));
    }

    @Override
    public List<BackupInfo> findByTenantIdAndConnectionId(String tenantId, String connectionId) {
        Map<String, List<String>> connectionBackups = tenantConnectionBackups.get(tenantId);
        if (connectionBackups == null) {
            return new ArrayList<>();
        }

        List<String> backupIds = connectionBackups.getOrDefault(connectionId, Collections.emptyList());
        Map<String, BackupInfo> backups = tenantBackups.get(tenantId);
        if (backups == null) {
            return new ArrayList<>();
        }

        return backupIds.stream()
            .map(backups::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public List<BackupInfo> findRecentBackupsByTenantIdAndConnectionId(String tenantId, String connectionId, int limit) {
        return findByTenantIdAndConnectionId(tenantId, connectionId).stream()
            .sorted((a, b) -> {
                if (a.getStartTime() == null) return 1;
                if (b.getStartTime() == null) return -1;
                return b.getStartTime().compareTo(a.getStartTime());
            })
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<BackupInfo> findByTenantId(String tenantId) {
        Map<String, BackupInfo> backups = tenantBackups.get(tenantId);
        if (backups == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(backups.values());
    }

    @Override
    public List<BackupInfo> findByTenantIdAndStatus(String tenantId, BackupInfo.BackupStatus status) {
        Map<String, BackupInfo> backups = tenantBackups.get(tenantId);
        if (backups == null) {
            return new ArrayList<>();
        }
        return backups.values().stream()
            .filter(b -> b.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public List<BackupInfo> findByTenantIdAndType(String tenantId, BackupInfo.BackupType type) {
        Map<String, BackupInfo> backups = tenantBackups.get(tenantId);
        if (backups == null) {
            return new ArrayList<>();
        }
        return backups.values().stream()
            .filter(b -> b.getType() == type)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteByIdAndTenantId(String id, String tenantId) {
        Map<String, BackupInfo> backups = tenantBackups.get(tenantId);
        Map<String, List<String>> connectionBackups = tenantConnectionBackups.get(tenantId);

        if (backups != null) {
            BackupInfo backup = backups.remove(id);
            if (backup != null && connectionBackups != null) {
                List<String> ids = connectionBackups.get(backup.getConnectionId());
                if (ids != null) {
                    ids.remove(id);
                }
            }
        }
    }

    @Override
    public long countByTenantIdAndConnectionId(String tenantId, String connectionId) {
        Map<String, List<String>> connectionBackups = tenantConnectionBackups.get(tenantId);
        if (connectionBackups == null) {
            return 0;
        }
        List<String> backupIds = connectionBackups.get(connectionId);
        return backupIds == null ? 0 : backupIds.size();
    }

    @Override
    public long countByTenantId(String tenantId) {
        Map<String, BackupInfo> backups = tenantBackups.get(tenantId);
        return backups == null ? 0 : backups.size();
    }

    @Override
    public long countByTenantIdAndStatus(String tenantId, BackupInfo.BackupStatus status) {
        Map<String, BackupInfo> backups = tenantBackups.get(tenantId);
        if (backups == null) {
            return 0;
        }
        return backups.values().stream()
            .filter(b -> b.getStatus() == status)
            .count();
    }

    /**
     * Clear all data for testing purposes.
     */
    public void clear() {
        tenantBackups.clear();
        tenantConnectionBackups.clear();
    }

    /**
     * Clear all data for a specific tenant.
     */
    public void clearByTenantId(String tenantId) {
        tenantBackups.remove(tenantId);
        tenantConnectionBackups.remove(tenantId);
    }
}
