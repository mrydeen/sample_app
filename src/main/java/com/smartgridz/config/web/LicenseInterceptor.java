package com.smartgridz.config.web;

import com.smartgridz.domain.ProductLicense;
import com.smartgridz.service.LicenseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

/**
 * This class will be called on every REST call to inject license information
 * into the model/view so that we can display the licensing for the
 * customer.
 */
@Component
public class LicenseInterceptor implements HandlerInterceptor {

    Logger LOG = LoggerFactory.getLogger(LicenseInterceptor.class);

    @Autowired
    LicenseService licenseService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object object) throws Exception {
        addToModelLicenseDetails(request.getSession());
        return true;
    }

    private void addToModelLicenseDetails(HttpSession session) {
        LOG.error("=============== addToModelLicenseDetails:session =========================");

        // Get the license info and add to the session.
        ProductLicense license = licenseService.getProductLicense();

        session.setAttribute("license_is_valid", license.getIsValidLicense());
        LOG.error("session.license_is_valid = {}", license.getIsValidLicense());
        session.setAttribute("license_num_days_left", license.getNumberOfDaysLeftInLicense());
        LOG.error("session.license_num_days_left = {}", license.getNumberOfDaysLeftInLicense());

        LOG.error("=============== addToModelLicenseDetails:session =========================");
    }

    @Override
    public void postHandle(
            HttpServletRequest req,
            HttpServletResponse res,
            Object o,
            ModelAndView model) throws Exception {

        if (model != null && !isRedirectView(model)) {
            addToModelLicenseDetails(model);
        }
    }

    public static boolean isRedirectView(ModelAndView mv) {
        String viewName = mv.getViewName();
        if (viewName.startsWith("redirect:/")) {
            return true;
        }
        View view = mv.getView();
        return (view != null && view instanceof SmartView
                && ((SmartView) view).isRedirectView());
    }

    private void addToModelLicenseDetails(ModelAndView model) {
        LOG.error("=============== addToModelLicenseDetails:model =========================");

        // Get the license info and add to the session.
        ProductLicense license = licenseService.getProductLicense();

        model.addObject("license_is_valid", license.getIsValidLicense());
        model.addObject("license_num_days_left", license.getNumberOfDaysLeftInLicense());

        LOG.error("session : " + model.getModel());
        LOG.error("=============== addToModelLicenseDetails:model =========================");
    }

}
