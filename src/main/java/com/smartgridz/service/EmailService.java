package com.smartgridz.service;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailService {

    void sendResetPasswordMsg(String email, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException;
}
