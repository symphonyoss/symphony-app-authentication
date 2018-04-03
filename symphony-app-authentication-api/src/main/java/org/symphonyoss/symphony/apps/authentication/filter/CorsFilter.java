package org.symphonyoss.symphony.apps.authentication.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Web filter to enable Cross-Origin Resource Sharing.
 *
 * Created by rsanchez on 09/01/18.
 */
public class CorsFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);

  private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

  private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

  private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

  private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

  private static final String ALLOW_METHODS = "GET, OPTIONS, HEAD, PUT, POST";

  private static final String OPTIONS_METHOD = "OPTIONS";

  private static final String CONTENT_TYPE_HEADER = "Content-Type";

  private static final String ORIGIN_HEADER = "Origin";

  private static final String WILDCARD = "*";

  @Override
  public void init(FilterConfig config) throws ServletException {}

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String origin = request.getHeader(ORIGIN_HEADER);

    // Check if the origin header exists
    if (origin == null) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    // Authorize (allow) origin to consume the content
    response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
    response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, ALLOW_METHODS);
    response.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());
    response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, CONTENT_TYPE_HEADER);

    // For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
    if (request.getMethod().equals(OPTIONS_METHOD)) {
      response.setStatus(HttpServletResponse.SC_OK);
      return;
    }

    // pass the request along the filter chain
    chain.doFilter(request, servletResponse);
  }

  @Override
  public void destroy() {}

}
