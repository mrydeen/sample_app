package com.smartgridz.config;

import com.smartgridz.controller.dto.EmailSetupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.*;
import java.util.Properties;

/**
 * This class is used to get and set email configuration parameters for the
 * application.
 */
@Configuration
@Service
public class EmailConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailConfigService.class);

    private SystemOptionsService systemOptionsService;

    /**
     * This is our local copy.
     */
    private JavaMailSenderImpl javaMailSender;

    public EmailConfigService(SystemOptionsService systemOptionsService) {
       this.systemOptionsService = systemOptionsService;
    }

    /**
     * When we go to send a mail, we want to make sure that the mail service
     * has the correct configuration from our system options file.  This will
     * get updated each time an email is sent.
     *
     * @return
     */
    @Bean
    @Qualifier("primaryMailSender")
    @Primary
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        this.javaMailSender = mailSender;
        updateJavaMailSender();
        return (JavaMailSender) mailSender;
    }

    /**
     * When there are email configuration changes, this will update the
     * mail sender with the changes.
     */
    private void updateJavaMailSender() {
        EmailSetupDto emailSetupDto = getEmailSettings();
        LOG.error("Updating email settings with: " + emailSetupDto.toString());

        if (emailSetupDto.getHost() != null) {
            this.javaMailSender.setHost(emailSetupDto.getHost());
            this.javaMailSender.setPort(Integer.valueOf(emailSetupDto.getPort()));
            this.javaMailSender.setUsername(emailSetupDto.getUserName());
            this.javaMailSender.setPassword(emailSetupDto.getPassword());

            Properties emailProps = this.javaMailSender.getJavaMailProperties();
            emailProps.put("mail.transport.protocol", "smtp");
            emailProps.put("mail.smtp.auth", emailSetupDto.getSmtpAuth());
            emailProps.put("mail.smtp.starttls.enable", emailSetupDto.getSmtpStartTlsEnable());
            emailProps.put("mail.debug", "true");
        }

    }

    /**
     * Retrieve the email configuration settings from the system options.
     *
     * @return current email configuration.
     */
    public EmailSetupDto getEmailSettings() {
        EmailSetupDto emailSetupDto = new EmailSetupDto();

        emailSetupDto.setHost(systemOptionsService.getOption(SystemOptions.EMAIL_HOST));
        emailSetupDto.setPort(systemOptionsService.getOption(SystemOptions.EMAIL_PORT));
        emailSetupDto.setUserName(systemOptionsService.getOption(SystemOptions.EMAIL_USERNAME));
        emailSetupDto.setPassword(systemOptionsService.getOption(SystemOptions.EMAIL_PASSWORD));
        emailSetupDto.setSmtpAuth(systemOptionsService.getOption(SystemOptions.EMAIL_SMTP_AUTH));
        emailSetupDto.setSmtpStartTlsEnable(systemOptionsService.getOption(SystemOptions.EMAIL_SMTP_STARTTLS_ENABLE));

        return emailSetupDto;
    }

    /**
     * Update the email settings in the system options.
     *
     * @param emailSetupDto to set
     */
    public void updateEmailSettings(EmailSetupDto emailSetupDto) {
        // Get the current email settings to validate against
        EmailSetupDto currentEmailSetupDto = getEmailSettings();

        // Look to update the host.
        if (emailSetupDto.getHost() != null) {
            systemOptionsService.setOption(SystemOptions.EMAIL_HOST, emailSetupDto.getHost());
        } else {
            systemOptionsService.setOption(SystemOptions.EMAIL_HOST, "");
        }

        // Look to update the port information.
        if (emailSetupDto.getPort() != null) {
            systemOptionsService.setOption(SystemOptions.EMAIL_PORT, emailSetupDto.getPort());
        } else {
            systemOptionsService.setOption(SystemOptions.EMAIL_PORT, "");
        }

        // Look to update the username information.
        if (emailSetupDto.getUserName() != null) {
            systemOptionsService.setOption(SystemOptions.EMAIL_USERNAME, emailSetupDto.getUserName());
        } else {
            systemOptionsService.setOption(SystemOptions.EMAIL_USERNAME, "");
        }

        // Look to update the password information.
        if (emailSetupDto.getPassword() != null) {
            systemOptionsService.setOption(SystemOptions.EMAIL_PASSWORD, emailSetupDto.getPassword());
        } else {
            systemOptionsService.setOption(SystemOptions.EMAIL_PASSWORD, "");
        }

        // Look to update the smtp auth flag.
        if (emailSetupDto.getSmtpAuth() != null) {
            systemOptionsService.setOption(SystemOptions.EMAIL_SMTP_AUTH, emailSetupDto.getSmtpAuth());
        } else {
            systemOptionsService.setOption(SystemOptions.EMAIL_SMTP_AUTH, "");
        }

        // Look to update the smtp starttls enable flag.
        if (emailSetupDto.getSmtpStartTlsEnable() != null) {
            systemOptionsService.setOption(SystemOptions.EMAIL_SMTP_STARTTLS_ENABLE, emailSetupDto.getSmtpStartTlsEnable());
        } else {
            systemOptionsService.setOption(SystemOptions.EMAIL_SMTP_STARTTLS_ENABLE, "false");
        }

        // Now update the mail sender with the new settings.
        updateJavaMailSender();
    }
}
