package com.ibs_notification_service.notification_service.service.impl;

import com.ibs_notification_service.notification_service.request.BillingLineNotificationPayload;
import com.ibs_notification_service.notification_service.request.InvoiceNotificationPayload;
import com.ibs_notification_service.notification_service.service.NotificationStrategy;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component("v2")
@RequiredArgsConstructor
@Slf4j
public class PdfNotificationStrategy implements NotificationStrategy {

    private final JavaMailSender mailSender;

    @Override
    public void sendMail(InvoiceNotificationPayload payload) {
        try {
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            generateInvoicePdf(payload, pdfOutputStream);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(payload.getBuyerEmail());
            helper.setSubject("Invoice from Supplier: " + payload.getSupplierEmail());
            helper.setText("Dear Customer,\n\nPlease find attached your invoice from " +
                    payload.getSupplierEmail() + ".\n\nThank you for your business!", false);

            InputStreamSource attachment = new ByteArrayResource(pdfOutputStream.toByteArray());
            helper.addAttachment("invoice_" + payload.getBillingId() + ".pdf", attachment);

            mailSender.send(message);
            log.info("Invoice PDF email sent to {}", payload.getBuyerEmail());

        } catch (Exception e) {
            log.error("Error sending PDF invoice email", e);
        }
    }

    private void generateInvoicePdf(InvoiceNotificationPayload payload, ByteArrayOutputStream outputStream) throws Exception {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Invoice")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
        );

        document.add(new Paragraph("Invoice ID: " + payload.getBillingId()));
        document.add(new Paragraph("Buyer Email: " + payload.getBuyerEmail()));
        document.add(new Paragraph("Supplier Email: " + payload.getSupplierEmail()));
        document.add(new Paragraph("Currency: " + payload.getCurrencyCode()));
        document.add(new Paragraph("Total Amount: " + payload.getTotalAmount()));
        document.add(new Paragraph("Tax: " + payload.getTax()));
        document.add(new Paragraph("Invoice Date: " + payload.getInvoiceDate()));
        document.add(new Paragraph("Due Date: " + payload.getPaymentDueDate()));
        document.add(new Paragraph("\n"));

        Table table = new Table(4);
        table.addHeaderCell("Item");
        table.addHeaderCell("Quantity");
        table.addHeaderCell("Unit Price");
        table.addHeaderCell("Total");

        for (BillingLineNotificationPayload line : payload.getBillingLines()) {
            table.addCell(line.getItemDescription());
            table.addCell(line.getQuantity().toString());
            table.addCell(line.getUnitPrice().toString());
            table.addCell(line.getTotalAmount().toString());
        }

        document.add(table);
        document.close();
    }
}
