package com.challenge.generic.server.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestSessionIdFilter extends OncePerRequestFilter {

    public static final String REQ_SESSION_ID_KEY = "reqSessionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String reqSessionId = gerRequestSessionIdFromHeader(request);
            response.setHeader(REQ_SESSION_ID_KEY, reqSessionId);

            MDC.put(REQ_SESSION_ID_KEY, reqSessionId);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("An error has happened during Request Session filter.", e);
        } finally {
            MDC.remove(REQ_SESSION_ID_KEY);
        }
    }

    private String gerRequestSessionIdFromHeader(HttpServletRequest request) {
        String reqSessionId = request.getHeader(REQ_SESSION_ID_KEY);

        boolean isBlank = Optional.ofNullable(reqSessionId)
                .map(String::isBlank)
                .orElse(true);

        if (isBlank) {
            return UUID.randomUUID()
                    .toString();
        }

        UUID uuid = UUID.fromString(reqSessionId);
        log.debug("Found reqSessionId={} in the http request header.", uuid);

        return reqSessionId;
    }
}
