package com.smartgridz.controller;

import com.smartgridz.config.EmailConfigService;
import com.smartgridz.controller.dto.EmailSetupDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EmailConfigController {

    private static final Logger LOG = LoggerFactory.getLogger(EmailConfigController.class);

    private EmailConfigService emailConfigService;

    public EmailConfigController(EmailConfigService emailConfigService) {
        this.emailConfigService = emailConfigService;
    }

    /**
     * Handler to get the email configuration.
     * @param model
     * @return
     */
    @GetMapping("/config_email")
    public String showEmailConfigForm(Model model){
        // Create model object to store form data
        EmailSetupDto emailSetupDto = emailConfigService.getEmailSettings();
        model.addAttribute("email", emailSetupDto);
        return "emailconfig/config_email";
    }

    /**
     * Handler to update the email configuration settings.
     * @param emailSetupDto
     * @param result
     * @param model
     * @return
     */
    @PostMapping("/config_email/save")
    public String registration(@Valid @ModelAttribute("email") EmailSetupDto emailSetupDto,
                               BindingResult result,
                               Model model){
        /*
            result.rejectValue("userName", null,
                    "There is already an account registered with the same user name");
        if(result.hasErrors()){
            model.addAttribute("user", userDto);
            return "register";
        }
         */

        emailConfigService.updateEmailSettings(emailSetupDto);
        return "redirect:/emailconfig/config_email?success";
    }
}
