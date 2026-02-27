package com.gogidix.rapidassist.notification.service.domain.port.out;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;

import java.util.concurrent.CompletableFuture;

public interface NotificationProvider {

    boolean supportsChannel(Notification.NotificationChannel channel);

    CompletableFuture<DeliveryResponse> send(Notification notification, String recipient);

    CompletableFuture<DeliveryStatus> getStatus(String messageId);

    CompletableFuture<Boolean> cancel(String messageId);

    interface DeliveryResponse {
        String getMessageId();
        DeliveryStatus getStatus();
        String getErrorCode();
        String getErrorMessage();
        Object getProviderResponse();
    }

    enum DeliveryStatus {
        SENT,
        DELIVERED,
        FAILED,
        PENDING
    }
}