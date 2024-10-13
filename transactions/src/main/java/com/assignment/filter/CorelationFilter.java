package com.assignment.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import static com.assignment.constants.TransactionConstants.CORRELATION_ID;

/**
 * Class to propagate the trace id from the calling API for logging and tracing.
 */
@Component
public class CorelationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No additional initialization required
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest httpServletRequest) {
            String correlationId = getOrGenerateCorrelationId(httpServletRequest);
            MDC.put(CORRELATION_ID, correlationId);

            try {
                filterChain.doFilter(httpServletRequest, servletResponse);
            } finally {
                MDC.remove(CORRELATION_ID); // Always remove correlation ID to avoid potential memory leaks
            }
        }
    }

    @Override
    public void destroy() {
        // No cleanup needed as of now
    }

    /**
     * Retrieves the correlation ID from the request header, or generates a new one if not present.
     *
     * @param request the HTTP servlet request
     * @return the correlation ID
     */
    private String getOrGenerateCorrelationId(HttpServletRequest request) {
        String id = request.getHeader(CORRELATION_ID);

        if (id != null && !id.isEmpty()) {
            return id;
        } else {
            return UUID.randomUUID().toString();  // Default or generated value
        }
    }

}
