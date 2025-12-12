package com.example.ProjectManagementBackend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Send Email verification
    public void sendVerificationEmail(String recipientEmail, String verificationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipientEmail);
            helper.setSubject("Complete Your Registration");
            helper.setFrom("no-reply@myapp.com");

            String content = "<p>Dear User,</p>"
                    + "<p>Thank you for registering. Please click the link below to verify your email address:</p>"
                    + "<p><a href=\"" + verificationUrl + "\">VERIFY EMAIL</a></p>"
                    + "<br><p>Regards,<br>MyApp Team</p>";

            helper.setText(content, true);
            mailSender.send(message);

            System.out.println("✅ Verification email sent to " + recipientEmail);

        } catch (MessagingException | MailSendException e) {
            System.err.println("❌ Failed to send email to " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Send Password reset link
    public void sendPasswordResetEmail(String recipientEmail, String passwordResetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipientEmail);
            helper.setSubject("Click Link to reset Password");
            helper.setFrom("no-reply@myapp.com");

            String content = "<p>Dear User,</p>"
                    + "<p>Please click the link below to reset your password:</p>"
                    + "<p><a href=\"" + passwordResetUrl + "\">Reset Password</a></p>"
                    + "<br><p>Regards,<br>MyApp Team</p>";

            helper.setText(content, true);
            mailSender.send(message);

            System.out.println("✅ Password reset email sent to " + recipientEmail);

        } catch (MessagingException | MailSendException e) {
            System.err.println("❌ Failed to send email to " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
