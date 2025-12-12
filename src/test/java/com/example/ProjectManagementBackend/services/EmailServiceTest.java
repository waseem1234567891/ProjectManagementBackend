package com.example.ProjectManagementBackend.services;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = Mockito.mock(JavaMailSender.class);// mock the mail sender
        emailService = new EmailService(mailSender);
    }

    @Test
    void testSendVerificationEmail_Success() throws Exception {
        String recipient = "test@example.com";
        String verificationUrl = "http://localhost:8585/auth/verify?token=123";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendVerificationEmail(recipient, verificationUrl);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendVerificationEmail_ExceptionHandled() throws Exception {
        String recipient = "fail@example.com";
        String verificationUrl = "http://localhost:8585/auth/verify?token=fail";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Simulate MailSendException
        doThrow(new MailSendException("Mail server down"))
                .when(mailSender).send(any(MimeMessage.class));

        // Should not throw because service catches exception
        assertDoesNotThrow(() -> emailService.sendVerificationEmail(recipient, verificationUrl));

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendPasswordResetEmail_Success() throws Exception {
        String recipient = "user@example.com";
        String resetUrl = "http://localhost:3000/reset-password?token=abc";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPasswordResetEmail(recipient, resetUrl);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendPasswordResetEmail_ExceptionHandled() throws Exception {
        String recipient = "fail@example.com";
        String resetUrl = "http://localhost:3000/reset-password?token=fail";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailSendException("Mail server down"))
                .when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(recipient, resetUrl));

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
