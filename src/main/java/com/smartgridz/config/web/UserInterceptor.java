package com.smartgridz.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class UserInterceptor implements HandlerInterceptor {

    Logger LOG = LoggerFactory.getLogger(UserInterceptor.class);

    public static boolean isUserLogged() {
        try {
            return !SecurityContextHolder.getContext().getAuthentication()
                    .getName().equals("anonymousUser");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object object) throws Exception {
        if (isUserLogged()) {
            addToModelUserDetails(request.getSession());
        }
        return true;
    }

    private void addToModelUserDetails(HttpSession session) {
        LOG.error("=============== addToModelUserDetails =========================");

        String loggedUsername
                = SecurityContextHolder.getContext().getAuthentication().getName();
        session.setAttribute("username", loggedUsername);

        LOG.error("user(" + loggedUsername + ") session : " + session);
        LOG.error("=============== addToModelUserDetails =========================");
    }

    @Override
    public void postHandle(
            HttpServletRequest req,
            HttpServletResponse res,
            Object o,
            ModelAndView model) throws Exception {

        if (model != null && !isRedirectView(model)) {
            if (isUserLogged()) {
                addToModelUserDetails(model);
            }
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

    private void addToModelUserDetails(ModelAndView model) {
        LOG.error("=============== addToModelUserDetails =========================");

        String loggedUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        model.addObject("loggedUsername", loggedUsername);

        LOG.trace("session : " + model.getModel());
        LOG.error("=============== addToModelUserDetails =========================");
    }

}
