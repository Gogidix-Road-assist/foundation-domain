package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.DispatchTracking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DispatchTrackingRepository extends MongoRepository<DispatchTracking, String> {

    List<DispatchTracking> findByDispatchIdOrderByTimestampDesc(String dispatchId);

    List<DispatchTracking> findByDispatchIdAndEventType(
        String dispatchId,
        DispatchTracking.TrackingEventType eventType
    );

    List<DispatchTracking> findByPerformedByAndTimestampBetween(
        String performedBy,
        LocalDateTime start,
        LocalDateTime end
    );

    List<DispatchTracking> findByEventTypeAndTimestampBetween(
        DispatchTracking.TrackingEventType eventType,
        LocalDateTime start,
        LocalDateTime end
    );

    List<DispatchTracking> findTop100ByTimestampBeforeOrderByTimestampDesc(LocalDateTime cutoffTime);

    void deleteByTimestampBefore(LocalDateTime cutoffTime);
}
