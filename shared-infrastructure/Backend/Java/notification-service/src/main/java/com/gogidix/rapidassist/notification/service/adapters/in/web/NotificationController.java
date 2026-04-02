package com.gogidix.rapidassist.notification.service.adapters.in.web;

import com.gogidix.rapidassist.notification.service.application.service.ComprehensiveNotificationService;
import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import com.gogidix.rapidassist.notification.service.domain.port.in.NotificationCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "APIs for sending and managing notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final ComprehensiveNotificationService notificationService;

    public NotificationController(ComprehensiveNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns the health status of the notification service")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "notification-service"
        ));
    }

    @PostMapping("/send/immediate")
    @Operation(summary = "Send immediate notification", description = "Sends a notification immediately to the specified recipients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    public ResponseEntity<Notification> sendImmediate(@RequestBody SendImmediateRequest request) {
        Notification notification = notificationService.sendImmediate(
            request.tenantId(),
            request.type(),
            request.recipients(),
            request.subject(),
            request.content(),
            request.channels()
        );
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/send/from-template")
    public ResponseEntity<Notification> sendFromTemplate(@RequestBody SendFromTemplateRequest request) {
        Notification notification = notificationService.sendFromTemplate(
            request.tenantId(),
            request.templateId(),
            request.recipients(),
            request.templateData(),
            request.channels()
        );
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/schedule")
    public ResponseEntity<Notification> scheduleNotification(@RequestBody ScheduleNotificationRequest request) {
        Notification notification = notificationService.scheduleNotification(
            request.tenantId(),
            request.type(),
            request.recipients(),
            request.subject(),
            request.content(),
            request.scheduledAt(),
            request.channels()
        );
        return ResponseEntity.created(URI.create("/api/notifications/" + notification.notificationId())).body(notification);
    }

    @PostMapping("/{notificationId}/cancel")
    public ResponseEntity<Map<String, Boolean>> cancelNotification(
        @PathVariable String notificationId,
        @RequestParam String tenantId) {

        boolean canceled = notificationService.cancelNotification(tenantId, notificationId);
        return ResponseEntity.ok(Map.of("canceled", canceled));
    }

    @PostMapping("/{notificationId}/resend")
    public ResponseEntity<Notification> resendNotification(
        @PathVariable String notificationId,
        @RequestParam String tenantId) {

        Notification notification = notificationService.resendNotification(tenantId, notificationId);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Notification>> getPendingNotifications(@RequestParam String tenantId) {
        List<Notification> notifications = notificationService.getPendingNotifications(tenantId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/scheduled")
    public ResponseEntity<List<Notification>> getScheduledNotifications(@RequestParam String tenantId) {
        List<Notification> notifications = notificationService.getScheduledNotifications(tenantId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<Notification.NotificationStatus, Long>> getStatistics(
        @RequestParam String tenantId,
        @RequestParam long fromEpoch,
        @RequestParam long toEpoch) {

        Instant from = Instant.ofEpochMilli(fromEpoch);
        Instant to = Instant.ofEpochMilli(toEpoch);

        Map<Notification.NotificationStatus, Long> stats = notificationService.getNotificationStatistics(tenantId, from, to);
        return ResponseEntity.ok(stats);
    }

    // Request records
    record SendImmediateRequest(
        String tenantId,
        Notification.NotificationType type,
        List<String> recipients,
        String subject,
        String content,
        List<Notification.NotificationChannel> channels
    ) {}

    record SendFromTemplateRequest(
        String tenantId,
        String templateId,
        List<String> recipients,
        Map<String, Object> templateData,
        List<Notification.NotificationChannel> channels
    ) {}

    record ScheduleNotificationRequest(
        String tenantId,
        Notification.NotificationType type,
        List<String> recipients,
        String subject,
        String content,
        Instant scheduledAt,
        List<Notification.NotificationChannel> channels
    ) {}
}
