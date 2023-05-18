package com.smartgridz.service.impl;

import com.smartgridz.service.EmailService;
import com.sun.nio.sctp.SendFailedNotification;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

    private JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
       this.mailSender = mailSender;
    }

    public void sendResetPasswordMsg(String email, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setFrom("noreply@smartgridz.com", "SmartGridz Commander");
            helper.setTo(email);
            helper.setSubject("Here's the link to reset your password");

            StringBuffer content = new StringBuffer();
            content.append("<p>Hello,</p>");
            content.append("<br>");
            content.append("<p>A request was made to reset the password for the user associated with this email.</p>");
            content.append("<p>Click the link below to change your password:</p>");
            content.append("<p><a href=\"" + resetPasswordLink + "\"> Change password</a></p>");
            content.append("<br>");
            content.append("<p>If you did not request a password change, or you remember your password,</p>");
            content.append("<p>ignore this email.</p>");
            content.append("<br>");
            content.append("<p>This link will be valid for 3 hours</p>");

            helper.setText(content.toString(), true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
           LOG.error("Failed to format and send message: ", e);
           throw e;
        }
    }
}
