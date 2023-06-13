package com.smartgridz.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionTimerInterceptor implements HandlerInterceptor {
    private static final long MAX_INACTIVE_SESSION_TIME = 12 * 10000;

    private HttpSession session;

    Logger LOG = LoggerFactory.getLogger(SessionTimerInterceptor.class);

    @Autowired
    public SessionTimerInterceptor(HttpSession session) {
        this.session = session;
    }

    public SessionTimerInterceptor() {
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("Pre handle method - check handling start time");
        long startTime = System.currentTimeMillis();
        request.setAttribute("executionTime", startTime);
        if (UserInterceptor.isUserLogged()) {
            session = request.getSession();
            LOG.info(String.format("Time since last request in this session: %d ms",
                    System.currentTimeMillis() - request.getSession().getLastAccessedTime()));
            if (System.currentTimeMillis() - session.getLastAccessedTime()
                    > MAX_INACTIVE_SESSION_TIME) {
                LOG.warn("Logging out, due to inactive session");
                SecurityContextHolder.clearContext();
                //request.logout();
                response.sendRedirect("/logout");

            }
        }
        return true;
    }

    @Override
    public void postHandle( HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler,
                            ModelAndView model) throws Exception {
        LOG.info("Post handle method - check execution time of handling");
        long startTime = (Long) request.getAttribute("executionTime");
        LOG.info("Execution time for handling the request was: {} ms", System.currentTimeMillis() - startTime);
    }

}
