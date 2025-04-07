package com.ibs_notification_service.notification_service.controller;

import com.ibs_notification_service.notification_service.request.InvoiceNotificationPayload;
import com.ibs_notification_service.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> notifyInvoice(
            @RequestBody InvoiceNotificationPayload payload,
            @RequestHeader(name = "X-Mail-Version", defaultValue = "v1") String version
    ) {
        notificationService.sendMail(version, payload);
        return ResponseEntity.ok("Notification processed using version: " + version);
    }
}
