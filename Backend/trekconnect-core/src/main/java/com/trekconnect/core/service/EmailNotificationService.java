package com.trekconnect.core.service;

import com.trekconnect.core.entity.EmailLog;
import com.trekconnect.core.repository.EmailLogRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service managing transactional email notifications for booking, payment, and organizer status updates.
 * Logs all dispatch events to the email_log table with defensive fallback if SMTP is offline.
 */
@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private final EmailLogRepository emailLogRepository;

    @Value("${spring.mail.username:noreply@trekconnect.com}")
    private String fromEmail;

    @Autowired
    public EmailNotificationService(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    /**
     * Sends booking confirmation email to trekker.
     */
    @Async
    public void sendBookingConfirmation(String recipientEmail, String userName, String trekName, int seats, BigDecimal amount, String bookingId) {
        String subject = "Booking Confirmed - " + trekName + " | TrekConnect";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;">
                <h2 style="color: #0f766e;">Booking Confirmed!</h2>
                <p>Hello <b>%s</b>,</p>
                <p>Your expedition booking for <b>%s</b> has been successfully confirmed.</p>
                <div style="background-color: #f8fafc; padding: 15px; border-radius: 6px; margin: 15px 0;">
                    <p style="margin: 5px 0;"><b>Booking ID:</b> %s</p>
                    <p style="margin: 5px 0;"><b>Seats Reserved:</b> %d</p>
                    <p style="margin: 5px 0;"><b>Total Paid:</b> ₹%s</p>
                    <p style="margin: 5px 0;"><b>Status:</b> <span style="color: #10b981; font-weight: bold;">CONFIRMED</span></p>
                </div>
                <p>You can view and download your official booking receipt directly from your dashboard.</p>
                <p style="color: #64748b; font-size: 12px; margin-top: 30px;">TrekConnect Platform &bull; Happy Trekking!</p>
            </div>
            """.formatted(userName != null ? userName : "Adventurer", trekName, bookingId, seats, amount, bookingId);

        sendEmail(recipientEmail, subject, htmlContent, "BOOKING_CONFIRMATION");
    }

    /**
     * Sends booking cancellation notice to trekker.
     */
    @Async
    public void sendBookingCancellation(String recipientEmail, String userName, String trekName, String bookingId) {
        String subject = "Booking Cancelled - " + trekName + " | TrekConnect";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;">
                <h2 style="color: #dc2626;">Booking Cancelled</h2>
                <p>Hello <b>%s</b>,</p>
                <p>Your booking for <b>%s</b> (ID: <code>%s</code>) has been cancelled as requested.</p>
                <p>If applicable, any refund will be processed in accordance with the expedition refund policy.</p>
                <p style="color: #64748b; font-size: 12px; margin-top: 30px;">TrekConnect Platform</p>
            </div>
            """.formatted(userName != null ? userName : "Adventurer", trekName, bookingId);

        sendEmail(recipientEmail, subject, htmlContent, "REFUND_UPDATE");
    }

    /**
     * Sends payment receipt notification.
     */
    @Async
    public void sendPaymentReceipt(String recipientEmail, String userName, String razorpayOrderId, BigDecimal amount) {
        String subject = "Payment Receipt - ₹" + amount + " | TrekConnect";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;">
                <h2 style="color: #0f766e;">Payment Successful</h2>
                <p>Hello <b>%s</b>,</p>
                <p>We received your payment of <b>₹%s</b> for Order ID <code>%s</code>.</p>
                <p style="color: #64748b; font-size: 12px; margin-top: 30px;">TrekConnect Payments</p>
            </div>
            """.formatted(userName != null ? userName : "Adventurer", amount, razorpayOrderId);

        sendEmail(recipientEmail, subject, htmlContent, "PAYMENT_RECEIPT");
    }

    /**
     * Sends organizer verification approval notification.
     */
    @Async
    public void sendOrganizerApproval(String recipientEmail, String userName) {
        String subject = "Organizer Application Approved! | TrekConnect";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;">
                <h2 style="color: #059669;">Congratulations, Organizer!</h2>
                <p>Hello <b>%s</b>,</p>
                <p>Your organizer application has been reviewed and <b>approved</b> by the TrekConnect administration.</p>
                <p>You can now access your Organizer Dashboard to create trek templates, schedule expedition batches, and manage bookings.</p>
                <p style="color: #64748b; font-size: 12px; margin-top: 30px;">TrekConnect Administration</p>
            </div>
            """.formatted(userName != null ? userName : "Organizer");

        sendEmail(recipientEmail, subject, htmlContent, "EVENT_APPROVED");
    }

    /**
     * Core email dispatch method with database audit logging and graceful degradation.
     */
    private void sendEmail(String recipientEmail, String subject, String htmlBody, String emailType) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            logger.warn("Cannot send email: recipient email is null or blank");
            return;
        }

        EmailLog emailLog = EmailLog.builder()
                .recipientEmail(recipientEmail)
                .type(emailType)
                .status("QUEUED")
                .createdAt(LocalDateTime.now())
                .build();

        try {
            if (mailSender != null) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(fromEmail);
                helper.setTo(recipientEmail);
                helper.setSubject(subject);
                helper.setText(htmlBody, true);

                mailSender.send(message);
                emailLog.setStatus("SENT");
                emailLog.setSentAt(LocalDateTime.now());
                logger.info("Successfully sent {} email to: {}", emailType, recipientEmail);
            } else {
                logger.info("JavaMailSender not configured. Simulated {} email dispatch to: {}", emailType, recipientEmail);
                emailLog.setStatus("SENT (SIMULATED)");
                emailLog.setSentAt(LocalDateTime.now());
            }
        } catch (Exception ex) {
            logger.warn("Failed to dispatch email to {} (Type: {}). Error: {}", recipientEmail, emailType, ex.getMessage());
            emailLog.setStatus("FAILED");
        } finally {
            try {
                emailLogRepository.save(emailLog);
            } catch (Exception dbEx) {
                logger.error("Failed to persist EmailLog: {}", dbEx.getMessage());
            }
        }
    }
}
