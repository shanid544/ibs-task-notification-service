package com.ibs_notification_service.notification_service.service.impl;

import com.ibs_notification_service.notification_service.request.BillingLineNotificationPayload;
import com.ibs_notification_service.notification_service.request.InvoiceNotificationPayload;
import com.ibs_notification_service.notification_service.service.NotificationStrategy;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component("v1")
@RequiredArgsConstructor
@Slf4j
public class PlainTextNotificationStrategy implements NotificationStrategy {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendMail(InvoiceNotificationPayload payload) {
        log.info("Preparing to send HTML email to buyer: {}", payload.getBuyerEmail());

        Context context = new Context();
        context.setVariable("supplierEmail", payload.getSupplierEmail());
        context.setVariable("billingId", payload.getBillingId());
        context.setVariable("currencyCode", payload.getCurrencyCode());
        context.setVariable("totalAmount", payload.getTotalAmount());
        context.setVariable("tax", payload.getTax());
        context.setVariable("invoiceDate", payload.getInvoiceDate());
        context.setVariable("paymentDueDate", payload.getPaymentDueDate());
        context.setVariable("billingLines", payload.getBillingLines());

        String htmlContent = templateEngine.process("invoice-email", context);

        sendHtmlEmail("hafeefak2001@gmail.com", "Your Invoice: " + payload.getBillingId(), htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("HTML email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email", e);
        }
    }
}
