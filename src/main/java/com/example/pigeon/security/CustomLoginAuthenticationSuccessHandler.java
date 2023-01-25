package com.example.pigeon.security;

import com.example.pigeon.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    ActiveUserStore activeUserStore;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        addWelcomeCookie(getUserName(authentication), response);
        redirectStrategy.sendRedirect(request, response, "/homepage.html?user=" + authentication.getName());

        final HttpSession session = request.getSession(false);

        if(session != null) {
            session.setMaxInactiveInterval(30 * 60);
            String username;
            if(authentication.getPrincipal() instanceof User) {
                username = ((User)authentication.getPrincipal()).getEmail();
            } else {
                username = authentication.getName();
            }

            LoggedUser user = new LoggedUser(username, activeUserStore);
            session.setAttribute("user", user);
        }

        clearAuthenticationAttributes(request);
    }

    private String getUserName(final Authentication authentication) {

        return ((User) authentication.getPrincipal()).getFirstName();
    }

    private void addWelcomeCookie(final String user, final HttpServletResponse response) {

        Cookie welcomeCookie = getWelcomeCookie(user);
        response.addCookie(welcomeCookie);
    }

    private Cookie getWelcomeCookie(final String user) {

        Cookie welcomeCookie = new Cookie("welcome", user);
        welcomeCookie.setMaxAge(60 * 60 * 24 * 30);
        return welcomeCookie;
    }

    protected void clearAuthenticationAttributes(final HttpServletRequest request) {

        final HttpSession session = request.getSession(false);
        if(session == null) { return; }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() { return redirectStrategy; }
}
