package com.example.pigeon.security;

import com.example.pigeon.event.OnDifferentLocationLoginEvent;
import com.example.pigeon.web.exception.UnusualLocationException;
import com.example.pigeon.model.NewLocationToken;
import com.example.pigeon.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class DifferentLocationChecker implements UserDetailsChecker {

    @Autowired
    private IUserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void check(UserDetails userDetails) {

        final String ip = getClientIp();
        final NewLocationToken token = userService.isNewLoginLocation(userDetails.getUsername(), ip);

        if(token != null) {
            final String url = "http://"
                    + request.getServerName()
                    + ":" + request.getServerPort()
                    + request.getContextPath();

            eventPublisher.publishEvent(new OnDifferentLocationLoginEvent(request.getLocale(),
                    userDetails.getUsername(), ip, token, url));

            try {
                throw new UnusualLocationException("unusual location");
            } catch (UnusualLocationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getClientIp() {

        final String xfHeader = request.getHeader("X-Forwarded-For");
        if(xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0];
    }
}
