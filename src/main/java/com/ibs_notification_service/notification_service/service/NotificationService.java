package com.ibs_notification_service.notification_service.service;

import com.ibs_notification_service.notification_service.request.InvoiceNotificationPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ApplicationContext context;

    public void sendMail(String version, InvoiceNotificationPayload payload) {
        NotificationStrategy strategy = context.getBean(version.toLowerCase(), NotificationStrategy.class);
        strategy.sendMail(payload);
    }
}
