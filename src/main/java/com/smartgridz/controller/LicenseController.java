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

/**
 * This controller deals with the licensing of the application.
 *   o - Is the application licensed?
 *   o - Add a license
 *   o - Update a license
 *   o - View the license
 */
@Controller
public class LicenseController {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseController.class);

    /**
     * Handler to get the email configuration.
     * @param model
     * @return
     */
    @GetMapping("/is_licensed")
    public String isLicensed(Model model){
        // Create model object to store form data
        model.addAttribute("isLicensed", false);
        return "license";
    }
}
