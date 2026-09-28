package com.trekconnect.auth.service;

import com.trekconnect.auth.entity.Role;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class WelcomeEmailService {

    private static final Logger log = LoggerFactory.getLogger(WelcomeEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@trekconnect.com}")
    private String fromEmail;

    @Autowired
    public WelcomeEmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendWelcomeEmail(String name, String toEmail, Role role, String password) {
        log.info("Preparing welcome email for new {} account: {}", role, toEmail);

        String subject = "Welcome to TrekConnect — Your Admin Account Has Been Created";
        String htmlContent = buildEmailTemplate(name, toEmail, role, password);

        if (mailSender == null) {
            log.warn("JavaMailSender bean is not present. Simulated email content:\nTo: {}\nSubject: {}\nPassword: {}", toEmail, subject, password);
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent welcome email to: {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("Failed to send welcome email to [{}]: {}. Simulated email was generated for user.", toEmail, e.getMessage());
            return false;
        }
    }

    private String buildEmailTemplate(String name, String email, Role role, String password) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; }
                    .card { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #10b981, #059669); padding: 30px; text-align: center; color: white; }
                    .header h1 { margin: 0; font-size: 24px; font-weight: 700; }
                    .body { padding: 30px; color: #334155; line-height: 1.6; }
                    .credential-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; margin: 20px 0; }
                    .label { font-size: 12px; text-transform: uppercase; color: #64748b; font-weight: 600; }
                    .val { font-size: 16px; font-weight: 600; color: #0f172a; margin-bottom: 12px; }
                    .pwd { font-family: monospace; font-size: 18px; color: #059669; background: #ecfdf5; padding: 6px 12px; border-radius: 6px; display: inline-block; }
                    .footer { text-align: center; padding: 20px; color: #94a3b8; font-size: 12px; border-top: 1px solid #f1f5f9; }
                    .notice { background: #fffbe6; border-left: 4px solid #f59e0b; padding: 12px; border-radius: 4px; font-size: 13px; color: #78350f; margin-top: 20px; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="header">
                      <h1>⛰️ Welcome to TrekConnect</h1>
                    </div>
                    <div class="body">
                      <p>Hello <strong>%s</strong>,</p>
                      <p>An administrator has created a new <strong>%s</strong> account for you on the TrekConnect Platform.</p>
                      
                      <div class="credential-box">
                        <div class="label">Registered Email</div>
                        <div class="val">%s</div>
                        
                        <div class="label">Assigned Role</div>
                        <div class="val">%s</div>

                        <div class="label">Temporary Password</div>
                        <div class="pwd">%s</div>
                      </div>

                      <div class="notice">
                        🔒 <strong>Security Recommendation:</strong> Please log in to your account and change your temporary password immediately from your profile settings.
                      </div>
                    </div>
                    <div class="footer">
                      &copy; 2026 TrekConnect Platform. All rights reserved.
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(name, role.name(), email, role.name(), password);
    }
}
