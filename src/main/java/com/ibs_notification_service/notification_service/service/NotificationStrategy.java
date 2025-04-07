package com.ibs_notification_service.notification_service.service;

import com.ibs_notification_service.notification_service.request.InvoiceNotificationPayload;

public interface NotificationStrategy {
    void sendMail(InvoiceNotificationPayload payload);
}
