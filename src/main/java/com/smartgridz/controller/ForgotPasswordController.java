package com.smartgridz.controller;

import com.smartgridz.domain.entity.User;
import com.smartgridz.service.EmailService;
import com.smartgridz.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;

@Controller
public class ForgotPasswordController {

    private static final Logger LOG = LoggerFactory.getLogger(ForgotPasswordController.class);

    private EmailService emailService;

    private UserService userService;

    public ForgotPasswordController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    /**
     * This method will direct the browser to the forget_password.html file
     * when the user select the link.
     * @return
     */
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        LOG.error("HANDING BACK ---> forgot_password");
        return "forgotpassword/forgot_password";
    }

    /**
     * This method will create a random token to be used by the customer to
     * reset the password.  This gets called when the customer hits submit
     * on the form.
     *
     * @param userName
     * @return
     */
    @PostMapping("/forgot_password")
    public String processForgotPassword(@RequestParam(name = "username") String userName, HttpServletRequest request, Model model) {
        // Create a new token using a random uuid but remove the "_"
        String token = UUID.randomUUID().toString().replaceAll("-","");

        try {
            // Update the token in the db.  Catch if the user does not exist.
            userService.updateResetPasswordToken(token, userName);
            User user = userService.findUserByUserName(userName);

            // We need to send them an email with link to the reset password form
            String resetPasswordLink = getSiteUrl(request) + "/forgotpassword/reset_password?token=" + token;
            emailService.sendResetPasswordMsg(user.getEmail(), resetPasswordLink);

        } catch (ResourceAccessException rae) {
            return "redirect:/forgotpassword/forgot_password?error";
        } catch (Exception e) {
            LOG.error("Failed to process password update:", e);
            return "redirect:/forgotpassword/forgot_password?error";
        }

        // Send the user back to the forgot_password.html which has the error
        // and success message processing.
        return "redirect:/forgotpassword/forgot_password?success";
    }

    /**
     * This method will get our server site address from the request
     * @return site url
     */
    private String getSiteUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    /**
     * When a customer selects the reset email link, they will be brought to this
     * page, so show the reset_password.html.
     * @return
     */
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@RequestParam(value = "token") String token, Model model) {
        User user = userService.findByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            // Send them to the message.html due to error
            return "message";
        }
        return "forgotpassword/reset_password";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(@RequestParam(name = "token") String token, @RequestParam(name = "password") String password, Model model) {
        User user = userService.findByResetPasswordToken(token);
        model.addAttribute("title", "Reset your Password");

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            // Send them to the message.html due to error
            return "message";
        }
        userService.updatePassword(user, password);

        return "redirect:/auth/login?success_password_reset";
    }
}
