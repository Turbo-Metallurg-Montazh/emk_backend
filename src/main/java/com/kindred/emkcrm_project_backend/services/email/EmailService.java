package com.kindred.emkcrm_project_backend.services.email;


import com.kindred.emkcrm_project_backend.config.EmailProperties;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final String REGISTRATION_EMAIL_TEMPLATE_PATH = "email-templates/registration-email.html";
    
    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;
    private String registrationEmailTemplate;

    public EmailService(
            JavaMailSender mailSender,
            EmailProperties emailProperties) {
        this.mailSender = mailSender;
        this.emailProperties = emailProperties;
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(REGISTRATION_EMAIL_TEMPLATE_PATH);
            if (!resource.exists()) {
                throw new IllegalStateException(
                        "Email template not found: " + REGISTRATION_EMAIL_TEMPLATE_PATH
                );
            }
            registrationEmailTemplate = StreamUtils.copyToString(
                    resource.getInputStream(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load email template: " + REGISTRATION_EMAIL_TEMPLATE_PATH,
                    e
            );
        }
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

    public void sendRegistrationEmail(String to, String username, String password, String loginUrl) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(emailProperties.host_email());
        helper.setTo(to);
        helper.setSubject("Регистрация в системе EMK-CRM");

        String htmlContent = buildRegistrationEmailHtml(username, password, loginUrl);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    private String buildRegistrationEmailHtml(String username, String password, String loginUrl) {
        return registrationEmailTemplate
                .replace("{{USERNAME}}", username)
                .replace("{{PASSWORD}}", password)
                .replace("{{LOGIN_URL}}", loginUrl);
    }
}
