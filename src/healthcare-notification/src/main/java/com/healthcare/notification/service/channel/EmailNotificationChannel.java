package com.healthcare.notification.service.channel;

import com.healthcare.notification.domain.Notification;
import com.healthcare.notification.domain.NotificationRecipient;
import com.healthcare.notification.domain.NotificationType;
import com.healthcare.notification.exception.NotificationDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailNotificationChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationChannel.class);

    private final JavaMailSender mailSender;

    public EmailNotificationChannel(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }

    @Override
    public void send(Notification notification) throws NotificationDeliveryException {
        NotificationRecipient recipient = notification.getRecipient();

        if (recipient == null || !recipient.hasEmail()) {
            throw NotificationDeliveryException.invalidRecipient();
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient.email());
            helper.setSubject(notification.getTitle());
            helper.setText(notification.getMessage(), false);

            log.info("Sending email notification: id={}, category={}",
                notification.getId(), notification.getCategory());

            mailSender.send(message);

            log.info("Email notification sent successfully: id={}", notification.getId());

        } catch (MessagingException e) {
            log.error("Failed to create email message: notificationId={}", notification.getId(), e);
            throw NotificationDeliveryException.emailFailed("Failed to create message: " + e.getMessage());
        } catch (MailException e) {
            log.error("Failed to send email: notificationId={}", notification.getId(), e);
            throw NotificationDeliveryException.emailFailed("Mail server error: " + e.getMessage());
        }
    }

    @Override
    public boolean isAvailable() {
        return mailSender != null;
    }
}
