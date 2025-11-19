package com.kindred.emkcrm_project_backend.authentication;


import com.kindred.emkcrm_project_backend.config.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    public EmailService(
            JavaMailSender mailSender,
            EmailProperties emailProperties) {
        this.mailSender = mailSender;
        this.emailProperties = emailProperties;
    }

    public void sendActivationEmail(String to, String activationLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(emailProperties.host_email());
        helper.setTo(to);
        helper.setSubject("Account Activation");
        helper.setText(String.format("To activate your account, please click the following link: %s", activationLink), true);
        mailSender.send(message);
    }
}
